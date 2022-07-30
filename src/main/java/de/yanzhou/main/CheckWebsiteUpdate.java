package de.yanzhou.main;

import de.yanzhou.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
public class CheckWebsiteUpdate extends Thread{

  private static final Logger logger = LogManager.getLogger(CheckWebsiteUpdate.class);
  private final Object lock;
  private final String pathNameMD5;
  private final String pathNameJob;
  private final String pathNameUpdateFile;
  public CheckWebsiteUpdate(Object lock, String pathNameJob, String pathNameMD5, String pathNameUpdateFile){
    this.lock = lock;
    this.pathNameJob = pathNameJob;
    this.pathNameMD5 = pathNameMD5;
    this.pathNameUpdateFile = pathNameUpdateFile;
  }
  Util util = new Util();

  @Override
  public void run(){
    synchronized (lock) {
      while (true) {
        ArrayList<String> listOfJobsOnWeb = util.readJobsOnWeb();
        if (!listOfJobsOnWeb.isEmpty()) {
          String strJobs = String.join("", listOfJobsOnWeb);
          String md5 = util.calculateMD5(strJobs);
          String originMD5 = util.readMd5File(pathNameMD5);
          if(!md5.equals(originMD5)){
            ArrayList<String> newElementsList = util.checkNewElements(util.readJobFile(pathNameJob), listOfJobsOnWeb);
            ArrayList<String> removedElementsList = util.checkRemovedElements(util.readJobFile(pathNameJob), listOfJobsOnWeb);
            util.createUpdateFile(pathNameUpdateFile, newElementsList, removedElementsList);
            util.init(pathNameJob, pathNameMD5);
            lock.notify();
            try {
              lock.wait();
            } catch (InterruptedException e) {
              logger.error("Thread interrupted. " +  e.getMessage());
            }
          }
        }
      }
    }
  }

}
