package y.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMailer {
	public GMailer(String username, String password) {
		this.username = username;
		this.password = password;
		
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
	}
	
	private Properties mailServerProperties;
	private String username;
	private String password;
	
	public void sendMail(String dest, String subject, String body) throws Exception {
		sendMail(dest, new String[]{}, subject, body);
	}
	
	public void sendMail(String dest, String[] ccs, String subject, String body) throws Exception {
		final Session getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		final MimeMessage generateMailMessage = new MimeMessage(getMailSession);
//		try {
			generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(dest));
			
			for (String cc : ccs)
				generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
			
			generateMailMessage.setSubject(subject);
			generateMailMessage.setContent(body, "text/html");
			
			final Transport transport = getMailSession.getTransport("smtp");
			transport.connect("smtp.gmail.com", username, password);
			transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
			transport.close();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
}
