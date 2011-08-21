package mattcroberts.offnet.resources;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



public abstract class Resource {

	public static final String DATA_DIR = System.getProperty("rootDir") + "/resources";
	public final UUID uuid = UUID.randomUUID();
	private String resourcePath;
	private String url;
	private Resource parent;
	private String name;
	private String content;
	protected List<Resource> subResources;
	
	public Resource(String url){
		
		this.subResources = new ArrayList<Resource>();
		this.url = url;
		this.name = extractName(url);
		this.resourcePath = DATA_DIR + "/" + this.getPrettyUrl(this.url) + "/" + this.getName();
	}

	public Resource(String url, Resource parent){
		this.subResources = new ArrayList<Resource>();
		this.parent = parent;
		this.url = url;
		this.name = extractName(url);
		this.resourcePath = DATA_DIR + "/" + this.getPrettyUrl(this.url) + "/" + this.getName();
	}

	protected void analyse(){
		
		
	}

	private String extractName(String url) {
		url = url.split("\\?")[0];
		String urlParts[] = url.split("/");
		String nameQuery = urlParts[urlParts.length-1];
		
		if(nameQuery.indexOf("?") == -1){
			if(getPrettyUrl(url).equals(nameQuery)){
				return "index.html";
			}
			
			
			return name = nameQuery;
		}else{
			return name = nameQuery.substring(0,nameQuery.indexOf("?"));
		}
	}

	private void updateUrl(String urlString){
		this.url = urlString;
		this.name = extractName(urlString);
		this.resourcePath = DATA_DIR + "/" + this.getPrettyUrl(this.url) + "/" + this.getName();
	}
	private byte[] fetchAsBinary() {
		System.err.println(url);
		URL url;
		try {
			url = new URL(this.getUrl());
			url.toURI();
		} catch (MalformedURLException e1) {
			return null;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}		
		
		InputStream in = null;
		ByteArrayOutputStream os = null;
		try {

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();
			
			
			if(con.getResponseCode() == 404){
				return null;
			}
			updateUrl(con.getURL().toString());
			in = con.getInputStream();
			
			BufferedInputStream bis = new BufferedInputStream(in);
			
			os = new ByteArrayOutputStream();
			
			int i;
			byte bytes[] = new byte[1024 * 1000];
			while((i = bis.read(bytes)) != -1){
				os.write(bytes,0,i);
			}
			os.flush();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			
			return null;
		} finally{
			
			
		}
		
		return os.toByteArray();
	}

	protected String fetchAsString() throws IOException {
		
		byte[] bytes = this.fetchAsBinary();
		String text = null;
		if(bytes != null){
			text = new String(bytes);
		}
		
		return text;
	}

	public String getContent() {
		return content;
	}

	public String getName() {
		return name;
	}

	public Resource getParent() {
		return parent;
	}

	protected String getPrettyUrl(String url) {
		url = url.split("\\?")[0];
		
		int start = 0;
		if(url.contains("://")){
			start = url.indexOf("://") + 3;
		}else if(url.startsWith("/") && this.getParent() != null){
			try {
				URL parentUrl = new URL(this.getParent().getUrl());
				url = parentUrl.getHost() + url;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		int end = url.lastIndexOf("/");
		if(end <= start){
			end = url.length();
		}
		return url.substring(start,end);
	}

	public String getResourcePath() {
		return resourcePath;
	}
	
	public List<Resource> getSubResources() {
		return subResources;
	}

	public String getUrl() {
		
		if(this.url.startsWith("/") && this.parent != null){
			try {
				URL parent = new URL(this.parent.getUrl());
				return parent.getProtocol() + "://" + parent.getHost() + this.url;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return url;
	}
	
	public boolean persist() throws IOException{
		
		File file = new File(this.resourcePath);
		
		file.getParentFile().mkdirs();
		
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(new FileOutputStream(file));
			byte data[] = this.fetchAsBinary();
			if(data != null){
				dos.write(data);
			}else{
				return false;
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} finally{
			if(dos != null){
				dos.flush();
				dos.close();
			}
			
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass().getSuperclass() != obj.getClass().getSuperclass()) 
			return false;
		Resource other = (Resource) obj;
		if (getUrl() == null) {
			if (other.getUrl() != null)
				return false;
		} else if (!getUrl().equals(other.getUrl()))
			return false;
		return true;
	}

	public String read() throws IOException{
		
		String content = "";
		
		FileInputStream fis = new FileInputStream(this.resourcePath);
		
		byte bytes[] = new byte[1000 * 1024];
		while(fis.read(bytes) != -1){
			content += new String(bytes);
		}
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setParent(Resource parent) {
		this.parent = parent;
	}
	
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	protected void setSubResources(List<Resource> subResources) {
		this.subResources = subResources;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void addSubResource(Resource subResource) {
		this.subResources.add(subResource);
	}

	public String getFileExtension() {
		int last = this.getUrl().lastIndexOf('.') + 1;
		return this.getUrl().substring(last);
		
	}
	

}
