package com.mycompany.todolistgui;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class EmailService {

    private final String username;
    private final String password;
    private final String host;
    private final String port;
    private final String from;
    private final boolean useSsl;

    public EmailService() {
        this.username = Env.require("SMTP_USERNAME");
        this.password = Env.require("SMTP_PASSWORD");
        this.host     = Env.get("SMTP_HOST", "smtp.gmail.com");
        this.port     = Env.get("SMTP_PORT", "465"); // 465=SSL, 587=STARTTLS
        this.from     = Env.get("SMTP_FROM", this.username); // optional override
        this.useSsl   = "465".equals(this.port);
    }

    public void sendTextEmail(String to, String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        if (useSsl) {
            props.put("mail.smtp.ssl.enable", "true");
        } else {
            props.put("mail.smtp.starttls.enable", "true");
        }

        Session session = Session.getInstance(props, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        session.setDebug(false);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject, StandardCharsets.UTF_8.name());
        message.setText(body, StandardCharsets.UTF_8.name());

        Transport.send(message);
    }
}
