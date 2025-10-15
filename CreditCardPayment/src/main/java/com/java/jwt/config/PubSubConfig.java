package com.java.jwt.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
    public class PubSubConfig {

//        @Value("${pubsub.subscription-name}")
//        private String subscriptionName;
//
//        @Bean
//        public MessageChannel pubsubInputChannel() {
//            return new PublishSubscribeChannel();
//        }
//
//        @Bean
//        public PubSubInboundChannelAdapter messageChannelAdapter(
//                @Qualifier("pubsubInputChannel") MessageChannel inputChannel,
//                PubSubTemplate pubSubTemplate) {
//
//            PubSubInboundChannelAdapter adapter =
//                    new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionName);
//            adapter.setOutputChannel(inputChannel);
//            adapter.setAckMode(AckMode.MANUAL);
//            adapter.setPayloadType(String.class);
//
//            return adapter;
//        }

    @Value("${pubsub.subscription1-name}")
    private String subscriptionOne;

    @Value("${pubsub.subscription2-name}")
    private String subscriptionTwo;

    @Bean
    public MessageChannel inputChannelOne(){
        return new PublishSubscribeChannel();
    }

    @Bean
    public MessageChannel inputChannelTwo(){
        return new PublishSubscribeChannel();
    }

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapterOne(PubSubTemplate pubSubTemplate){
        PubSubInboundChannelAdapter adapter = createAdapter(pubSubTemplate,subscriptionOne);
        adapter.setOutputChannel(inputChannelOne());
        return adapter;
    }

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapterTwo(PubSubTemplate pubSubTemplate){
        PubSubInboundChannelAdapter adapter = createAdapter(pubSubTemplate,subscriptionTwo);
        adapter.setOutputChannel(inputChannelTwo());
        return adapter;
    }

    private PubSubInboundChannelAdapter createAdapter(PubSubTemplate pubSubTemplate, String subscription) {
        PubSubInboundChannelAdapter adapter =  new PubSubInboundChannelAdapter(pubSubTemplate, subscription);
        adapter.setAckMode(AckMode.AUTO);
        adapter.setPayloadType(String.class);
        return adapter;
    }

    }


