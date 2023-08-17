package com.jinseong.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jinseong.demo.entity.Reservation;
import com.jinseong.demo.repository.ReservationRepository;

@Service
public class ReservationService {
	
	@Autowired
	ReservationRepository reservationRepository;
	
	public List<Reservation> findReservation(){
		reservationRepository.findAll();
		return null;
	}
}
