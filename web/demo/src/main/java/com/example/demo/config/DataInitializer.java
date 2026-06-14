package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        productRepository.save(createProduct(
                "Baklawa Tray",
                "Layers of flaky pastry filled with nuts and honey.",
                300));
        productRepository.save(createProduct(
                "Kalb El Louz Tray",
                "Semolina cake soaked in orange blossom syrup.",
                800));
        productRepository.save(createProduct(
                "VIP Wedding Box",
                "Premium ceremonial box for special occasions.",
                1000));
    }

    private Product createProduct(String name, String description, int price) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        return product;
    }
}
