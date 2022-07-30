package de.yanzhou.util;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.net.URI;
import java.util.Properties;

public class EmailService {
  private final String username;
  private final String password;
  private final Properties prop;
  private final String pathAttachedFile;
  private static final String FROM_EMAIL = "xlin@xlin.me";
  private static final String TO_EMAIL = "ifreicn@gmail.com";
  public EmailService(String host, int port, String username, String password, String pathAttachedFile) {
    prop = new Properties();
    prop.put("mail.smtp.auth", true);
    prop.put("mail.smtp.starttls.enable", "true");
    prop.put("mail.smtp.host", host);
    prop.put("mail.smtp.port", port);
    prop.put("mail.smtp.ssl.trust", host);
    prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
    this.username = username;
    this.password = password;
    this.pathAttachedFile = pathAttachedFile;
  }
  public void sendMail() throws Exception {

    Session session = Session.getInstance(prop, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(FROM_EMAIL));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_EMAIL));
    message.setSubject("Job Alert");

    String msg = "See the attached file.";

    MimeBodyPart mimeBodyPart = new MimeBodyPart();
    mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

    //String msgStyled = "This is my <b style='color:red;'>bold-red email</b> using JavaMailer";
    //MimeBodyPart mimeBodyPartWithStyledText = new MimeBodyPart();
    //mimeBodyPartWithStyledText.setContent(msgStyled, "text/html; charset=utf-8");

    MimeBodyPart attachmentBodyPart = new MimeBodyPart();

    //attachmentBodyPart.attachFile(getFile());
    attachmentBodyPart.attachFile(pathAttachedFile);

    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(mimeBodyPart);
    //multipart.addBodyPart(mimeBodyPartWithStyledText);
    multipart.addBodyPart(attachmentBodyPart);

    message.setContent(multipart);

    Transport.send(message);
  }
  private File getFile() throws Exception {
    URI uri = this.getClass()
            .getClassLoader()
            .getResource("job.txt")
            .toURI();
    return new File(uri);
  }

}
