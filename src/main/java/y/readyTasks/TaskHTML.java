package y.readyTasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import y.utils.GeneralProperties;
import y.utils.Notifiable;

public abstract class TaskHTML extends TaskParsable {
	
	public TaskHTML(Notifiable tracker, GeneralProperties<String> config, String url, long every) {
		this(tracker, config, url, every, null);
	}
	
	public TaskHTML(Notifiable tracker, GeneralProperties<String> config, String url, long every, List<Entry> entries) {
		super(tracker, config, url, every, entries);
	}

	@Override
	public String getType() {
		return "html";
	}

	@Override
	public String getContent(String url) throws Exception {
		return getHTML(url);
	}


	public static String getHTML(String urlToRead) throws Exception {
		
		if (!urlToRead.startsWith("http"))
			urlToRead = "http://" + urlToRead;
		
		final StringBuilder result = new StringBuilder();
		final URL url = new URL(urlToRead);
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		@SuppressWarnings("unused")
		final int responseCode = conn.getResponseCode();
		final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String line;
		while ((line = rd.readLine()) != null)
			result.append(line);
		rd.close();
		
		return result.toString();
	}
}
