# kafka-with-springboot
Kafka integration with Springboot

Publish Message in Kafka using Springboot.

Here using this sample code you can publish your Java class object in kafka and you can consume, So let's start.

You must have one springboot project which is capable to facilitate for releasing REST endpoints and I am hoping you guys aware with that.

So what POM reqires for Kafka check out below and put in your POM:

```
<dependency>
	<groupId>org.springframework.kafka</groupId>
	<artifactId>spring-kafka</artifactId>
	<version>2.5.8.RELEASE</version>
</dependency>
```

Greate now our springboot project ready to implement Kafka stuff , let's do that:

### Configure Kafka Producer

For configuring kafka stuff there are two ways either properties files or Java based So here I am only explaining Java based configurations.

```
package com.example.pubsub.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {
   
   @Bean
   public ProducerFactory<String, Object> producerFactory() {
      Map<String, Object> configProps = new HashMap<>();
      configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
      configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
      return new DefaultKafkaProducerFactory<>(configProps);
   }
   @Bean
   public KafkaTemplate<String, Object> kafkaTemplate() {
	   
      return new KafkaTemplate<>(producerFactory());
   }
}
```

Now , the next step to configure Consumer.

### Configure Kafka Consumer

```
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
```

Now, My Producer and Consumer both are configured.

In my case , I want to publish my Order class , please take a look:

```
package com.example.pubsub.modals;
import java.io.Serializable;
import java.util.Date;


public class Order implements Serializable {

    private String content;
    private String orderId;
    private String address;
    private Double amount;
    private Date timestamp;

    public Order() {
    }

    public Order(String content, Date timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Order [content=" + content + ", orderId=" + orderId + ", address=" + address + ", amount=" + amount
				+ ", timestamp=" + timestamp + "]";
	}

}
```

### Create OrderProducer 

```
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
```

### Create OrderConsumer

```
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
```

### Let's execute producer via REST endpoint

In my case , I did not set contextpath for my application So my application running on configured port 9090 with '' contextpath

##### Url: http://localhost:9090/index/publish

##### Method : POST , Body :

```
{
  "orderId" : "YOUR-order-ID",
  "amount"  : 123.21,
  "content" : "",
	"address" : "delhi"
}
```

# Thank You!

