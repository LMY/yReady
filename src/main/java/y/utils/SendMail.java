package y.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	public static boolean mail(String hostname, String srcAddress, String destAddress, String subject, String content) {

		final Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", hostname);

		final Session session = Session.getDefaultInstance(properties);

		try {
			final Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(srcAddress));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(destAddress));
			message.setSubject(subject);
			message.setText(content);
			Transport.send(message);
			
			return true;
		}
		catch (MessagingException mex) {
			Utils.MessageBox(mex.toString(), "MAIL ERROR");
			mex.printStackTrace();
			return false;
		}
	}
}
