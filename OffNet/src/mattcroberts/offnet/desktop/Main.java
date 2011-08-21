package mattcroberts.offnet.desktop;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import mattcroberts.gson.twitter.Tweet;
import mattcroberts.offnet.resources.Resource;
import mattcroberts.offnet.resources.WebResource;
import mattcroberts.offnet.services.twitter.Twitter;
import mattcroberts.offnet.utils.PersistablePropertyHolder;

public class Main implements PersistablePropertyHolder {

	public final static String persistableProperties[] = {"rootDir"};
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException{
		
		
		Properties properties = new Properties();
		properties.load(new FileReader("offnet.properties"));
		
		for(Object p : properties.keySet()){
			String name = (String) p;
			String value = properties.getProperty(name);
			System.setProperty(name, value);
		}
		
		String url = "http://t.co/C4BgjqA";
		//singleUrlTest(url);

		twitterTest();
		
		String props[] = Arrays.copyOf(persistableProperties, persistableProperties.length + Twitter.persistableProperties.length);
		System.arraycopy(Twitter.persistableProperties, 0, props, persistableProperties.length, Twitter.persistableProperties.length);
		persistProperties(props);
		
		
	}

	private static void singleUrlTest(String url) {
		Resource r = new WebResource(url);

		try {
			r.persist();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void twitterTest() {
		
		Twitter twitter = new Twitter();
		
		if(!twitter.hasAuthentication()){
			twitter.authenticate();
		}
		
		Tweet tweets[] = twitter.getTweets();
		for(Tweet tweet: tweets){
			System.out.println("\n\n" + tweet.getText());
			mattcroberts.gson.twitter.URL[] urls = tweet.getEmbeddedUrls();
			
			System.out.println("URLS:");
			if(urls != null){
				for(mattcroberts.gson.twitter.URL url:urls){
					//System.out.println(url.getUrl());
					Resource r = new WebResource(url.getExpanded_url());

					try {
						r.persist();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}	
			}
			
		}
	}
	
	public static void persistProperties(String propertyNames[]) throws IOException{
		
		Properties properties = new Properties();
		
		for(String key : propertyNames){
			String value = System.getProperty(key);
			
			if(value != null){
				properties.setProperty(key, value);
			}
		}
		
		properties.store(new FileWriter("offnet.properties"), "Offnet properties");
	}
}
