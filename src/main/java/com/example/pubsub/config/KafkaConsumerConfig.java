package com.example.pubsub.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
  
   @Bean
   public ConsumerFactory<String, Object> consumerFactory() {
      Map<String, Object> props = new HashMap<>();
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-id");
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
      props.put(ConsumerConfig.GROUP_ID_CONFIG, "json");
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
      //return new DefaultKafkaConsumerFactory<>(props);
      
      JsonDeserializer<Object> deserializer = new JsonDeserializer<Object>();
    		  
      deserializer.addTrustedPackages("*");
    		  
      return new DefaultKafkaConsumerFactory<>(
    		  props,
              new StringDeserializer(),
              deserializer);
   }
  
   @Bean
   public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
      
	   ConcurrentKafkaListenerContainerFactory<String, Object> 
	   factory = new ConcurrentKafkaListenerContainerFactory<>();
	   factory.setConsumerFactory(consumerFactory());
	   return factory;
   }
}