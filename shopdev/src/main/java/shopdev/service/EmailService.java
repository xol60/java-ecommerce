package shopdev.service;



import shopdev.entity.EmailEntity;

public interface EmailService {
    void sendEmail(EmailEntity emailEntity);
}