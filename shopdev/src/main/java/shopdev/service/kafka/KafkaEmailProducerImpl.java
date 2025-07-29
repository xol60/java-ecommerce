package shopdev.service.kafka;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import shopdev.entity.EmailEntity;
import shopdev.service.KafkaEmailProducer;

public class KafkaEmailProducerImpl implements KafkaEmailProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendEmail(String userId, EmailEntity emailEntity) {
        String emailJson = "{\"to\":\"" + emailEntity.getTo() + "\",\"subject\":\"" + emailEntity.getSubject() + "\",\"content\":\"" + emailEntity.getContent() + "\"}";
        kafkaTemplate.send("email-topic", userId, emailJson);
    }


}
