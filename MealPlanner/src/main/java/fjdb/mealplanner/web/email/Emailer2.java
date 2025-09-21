package fjdb.mealplanner.web.email;


import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static fjdb.mealplanner.util.CheckedFunction.wrap;

public class Emailer2 {

    private static final String userName = "stormbost@hotmail.com";
    private static final String password = "diQmuj-pojrob-0dordu";

    public static void main(String[] args) throws MessagingException {
//        String userName = "francis.bostock@gmail.com";
//        String password = "b1gglesWickio";

        sendMessage("Testing pdf", List.of("francis.bostock@gmail.com"), "See attached message", new File("/Users/francisbostock/Downloads/LoadDocstore-14.Pdf"));

//        tryHotmail(userName, password);
//        test(userName, password);
        if (true) return;
    }

    public static void sendMessage(String subject, List<String> toAddresses, String message, File attachment) {
        Session session = Session.getDefaultInstance(makeProps(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

        Message emailMessage = null;
        try {
            emailMessage = generateMessage(subject, message, attachment, session);
            emailMessage.setFrom(new InternetAddress(userName));
            List<InternetAddress> collect = toAddresses.stream().map(wrap(InternetAddress::new)).toList();
            emailMessage.setRecipients(Message.RecipientType.TO, collect.toArray(new InternetAddress[0]));
//            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse("francis.bostock@gmail.com,kate.bostock@yahoo.co.uk"));
            Transport.send(emailMessage);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    private static Properties makeProps() {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");
//        props.setProperty("mail.host", "smtp.live.com");
        props.setProperty("mail.host", "smtp.office365.com");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        return props;
    }

    private static Message generateMessage(String subject, String msg, File attachment, Session session) throws MessagingException, IOException {
        Message message = new MimeMessage(session);

        message.setSubject(subject);
        message.setFrom(new InternetAddress("MealPlanner@gmail.com"));

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(attachment);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        multipart.addBodyPart(attachmentPart);
        message.setContent(multipart);
        return message;
    }

    private static Message getMessage(String userName, Session session) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(userName));

        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse("francis.bostock@gmail.com"));
        message.setSubject("Mail Subject");
        message.setFrom(new InternetAddress("MealPlanner@gmail.com"));

        String msg = "This is my first email using JavaMailer";

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);
        return message;
    }


    public static void tryHotmail(String username, String password) throws MessagingException {

        Properties props = makeProps();

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        session.setDebug(true);
        Message message = getMessage(username, session);
        Transport.send(message);
//        Transport trans = session.getTransport("smtp");
//        trans.connect("smtp.office365.com", 587, username, password);
//        trans.sendMessage(message, InternetAddress.parse("francis.bostock@gmail.com"));
//        Transport.send(message, username, password);
    }


}
