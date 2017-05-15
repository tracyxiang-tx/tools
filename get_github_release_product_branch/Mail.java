package com.github;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;  
import java.util.Scanner;

import javax.activation.DataHandler;  
import javax.activation.FileDataSource;  
import javax.mail.Address;  
import javax.mail.BodyPart;  
import javax.mail.Message;  
import javax.mail.Multipart;  
import javax.mail.Session;  
import javax.mail.Transport;  
import javax.mail.internet.InternetAddress;  
import javax.mail.internet.MimeBodyPart;  
import javax.mail.internet.MimeMessage;  
import javax.mail.internet.MimeMultipart;  
  
  
public class Mail {   
  
    private MimeMessage mimeMsg; //MIME邮件对象   
    private Session session; //邮件会话对象   
    private Properties props; //系统属性   
    private boolean needAuth = false; //smtp是否需要认证   
    //smtp认证用户名和密码   
    private String username;   
    private String password;   
    private Multipart mp; //Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象   
       
    /** 
     * Constructor 
     * @param smtp 邮件发送服务器 
     */  
    public Mail(String smtp){   
        setSmtpHost(smtp);   
        createMimeMessage();   
    }   
  
    /** 
     * 设置邮件发送服务器 
     * @param hostName String  
     */  
    public void setSmtpHost(String hostName) {   
        //System.out.println("set system property:mail.smtp.host = "+hostName);   
        if(props == null)  
            props = System.getProperties(); //获得系统属性对象    
        props.put("mail.smtp.host",hostName); //设置SMTP主机   
    }   
  
  
    /** 
     * 创建MIME邮件对象   
     * @return 
     */  
    public boolean createMimeMessage()   
    {   
        try {   
            //System.out.println("Get Email Session Object!");   
            session = Session.getDefaultInstance(props,null); //获得邮件会话对象   
        }   
        catch(Exception e){   
            System.err.println("Get Email Session Object Failed！"+e);   
            return false;   
        }   
      
        //System.out.println("Create MIME Email Object！");   
        try {   
            mimeMsg = new MimeMessage(session); //创建MIME邮件对象   
            mp = new MimeMultipart();   
          
            return true;   
        } catch(Exception e){   
            System.err.println("Create MIME Email Object Failed！"+e);   
            return false;   
        }   
    }     
      
    /** 
     * 设置SMTP是否需要验证 
     * @param need 
     */  
    public void setNeedAuth(boolean need) {   
        //System.out.println("Set SMTP Auth：mail.smtp.auth = "+need);   
        if(props == null) props = System.getProperties();   
        if(need){   
            props.put("mail.smtp.auth","true");   
        }else{   
            props.put("mail.smtp.auth","false");   
        }   
    }   
  
    /** 
     * 设置用户名和密码 
     * @param name 
     * @param pass 
     */  
    public void setNamePass(String name,String pass) {   
        username = name;   
        password = pass;   
    }   
  
    /** 
     * 设置邮件主题 
     * @param mailSubject 
     * @return 
     */  
    public boolean setSubject(String mailSubject) {   
        //System.out.println("set subject");   
        try{   
            mimeMsg.setSubject(mailSubject);   
            return true;   
        }   
        catch(Exception e) {   
            System.err.println("set subject error!");   
            return false;   
        }   
    }  
      
    /**  
     * 设置邮件正文 
     * @param mailBody String  
     */   
    public boolean setBody(String mailBody) {   
        try{   
            BodyPart bp = new MimeBodyPart();   
            bp.setContent(""+mailBody,"text/html;charset=GBK");   
            mp.addBodyPart(bp);   
          
            return true;   
        } catch(Exception e){   
        System.err.println("Set Email Body Failed！"+e);   
        return false;   
        }   
    }   
    /**  
     * 添加附件 
     * @param filename String  
     */   
    public boolean addFileAffix(String filename) {   
      
        //System.out.println("Add Email Attachment："+filename);   
        try{   
            BodyPart bp = new MimeBodyPart();   
            FileDataSource fileds = new FileDataSource(filename);   
            bp.setDataHandler(new DataHandler(fileds));   
            bp.setFileName(fileds.getName());   
              
            mp.addBodyPart(bp);   
              
            return true;   
        } catch(Exception e){   
            System.err.println("Add Email Attachment ："+filename+" failed！"+e);   
            return false;   
        }   
    }   
      
