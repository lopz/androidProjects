package net.rocklabs.domodemo;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class RequestTask extends AsyncTask<String, String, String> {
	private final String TAG = "RequestTask";
	private final String WIT_URL = "http://192.168.1.10:8080/message?q=";
	private final String VERSION = "20140501";
	private final String AUTHORIZATION_HEADER = "Authorization";
	private final String BEARER_FORMAT = "Bearer %s";
	private final String ACCEPT_HEADER = "Accept";
	private final String ACCEPT_VERSION = "application/vnd.wit." + VERSION;
	private String _accessToken;

	public RequestTask(String accessToken) {
		_accessToken = accessToken;
	}
	public static String streamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		is.close();
		return sb.toString();
	}
	@Override
	protected String doInBackground(String... text) {
		String response = null;
		try {
			Log.d(TAG, "Requesting ...." + text[0]);
			final String getUrl = String.format("%s%s", WIT_URL, URLEncoder.encode(text[0], "utf-8"));
			URL url = new URL(getUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			//urlConnection.addRequestProperty(AUTHORIZATION_HEADER, String.format(BEARER_FORMAT, _accessToken));
			//urlConnection.addRequestProperty(ACCEPT_HEADER, ACCEPT_VERSION);
			try {
				final InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				response = streamToString(in);
				in.close();
			} finally {
				urlConnection.disconnect();
			}
		} catch (Exception e) {
			Log.e(TAG, "An error occurred during the request, did you set your token correctly?", e);
		}
		return response;
	}
	@Override
	protected void onPostExecute(String result) {
	}
}