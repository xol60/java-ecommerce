package com.xol.authservice.kafka;

import com.xol.authservice.dto.UserRegisterEvent;
import com.xol.authservice.dto.UserRegisterResponseEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class KafkaUserClient {

    private final ReplyingKafkaTemplate<String, UserRegisterEvent, UserRegisterResponseEvent> kafkaTemplate;

    public boolean sendUserCreateRequest(UserRegisterEvent event) throws Exception {
        ProducerRecord<String, UserRegisterEvent> record = new ProducerRecord<>("user-register", event);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "user-register-reply".getBytes()));

        RequestReplyFuture<String, UserRegisterEvent, UserRegisterResponseEvent> future = kafkaTemplate
                .sendAndReceive(record);

        ConsumerRecord<String, UserRegisterResponseEvent> response = future.get(10, TimeUnit.SECONDS);
        return response.value().isSuccess();
    }
}