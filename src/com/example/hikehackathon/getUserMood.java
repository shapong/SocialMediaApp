package com.example.hikehackathon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class getUserMood extends AsyncTask<String, Void, String>{

	protected String doInBackground(String... urls) {
		String msg = urls[0];
        String apiCall =
        "http://access.alchemyapi.com/calls/text/TextGetTextSentiment?" +
        "apikey=5ac497bfe28fde547bedaac4dba2d72cf48913d5&"+
        "text="+msg+"&"+
        "outputMode=json";
        	StringBuilder builder = new StringBuilder();
        	HttpClient client = new DefaultHttpClient();
        	HttpGet httpGet = new HttpGet(apiCall);
        	System.out.println("sdlkfhglskdfnglksdfjglksdfjglksdfjglkdf");
        	try{
        		HttpResponse response = client.execute(httpGet);
        		StatusLine statusLine = response.getStatusLine();
        		int statusCode = statusLine.getStatusCode();
        		if(statusCode == 200){
        			HttpEntity entity = response.getEntity();
        			InputStream content = entity.getContent();
        			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        			String line;
        			while((line = reader.readLine()) != null){
        				builder.append(line);
        			}
        		} else {
        			Log.e(MainActivity.class.toString(),"Failed to get JSON object");
        		}
        	}catch(ClientProtocolException e){
            	System.out.println("sdlkfhglskdfnglksdfjglksdfjglksdfjglkdf");
        		e.printStackTrace();
        	} catch (IOException e){
        		e.printStackTrace();
        	}
        	return builder.toString();
	}
	
	protected void onPostExecute(String url) {
	
	}
}
