package com.jinseong.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jinseong.demo.service.ReservationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RervationRestController {

	@Autowired
	ReservationService reservationService;
	
	@PatchMapping("/api/approveRes/{id}")
	public ResponseEntity<Integer> approveReservation(@PathVariable Long id){
		log.info("====> approve " + id);
		Long rid = reservationService.approveReservation(id);
		return new ResponseEntity(rid, HttpStatus.OK);
	}
	
	@PatchMapping("/api/rejectRes/{id}")
	public ResponseEntity<Integer> rejectReservation(@PathVariable Long id){
		log.info("====> reject " + id);
		Long rid = reservationService.rejectReservation(id);
		return new ResponseEntity(rid, HttpStatus.OK);
	}
}
