package com.example.pubsub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pubsub.modals.Order;

@Service
public class IndexService {

	private static Logger log = LoggerFactory.getLogger(IndexService.class);
	
	@Autowired
	private OrderProducer orderProducer;
	
	public void publishMsg(Order order) {
		
		orderProducer.sendMessage(order);
	}

}
