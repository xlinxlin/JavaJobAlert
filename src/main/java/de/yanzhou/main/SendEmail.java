package de.yanzhou.main;

import de.yanzhou.util.EmailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SendEmail extends Thread{
  private final Object lock;
  private final String pathAttachedFile;
  private static final String HOST = "smtp.YOUR_HOST.com";
  private static final int PORT = 25;
  private static final String USERNAME = "YOUR_EMAIL_ADDRESS";
  private static final String PASSWORD = "YOUR_PASSWORD";

  private static final Logger logger = LogManager.getLogger(SendEmail.class);
  public SendEmail(Object lock, String pathAttachedFile){
    this.lock = lock;
    this.pathAttachedFile = pathAttachedFile;
  }

  @Override
  public void run(){
    synchronized (lock) {
      while (true) {
        EmailService emailService = new EmailService(HOST, PORT, USERNAME, PASSWORD, pathAttachedFile);
        try {
          emailService.sendMail();
        } catch (Exception e) {
          logger.error("Can not send email. " + e.getMessage());
        }
        lock.notify();
        try {
          lock.wait();
        } catch (InterruptedException e) {
          logger.error("Thread interrupted. " + e.getMessage());
        }
      }
    }
  }
}
