package com.example.pubsub.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

	@KafkaListener(topics = "tutorialspoint", groupId = "group-id")
	public void listen(Object message) {
		
	      System.out.println("Received Messasge in group - group-id: " + message.toString());
	}
}
