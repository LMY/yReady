package y.readyTasks;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import y.utils.GeneralProperties;
import y.utils.Notifiable;
import y.utils.Utils;

public class TaskSubito extends TaskHTML {

	public TaskSubito(Notifiable tracker, GeneralProperties<String> config, String url, long every, List<Entry> entries) {
		super(tracker, config, url, every, entries);
	}

	@Override
	public String getType() {
		return "subito";
	}
	
	@Override
	public List<Entry> parse(String content) {
		return parseContent(content);
	}

	
	public static List<Entry> parseContent(String content) {
		
		List<Entry> ret = new ArrayList<Entry>();
		
		try {
			final Document doc = Jsoup.parse(content);
			
			final Elements elements = doc.select("div.item_list_section.item_description");
	
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
	
	public final static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss");
	
	
	
	private static Entry inspectElement(Element e) {
		try {
			final String name = getAttr(e, "a[href]");
			final String link = e.select("a").first().attr("href");
			final String price = getAttr(e, "span.item_price");
			final String category = getAttr(e, "span.item_category");
			final String spec = getAttr(e, "span.item_specs");
			final String datetime = e.select("time").attr("datetime");

			return new Entry(DateTime.parse(datetime, formatter), link, name, price, category, spec);
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
