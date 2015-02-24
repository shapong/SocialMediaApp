package com.example.hikehackathon;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap.Config;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.apache.http.client.ClientProtocolException;
import java.io.IOException;
//import android.app.usage.UsageStats;

public class MainFragment extends Fragment {
	
	//Users Data....
	private TextView userNameView;
	private TextView userGenderView;
	private TextView userAgeView;
	private TextView userInterestsView;
	private TextView userFriendsView;
	private TextView userStatusView;
	private TextView userMoodView;
	private String myName;

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private TriggerEventListener mTriggerEventListener;
	private static final String TAG = "MainFragment";
	private Map<String, Integer> friend_count = new HashMap<String, Integer>();
	

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.activity_main, container, false);

	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    //Setting permissions....
	    authButton.setReadPermissions(Arrays.asList("user_likes", "user_status", "user_birthday", "user_photos"));
	    authButton.setFragment(this);
	    
	    //Set up user data...
	    userNameView = (TextView) view.findViewById(R.id.user_name);
	    userGenderView = (TextView) view.findViewById(R.id.user_gender);
	    userAgeView = (TextView) view.findViewById(R.id.user_age);
	    userInterestsView = (TextView) view.findViewById(R.id.user_interests);
	    userFriendsView = (TextView) view.findViewById(R.id.user_friends);
	    userStatusView = (TextView) view.findViewById(R.id.user_status);
	    //userMoodView = (TextView) view.findViewById(R.id.user_mood);
		
		//ArrayList<UsageStats> mStats = new ArrayList<UsageStats>();
		//System.out.println(mStats.get(0).getTotalTimeInForeground());
		
		/*Context ctxt = getActivity();
		List<PackageInfo> packs = ctxt.getPackageManager().getInstalledPackages(0);
		for(int i=0;i<packs.size();i++) {
			PackageInfo p = packs.get(i);
			System.out.println(p.applicationInfo.loadLabel(ctxt.getPackageManager()).toString());
		}*/
		/*for (ApplicationInfo app : ctxt.getPackageManager().getInstalledApplications(0)) {
			
			appNames.put(app.uid, app.packageName);
		}*/
		//System.out.println(apps+ "////////" + apps.size());
		//System.out.println(appNames+" ///////////// "+appNames.size());
		
		//mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		//mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
	    new getUserMood().execute((String)userStatusView.getText());
	    return view;
	}
	
	private void makeMeRequest(Session session) {
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	                if (user != null) {
	                    // Set the Textview's text to the user's name.
	                	myName = user.getName();
	                    userNameView.setText("\t"+user.getName());
	                    userGenderView.setText("\t"+(CharSequence) user.getProperty("gender"));
	                    userAgeView.setText("\t"+user.getBirthday());
	                }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    }); //------end of request--------
	    request.executeAsync();
	}//end of request method...    
	
	private void makeInterestsRequest(Session session) {
		session = Session.getActiveSession();
	    Request interests = new Request(
	    		session,
	    	    "/me/likes",
	    	    null,
	    	    HttpMethod.GET,
	    	    new Request.Callback() {
	    	        public void onCompleted(Response response) {
	    	        	// handle the result 
	    	        	try
         	            {
         	        		Log.d("likes", "is it?");
         	                GraphObject gObj  = response.getGraphObject();
         	                JSONObject jso = gObj.getInnerJSONObject();
         	                JSONArray arr = jso.getJSONArray( "data" );
                            String like_string = "";
                            Map<String, List<String> > likes = new HashMap<String, List<String>>(); 
                            
                            //Log.d("dkljfg", "Hey "+ arr.length());
         	                for ( int i = 0; i < ( arr.length() ); i++ )
         	                {
         	                    JSONObject json_obj = arr.getJSONObject( i );
         	                    String name = json_obj.getString("name").toString();
         	                    String category = json_obj.getString("category").toString();
                                Log.d("likes",name);
                                
                                if(likes.containsKey(category)) {
                                	likes.get(category).add(name);
                                }
                                else {
                                	List<String> tmp = new ArrayList<String>();
                                	tmp.add(name);
                                	likes.put(category, tmp);
                                }
         	                    
         	                }
         	                
         	                Iterator<Entry<String, List<String>>> it = likes.entrySet().iterator();
         	                while (it.hasNext()) {
         	                	Map.Entry pairs = (Map.Entry)it.next();
         	                	//System.out.println(pairs.getKey() + " = " + pairs.getValue());
         	                	like_string = like_string + pairs.getKey() + "\n";// + pairs.getValue();
         	                	List<String> v = (List<String>) pairs.getValue();
         	                	like_string += "-";
         	                	for(int i=0;i<v.size();i++) {
         	                		if(i!=v.size()-1) like_string = like_string + v.get(i) + ", ";
         	                		else like_string = like_string + v.get(i);
         	                	}
         	                	like_string = like_string + "\n\n";
         	                	it.remove();
         	                }
         	                System.out.println(likes);
         	                
         	                if(like_string!="") userInterestsView.setText(like_string);
         	                else userInterestsView.setText("None");
         	                userInterestsView.setVisibility(1);
         	            }
         	            catch ( Throwable t )
         	            {
         	                t.printStackTrace();
         	            }
	    	        }
	    	    }
	    );
	    
	    interests.executeAsync();
	} //end of interests request
	    
	private void makeFriendsRequest(Session session) {
	    //To find a users best friend find who has been tagged the most with the user...
	    friend_count = new HashMap<String, Integer>();
	    session = Session.getActiveSession();
	    Request user_photos = new Request(
	    		session,
	    	    "/me/photos",
	    	    null,
	    	    HttpMethod.GET,
	    	    new Request.Callback() {
	    	        public void onCompleted(Response response) {
	    	        	// handle the result
	    	        	try
         	            {
         	                GraphObject gObj  = response.getGraphObject();
         	                JSONObject jso = gObj.getInnerJSONObject();
         	                JSONArray arr = jso.getJSONArray( "data" );
         	                List<String> photos = new ArrayList<String>();
         	                for(int i=0;i<arr.length();i++) {
         	                	JSONObject json_obj = arr.getJSONObject( i );
    	                    	String photo_id = json_obj.getString("id").toString();
    	                    	photos.add(photo_id);
         	                }
    	    	        	//System.out.println(photos);
    	    	        	//userFriendsView.setText(friend_count.toString());
         	               getCloseFriend(photos);
         	               
    	    	        	
         	            } catch (Throwable t) {
         	            	t.printStackTrace();
         	            }
	    	        	//userFriendsView.setText(friend_count.toString());
	    	        }
	    	    }
	    		
	    );
	    //call and get all photo_ids...
	    user_photos.executeAsync();
	    //call to get close friend tags....
	    
	    //System.out.println("///// "+friend_count);
	}//end of friends request...
		 
	private void getCloseFriend(List<String> photos) {
		String req = "";
		//System.out.println("////// " + photos);
		Session session = Session.getActiveSession();
		
		for(int i=0;i<photos.size();i++) {
			req = "/" + photos.get(i) +"/tags";
			Request closeFriends = new Request(
		    		session,
		    	    req,
		    	    null,
		    	    HttpMethod.GET,
		    	    new Request.Callback() {
		    			
			    		@Override
			    	    public void onCompleted(Response response) {
			    	    	// TODO Auto-generated method stub
							try {
				    			GraphObject gObj  = response.getGraphObject();
	         	                JSONObject jso = gObj.getInnerJSONObject();
	         	                JSONArray arr = jso.getJSONArray( "data" );
	         	                        
	         	                for(int i=0;i<arr.length();i++) {
	         	                	JSONObject json_obj = arr.getJSONObject( i );
	    	                    	String friend = json_obj.getString("name").toString();
	    	                    	if(friend_count.containsKey(friend)) {
	    	                    		int tmp = (int) friend_count.get(friend);
	    	                    		friend_count.put(friend, tmp+1);
	    	                    	} else {
	    	                    		friend_count.put(friend,1);
	    	                    	}
	         	                }
	         	                int maxValueInMap=(Collections.max(friend_count.values()));
	         	                friend_count.remove(myName);
	         	                for (Entry<String, Integer> entry : friend_count.entrySet()) {
	         	                	if (entry.getValue()==maxValueInMap) {
	         	                		//System.out.println(entry.getKey());
	         	                		userFriendsView.setText("\t"+entry.getKey());
	         	                	}
	         	                }
	         	                
							} catch (Throwable t) {
								// TODO Auto-generated catch block
								t.printStackTrace();
							}
			    	    }
		    		
		    		}
			);	
			closeFriends.executeAsync();
            
		}
	} /// end of close friends request...
	
	private void makeStatusRequest(Session session) {
		session = Session.getActiveSession();
	    Request status = new Request(
	    		session,
	    	    "/me/statuses",
	    	    null,
	    	    HttpMethod.GET,
	    	    new Request.Callback() {
	    	        public void onCompleted(Response response) {
	    	        	// handle the result
	    	        	try {
	    	        		GraphObject gObj  = response.getGraphObject();
         	                JSONObject jso = gObj.getInnerJSONObject();
         	                JSONArray arr = jso.getJSONArray( "data" );
         	                JSONObject json_obj = arr.getJSONObject( 0 );
         	                String msg = json_obj.getString("message").toString();
         	                userStatusView.setText(msg);
         	                //String mood = getUserStatus();
         	                //userMoodView.setText(mood);
         	            }
         	            catch ( Throwable t )
         	            {
         	                t.printStackTrace();
         	            }
	    	        }
	    	    }
	    );
	    
	    status.executeAsync();
	} //end of status request
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        makeMeRequest(session);
	        makeInterestsRequest(session);
	        makeFriendsRequest(session);
 	   		makeStatusRequest(session);
 	   		
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	    }
	}
	
	final String getUserStatus() {
		String msg = (String)userStatusView.getText();
         String apiCall =
         "http://access.alchemyapi.com/calls/text/TextGetTextSentiment?" +
         "apikey=5ac497bfe28fde547bedaac4dba2d72cf48913d5&"+
         "text="+"hello"+"&"+
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

	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};

	
	private UiLifecycleHelper uiHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	    /*mTriggerEventListener = new TriggerEventListener() {
		    @Override
		    public void onTrigger(TriggerEvent event) {
		        // Do work
		    	System.out.println("kldhfglksdjflgkd//////////---////zldkfhgdlkfg");
		    }
		};
		
		mSensorManager.requestTriggerSensor(mTriggerEventListener, mSensor);*/
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }

	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

}
