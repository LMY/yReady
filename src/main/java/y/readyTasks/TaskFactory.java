package y.readyTasks;

import java.io.File;
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

import y.readyTasks.OutputPolicies.OutputPolicy;
import y.readyTasks.OutputPolicies.OutputPolicyFactory;
import y.utils.GeneralProperties;
import y.utils.Notifiable;
import y.utils.Notifier;
import y.utils.Utils;

public class TaskFactory {
	
	public final static String SEPARATOR = "\t";
	public final static String TASKS_FILENAME = Task.TASKS_FOLDER + "tasks.xml";
	
	public final static long DEFAULT_TIME = 300;
	
	public static String TAG_TASKS_TASKS = "tasks";
	public static String TAG_TASKS_TASK = "task";
	public static String TAG_TASKS_TYPE = "type";
	public static String TAG_TASKS_URL = "url";
	public static String TAG_TASKS_TIME = "time";
	public static String TAG_TASKS_OUT = "output";
	
	static {
		final File file = new File(Task.TASKS_FOLDER);
		file.mkdirs();
		if (!new File(TASKS_FILENAME).exists())
			try {
				saveXML(TASKS_FILENAME, new ArrayList<Task>());
			}
			catch (Exception e) {}
	}
	
	
	public static Task createTask(Notifiable tracker, GeneralProperties<String> config, OutputPolicy defaultPolicy, String type, String url, String time, String out) {
		
		long itime;
		try { itime = Long.parseLong(time); }
		catch (Exception e) { itime = DEFAULT_TIME; }
		
		OutputPolicy policy = OutputPolicyFactory.create(config, out);
		if (policy == null)
			policy = defaultPolicy;
		
		final Notifier notifier = new Notifier(tracker, policy);
		
		switch (type) {
			case "subito" : return new TaskSubito(notifier, config, url, itime, null);
			case "kijiji" :return new TaskKijiji(notifier, config, url, itime, null);
			case "insegreto" : return new TaskInsegreto(notifier, config, url, itime, null);
			case "file" : return new TaskFile(notifier, config, url);
			default: return null;
		}
	}
	
	public static List<Task> parseXML(String filename, Notifiable tracker, GeneralProperties<String> config) throws Exception {
		
		List<Task> ret = new ArrayList<Task>();
		
		final OutputPolicy defaultPolicy = OutputPolicyFactory.configPolicy(config);

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

					if (nodename.equals(TAG_TASKS_TASK)) {
						final String type = xmlAttributeGet(node, TAG_TASKS_TYPE);
						final String url = xmlAttributeGet(node, TAG_URL);
						final String time = xmlAttributeGet(node, TAG_TASKS_TIME);
						final String out = xmlAttributeGet(node, TAG_TASKS_OUT);

						final Task t = createTask(tracker, config, defaultPolicy, type, url, time, out);
						if (t != null)
							ret.add(t);
					}
				}
			}
		}
		catch (Exception e) {
			Utils.MessageBox("Error reading "+filename+"\n"+e.getMessage(), "ERROR PARSING XML");
		}
		
		return ret;
	}
	
	public static boolean saveXML(String filename, List<Task> tasks) throws Exception {
		try {
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			final Document doc = docBuilder.newDocument();
			final Element root = doc.createElement(TAG_TASKS_TASKS);
			doc.appendChild(root);
			
			if (tasks.size() > 0)
				for (Task t : tasks)
					root.appendChild(exportXML(doc, t));
			
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
	
	
	private static Node exportXML(Document doc, Task t) {
		final Element rootElement = doc.createElement(TAG_TASKS_TASK);
		
		xmlAttributeSet(doc, rootElement, TAG_TASKS_TYPE, t.getType());
		xmlAttributeSet(doc, rootElement, TAG_TASKS_URL, t.getUrl());
		xmlAttributeSet(doc, rootElement, TAG_TASKS_TIME, ""+t.getEvery());
		xmlAttributeSet(doc, rootElement, TAG_TASKS_OUT, ""+t.getOutputString());
		
		return rootElement;
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
			Utils.MessageBox(e.getMessage()+"\n"+e.toString(), "ERROR saving list");
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
