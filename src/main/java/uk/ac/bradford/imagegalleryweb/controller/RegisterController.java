package uk.ac.bradford.imagegalleryweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.bradford.imagegalleryweb.service.RegisterService;

@Controller
public class RegisterController {

    private final RegisterService registerService;

    // Injects the register service.
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @GetMapping("/register")
    public String showRegisterPage() {

        // Opens the register page.
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password) {

        // Sends the form data to the service.
        registerService.register(username, password);

        // After registering, go to login page.
        return "redirect:/login";
    }
}