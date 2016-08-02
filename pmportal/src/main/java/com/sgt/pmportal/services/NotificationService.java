package com.sgt.pmportal.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.test.email.EmailTest;
import com.test.email.EmailTest.SMTPAuthenticator;

public class NotificationService {
	 String  d_email = "ajmital@gmail.com",
	            d_password = "ComPuteR90",
	            d_host = "smtp.gmail.com",
	            d_port  = "465",
	            m_to = "ajmital@gmail.com",
	            m_subject = "Testing",
	            m_text = "Hey, this is the testing email.";
	    
	    public EmailTest()
	    {
	        Properties props = new Properties();
	        props.put("mail.smtp.user", d_email);
	        props.put("mail.smtp.host", d_host);
	        props.put("mail.smtp.port", d_port);
	        props.put("mail.smtp.starttls.enable","true");
	        props.put("mail.smtp.auth", "true");
	        //props.put("mail.smtp.debug", "true");
	        props.put("mail.smtp.socketFactory.port", d_port);
	        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	        props.put("mail.smtp.socketFactory.fallback", "false");

	        SecurityManager security = System.getSecurityManager();

	        try
	        {
	            Authenticator auth = new SMTPAuthenticator();
	            Session session = Session.getInstance(props, auth);
	            //session.setDebug(true);

	            MimeMessage msg = new MimeMessage(session);
	            msg.setText(m_text);
	            msg.setSubject(m_subject);
	            msg.setFrom(new InternetAddress(d_email));
	            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(m_to));
	            Transport.send(msg);
	        }
	        catch (Exception mex)
	        {
	            mex.printStackTrace();
	        } 
	    }
	    
	    public static void main(String[] args)
	    {
	        EmailTest blah = new EmailTest();
	    }

	    private class SMTPAuthenticator extends javax.mail.Authenticator{
	        public PasswordAuthentication getPasswordAuthentication()
	        {
	            return new PasswordAuthentication(d_email, d_password);
	        }
	    }
}
