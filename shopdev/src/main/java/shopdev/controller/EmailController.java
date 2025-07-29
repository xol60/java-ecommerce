package shopdev.controller;

import java.util.logging.Logger;

import org.hibernate.engine.jdbc.env.internal.LobCreationLogging_.logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shopdev.entity.EmailEntity;
import shopdev.service.EmailService;
import shopdev.service.KafkaEmailProducer;

@Controller
@RequestMapping("/email")
public class EmailController {
    @Autowired
    private EmailService emailService;
    private KafkaEmailProducer kafkaEmailProducer;
    private Logger logger = Logger.getLogger(EmailController.class.getName());
    @PostMapping("/send")
    public void sendEmail(EmailEntity emailEntity) {
        emailService.sendEmail(emailEntity);
    }
    @PostMapping("/sendKafka")
    public void sendEmailKafka(@RequestParam("userId") String userId,@RequestBody EmailEntity emailEntity) {
        logger.info("userId: " + userId);
        logger.info("emailEntity: " + emailEntity);
        kafkaEmailProducer.sendEmail(userId, emailEntity);
    }
}
