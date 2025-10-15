package com.java.jwt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.java.jwt.dto.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PubSubService {

    private final String TOPIC = "transaction";

    @Autowired
    PubSubTemplate pubSubTemplate;

    public void publishMessage(TransactionEvent event)throws Exception{

        try {
            String msg = new ObjectMapper().writeValueAsString(event);

            pubSubTemplate.publish(TOPIC, msg);
            log.info("message published: {}", msg);
        } catch (Exception e) {
            log.info("Something went wrong while publishing message due to: {}",e.getMessage());
        }
    }

}
