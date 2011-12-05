package mattcroberts.offnet;

import mattcroberts.gson.twitter.Tweet;
import mattcroberts.offnet.services.twitter.Twitter;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TweetList extends ListActivity {

    private static String TAG = "offnet.android";

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
	private Twitter twitter;
	private Tweet tweets[];

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		System.setProperty("oauth_token",getString(R.string.oauth_token));
		System.setProperty("oauth_verifier", getString(R.string.oauth_verifier));
		
		this.twitter = new Twitter();
		if (!twitter.hasAuthentication()) {
			twitter.authenticate();
		}
		this.tweets = twitter.getTweets();

		System.out.println("tt:" + tweets);
		setListAdapter(new ArrayAdapter<Tweet>(this, R.layout.tweet_list,
				this.tweets));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
	}

}

