package com.jinseong.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jinseong.demo.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>{

}