    /**  
     * 设置发信人 
     * @param from String  
     */   
    public boolean setFrom(String from) {   
       // System.out.println("set sender");   
        try{   
            mimeMsg.setFrom(new InternetAddress(from)); //设置发信人   
            return true;   
        } catch(Exception e) {   
            return false;   
        }   
    }   
    /**  
     * 设置收信人 
     * @param to String  
     */   
    public boolean setTo(String to){   
        if(to == null)return false;   
        try{   
            mimeMsg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));   
            return true;   
        } catch(Exception e) {   
            return false;   
        }     
    }   
    
    public boolean setToList(String[] toList){   
        if(toList == null)return false; 
               
      //  Address[] tos = null; 
                
        try{  
        	
             
        	List list = new ArrayList();//不能使用string类型的类型，这样只能发送一个收件人
        	// String []median=tto.split(",");//对输入的多个邮件进行逗号分割
        	 for(int i=0;i<toList.length;i++){
        	     list.add(new InternetAddress(toList[i]));
        	 }
        	 InternetAddress[] address =(InternetAddress[])list.toArray(new InternetAddress[list.size()]);
        	 
        	 mimeMsg.setRecipients(Message.RecipientType.TO,address);
        	 
        	//message.setRecipients(Message.RecipientType.TO,address);
            // 将所有接收者地址都添加到邮件接收者属性中
           // mimeMsg.setRecipients(Message.RecipientType.TO, address);
                                    
        	return true;
        } catch(Exception e) {   
            return false;   
        }     
    }   
      
    /**  
     * 设置抄送人 
     * @param copyto String   
     */   
    public boolean setCopyToList(String[] copytoList)   
    {   
        if(copytoList == null)return false;   
        try{  
        	List list = new ArrayList();//不能使用string类型的类型，这样只能发送一个收件人
        	 for(int i=0;i<copytoList.length;i++){
        	     list.add(new InternetAddress(copytoList[i]));
        	 }
        	 InternetAddress[] address =(InternetAddress[])list.toArray(new InternetAddress[list.size()]);
        	 
        	 mimeMsg.setRecipients(Message.RecipientType.CC,address);
        	
      //  mimeMsg.setRecipients(Message.RecipientType.CC,(Address[])InternetAddress.parse(copyto));   
        return true;   
        }   
        catch(Exception e)   
        { return false; }   
    }   
      
    /**  
     * 发送邮件 
     */   
    public boolean sendOut()   
    {   
        try{   
            mimeMsg.setContent(mp);   
            mimeMsg.saveChanges();   
            System.out.println("sending email......");   
              
            Session mailSession = Session.getInstance(props,null);   
            Transport transport = mailSession.getTransport("smtp");   
            transport.connect((String)props.get("mail.smtp.host"),username,password);   
            transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.TO));   
            transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.CC));   
            //transport.send(mimeMsg);   
              
            System.out.println("Sent successful!");   
            transport.close();   
              
            return true;   
        } catch(Exception e) {   
            System.err.println("Sent failed!"+e);   
            return false;   
        }   
    }   
  
    /** 
     * 调用sendOut方法完成邮件发送 
     * @param smtp 
     * @param from 
     * @param to 
     * @param subject 
     * @param content 
     * @param username 
     * @param password 
     * @return boolean 
     */  
    public static boolean send(String smtp,String from,String to,String subject,String content,String username,String password) {  
        Mail theMail = new Mail(smtp);  
        theMail.setNeedAuth(true); //需要验证  
          
        if(!theMail.setSubject(subject)) return false;  
        if(!theMail.setBody(content)) return false;  
        if(!theMail.setTo(to)) return false;  
        if(!theMail.setFrom(from)) return false;  
        theMail.setNamePass(username,password);  
          
        if(!theMail.sendOut()) return false;  
        return true;  
    }  
      
    /** 
     * 调用sendOut方法完成邮件发送,带抄送 
     * @param smtp 
     * @param from 
     * @param to 
     * @param copyto 
     * @param subject 
     * @param content 
     * @param username 
     * @param password 
     * @return boolean 
     */  
    public static boolean sendAndCc(String smtp,String from,String[] toList,String[] copytoList,String subject,String content,String username,String password) {  
        Mail theMail = new Mail(smtp);  
        theMail.setNeedAuth(true); //需要验证  
          
        if(!theMail.setSubject(subject)) return false;  
        if(!theMail.setBody(content)) return false;  
        if(!theMail.setToList(toList)) return false;  
        if(!theMail.setCopyToList(copytoList)) return false;  
        if(!theMail.setFrom(from)) return false;  
        theMail.setNamePass(username,password);  
          
        if(!theMail.sendOut()) return false;  
        return true;  
    }  
      
    /** 
     * 调用sendOut方法完成邮件发送,带附件 
     * @param smtp 
     * @param from 
     * @param to 
     * @param subject 
     * @param content 
     * @param username 
     * @param password 
     * @param filename 附件路径 
     * @return 
     */  
    public static boolean send(String smtp,String from,String to,String subject,String content,String username,String password,String filename) {  
        Mail theMail = new Mail(smtp);  
        theMail.setNeedAuth(true); //需要验证  
          
        if(!theMail.setSubject(subject)) return false;  
        if(!theMail.setBody(content)) return false;  
        if(!theMail.addFileAffix(filename)) return false;   
        if(!theMail.setTo(to)) return false;  
        if(!theMail.setFrom(from)) return false;  
        theMail.setNamePass(username,password);  
          
        if(!theMail.sendOut()) return false;  
        return true;  
    }  
      
    /** 
     * 调用sendOut方法完成邮件发送,带附件和抄送 
     * @param smtp 
     * @param from 
     * @param to 
     * @param copyto 
     * @param subject 
     * @param content 
     * @param username 
     * @param password 
     * @param filename 
     * @return 
     */  
    public static boolean sendAndCc(String smtp,String from,String[] toList,String[] copytoList,String subject,String content,String username,String password,String filename) {  
        Mail theMail = new Mail(smtp);  
        theMail.setNeedAuth(true); //需要验证  
          
        if(!theMail.setSubject(subject)) return false;  
        if(!theMail.setBody(content)) return false;  
        if(!theMail.addFileAffix(filename)) return false;   
        if(!theMail.setToList(toList)) return false;  
        if(!theMail.setCopyToList(copytoList)) return false;  
        if(!theMail.setFrom(from)) return false;  
        theMail.setNamePass(username,password);  
          
        if(!theMail.sendOut()) return false;  
        return true;  
    }  
      
    
} 


