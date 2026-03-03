package com.shopkart.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig {
    public static final String PRODUCTS = "products";
    public static final String CATEGORIES = "categories";
    public static final String CARTS = "carts";
    public static final String ORDERS = "orders";
    public static final String PAYMENTS = "payments";

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager manager = new ConcurrentMapCacheManager(
                PRODUCTS, CATEGORIES, CARTS, ORDERS, PAYMENTS
        );
        manager.setAllowNullValues(false);
        return manager;
    }

    @Bean("cacheKeyGenerator")
    public KeyGenerator cacheKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName())
                    .append('.')
                    .append(method.getName());

            if (params.length > 0) {
                sb.append(':').append(Arrays.deepHashCode(params));
            }
            return sb.toString();
        };
    }
}
