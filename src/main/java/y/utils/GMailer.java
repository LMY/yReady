package y.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMailer {
	
	// this works iif account is set to "allow less secure apps" on:
	// https://www.google.com/settings/security/lesssecureapps
	// as stated in http://stackoverflow.com/questions/34729675/cant-send-mail-using-javamail-due-to-javax-mail-authenticationfailedexception
	
	public GMailer(String username, String password) {
		this.username = username;
		this.password = password;
		
		mailServerProperties = new Properties();
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		mailServerProperties.put("mail.smtp.host", "smtp.gmail.com");
		mailServerProperties.put("mail.smtp.port", "587");
	}
	
	private Properties mailServerProperties;
	private String username;
	private String password;
	

	// various methods to sent do 1|many TO, 0|1|many ccs 
	
	public void sendMail(String dest, String subject, String body) throws Exception {
		sendMail(new String[]{dest}, new String[]{}, subject, body);
	}

	public void sendMail(String dest, String cc, String subject, String body) throws Exception {
		sendMail(new String[]{dest}, new String[]{cc}, subject, body);
	}
	
	public void sendMail(String dest, String[] ccs, String subject, String body) throws Exception {
		sendMail(new String[]{dest}, ccs, subject, body);
	}
	
	public void sendMail(String[] dests, String subject, String body) throws Exception {
		sendMail(dests, new String[]{}, subject, body);
	}

	public void sendMail(String[] dests, String cc, String subject, String body) throws Exception {
		sendMail(dests, new String[]{cc}, subject, body);
	}
	
	public void sendMail(String[] dest, String[] ccs, String subject, String body) throws Exception {
		
		final Session session = Session.getDefaultInstance(mailServerProperties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	
		final Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(username));
		
		for (String d : dest)
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(d));
		
		for (String cc : ccs)
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(cc));
		
		message.setSubject(subject);
		message.setText(body);

		// Transport.send(message"); does not work. these lines are needed
	    final Transport transport = session.getTransport("smtp");
	    transport.connect("smtp.gmail.com", username, password);
	    transport.sendMessage(message, message.getAllRecipients());
	    transport.close();
	}
	
}
