package com.finale.bif;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Splasher extends Activity {
    /** Duration of wait **/
	final String LOGIN_URL = "http://198.12.152.47/~ahujakaran24/webservice/get_url.php";
	final String TAG_SUCCESS = "success";
	final String TAG_MESSAGE = "message";
	final String TAG_URL = "url";
	Appref ap;
	int id=1;
	String url;
	JsonBhai jsonParser = new JsonBhai();
    private int SPLASH_DISPLAY_LENGTH = 5000;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splasheees);
        ap=new Appref(getApplicationContext());
        if(ap.getfeed().equals(" ") && isOnline())
        {
        new  AttemptLogin().execute();
        SPLASH_DISPLAY_LENGTH = 10000;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() 
            {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(Splasher.this,MainWaliActi.class);
                Splasher.this.startActivity(mainIntent);
                Splasher.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
        }
        else if (!isOnline() && !ap.getfeed().equals(" ") )
        {
        	Toast.makeText(getApplicationContext(),"Entering in Offline mode",  Toast.LENGTH_LONG).show();
            SPLASH_DISPLAY_LENGTH = 10000;
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() 
                {
                    /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(Splasher.this,MainWaliActi.class);
                    Splasher.this.startActivity(mainIntent);
                    Splasher.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
            
        }
        else if( isOnline() && !ap.getfeed().equals(" ") )
        {

            SPLASH_DISPLAY_LENGTH = 10000;
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() 
                {
                    /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(Splasher.this,MainWaliActi.class);
                    Splasher.this.startActivity(mainIntent);
                    Splasher.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
            
        }
        else
        {
        	Toast.makeText(getApplicationContext(), "Since this is your first launch, please switch on internet.", Toast.LENGTH_LONG).show();
        	finish();
        
        }
        /* New Handler to start the Menu-Activity 
         * and close this Splash-Screen after some seconds.*/
      
    }
    class AttemptLogin extends AsyncTask<String, String, String>  // Thread that sends the data for verification
	{
		boolean failure = false;
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... args)
		{
			int success;
			int isDriver1;
			
			try 
			{
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", String.valueOf(id)));
				Log.d("request!", "starting");
				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);
				// check your log for json response
				// json success tag
				success = json.getInt(TAG_SUCCESS);
				url=json.getString(TAG_URL);
				Log.d("Fetch Successful!", json.toString());
		     
				return json.getString(TAG_MESSAGE);
			}
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(String file_url) 
		{
			ap.setfeed(url);
		//	Toast.makeText(getApplicationContext(), String.valueOf(id), Toast.LENGTH_LONG).show();
			//Toast.makeText(getApplicationContext(), String.valueOf(url), Toast.LENGTH_LONG).show();
		}
}
    public boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
