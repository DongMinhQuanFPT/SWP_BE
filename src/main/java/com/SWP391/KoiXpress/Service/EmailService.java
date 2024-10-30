package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.EmailDetail;
import com.SWP391.KoiXpress.Exception.EmailNotVerifiedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void sendEmailThankYou(EmailDetail emailDetail)  {
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
        }catch(MessagingException e){
            throw new EmailNotVerifiedException("Error sending  email, please check your mail again");
        }
    }

}
