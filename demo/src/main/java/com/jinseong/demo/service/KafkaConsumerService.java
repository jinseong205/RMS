package com.jinseong.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.jinseong.demo.entity.Reservation;
import com.jinseong.demo.handler.SocketHandler;
import com.jinseong.demo.repository.ReservationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumerService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SocketHandler socketHandler;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = "reservation-topic", groupId = "my-consumer-group")
    public void receiveReservationData(String message) {
        log.info("Received message: " + message);

        // JSON 형식의 메시지를 파싱하여 Reservation 객체로 변환
        Reservation reservation = parseReservationMessage(message);

        try {
            // 예약 정보를 데이터베이스에 저장	
            reservationRepository.save(reservation);
            kafkaProducerService.sendReservationToSuccessTopic(reservation);

            // WebSocket을 통해 클라이언트로 데이터 전송
            socketHandler.broadcastMessage(reservation.toString());
        }catch(Exception e) {
            
            kafkaProducerService.sendReservationToErrorTopic(reservation);
        }

    }

    private Reservation parseReservationMessage(String message) {
        Gson gson = new Gson();
        
        try {
            // JSON 문자열을 Reservation 객체로 파싱
            Reservation reservation = gson.fromJson(message, Reservation.class);
            return reservation;
        } catch (Exception e) {
            // 파싱 실패 시 예외 처리
            log.error("Failed to parse JSON message: " + message, e);
            return null;
        }
    }
}
