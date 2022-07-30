package de.yanzhou.main;

import de.yanzhou.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class Main {
  private static final String PATH_NAME_JOB = "src/dir/job.txt";
  private static final String PATH_NAME_MD5 = "src/dir/md5.txt";
  private static final String PATH_NAME_UPDATE = "src/dir/update.txt";
  private static final Logger logger = LogManager.getLogger(Main.class);
  private static final Object lock = new Object();
  private static final Util util = new Util();

  public static void main(String[] args){

    util.init(PATH_NAME_JOB, PATH_NAME_MD5);

    CheckWebsiteUpdate checkWebsiteUpdate = new CheckWebsiteUpdate(lock, PATH_NAME_JOB, PATH_NAME_MD5,PATH_NAME_UPDATE);
    checkWebsiteUpdate.start();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      logger.error("Thread is interrupted. " +  e.getMessage());
    }
    SendEmail sendEmail = new SendEmail(lock,PATH_NAME_UPDATE);
    sendEmail.start();
  }
}
