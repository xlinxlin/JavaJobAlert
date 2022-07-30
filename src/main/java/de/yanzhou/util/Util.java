package de.yanzhou.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
public class Util {
  //private static final String JOB_URL = "https://www.cegat.de/ueber-cegat/stellenangebote/";
  private static final Logger logger = LogManager.getLogger(Util.class);
  public String readMd5File(String pathName) {
    String md5 = null;
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(pathName));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    try {
      md5 = reader.readLine();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    try {
      reader.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return md5;
  }
  public ArrayList<String> readJobFile(String pathName){
    ArrayList<String> listOfJobs = new ArrayList<String>();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(pathName));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    try {
      String job;
      while ((job = reader.readLine()) != null) {
        listOfJobs.add(job);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    try {
      reader.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return listOfJobs;
  }
  public ArrayList<String> readJobsOnWeb(){
    Document doc = null;
    try {
      doc = Jsoup.connect("https://www.cegat.de/ueber-cegat/stellenangebote/").get();
    } catch (IOException e) {
      logger.error("Can not open the webpage.");
    }
    Elements elements = doc.getElementsByClass("iconlist_content_wrap");
    ArrayList<String> listOfJobs = new ArrayList<String>();
    for (Element element : elements){
      String job = element.text();
      listOfJobs.add(job);
    }
    return listOfJobs;
  }

  /*
  public void modifyMd5File(String pathName, String oldMD5, String newMD5) {
    Path path = Paths.get(pathName);
    Charset charset = StandardCharsets.UTF_8;
    String content = null;
    try {
      content = Files.readString(path, charset);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    content = content.replaceAll(oldMD5, newMD5);
    try {
      Files.writeString(path, content, charset);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  */

  public void createFile(String pathName, ArrayList<String> arrayList){
    File file = new File(pathName);
    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      logger.error("Can not create job.txt." + e.getMessage());
    }
    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
    for (String element : arrayList) {
      try {
        bufferedWriter.write(element);
        bufferedWriter.newLine();
      } catch (IOException e) {
        logger.error("Can not write job.txt." +  e.getMessage());
      }
    }
    try {
      bufferedWriter.close();
    } catch (IOException e) {
      logger.error("Can not write job.txt." + e.getMessage());
    }
  }
  public String calculateMD5(String str){
    return DigestUtils.md5Hex(str);
  }

  public ArrayList<String> checkNewElements(ArrayList<String> oldList, ArrayList<String> newList){
    return newList.stream().filter(i -> !oldList.contains(i))
            .collect(Collectors.toCollection(ArrayList::new));
  }
  public ArrayList<String> checkRemovedElements(ArrayList<String> oldList, ArrayList<String> newList){
    return oldList.stream().filter(i -> !newList.contains(i))
            .collect(Collectors.toCollection(ArrayList::new));
  }

  public void createUpdateFile(String pathName, ArrayList<String> newElementsList, ArrayList<String> removedElementsList){
    File file = new File(pathName);
    try {
      file.createNewFile();
      //new File(pathName).createNewFile();
    } catch (IOException e) {
      logger.error("Can not create update.txt. " +  e.getMessage());
    }
    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      logger.error("Can not write update.txt."  + e.getMessage());
    }
    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    Date date = new Date(System.currentTimeMillis());
    try {
      bufferedWriter.write(formatter.format(date));
      bufferedWriter.newLine();
      bufferedWriter.write("New jobs:");
      bufferedWriter.newLine();
    } catch (IOException e) {
      logger.error("Can not write update.txt. " + e.getMessage());
    }
    for (String element : newElementsList) {
      try {
        bufferedWriter.write(element);
        bufferedWriter.newLine();
      } catch (IOException e) {
        logger.error("Can not write job.txt. " +  e.getMessage());
      }
    }
    try {
      bufferedWriter.write("Removed jobs:");
      bufferedWriter.newLine();
    } catch (IOException e) {
      logger.error("Can not write job.txt." +  e.getMessage());
    }
    for (String element : removedElementsList) {
      try {
        bufferedWriter.write(element);
        bufferedWriter.newLine();
      } catch (IOException e) {
        logger.error("Can not write job.txt." +  e.getMessage());
      }
    }
    try {
      bufferedWriter.close();
    } catch (IOException e) {
      logger.error("Can not write job.txt." + e.getMessage());
    }
  }
  public void init(String pathJobFile, String pathMD5File){
    //new File(pathJobFile).delete();
    ArrayList<String> listOfJobs = readJobsOnWeb();
    createFile(pathJobFile, listOfJobs);
    String strJobs = String.join("", listOfJobs);
    String md5 = calculateMD5(strJobs);
    //new File(pathMD5File).delete();
    createFile(pathMD5File, new ArrayList<>(Collections.singletonList(md5)));
  }

}
