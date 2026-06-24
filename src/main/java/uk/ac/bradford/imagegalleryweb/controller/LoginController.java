package uk.ac.bradford.imagegalleryweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {

        // Opens the login page.
        return "login";
    }
}