package lv.events.communication;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmailSender {

    private final String SMTP_AUTH_USER;

    private final String SMTP_AUTH_PWD;

    public void sendMail(String to, String clientName) throws Exception {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "smtp.sendgrid.net");
        properties.put("mail.smtp.port", 587);
        properties.put("mail.smtp.auth", "true");

        Authenticator auth = new SMTPAuthenticator();
        Session mailSession = Session.getDefaultInstance(properties, auth);

        MimeMessage message = new MimeMessage(mailSession);
        Multipart multipart = new MimeMultipart("alternative");
        BodyPart part1 = new MimeBodyPart();
        part1.setText("Hello, Your reservation applied successfully!");
        BodyPart part2 = new MimeBodyPart();
        part2.setContent("<p>Hello,</p><p>Your reservation <b>applied successfully!</b>.</p><p>Thank you,<br>" + clientName + "</br></p>", "text/html; charset=utf-8");
        multipart.addBodyPart(part1);
        multipart.addBodyPart(part2);
        message.setFrom(new InternetAddress("admin@eventmanager.net"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("Your reservation");
        message.setContent(multipart);

        Transport transport = mailSession.getTransport();
        transport.connect();
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PWD;
            return new PasswordAuthentication(username, password);
        }
    }
}
