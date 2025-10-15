//package com.java.jwt.service;
//
//import com.google.cloud.pubsub.v1.AckReplyConsumer;
//import com.google.cloud.pubsub.v1.Subscriber;
//import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
//import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
//import com.google.pubsub.v1.ProjectSubscriptionName;
//import com.google.pubsub.v1.PubsubMessage;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.stereotype.Service;
//
//
//import org.springframework.stereotype.Component;
//
//
//@Component
//@Slf4j
//public class PubSubSubscriber {
//    @ServiceActivator(inputChannel = "pubSubInputChannel")
//    public void messageReceiver(String payload,
//                                @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message
//    ){
//        try{
//            log.info("Message received: {}",payload);
//        } catch (Exception e) {
//            log.info("Exception while processing message due to : {}",e.getMessage());
//        }
//    }
//
//}
//
//
////private final PubSubSubscriberTemplate subscriberTemplate;
////
////public PubSubSubscriber(PubSubSubscriberTemplate subscriberTemplate) {
////    this.subscriberTemplate = subscriberTemplate;
////}
////
////@PostConstruct
////public void subscribe() {
////    // Pull messages from subscription
////    subscriberTemplate.subscribe("my-subscription", message -> {
////        System.out.println("Received message: " + new String(message.getData()));
////    });
////}