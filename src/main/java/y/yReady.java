package y;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.UIManager;

import y.ui.MainWindow;
import y.utils.GeneralProperties;
import y.utils.GeneralPropertiesExporter;
import y.utils.Utils;


public class yReady {
	public static final String ReleaseDate = "2016-02-07";
	public static final String VersionString = "1.02" + " (" + ReleaseDate + ")"; 
	
	public static final String CONFIG_FILENAME = "yReady.conf";
	
	
	public static void main(String args[])
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception exception) {
			Utils.MessageBox(null, "Invalid look and feel.", "ERROR");
		}
		
		Locale.setDefault(Locale.US);
		NumberFormat.getInstance(Locale.US).setGroupingUsed(false);
		
		GeneralProperties<String> config = null;
		try {
			config = GeneralPropertiesExporter.read(CONFIG_FILENAME, p -> { return p; });
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		setGlobalProxy(config);
		final MainWindow main = new MainWindow(config);
		main.startAllTasks();
	}
	
	private static void setGlobalProxy(GeneralProperties<String> config) {
		final String proxyHost = config.get(String.class, "proxy_host");
		final Integer proxyPort = config.get(Integer.class, "proxy_port");
		
		final String authUser = config.get(String.class, "proxy_username");
		final String authPassword = config.get(String.class, "proxy_password");

		if (proxyHost != null && !proxyHost.isEmpty())
			System.setProperty("http.proxyHost", proxyHost);
		if (proxyPort != null && proxyPort != 0)
			System.setProperty("http.proxyPort", ""+proxyPort);
		
		
		if (authUser != null && authPassword != null) {
			Authenticator.setDefault(new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(authUser, authPassword.toCharArray());
				}
			});
	
			System.setProperty("http.proxyUser", authUser);
			System.setProperty("http.proxyPassword", authPassword);
		}
	}
}
