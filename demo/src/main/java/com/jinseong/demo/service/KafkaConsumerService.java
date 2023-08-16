package com.jinseong.demo.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.jinseong.demo.handler.SocketHandler;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumerService {

    @Autowired
    private SocketHandler socketHandler;

    @KafkaListener(topics = "reservation-topic", groupId = "my-consumer-group")
    public void receiveReservationData(String message) throws IOException {
    	log.debug("message --> " + message);
    	// Kafka 메시지 수신 시 WebSocket을 통해 클라이언트로 데이터 전송
    	socketHandler.broadcastMessage(message);
    }
}
