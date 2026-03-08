package com.shopkart.cart.service;

import com.shopkart.cart.model.CartEntity;
import com.shopkart.cart.repo.CartRepo;
import com.shopkart.cart.util.CartConstants.Keys;
import com.shopkart.cart.util.CartExceptionStore;
import com.shopkart.catalog.dto.enums.Size;
import com.shopkart.catalog.internalagent.ProductAgent;
import com.shopkart.common.util.Constants;
import com.shopkart.common.util.CurrencyUtil;
import com.shopkart.order.internalagent.FeeDetailsAgent;
import com.shopkart.order.internalagent.resource.FeeResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("1000");

    private final CartRepo cartRepo;
    private final ProductAgent productAgent;
    private final FeeDetailsAgent feeDetailsAgent;

    public CartService(CartRepo cartRepo, ProductAgent productAgent, FeeDetailsAgent feeDetailsAgent) {
        this.cartRepo = cartRepo;
        this.productAgent = productAgent;
        this.feeDetailsAgent = feeDetailsAgent;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCart(Long userId) {
        List<CartEntity> cartEntities = cartRepo.findByUserId(userId);

        BigDecimal orderAmount = BigDecimal.ZERO;
        BigDecimal orderSavings = BigDecimal.ZERO;
        List<Map<String, Object>> cartProducts = new ArrayList<>();

        for(CartEntity entity : cartEntities) {
            Map<String, Object> productMap = productAgent.getProductDetails(entity.getProductId(), Size.getName(entity.getSize()));

            Integer stockLeft = (Integer) productMap.get(Keys.QUANTITY);
            int quantity = Math.min(entity.getQuantity(), stockLeft);
            String quantityFormatted = quantity == 0 ? Constants.Phrases.OUT_OF_STOCK : Constants.Phrases.STOCK_AVAILABLE;

            BigDecimal sellingPrice = (BigDecimal) productMap.get(Keys.SELLING_PRICE);
            BigDecimal originalPrice = (BigDecimal) productMap.get(Keys.ORIGINAL_PRICE);
            BigDecimal itemSellingTotal = sellingPrice.multiply(BigDecimal.valueOf(quantity));
            BigDecimal itemOriginalTotal = originalPrice.multiply(BigDecimal.valueOf(quantity));
            BigDecimal itemSavings = originalPrice.subtract(sellingPrice).multiply(BigDecimal.valueOf(quantity));

            orderAmount = orderAmount.add(itemSellingTotal);
            orderSavings = orderSavings.add(itemSavings);

            Map<String, Object> item = new LinkedHashMap<>(productMap);
            item.put(Keys.CART_ID, entity.getCartId());
            item.put(Keys.QUANTITY, quantity);
            item.put(Keys.QUANTITY + Keys.FORMATTED_SUFFIX, quantityFormatted);
            item.put(Keys.ITEM_SELLING_TOTAL, itemSellingTotal);
            item.put(Keys.ITEM_SELLING_TOTAL + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(itemSellingTotal));
            item.put(Keys.ITEM_ORIGINAL_TOTAL, itemOriginalTotal);
            item.put(Keys.ITEM_ORIGINAL_TOTAL + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(itemOriginalTotal));
            item.put(Keys.ITEM_SAVINGS, itemSavings);
            item.put(Keys.ITEM_SAVINGS + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(itemSavings));
            cartProducts.add(item);
        }

        FeeResource fees = feeDetailsAgent.getConvenienceFees();
        BigDecimal deliveryFee = fees.getDeliveryFee();
        String deliveryFeeFormatted;

        if(orderAmount.compareTo(FREE_DELIVERY_THRESHOLD) >= 0) {
            deliveryFee = BigDecimal.ZERO;
            deliveryFeeFormatted = Constants.Phrases.FREE;
        } else {
            deliveryFeeFormatted = CurrencyUtil.formatWithINR(deliveryFee);
        }

        BigDecimal orderTotal = orderAmount.add(fees.getPlatformFee()).add(deliveryFee);

        Map<String, Object> convenienceFee = new LinkedHashMap<>();
        convenienceFee.put(Keys.DELIVERY_FEE, deliveryFee);
        convenienceFee.put(Keys.DELIVERY_FEE + Keys.FORMATTED_SUFFIX, deliveryFeeFormatted);
        convenienceFee.put(Keys.PLATFORM_FEE, fees.getPlatformFee());
        convenienceFee.put(Keys.PLATFORM_FEE + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(fees.getPlatformFee()));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.CART_PRODUCTS, cartProducts);
        response.put(Keys.ORDER_AMOUNT, orderAmount);
        response.put(Keys.ORDER_AMOUNT + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(orderAmount));
        response.put(Keys.ORDER_SAVINGS, orderSavings);
        response.put(Keys.ORDER_SAVINGS + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(orderSavings));
        response.put(Keys.CONVENIENCE_FEE, convenienceFee);
        response.put(Keys.ORDER_TOTAL, orderTotal);
        response.put(Keys.ORDER_TOTAL + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(orderTotal));
        return response;
    }

    @Transactional
    public Map<String, Object> addToCart(Long userId, Long productId, Integer sizeCode) {
        return cartRepo.findByUserIdAndProductIdAndSize(userId, productId, sizeCode)
                .map(this::buildCartItemResponse)
                .orElseGet(() -> {
                    CartEntity saved = cartRepo.save(
                            CartEntity.builder()
                                    .userId(userId)
                                    .productId(productId)
                                    .size(sizeCode)
                                    .quantity(1)
                                    .build()
                    );
                    return buildCartItemResponse(saved);
                });
    }

    @Transactional
    public Map<String, Object> updateQuantity(Long userId, Long cartId, Integer quantity) {
        CartEntity entity = cartRepo.findById(cartId)
                .orElseThrow(CartExceptionStore.PRODUCT_NOT_IN_CART::exception);
        if(!entity.getUserId().equals(userId)) {
            throw CartExceptionStore.CART_ITEM_ACCESS_DENIED.exception();
        }
        Integer stockLeft = productAgent.getStockQuantity(entity.getProductId(), entity.getSize());
        if(quantity > stockLeft) {
            throw CartExceptionStore.QUANTITY_EXCEEDS_STOCK.exception();
        }
        entity.setQuantity(quantity);
        return buildCartItemResponse(cartRepo.save(entity));
    }

    @Transactional
    public void removeFromCart(Long userId, Long cartId) {
        CartEntity entity = cartRepo.findById(cartId)
                .orElseThrow(CartExceptionStore.PRODUCT_NOT_IN_CART::exception);
        if(!entity.getUserId().equals(userId)) {
            throw CartExceptionStore.CART_ITEM_ACCESS_DENIED.exception();
        }
        cartRepo.delete(entity);
    }

    private Map<String, Object> buildCartItemResponse(CartEntity entity) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.CART_ID, entity.getCartId());
        response.put(Keys.PRODUCT_ID, entity.getProductId());
        response.put(Keys.SIZE, Size.getName(entity.getSize()));
        response.put(Keys.SIZE + Keys.FORMATTED_SUFFIX, Size.getDisplayName(entity.getSize()));
        response.put(Keys.QUANTITY, entity.getQuantity());
        return response;
    }
}
