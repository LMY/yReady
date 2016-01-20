package y.readyTasks;

import org.joda.time.DateTime;

public class Entry {
	private DateTime time;
	private String url;
	private String name;
	private String price;
	private String category;
	private String spec;
	
	public Entry(DateTime time, String url, String name, String price, String category, String spec) {
		this.time = time;
		this.url = url;
		this.name = name;
		this.price = price;
		this.category = category;
		this.spec = spec;
	}

	public DateTime getTime() {
		return time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}
}
