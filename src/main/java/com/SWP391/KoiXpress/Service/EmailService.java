package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.EmailDetail;
import com.SWP391.KoiXpress.Exception.EmailNotVerifiedException;
import com.SWP391.KoiXpress.Model.response.Email.EmailResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TemplateEngine templateEngine;

    public boolean sendEmailVerify(EmailDetail emailDetail)  {
        try{
            Context context = new Context();
            context.setVariable("name",emailDetail.getUsers().getEmail());
            context.setVariable("button","Click Here to verify");
            context.setVariable("link",emailDetail.getLink());
            context.setVariable("email",emailDetail.getUsers().getEmail());
            String template = templateEngine.process("EmailVerify",context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);

            messageHelper.setFrom("admin@gmail.com");
            messageHelper.setTo(emailDetail.getUsers().getEmail());
            messageHelper.setText(template,true);
            messageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(message);
            return true;
        }catch(MessagingException e){
            throw new EmailNotVerifiedException("Error sending  email, please check your mail again");
        }
    }

    public void sendEmailResetPassword(EmailDetail emailDetail)  {
        try{
            Context context = new Context();
            context.setVariable("name",emailDetail.getUsers().getEmail());
            context.setVariable("button","Reset Password");
            context.setVariable("link",emailDetail.getLink());
            context.setVariable("email",emailDetail.getUsers().getEmail());
            String template = templateEngine.process("EmailResetPassword",context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);

            messageHelper.setFrom("admin@gmail.com");
            messageHelper.setTo(emailDetail.getUsers().getEmail());
            messageHelper.setText(template,true);
            messageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(message);
        }catch(MessagingException e){
            throw new EmailNotVerifiedException("Error sending  email, please check your mail again");
        }
    }

    public void sendEmailAccount(EmailDetail emailDetail)  {
        try{
            Context context = new Context();
            context.setVariable("name",emailDetail.getUsers().getEmail());
            context.setVariable("link",emailDetail.getLink());
            context.setVariable("email",emailDetail.getUsers().getEmail());
            context.setVariable("username", emailDetail.getUsers().getFullname());
            context.setVariable("password", emailDetail.getUsers().getFullname());
            String template = templateEngine.process("EmailAccount",context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);

            messageHelper.setFrom("admin@gmail.com");
            messageHelper.setTo(emailDetail.getUsers().getEmail());
            messageHelper.setText(template,true);
            messageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(message);
        }catch(MessagingException e){
            throw new EmailNotVerifiedException("Error sending  email, please check your mail again");
        }
    }

    public boolean sendEmailThankYou(EmailDetail emailDetail)  {
        try{
            Context context = new Context();
            context.setVariable("name",emailDetail.getUsers().getEmail());
            context.setVariable("button","Our Website");
            context.setVariable("link",emailDetail.getLink());
            context.setVariable("email",emailDetail.getUsers().getEmail());
            String template = templateEngine.process("EmailThankYou",context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);

            messageHelper.setFrom("admin@gmail.com");
            messageHelper.setTo(emailDetail.getUsers().getEmail());
            messageHelper.setText(template,true);
            messageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(message);
            return true;
        }catch(MessagingException e){
            throw new EmailNotVerifiedException("Error sending  email, please check your mail again");
        }
    }

    public void sendEmailOrderStatusUpdate(String toEmail, Long orderId, String newStatus) {
        String subject = "Order Status Update for Order #" + orderId;
        String message = "Dear Customer,\n\nYour order #" + orderId + " status has been updated to: " + newStatus + ".\n\nThank you for using our service!";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }


    private final String MailBox_API_KEY_URL = "http://apilayer.net/api/check?access_key=18c105525047d2c536134cc9638de4f9&smtp=1&format=1";

    public Boolean verifyEmail(String email){
        String url = MailBox_API_KEY_URL + "&email=" + email;
        EmailResponse emailResponse = restTemplate.getForObject(url, EmailResponse.class);
        if(emailResponse!=null){
            return emailResponse.isFormat_valid() && emailResponse.isMx_found();
        }
        return false;
    }
}
