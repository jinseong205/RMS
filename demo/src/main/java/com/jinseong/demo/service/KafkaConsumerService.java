package com.jinseong.demo.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.jinseong.demo.entity.Reservation;
import com.jinseong.demo.handler.SocketHandler;
import com.jinseong.demo.repository.ReservationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumerService {

	@Autowired
    ReservationRepository reservationRepository;
	
	@Autowired
    private SocketHandler socketHandler;

    @KafkaListener(topics = "reservation-topic", groupId = "my-consumer-group")
    public void receiveReservationData(String message) throws IOException {
    	log.info("message --> " + message);
    	String[] items = message.split(",");
    	
    	for(int i = 0; i < items.length; i++) {
    		items[i] = items[i].substring(items[i].indexOf(":") + 1).trim();
    	}
    	
    	Reservation r = Reservation.builder()
    			.name(items[0])
    			.date(items[1])
    			.time(items[2])
    			.count(items[3])
    			.number(items[4])
    			.status("WAIT")
    			.build();
    	reservationRepository.save(r);
    	
    	log.info(r.toString());
    	
    	// Kafka 메시지 수신 시 WebSocket을 통해 클라이언트로 데이터 전송
    	socketHandler.broadcastMessage(r.toString());
    }
}
