package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.User;
import com.example.demo.service.LoginAttemptService;
import com.example.demo.service.UserService;

@Controller
public class AuthController {
    private final UserService userService;
    private final LoginAttemptService loginAttemptService;

    public AuthController(UserService userService, LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.loginAttemptService = loginAttemptService;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.register(username.trim(), password);
            redirectAttributes.addFlashAttribute("message", "Account created. Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        String loginKey = loginAttemptService.buildKey(username, request.getRemoteAddr());
        if (loginAttemptService.isBlocked(loginKey)) {
            redirectAttributes.addFlashAttribute("error", "Too many failed login attempts. Try again later.");
            return "redirect:/login";
        }

        try {
            User user = userService.login(username.trim(), password);
            loginAttemptService.recordSuccess(loginKey);
            session.setAttribute("userId", user.getId());
            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            loginAttemptService.recordFailure(loginKey);
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
