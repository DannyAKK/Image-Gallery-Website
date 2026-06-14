package uk.ac.bradford.imagegalleryweb.controller;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String showHomePage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "home";
    }
}