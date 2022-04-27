package net.rocklabs.domodemo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONArray;

public class Utils {


	public void log(String str){
		System.out.println(str);
	}

	public static String httpPost(String targetURL, String urlParameters)
	{
		URL url;
		HttpURLConnection connection = null;
		try {
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes (urlParameters);
			wr.flush ();
			wr.close ();

			//Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		} finally {

			if(connection != null) {
				connection.disconnect();
			}
		}
	}

	public static String httpGet(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}

		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line + "\n");
		}
		rd.close();

		conn.disconnect();
		return sb.toString();
	}

	private  HttpURLConnection getHttpConnection(String url, String type){
		URL uri;
		HttpURLConnection con = null;
		try{
			uri = new URL(url);
			con = (HttpURLConnection) uri.openConnection();
			con.setRequestMethod(type); //type: POST, PUT, DELETE, GET
			//con.setDoOutput(true);
			//con.setDoInput(true);
			//con.setConnectTimeout(60000); //60 secs
			//con.setReadTimeout(60000); //60 secs
			//con.setRequestProperty("Accept-Encoding", "Your Encoding");
			//con.setRequestProperty("Accept", "application/json");
		}catch(Exception e){
			log("connection i/o failed");
		}
		return con;
	}

	protected String http(String url, String type, String reqbody){
		HttpURLConnection con = null;
		String result = null;
		try {
			con = getHttpConnection(url, type);
			//you can add any request body here if you want to post
			if( reqbody != null){
				con.setDoInput(true);
				con.setDoOutput(true);
				DataOutputStream out = new  DataOutputStream(con.getOutputStream());
				out.writeBytes(reqbody);
				out.flush();
				out.close();

			}
			con.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String temp = null;
			StringBuilder sb = new StringBuilder();
			while((temp = in.readLine()) != null){
				sb.append(temp + "\n");
			}
			result = sb.toString();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			log(e.getMessage());
		}
		finally {
			con.disconnect();
		}
		return result;
	}
}
