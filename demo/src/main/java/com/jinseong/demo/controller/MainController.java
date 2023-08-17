package com.jinseong.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index(Model model) {
        return "index"; // index.html 뷰 반환
    }

    @GetMapping("/manageReservation")
    public String manageReservation(Model model) {
        return "manageReservation"; // index.html 뷰 반환
    }
}
