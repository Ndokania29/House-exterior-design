package com.exteriorp.designEx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling home/index page requests.
 */
@Controller
public class HomeController {

    /**
     * Redirects root requests to the index.html page.
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }
} 