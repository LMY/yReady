package y.readyTasks;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import y.utils.GeneralProperties;
import y.utils.Notifiable;
import y.utils.Utils;

public class TaskKijiji extends TaskHTML {

	public TaskKijiji(Notifiable tracker, GeneralProperties<String> config, String url, long every, List<Entry> entries) {
		super(tracker, config, url, every, entries);
	}

	@Override
	public String getType() {
		return "kijiji";
	}
	
	@Override
	public List<Entry> parse(String content) {
		return parseContent(content);
	}

	
	public static List<Entry> parseContent(String content) {
		
		List<Entry> ret = new ArrayList<Entry>();
		
		try {
			final Document doc = Jsoup.parse(content);
			
			final Elements elements = doc.select("a.cta");
	
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
			final String name = getAttr(e, "p.description");
			final String link = e.select("a").first().attr("href");
			final String price = getAttr(e, "h4.price");
			final String category = getAttr(e, "p.locale");
			final String spec = getAttr(e, "h3.title");
			final DateTime datetime = translateItaDate(getAttr(e, "p.timestamp"));

			return new Entry(datetime, link, name, price, category, spec);
		}
		catch (Exception ex) { return null; }
	}
	
	private static String getAttr(Element e, String what) {
		try {
			return e.select(what).first().text();
		}
		catch (Exception ex) { return ""; }
	}
	
	
	private static DateTime translateItaDate(String s) {
		
		s = s.toLowerCase().trim();
		
		final DateTime today = DateTime.now();
		
		if (s.startsWith("oggi")) {
			final String time = s.replaceAll(",", "").replace("oggi", "").trim();
			final String[] parts = time.split(":");
			
			today.withHourOfDay(Integer.parseInt(parts[0]));
			today.withMinuteOfHour(Integer.parseInt(parts[1]));
			return today;
		}
		else if (s.startsWith("ieri")) {
			today.minusDays(1);
			
			final String time = s.replaceAll(",", "").replace("ieri", "").trim();
			final String[] parts = time.split(":");
			
			today.withHourOfDay(Integer.parseInt(parts[0]));
			today.withMinuteOfHour(Integer.parseInt(parts[1]));
			return today;
		}
		else {
			// "28 dicembre, 18:33"
			final String[] parts = s.split(" ");
			final String[] hparts = parts[2].split(":");
			
			final int day = Integer.parseInt(parts[0]);
			final int month = translateITAmonth(parts[1]);
			final int year = today.getMonthOfYear() >= month ?  today.getYear() :  today.getYear()-1;
			
			final int hr = Integer.parseInt(hparts[0]);
			final int mins = Integer.parseInt(hparts[1]);
			
			return new DateTime(year, month, day, hr, mins);
		}
	}
	
	
	public final static String[] ITA_MONTHS = { "gennaio", "febbraio", "marzo", "aprile", "maggio", "giugno", "luglio", "agosto", "settembre", "ottobre", "novembre", "dicembre" };
	
	private static int translateITAmonth(String s) {
		
		s = s.replaceAll(",", "").trim();
		
		for (int i=0; i<ITA_MONTHS.length; i++)
			if (s.equals(ITA_MONTHS[i]))
				return i+1;
		
		return 1;
	}
}
