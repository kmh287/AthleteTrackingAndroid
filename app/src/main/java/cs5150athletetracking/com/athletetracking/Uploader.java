package cs5150athletetracking.com.athletetracking;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Uploader implements Runnable {
	// URL should be updated. We are currently using a test script "test_server.php"
	private static final String URL = "http://54.165.208.160/php/test_server.php";
	private static final String TAG_SUCCESS = "success";
	private String timestamp;
	private String username;
	private String bibnumber;
	private String raceid;
	private String latitude;
	private String longitude;
	private String altitude;

	public Uploader(String user, int bib, int race, double lat, double lon, double alt) {
		this.timestamp = Long.toString(System.currentTimeMillis());
		this.username  = user;
		this.bibnumber = Integer.toString(bib);
		this.raceid    = Integer.toString(race);
		this.latitude  = Double.toString(lat);
		this.longitude = Double.toString(lon);
		this.altitude  = Double.toString(alt);
	}

	@Override
	public void run() {
		System.out.println("Uploader Running...");
		JSON jsonParser = new JSON();

		// Building JSON Pairs...
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("timestamp", this.timestamp));
		params.add(new BasicNameValuePair("username",  this.username));
		params.add(new BasicNameValuePair("bibnumber", this.bibnumber));
		params.add(new BasicNameValuePair("raceid",    this.raceid));
		params.add(new BasicNameValuePair("latitude",  this.latitude));
		params.add(new BasicNameValuePair("longitude", this.longitude));
		params.add(new BasicNameValuePair("altitude",  this.altitude));

		// Sending modified data through HTTP request using "POST" argument
		JSONObject json = jsonParser.makeHttpRequest(URL, "POST", params);

		// Check JSON success tag
		try {
			int success = json.getInt(TAG_SUCCESS);

			if (success == 0) {
				// If server upload was successful, print reponse message.
				System.out.println(json.getString("message"));
			} else {
				// Otherwise, print a system error.
				System.err.println("Failed to upload data!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}