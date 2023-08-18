package com.jinseong.demo.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jinseong.demo.entity.Reservation;
import com.jinseong.demo.repository.ReservationRepository;

@Service
public class ReservationService {
	
	@Autowired
	ReservationRepository reservationRepository;
	
	public Page<Reservation> findReservations(Pageable pageable){
		Page<Reservation> reservations = reservationRepository.findAllOrderByIdDesc(pageable);
		
		return reservations;
	}
	
	
	public Long approveReservation(Long id) {
		Reservation r = reservationRepository.findById(id).orElseThrow(()-> new EntityNotFoundException());
		r.setStatus("APPROVE");
		reservationRepository.save(r);
		return r.getId();
	}

	public Long rejectReservation(Long id) {
		Reservation r = reservationRepository.findById(id).orElseThrow(()-> new EntityNotFoundException());
		r.setStatus("REJECT");
		reservationRepository.save(r);
		return r.getId();
	}
}
