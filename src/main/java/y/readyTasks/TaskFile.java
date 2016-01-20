package y.readyTasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.joda.time.DateTime;

import y.utils.GeneralProperties;
import y.utils.Notifier;

public class TaskFile extends Task {

	public TaskFile(Notifier notifier, GeneralProperties<String> config, String url) {
		super(notifier, config, url, -1);
	}

	@Override
	public String getType() {
		return "file";
	}

	@Override
	public void go() {
		notifier.notify_start();
		
		final String url = getUrl();
		final String folder = getFolder();
		
		try {
			last = DateTime.now();
			if (first == null)
				first = last;
			
			saveContent(url, folder+File.separator+getFilename(url));
		}
		catch (Exception e) {
			notifier.notify_message(e.getMessage());
			notifier.notify_abort();
			return; // do not call notify_end()
		}
		
		notifier.notify_end();
	}
	
	private static String regExpInvalidFilenameChars = "[?^#]";
	
	public static String getFilename(String url) {
		try {
			return url.substring(url.lastIndexOf('/')+1).replaceAll(regExpInvalidFilenameChars, "");
		}
		catch (Exception e) {
			return url.replaceAll(regExpInvalidFilenameChars, "");
		}
	}
	
	public String saveContent(String urlToRead, String filename) throws Exception {
		if (!urlToRead.startsWith("http"))
			urlToRead = "http://" + urlToRead;
		
		final URL url = new URL(urlToRead);
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		@SuppressWarnings("unused")
		final int responseCode = conn.getResponseCode();
		
		final InputStream input = conn.getInputStream();
		final byte[] buffer = new byte[4096];
		int n = - 1;

		final OutputStream output = new FileOutputStream(filename);
		while ((n = input.read(buffer)) != -1) 
		    output.write(buffer, 0, n);
		output.close();
		
		return "";
	}
}
