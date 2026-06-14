package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ShopService;
import com.example.demo.service.UserService;

@Controller
public class ShopController {
    private static final String FLAG = "FLAG{integer_overflow_spring_boot_ctf}";

    private final UserService userService;
    private final ProductRepository productRepository;
    private final ShopService shopService;

    public ShopController(UserService userService,
                          ProductRepository productRepository,
                          ShopService shopService) {
        this.userService = userService;
        this.productRepository = productRepository;
        this.shopService = shopService;
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        User user = getSessionUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("products", productRepository.findAll());
        return "index";
    }

    @PostMapping("/claim-bonus")
    public String claimBonus(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = getSessionUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        boolean claimed = userService.claimBonus(user.getId());
        if (claimed) {
            redirectAttributes.addFlashAttribute("message", "Bonus claimed: +500 DA");
        } else {
            redirectAttributes.addFlashAttribute("error", "Bonus already claimed");
        }
        return "redirect:/";
    }

    @PostMapping("/buy")
    public String buy(HttpSession session,
                      @RequestParam Long productId,
                      @RequestParam int quantity,
                      RedirectAttributes redirectAttributes) {
        User user = getSessionUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        try {
            boolean vip = shopService.buy(user.getId(), productId, quantity);
            redirectAttributes.addFlashAttribute("message", "Order placed successfully.");
            if (vip) {
                redirectAttributes.addFlashAttribute("flag", FLAG);
            }
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/";
    }

    private User getSessionUser(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId instanceof Long id) {
            try {
                return userService.getById(id);
            } catch (IllegalArgumentException ex) {
                session.invalidate();
            }
        }
        return null;
    }
}
