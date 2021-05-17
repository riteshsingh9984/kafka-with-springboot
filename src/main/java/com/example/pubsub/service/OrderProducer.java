package com.example.pubsub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.example.pubsub.modals.Order;

@Service
public class OrderProducer {

	private static Logger logger = LoggerFactory.getLogger(OrderProducer.class);

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	public void sendMessage(Order message) {
		
		logger.info(String.format("$$$$ => Producing message: %s", message));

		ListenableFuture<SendResult<String, Object>> future = this.kafkaTemplate.send("tutorialspoint", message);
		
		future.addCallback(new ListenableFutureCallback<>() {
			@Override
			public void onFailure(Throwable ex) {
				logger.info("Unable to send message=[ {} ] due to : {}", message, ex.getMessage());
			}

			@Override
			public void onSuccess(SendResult<String, Object> result) {
				logger.info("Sent message=[ {} ] with offset=[ {} ]", message, result.getRecordMetadata().offset());
			}
		});
		
	}
}
