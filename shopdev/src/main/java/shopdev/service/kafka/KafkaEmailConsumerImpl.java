package shopdev.service.kafka;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import shopdev.entity.EmailEntity;
import shopdev.service.EmailService;
import shopdev.service.KafkaEmailConsumer;

public class KafkaEmailConsumerImpl implements KafkaEmailConsumer{
    @Autowired
    private EmailService emailService;
    private Logger logger = Logger.getLogger(KafkaEmailConsumerImpl.class.getName());
    ObjectMapper mapper ;

    @KafkaListener(topics = "email", groupId = "email-group")
    @Override
    public void consumeEmail(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            EmailEntity emailEntity = mapper.readValue(message, EmailEntity.class);
            emailService.sendEmail(emailEntity);
        } catch (JsonProcessingException e) {
            // Handle the exception, for example:
            System.out.println("Error parsing JSON: " + e.getMessage());
            logger.info("Error parsing JSON: " + e.getMessage());
        };
    }
}
