package com.example.demo.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ShopService {
    private static final String VIP_PRODUCT_NAME = "VIP Wedding Box";

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ShopService(UserRepository userRepository,
                       ProductRepository productRepository,
                       OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public boolean buy(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        int total = quantity * product.getPrice();
        if (user.getBalance() < total) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        user.setBalance(user.getBalance() - total);
        userRepository.save(user);

        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setTotal(total);
        order.setCreatedAt(Instant.now());
        orderRepository.save(order);

        return VIP_PRODUCT_NAME.equals(product.getName());
    }
}
