package com.shopkart.cart.internalagent;

import com.shopkart.cart.model.CartEntity;
import com.shopkart.cart.repo.CartRepo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CartAgent {

    private final CartRepo cartRepo;

    public CartAgent(CartRepo cartRepo) {
        this.cartRepo = cartRepo;
    }

    public List<CartEntity> getCartEntities(Long userId) {
        return cartRepo.findByUserId(userId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartRepo.deleteByUserId(userId);
    }
}
