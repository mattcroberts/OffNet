package mattcroberts.gson.twitter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.annotations.SerializedName;


public class Tweet {

	private long id;
	private String text;
	private Entities entities;
	
	@SerializedName("created_at")
	private Date creationDate;
	public Date getCreationDate(){
		return this.creationDate;
	}


	public mattcroberts.gson.twitter.URL[] getEmbeddedUrls(){
		return this.entities != null? this.entities.getUrls() : null;
	}
	public URL[] extractEmbeddedUrls(){
		ArrayList<URL> urls  = new ArrayList<URL>();
		
		String text = this.getText();
		
		String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		Pattern pattern = Pattern.compile(regex);
		
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()){
			String link = matcher.group();
			
			try {
				urls.add(new URL(link));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		return urls.toArray(new URL[urls.size()]);
	}
	
	public Entities getEntities() {
		return entities;
	}
	public long getId() {
		return id;
	}
	public String getText() {
		return text;
	}

	public void setCreationDate(Date date){
		this.creationDate = date;
	}
	
	
	public void setEntities(Entities entities) {
		this.entities = entities;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	

	public void setText(String text) {
		this.text = text;
	}
	
	
}
