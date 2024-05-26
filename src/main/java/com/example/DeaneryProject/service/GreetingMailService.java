package com.example.DeaneryProject.service;

import com.example.DeaneryProject.response.EmailVerificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.net.ssl.SSLException;
import java.util.Hashtable;
import java.net.InetAddress;
import java.util.Hashtable;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GreetingMailService {
    private final JavaMailSender javaMailSender;

    public GreetingMailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public boolean sendGreetingMailToStudent(String to, String password, String creatorEmail) {
        try {
            if (!hasMXRecord(to)) {
                return false;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Welcome to DeaneryDesk service!");

            message.setText("Hello! "
                    + "\n\nYour student account has been successfully created. "
                    + "\nYour password is: " + password
                    + ".\nPlease, change it as soon as possible, "
                    + "if this email was sent to you by mistake, please contact us."
                    + "\nVisit our website: http://localhost:8080/login"
                    + "\nContact: " + creatorEmail
                    + "\n\nBest regards, DeaneryDesk team.");

            javaMailSender.send(message);
            log.info("Greeting email sent to new student, email: " + to);
            return true;
        } catch (MailException e) {
            return false;
        }
    }

    public boolean sendGreetingMailToWorker(String to, String password, String creatorEmail) {
        try {
            if (!hasMXRecord(to)) {
                return false;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Welcome to DeaneryDesk service!");

            message.setText("Hello! "
                    + "\n\nYour worker account has been successfully created. "
                    + "\nYour password is: " + password
                    + ".\nPlease, change it as soon as possible, "
                    + "if this email was sent to you by mistake, please contact us."
                    + "\nVisit our website: http://localhost:8080/login"
                    + "\nContact: " + creatorEmail
                    + "\n\nBest regards, DeaneryDesk team.");

            javaMailSender.send(message);
            log.info("Greeting email sent to new worker, email: " + to);
            return true;
        } catch (MailException e) {
            return false;
        }
    }

    public boolean hasMXRecord(String email) {
        String domain = email.substring(email.indexOf('@') + 1);
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            DirContext ictx = new InitialDirContext(env);
            Attributes attrs = ictx.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");
            return (attr != null && attr.size() > 0);
        } catch (Exception e) {
            log.error("Failed to check MX records for domain: " + domain, e);
            return false;
        }
    }
}