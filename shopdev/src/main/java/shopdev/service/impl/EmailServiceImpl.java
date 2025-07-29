package shopdev.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import shopdev.entity.EmailEntity;
import shopdev.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailEntity emailEntity) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailEntity.getTo());
        message.setSubject(emailEntity.getSubject());
        message.setText(emailEntity.getContent());
        javaMailSender.send(message);
    }   
}
