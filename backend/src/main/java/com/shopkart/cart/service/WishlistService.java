package com.shopkart.cart.service;

import com.shopkart.cart.model.WishlistEntity;
import com.shopkart.cart.repo.WishlistRepo;
import com.shopkart.cart.util.CartConstants.Keys;
import com.shopkart.cart.util.CartExceptionStore;
import com.shopkart.catalog.internalagent.ProductAgent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WishlistService {

    private final WishlistRepo wishlistRepo;
    private final ProductAgent productAgent;

    public WishlistService(WishlistRepo wishlistRepo, ProductAgent productAgent) {
        this.wishlistRepo = wishlistRepo;
        this.productAgent = productAgent;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getWishlist(Long userId) {
        List<WishlistEntity> entities = wishlistRepo.findByUserId(userId);
        List<Map<String, Object>> wishListMap = new ArrayList<>();
        for(WishlistEntity entity : entities) {
            Map<String, Object> productMap = productAgent.getProductDetails(entity.getProductId());
            Map<String, Object> item = new LinkedHashMap<>(productMap);
            item.put(Keys.WISHLIST_ID, entity.getWishlistId());
            item.put(Keys.CREATED_TIME, entity.getCreatedTime());
            wishListMap.add(item);
        }
        return wishListMap;
    }

    @Transactional
    public Map<String, Object> addToWishlist(Long userId, Long productId) {
        if(wishlistRepo.existsByUserIdAndProductId(userId, productId)) {
            throw CartExceptionStore.PRODUCT_ALREADY_IN_WISHLIST.exception();
        }
        WishlistEntity saved = wishlistRepo.save(
                WishlistEntity.builder().userId(userId).productId(productId).build()
        );
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.WISHLIST_ID, saved.getWishlistId());
        response.put(Keys.PRODUCT_ID, saved.getProductId());
        response.put(Keys.CREATED_TIME, saved.getCreatedTime());
        return response;
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long wishlistId) {
        WishlistEntity entity = wishlistRepo.findById(wishlistId)
                .orElseThrow(CartExceptionStore.WISHLIST_ITEM_NOT_FOUND::exception);
        if(!entity.getUserId().equals(userId)) {
            throw CartExceptionStore.WISHLIST_ITEM_ACCESS_DENIED.exception();
        }
        wishlistRepo.delete(entity);
    }
}
