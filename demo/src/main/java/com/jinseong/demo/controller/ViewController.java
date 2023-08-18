package com.jinseong.demo.controller;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.jinseong.demo.entity.Reservation;
import com.jinseong.demo.service.ReservationService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ViewController {
	
	@Autowired
	ReservationService reservationService;
	
    @GetMapping("/")
    public String index(Model model) {
        return "index"; 
    }

    @GetMapping("/manageReservation")
    public String manageReservation(Model model, Optional<Integer> page) {
		Pageable pegeable = PageRequest.of(page.isPresent()? page.get(): 0,10);
    	
		Page<Reservation> reservations = reservationService.findReservations(pegeable);
		
		for(Reservation reservation : reservations) {
			log.info(reservation.getStatus() + " ");
		}
		
		model.addAttribute("reservations", reservations);
        return "manageReservation";
    }
}
