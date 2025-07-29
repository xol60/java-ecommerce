package shopdev.service;

import shopdev.entity.EmailEntity;

public interface KafkaEmailProducer {
    public void sendEmail(String userId, EmailEntity emailEntity);
}