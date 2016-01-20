package y.readyTasks;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import y.utils.GeneralProperties;
import y.utils.Notifier;
import y.utils.Utils;

public class TaskInsegreto extends TaskHTML {

	public TaskInsegreto(Notifier notifier, GeneralProperties<String> config, String url, long every, List<Entry> entries) {
		super(notifier, config, url, every, entries);
	}

	@Override
	public String getType() {
		return "insegreto";
	}

	@Override
	public List<Entry> parse(String content) {
		return parseContent(content);
	}

	
	public static List<Entry> parseContent(String content) {
		List<Entry> ret = new ArrayList<Entry>();
		
		try {
			final Document doc = Jsoup.parse(content);
			
			final Elements elements = doc.select("article");
	
			for (Element e : elements) {
				final Entry l = inspectElement(e);
				if (l == null)
					continue;
				
				ret.add(l);
			}
		}
		catch (Exception e) {
			Utils.MessageBox(e.getMessage(), "ERROR PARSING");
		}
		
		return ret;
	}

	
	private static Entry inspectElement(Element e) {
		try {
			final String name = getAttr(e, "p.message");
			final String link = e.select("a").first().attr("href");
			final String price = "";
			final String category = getAttr(e, "a[href]");	// who
			final String spec = "";

			return new Entry(DateTime.now(), link, name, price, category, spec);
		}
		catch (Exception ex) { return null; }
	}
	
	private static String getAttr(Element e, String what) {
		try {
			return e.select(what).first().text();
		}
		catch (Exception ex) { return ""; }
	}	
}
