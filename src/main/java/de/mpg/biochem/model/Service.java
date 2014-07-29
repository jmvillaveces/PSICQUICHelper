package de.mpg.biochem.model;

public class Service implements java.io.Serializable{


	private static final long serialVersionUID = 1L;
	
	private String name, restURL, version, query;
	private long count;
	private boolean indexed;
	
	public Service(){
		indexed = false;
	}
	
	public Service(String name, String restURL, String version, long count) {
		this();
		this.name = name;
		this.restURL = restURL;
		this.version = version;
		this.count = count;
	}
	
	public Service(String name, String restURL, String version, long count, String query) {
		this(name, restURL, version, count);
		this.query = query;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	public String getRestURL() {
		return restURL;
	}
	
	public void setRestURL(String restURL) {
		this.restURL = restURL;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
