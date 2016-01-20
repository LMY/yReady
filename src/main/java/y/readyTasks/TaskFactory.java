package y.readyTasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.joda.time.DateTime;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import y.utils.GeneralProperties;
import y.utils.Notifiable;
import y.utils.Utils;

public class TaskFactory {
	
	public final static String SEPARATOR = "\t";
	public final static String TASKS_FILENAME = "tasks.csv";
	
	public static Task parseLine(Notifiable tracker, GeneralProperties<String> config, String line) {
		final String[] parts = line.split(SEPARATOR);
		
		if (parts.length < 2)
			return null;
		
		try {
			final long every = Long.parseLong(parts[1]);
			
			if (parts[0].equals("subito"))
				return new TaskSubito(tracker, config, parts[2], every, null);
			else if (parts[0].equals("kijiji"))
				return new TaskKijiji(tracker, config, parts[2], every, null);
			else if (parts[0].equals("insegreto"))
				return new TaskInsegreto(tracker, config, parts[2], every, null);
			else if (parts[0].equals("file"))
				return new TaskFile(tracker, config, parts[2]);
			
//			else if (parts[0].equals("general"))
//				return new Task(tracker, config, parts[2], every);
		}
		catch (Exception e) {}
		
		return null;	// reached if exception is thrown in parseLong or no "else if" matches
	}
	
	public static String taskToString(Task t) {
		return t.getType() + SEPARATOR + t.getUrl();
	}
	

	public static List<Task> parse(String filename, Notifiable tracker, GeneralProperties<String> config) throws Exception {
		
		List<Task> ret = new ArrayList<Task>();
		
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(filename));
			
			String line;
			while ((line=br.readLine()) != null) {
				if (line.isEmpty() || line.startsWith("//"))
					continue;
				
				final Task newtask = parseLine(tracker, config, line.trim());
				if (newtask != null)
					ret.add(newtask);
			}
		}
		finally {
			if (br != null)
				try { br.close(); }
				catch (Exception e2) {}
		}
		
		return ret;
	}
	
	public static void save(String filename, List<Task> tasks) throws Exception {
		BufferedWriter br = null;
		
		try {
			br = new BufferedWriter(new FileWriter(filename));
			
			for (final Task t : tasks)
				br.write(taskToString(t)+"\n");
		}
		finally {
			if (br != null)
				try { br.close(); }
				catch (Exception e2) {}
		}
	}
	

	// XML
	
	public static String TAG_ROOT = "entries";
	public static String TAG_ENTRY = "entry";
	public static String TAG_TIME = "time";
	public static String TAG_URL = "url";
	public static String TAG_NAME = "name";
	public static String TAG_PRICE = "price";
	public static String TAG_CATEGORY = "category";
	public static String TAG_SPEC = "spec";
	
	
	public static boolean saveList(String filename, List<Entry> list) {
		try {
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			final Document doc = docBuilder.newDocument();
			final Element root = doc.createElement(TAG_ROOT);
			doc.appendChild(root);
			
			if (list.size() > 0)
				for (Entry e : list)
					root.appendChild(exportXML(doc, e));
			
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(doc);
			final StreamResult result = new StreamResult(new File(filename));
	 
			transformer.transform(source, result);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	private static Node exportXML(Document doc, Entry e) {
		final Element rootElement = doc.createElement(TAG_ENTRY);
		
		xmlAttributeSet(doc, rootElement, TAG_TIME, e.getTime().toString());
		xmlAttributeSet(doc, rootElement, TAG_URL, e.getUrl());
		xmlAttributeSet(doc, rootElement, TAG_NAME, e.getName());
		xmlAttributeSet(doc, rootElement, TAG_PRICE, e.getPrice());
		xmlAttributeSet(doc, rootElement, TAG_CATEGORY, e.getCategory());
		xmlAttributeSet(doc, rootElement, TAG_SPEC, e.getSpec());
		
		return rootElement;
	}
	
	public static List<Entry> loadList(String filename, boolean report_errors) {
		final List<Entry> list = new ArrayList<Entry>();
		
		try {
			final File file = new File(filename);
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();

			final Element root = doc.getDocumentElement();
			final NodeList nodes = root.getChildNodes();
			
			for (int i = 0; i < nodes.getLength(); i++) {
				final Node node = nodes.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					final String nodename = node.getNodeName();

					if (nodename.equals(TAG_ENTRY)) {
						final String time = xmlAttributeGet(node, TAG_TIME);
						final String url = xmlAttributeGet(node, TAG_URL);
						final String name = xmlAttributeGet(node, TAG_NAME);
						final String price = xmlAttributeGet(node, TAG_PRICE);
						final String category = xmlAttributeGet(node, TAG_CATEGORY);
						final String spec = xmlAttributeGet(node, TAG_SPEC);
						
						list.add(new Entry(DateTime.parse(time), url, name, price, category, spec));
					}
				}
			}
		}
		catch (Exception e) {
			if (report_errors)
				Utils.MessageBox("Error reading "+filename+"\n"+e.getMessage(), "ERROR PARSING XML");
		}
		
		return list;
	}
	
//	private static Node createXMLNode(Document doc, Node parent, String name)
//	{
//		if (!name.isEmpty()) {
//			Element e = doc.createElement(name);
//			parent.appendChild(e);
//			return e;
//		}
//		else
//			return null;
//	}
//	
//	private static Node createXMLTextNode(Document doc, Node parent, String name, String value)
//	{
//		if (!value.isEmpty() && !name.isEmpty()) {
//			Element e = doc.createElement(name);
//			e.appendChild(doc.createTextNode(value));
//			parent.appendChild(e);
//			return e;
//		}
//		else
//			return null;
//	}
//	
//	private static String getXMLNodeValue(Node node) {
//		return node.getLastChild().getTextContent().trim();
//	}
	
	public static void xmlAttributeSet(Document doc, Element e, String name, String value) {
		final Attr attr = doc.createAttribute(name);
		attr.setValue(value);
		e.setAttributeNode(attr);
	}
	
	public static String xmlAttributeGet(Node node, String attr) {
		try {
			return node.getAttributes().getNamedItem(attr).getNodeValue();
		}
		catch (Exception e) {
			return "";
		}
	}
}
