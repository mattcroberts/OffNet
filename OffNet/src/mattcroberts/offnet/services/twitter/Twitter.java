package mattcroberts.offnet.services.twitter;

import java.util.Scanner;

import mattcroberts.gson.twitter.Tweet;
import mattcroberts.offnet.utils.PersistablePropertyHolder;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Twitter implements PersistablePropertyHolder{

	public static final String persistableProperties[] = {Twitter.OAUTH_TOKEN, Twitter.OAUTH_VERIFIER};
	public static final String OAUTH_VERIFIER = "oauth_verifier";
	public static final String OAUTH_TOKEN = "oauth_token";
	private final String API_KEY = "cj8gajHzQzBYjeqREqaNw";
	private final String API_SECRET = "oqJJBmOuBFMuabUoJGFbt5cWMF0TQECjR3NtUk5TI";
	private OAuthService service;
	public final String DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
	private Gson gson;
	
	Token accessToken;
	
	public Twitter(){
		OAuthService service = new ServiceBuilder()
        .provider(TwitterApi.class)
        .apiKey(API_KEY)
        .apiSecret(API_SECRET).callback("http://offnet.mattcroberts.com")
        .build();
		
		this.service = service;
		
		GsonBuilder gsonBuilder = new GsonBuilder()
		.setPrettyPrinting()
		.setDateFormat(DATE_FORMAT);
		
		this.gson = gsonBuilder.create();
	}
	
	public Token authenticate(){
		
		
		
		Token requestToken = service.getRequestToken();
		System.out.println("URL:" +service.getAuthorizationUrl(requestToken));
		Scanner in = new Scanner(System.in);
		System.out.println("verify");
		String secret = in.nextLine();
		
		Verifier verifier = new Verifier(secret);
		
		Token accessToken = service.getAccessToken(requestToken, verifier);
		this.accessToken = accessToken;
		
		System.setProperty(OAUTH_TOKEN, accessToken.getToken());
		System.setProperty(OAUTH_VERIFIER, accessToken.getSecret());
		
		return accessToken;				
	}
	
	public boolean hasAuthentication(){
		
		return this.getAccessToken() != null;
	}
	


	public String makeRequest(Token at, String url){
		
		OAuthRequest request = new OAuthRequest(Verb.GET, url);

		service.signRequest(at, request);
	    Response response = request.send();
	    	    
	    return response.getBody();
	}
	
	public Tweet[] getTweets(){
		String resp = this.makeRequest(this.getAccessToken(), "http://api.twitter.com/1/statuses/home_timeline.json?include_entities=true");
		System.out.println(resp);
		Tweet tweets[] = gson.fromJson(resp, Tweet[].class);
		
		return tweets;
	}

	private Token getAccessToken() {
		
		String oauth_token = System.getProperty(OAUTH_TOKEN);
		String oauth_verifier = System.getProperty(OAUTH_VERIFIER);
		
		if(this.accessToken != null){
			return this.accessToken;
		}else if(oauth_token != null && oauth_verifier != null){
			
			this.accessToken = new Token(oauth_token,oauth_verifier);
			return this.accessToken;
		}
		
		return null;
		
	}
}
