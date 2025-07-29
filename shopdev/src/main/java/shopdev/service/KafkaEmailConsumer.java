package shopdev.service;

import shopdev.entity.EmailEntity;

public interface KafkaEmailConsumer {
    void consumeEmail(String message);
}