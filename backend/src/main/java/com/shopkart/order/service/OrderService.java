package com.shopkart.order.service;

import com.shopkart.cart.internalagent.CartAgent;
import com.shopkart.cart.model.CartEntity;
import com.shopkart.catalog.dto.enums.Size;
import com.shopkart.catalog.internalagent.ProductAgent;
import com.shopkart.catalog.util.CatalogConstants;
import com.shopkart.common.util.Constants;
import com.shopkart.common.util.CurrencyUtil;
import com.shopkart.common.util.PageConstants;
import com.shopkart.common.util.PageRequestBuilder;
import com.shopkart.common.util.PagedResponse;
import com.shopkart.order.dto.enums.OrderStatus;
import com.shopkart.order.dto.enums.PaymentMode;
import com.shopkart.order.dto.enums.PaymentStatus;
import com.shopkart.order.dto.request.CancelPaymentRequest;
import com.shopkart.order.dto.request.CreateOrderRequest;
import com.shopkart.order.dto.request.UpdateOrderStatusRequest;
import com.shopkart.order.internalagent.AddressAgent;
import com.shopkart.order.internalagent.FeeDetailsAgent;
import com.shopkart.order.internalagent.resource.AddressResource;
import com.shopkart.order.internalagent.resource.FeeResource;
import com.shopkart.order.model.OrderAddressEntity;
import com.shopkart.order.model.OrderEntity;
import com.shopkart.order.model.OrderMappingEntity;
import com.shopkart.order.repo.OrderAddressRepo;
import com.shopkart.order.repo.OrderMappingRepo;
import com.shopkart.order.repo.OrderRepo;
import com.shopkart.order.util.OrderConstants;
import com.shopkart.order.util.OrderConstants.Keys;
import com.shopkart.order.util.OrderExceptionStore;
import com.shopkart.payment.dto.response.ExternalAPIResponse;
import com.shopkart.payment.internalagent.PaymentAgent;
import com.shopkart.payment.internalagent.resource.PaymentIntentResource;
import com.shopkart.payment.service.PaymentGatewayClient;
import com.shopkart.payment.util.PaymentExceptionStore;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("1000");

    private final OrderRepo orderRepo;
    private final OrderMappingRepo orderMappingRepo;
    private final OrderAddressRepo orderAddressRepo;
    private final ProductAgent productAgent;
    private final FeeDetailsAgent feeDetailsAgent;
    private final AddressAgent addressAgent;
    private final CartAgent cartAgent;
    private final PaymentAgent paymentAgent;
    private final PaymentGatewayClient paymentGatewayClient;

    @Transactional
    public Map<String, Object> createOrder(Long userId, CreateOrderRequest request) {

        List<CartEntity> cartEntities = cartAgent.getCartEntities(userId);
        if(cartEntities.isEmpty()) {
            throw OrderExceptionStore.EMPTY_CART.exception();
        }

        AddressResource address = addressAgent.getAddress(userId, request.getAddressId());
        if(address == null) {
            throw OrderExceptionStore.ADDRESS_NOT_FOUND.exception();
        }

        BigDecimal orderAmount = BigDecimal.ZERO;
        BigDecimal orderSavings = BigDecimal.ZERO;
        List<OrderMappingEntity> mappings = new ArrayList<>();

        for(CartEntity cart : cartEntities) {
            String sizeName = Size.getName(cart.getSize());
            Map<String, Object> productMap = productAgent.getProductDetails(cart.getProductId(), sizeName);

            BigDecimal sellingPrice = (BigDecimal) productMap.get(CatalogConstants.Keys.SELLING_PRICE);
            BigDecimal originalPrice = (BigDecimal) productMap.get(CatalogConstants.Keys.ORIGINAL_PRICE);
            String imageUrl = (String) productMap.get(CatalogConstants.Keys.IMAGE);
            String productName = (String) productMap.get(CatalogConstants.Keys.NAME);
            BigDecimal savings = originalPrice.subtract(sellingPrice);

            orderAmount = orderAmount.add(sellingPrice.multiply(BigDecimal.valueOf(cart.getQuantity())));
            orderSavings = orderSavings.add(savings.multiply(BigDecimal.valueOf(cart.getQuantity())));

            mappings.add(OrderMappingEntity.builder()
                    .userId(userId)
                    .productId(cart.getProductId())
                    .imageUrl(imageUrl)
                    .productName(productName)
                    .size(cart.getSize())
                    .quantity(cart.getQuantity())
                    .sellingPrice(sellingPrice)
                    .originalPrice(originalPrice)
                    .savings(savings)
                    .build());
        }

        FeeResource fees = feeDetailsAgent.getConvenienceFees();
        BigDecimal deliveryFee = fees.getDeliveryFee();
        if(orderAmount.compareTo(FREE_DELIVERY_THRESHOLD) >= 0) {
            deliveryFee = BigDecimal.ZERO;
        }
        BigDecimal convenienceFee = deliveryFee.add(fees.getPlatformFee());
        BigDecimal orderTotal = orderAmount.add(convenienceFee);

        boolean isPOD = PaymentMode.PAY_ON_DELIVERY.name.equals(request.getPaymentMode());

        OrderEntity order = orderRepo.save(OrderEntity.builder()
                .userId(userId)
                .orderAmount(orderAmount)
                .orderSavings(orderSavings)
                .convenienceFee(convenienceFee)
                .orderTotal(orderTotal)
                .paymentMode(PaymentMode.getCode(request.getPaymentMode()))
                .paymentStatus(isPOD ? PaymentStatus.YET_TO_BE_PAID.code : PaymentStatus.INITIATED.code)
                .orderStatus(isPOD ? OrderStatus.CONFIRMED.code : OrderStatus.PAYMENT_PENDING.code)
                .initiatedTime(System.currentTimeMillis())
                .build());

        Long orderId = order.getOrderId();

        for(OrderMappingEntity mapping : mappings) {
            mapping.setOrderId(orderId);
            if(isPOD) {
                productAgent.deductStock(mapping.getProductId(), mapping.getSize(), mapping.getQuantity());
            }
        }
        orderMappingRepo.saveAll(mappings);

        orderAddressRepo.save(OrderAddressEntity.builder()
                .orderId(orderId)
                .name(address.getName())
                .contactNumber(address.getContactNumber())
                .firstLine(address.getFirstLine())
                .secondLine(address.getSecondLine())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .build());

        Map<String, Object> response = new LinkedHashMap<>();

        if(isPOD) {
            cartAgent.clearCart(userId);
            response.put(Keys.ORDER_ID, orderId);
            response.put(Keys.ORDER_STATUS, OrderStatus.getName(order.getOrderStatus()));
            response.put(Keys.ORDER_STATUS + Keys.FORMATTED_SUFFIX, OrderStatus.getDisplayName(order.getOrderStatus()));
        } else {
            Long paymentIntentId = paymentAgent.createPaymentIntent(userId, orderId, orderTotal);
            ExternalAPIResponse externalAPIResponse = paymentGatewayClient.createPaymentMockSuccess(paymentIntentId, userId, orderTotal);

            if(!externalAPIResponse.isSuccess()) {
                paymentAgent.updatePaymentIntentStatus(paymentIntentId, PaymentStatus.FAILED.code);
                order.setPaymentStatus(PaymentStatus.FAILED.code);
                order.setOrderStatus(OrderStatus.PAYMENT_FAILED.code);
                orderRepo.save(order);
                throw PaymentExceptionStore.PAYMENT_CREATION_FAILED.exception();
            }

            response.put(Keys.ORDER_ID, orderId);
            response.put(Keys.PAYMENT_INTENT_ID, paymentIntentId);
        }

        return response;
    }

    @Transactional
    public Map<String, Object> cancelPayment(Long userId, CancelPaymentRequest request) {
        PaymentIntentResource intent = paymentAgent.getPaymentIntent(request.getPaymentIntentId());
        if(intent == null) {
            throw PaymentExceptionStore.PAYMENT_INTENT_NOT_FOUND.exception();
        }
        if(!intent.getUserId().equals(userId)) {
            throw OrderExceptionStore.ORDER_ACCESS_DENIED.exception();
        }

        if(intent.getPaymentStatus().equals(PaymentStatus.INITIATED.code)) {
            paymentAgent.updatePaymentIntentStatus(intent.getPaymentIntentId(), PaymentStatus.FAILED.code);

            orderRepo.findById(intent.getOrderId()).ifPresent(order -> {
                order.setPaymentStatus(PaymentStatus.FAILED.code);
                order.setOrderStatus(OrderStatus.PAYMENT_FAILED.code);
                orderRepo.save(order);
            });
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.STATUS, intent.getPaymentStatus());
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOrders(Long userId, Map<String, String> params) {
        Map<String, String> effectiveParams = new HashMap<>(params);
        if(!effectiveParams.containsKey(PageConstants.Params.SORT_ORDER)) {
            effectiveParams.put(PageConstants.Params.SORT_ORDER, Constants.Sort.DESC);
        }

        Pageable pageable = PageRequestBuilder.build(
                effectiveParams, OrderConstants.SortLabels.INITIATED_TIME, OrderConstants.SORT_FIELD_MAP);

        Page<OrderEntity> orderPage;

        String orderIdSearch = params.get(Keys.ORDER_ID);
        if(orderIdSearch != null && !orderIdSearch.isBlank()) {
            try {
                Long searchOrderId = Long.parseLong(orderIdSearch);
                List<OrderEntity> list = orderRepo.findByOrderIdAndUserId(searchOrderId, userId)
                        .map(List::of).orElse(List.of());
                orderPage = new PageImpl<>(list, pageable, list.size());
            } catch(NumberFormatException e) {
                orderPage = Page.empty(pageable);
            }
        } else {
            String statusFilter = params.get(Keys.ORDER_STATUS);
            if(statusFilter != null && !statusFilter.isBlank()) {
                List<Integer> statusCodes = Arrays.stream(statusFilter.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(OrderStatus::getCode)
                        .collect(Collectors.toList());
                orderPage = orderRepo.findByUserIdAndOrderStatusIn(userId, statusCodes, pageable);
            } else {
                orderPage = orderRepo.findByUserId(userId, pageable);
            }
        }

        List<Long> orderIds = orderPage.getContent().stream()
                .map(OrderEntity::getOrderId)
                .collect(Collectors.toList());
        Map<Long, List<OrderMappingEntity>> mappingsByOrderId = orderIds.isEmpty()
                ? Map.of()
                : orderMappingRepo.findByOrderIdIn(orderIds).stream()
                .collect(Collectors.groupingBy(OrderMappingEntity::getOrderId));

        List<Map<String, Object>> items = new ArrayList<>();
        for(OrderEntity order : orderPage.getContent()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put(Keys.ORDER_ID, order.getOrderId());
            item.put(Keys.ORDER_STATUS, OrderStatus.getName(order.getOrderStatus()));
            item.put(Keys.ORDER_STATUS + Keys.FORMATTED_SUFFIX, OrderStatus.getDisplayName(order.getOrderStatus()));
            item.put(Keys.ORDER_TOTAL, order.getOrderTotal());
            item.put(Keys.ORDER_TOTAL + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(order.getOrderTotal()));
            item.put(Keys.INITIATED_TIME, order.getInitiatedTime());

            List<Map<String, Object>> products = new ArrayList<>();
            for(OrderMappingEntity mapping : mappingsByOrderId.getOrDefault(order.getOrderId(), List.of())) {
                Map<String, Object> product = new LinkedHashMap<>();
                product.put(Keys.IMAGE_URL, mapping.getImageUrl());
                product.put(Keys.PRODUCT_NAME, mapping.getProductName());
                product.put(Keys.SIZE, Size.getName(mapping.getSize()));
                product.put(Keys.SIZE + Keys.FORMATTED_SUFFIX, Size.getDisplayName(mapping.getSize()));
                product.put(Keys.QUANTITY, mapping.getQuantity());
                products.add(product);
            }
            item.put(Keys.PRODUCTS, products);
            items.add(item);
        }

        return PagedResponse.from(orderPage, items);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOrderDetails(Long userId, Long orderId) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(OrderExceptionStore.ORDER_NOT_FOUND::exception);
        if(!order.getUserId().equals(userId)) {
            throw OrderExceptionStore.ORDER_ACCESS_DENIED.exception();
        }
        return buildOrderDetailsResponse(order);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOrderDetailsAdmin(Long orderId) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(OrderExceptionStore.ORDER_NOT_FOUND::exception);
        return buildOrderDetailsResponse(order);
    }

    private Map<String, Object> buildOrderDetailsResponse(OrderEntity order) {
        Long orderId = order.getOrderId();
        List<OrderMappingEntity> mappings = orderMappingRepo.findByOrderId(orderId);
        OrderAddressEntity address = orderAddressRepo.findByOrderId(orderId).orElse(null);
        Map<String, Object> paymentDetails = paymentAgent.getPaymentByOrderId(orderId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.ORDER_ID, order.getOrderId());
        response.put(Keys.USER_ID, order.getUserId());
        response.put(Keys.ORDER_AMOUNT, order.getOrderAmount());
        response.put(Keys.ORDER_AMOUNT + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(order.getOrderAmount()));
        response.put(Keys.ORDER_SAVINGS, order.getOrderSavings());
        response.put(Keys.ORDER_SAVINGS + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(order.getOrderSavings()));
        response.put(Keys.ORDER_TOTAL, order.getOrderTotal());
        response.put(Keys.ORDER_TOTAL + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(order.getOrderTotal()));
        response.put(Keys.PAYMENT_MODE, PaymentMode.getName(order.getPaymentMode()));
        response.put(Keys.PAYMENT_MODE + Keys.FORMATTED_SUFFIX, PaymentMode.getDisplayName(order.getPaymentMode()));
        response.put(Keys.PAYMENT_STATUS, PaymentStatus.getName(order.getPaymentStatus()));
        response.put(Keys.PAYMENT_STATUS + Keys.FORMATTED_SUFFIX, PaymentStatus.getDisplayName(order.getPaymentStatus()));
        response.put(Keys.ORDER_STATUS, OrderStatus.getName(order.getOrderStatus()));
        response.put(Keys.ORDER_STATUS + Keys.FORMATTED_SUFFIX, OrderStatus.getDisplayName(order.getOrderStatus()));
        response.put(Keys.INITIATED_TIME, order.getInitiatedTime());
        response.put(Keys.DELIVERED_TIME, order.getDeliveredTime());

        FeeResource fees = feeDetailsAgent.getConvenienceFees();
        BigDecimal deliveryFee = fees.getDeliveryFee();
        if(order.getOrderAmount().compareTo(FREE_DELIVERY_THRESHOLD) >= 0) {
            deliveryFee = BigDecimal.ZERO;
        }
        String deliveryFeeFormatted = deliveryFee.compareTo(BigDecimal.ZERO) == 0
                ? Constants.Phrases.FREE
                : CurrencyUtil.formatWithINR(deliveryFee);

        Map<String, Object> convenienceFeeMap = new LinkedHashMap<>();
        convenienceFeeMap.put(Keys.DELIVERY_FEE, deliveryFee);
        convenienceFeeMap.put(Keys.DELIVERY_FEE + Keys.FORMATTED_SUFFIX, deliveryFeeFormatted);
        convenienceFeeMap.put(Keys.PLATFORM_FEE, fees.getPlatformFee());
        convenienceFeeMap.put(Keys.PLATFORM_FEE + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(fees.getPlatformFee()));
        response.put(Keys.CONVENIENCE_FEE, convenienceFeeMap);

        List<Map<String, Object>> products = new ArrayList<>();
        for(OrderMappingEntity mapping : mappings) {
            Map<String, Object> product = new LinkedHashMap<>();
            product.put(Keys.IMAGE_URL, mapping.getImageUrl());
            product.put(Keys.PRODUCT_NAME, mapping.getProductName());
            product.put(Keys.SIZE, Size.getName(mapping.getSize()));
            product.put(Keys.SIZE + Keys.FORMATTED_SUFFIX, Size.getDisplayName(mapping.getSize()));
            product.put(Keys.QUANTITY, mapping.getQuantity());
            product.put(Keys.SELLING_PRICE, mapping.getSellingPrice());
            product.put(Keys.SELLING_PRICE + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(mapping.getSellingPrice()));
            product.put(Keys.ORIGINAL_PRICE, mapping.getOriginalPrice());
            product.put(Keys.ORIGINAL_PRICE + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(mapping.getOriginalPrice()));
            product.put(Keys.SAVINGS, mapping.getSavings());
            product.put(Keys.SAVINGS + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(mapping.getSavings()));
            products.add(product);
        }
        response.put(Keys.PRODUCTS, products);

        if(address != null) {
            Map<String, Object> addressMap = new LinkedHashMap<>();
            addressMap.put(Keys.NAME, address.getName());
            addressMap.put(Keys.CONTACT_NUMBER, address.getContactNumber());
            addressMap.put(Keys.FIRST_LINE, address.getFirstLine());
            addressMap.put(Keys.SECOND_LINE, address.getSecondLine());
            addressMap.put(Keys.LANDMARK, address.getLandmark());
            addressMap.put(Keys.CITY, address.getCity());
            addressMap.put(Keys.STATE, address.getState());
            addressMap.put(Keys.PINCODE, address.getPincode());
            response.put(Keys.ADDRESS, addressMap);
        }

        if(paymentDetails != null) {
            response.putAll(paymentDetails);
        }

        return response;
    }

    private static final Map<String, List<OrderStatus>> VALID_STATUS = Map.of(
            OrderStatus.SHIPPED.name, List.of(OrderStatus.CONFIRMED),
            OrderStatus.DELIVERED.name, List.of(OrderStatus.SHIPPED)
    );

    @Transactional
    public Map<String, Object> updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(OrderExceptionStore.ORDER_NOT_FOUND::exception);

        if(VALID_STATUS.containsKey(request.getOrderStatus()) && !VALID_STATUS.get(request.getOrderStatus()).contains(OrderStatus.getOrderStatus(order.getOrderStatus()))) {
            throw OrderExceptionStore.INVALID_ORDER_STATUS.exception();
        }

        int newStatusCode = OrderStatus.getCode(request.getOrderStatus());
        order.setOrderStatus(newStatusCode);

        if(newStatusCode == OrderStatus.DELIVERED.code) {
            order.setDeliveredTime(System.currentTimeMillis());
            order.setPaymentStatus(PaymentStatus.PAID.code);
        }

        if(newStatusCode == OrderStatus.CONFIRMED.code) {
            List<OrderMappingEntity> mappings = orderMappingRepo.findByOrderId(orderId);
            for(OrderMappingEntity mapping : mappings) {
                productAgent.deductStock(mapping.getProductId(), mapping.getSize(), mapping.getQuantity());
            }
        }

        orderRepo.save(order);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.ORDER_ID, order.getOrderId());
        response.put(Keys.ORDER_STATUS, OrderStatus.getName(order.getOrderStatus()));
        response.put(Keys.ORDER_STATUS + Keys.FORMATTED_SUFFIX, OrderStatus.getDisplayName(order.getOrderStatus()));
        return response;
    }
}
