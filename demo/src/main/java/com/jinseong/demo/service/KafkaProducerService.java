package com.jinseong.demo.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.jinseong.demo.entity.Reservation;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Kafka Producer를 사용하여 처리 성공 정보를 "reservation-success-topic"으로 전송
    public void sendReservationToSuccessTopic(Reservation reservation) {
    	log.info("성공 - " + reservation.toString()  );
        kafkaTemplate.send("reservation-result-topic", reservation.getName() + "님 예약이 전송되었습니다.");
    }
    
    // Kafka Producer를 사용하여 처리 실패 정보를 "reservation-error-topic"으로 전송
    public void sendReservationToErrorTopic(Reservation reservation) {
    	log.info("실패 - " +  reservation.toString());
        kafkaTemplate.send("reservation-result-topic", reservation.getName() + "님 예약이 전송 중 오류가 발생하였습니다.");
    }


}
