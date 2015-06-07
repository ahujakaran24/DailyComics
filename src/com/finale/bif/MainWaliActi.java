package com.finale.bif;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainWaliActi extends FragmentActivity {
	int id = 1;
	Fragment fragment = new McFrag();
	String dat;
	ImageButton forward,bck,rewind,fforward,shuffle; 
	WebSettings wS ;
	static Appref ap;
	int previous=0,current=0,temp=0;
	ProgressBar progressBar;
	ViewPager myPager;
	WebView webView;
	String url;
	AsyncTaskRunner ref=new AsyncTaskRunner();
	TextView t;
	int year1=1985;
	int month1=11;
	int  date1=16;
	final String LOGIN_URL = "http://198.12.152.47/~ahujakaran24/webservice/get_url.php";
	JsonBhai jsonParser = new JsonBhai();
	final String TAG_SUCCESS = "success";
	final String TAG_MESSAGE = "message";
	final String TAG_URL = "url";
	int count;
	int f=0;
	static final  String PREFS_Zoom = "PREFS_Zoom";
	private String zoomlevel;
	private int Default_zoomlevel=100;
	FileOutputStream out;
	String img_url="";
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.mainer);
		ap=new Appref(getApplicationContext());
		bck=(ImageButton)findViewById(R.id.imageButton1);
		rewind=(ImageButton)findViewById(R.id.imageButton2);
		shuffle=(ImageButton)findViewById(R.id.imageButton3);
		fforward=(ImageButton)findViewById(R.id.imageButton5);
		forward=(ImageButton)findViewById(R.id.imageButton4);
		count=ap.getfav();
		img_url=ap.getfeed()+year1+"/"+month1+"/"+date1;
		if(count==1)
		{
			Intent i= new Intent(getApplicationContext(),ComFedtr.class);
			startActivity(i);
			ap.setfav(100);
		}



		/*****************************************LISTENERS FOR IMAGEBUTTONS********************************/

		bck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SaveZoom();
				loadwebviewbck();
			}
		});
		forward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SaveZoom();
				loadwebviewforward();
			}
		});
		rewind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SaveZoom();
				loadwebviewstart();
			}
		});
		fforward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SaveZoom();
				loadwebviewend();
			}
		});
		shuffle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SaveZoom();
				
				loadwebviewshuffle();
			}
		});

		/*********************************END LISTENERS***********************************************/
		File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CAHRerun");
		boolean success = true;
		if (!folder.exists()) 
		{
			success = folder.mkdir();
		}
		if (success) {
			//	Toast.makeText(getApplicationContext(), "WebcomicPlus Folder has been created", Toast.LENGTH_LONG).show();
		} else {
			// Do something else on failure 
		}
		ap.setfeed("http://www.gocomics.com/calvinandhobbes/");
		startvp();

	}
	@Override
	public void onResume()
	{
		super.onResume();
		startvp();
	}
	public void startvp()
	{
		MyPagerAdapter adapter = new MyPagerAdapter();
		myPager = (ViewPager) findViewById(R.id.pager);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(0);
		myPager.setPageTransformer(true, new ZoomOutPageTransformer());
		//myPager.setOffscreenPageLimit(20);
		myPager.setOnPageChangeListener(
				new ViewPager.SimpleOnPageChangeListener()
				{
					@Override
					public void onPageSelected(int position) 
					{	
						if(position!=0)
						{
							webView=(WebView)findViewById(R.id.mywebview);
							progressBar=(ProgressBar)findViewById(R.id.progressBar1);
							t=(TextView)findViewById(R.id.textView1);
						}
					}
				});
		// Create a tab listener that is called when the user changes tabs.
		//ref.execute();
		//	loadwebview();

	}
	private class MyPagerAdapter extends PagerAdapter {
		public int getCount()
		{
			return 1000;
		}

		public Object instantiateItem(ViewGroup container, int position) 
		{

			previous=current;
			current=position;
			LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
			// Using different layouts in the view pager instead of images.
			int resId = -1;
			//Getting my layout's in my adapter. Three layouts defined.
			resId = R.layout.fragged;
			View view = inflater.inflate(resId, container, false);
			((ViewPager) container).addView(view, 0);
			if(position==0)
			{
				webView=(WebView)findViewById(R.id.mywebview);
				progressBar=(ProgressBar)findViewById(R.id.progressBar1);
				t=(TextView)findViewById(R.id.textView1);
				loadwebview();
			}

			if(previous<=current)
			{
				SaveZoom();
				loadwebviewforward();
				//	Toast.makeText(getApplicationContext(), String.valueOf(previous)+"<-prev cur->"+String.valueOf(current), Toast.LENGTH_LONG).show();
			}
			else
			{
				SaveZoom();
				loadwebviewbck();
				//	Toast.makeText(getApplicationContext(), "bck called", Toast.LENGTH_LONG).show();
			}

			return view;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}
	private class AsyncTaskRunner extends AsyncTask<String, String, String> {
		private String resp;
		//final ApPrefs appPrefs = new ApPrefs(getApplicationContext());
		@Override
		protected String doInBackground(String... params) {
			if(!isCancelled())
			{
				Document doc = null;
				try {
					int i =0;
					doc = Jsoup.connect(img_url).get();
					Elements elements = doc.getAllElements();
					for(Element element : elements) {

						if(element.hasAttr("src"))
						{
							i++;
							if(i==8)
							{
								dat = element.attr("src");
								Log.d("dat",dat);
								//	Toast.makeText(getApplicationContext(), dat.toString(), Toast.LENGTH_LONG).show();
							}
						}		
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			ap.setdat(dat);
			if(!isCancelled())
			{
				GetZoom();
				webView.setInitialScale(Default_zoomlevel);
				webView.loadUrl(ap.getdat());
				webView.setWebChromeClient(new WebChromeClient() {
					public void onProgressChanged(WebView view, int progress) {
						progressBar.setVisibility(View.VISIBLE);
						progressBar.setProgress(progress);
					}
				});
				webView.setWebViewClient(new WebViewClient(){
					public void onPageFinished(WebView view, String url)
					{
						progressBar.setProgress(0);
						progressBar.setVisibility(View.INVISIBLE);
					}   
				});
			}
		}
	}

	/****************FUNCTIONS TO NAVIGATE**************************/

	public synchronized void loadwebview()
	{

		if (!isOnline() ) {
			Toast.makeText(getApplicationContext(), "Please turn on Wi-Fi/3g", Toast.LENGTH_LONG).show();
		} 
		t=(TextView)findViewById(R.id.textView1);
		int year1,month1,date1;
		year1=ap.getyear();
		month1=ap.getmonth();
		date1=ap.getdate();
		t.setText(String.valueOf(month1)+"-"+String.valueOf(date1)+"-"+String.valueOf(year1));
		img_url=ap.getfeed()+year1+"/"+month1+"/"+date1;

		webView.setOnTouchListener(new View.OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) { 
				return false;
			}
		});
		wS= webView.getSettings();
		webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		wS.setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB
		wS.setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
		wS.setAllowFileAccess( true );
		wS.setAppCacheEnabled( true );
		wS.setJavaScriptEnabled( true );
		wS.setCacheMode( wS.LOAD_DEFAULT ); // load online by default
		//	if (!isOnline(c) ) { // loading offline
		//		wS.setCacheMode( wS.LOAD_CACHE_ELSE_NETWORK );
		//	}
		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setInitialScale(110);
		wS.setUseWideViewPort(true);
		progressBar.setVisibility(View.VISIBLE);
		ref.cancel(true);
		ref=new AsyncTaskRunner();
		ref.execute();
		//	Toast.makeText(getApplicationContext(), dat.toString(), Toast.LENGTH_LONG).show();
	}
	public synchronized void loadwebviewforward()
	{
		if (!isOnline() ) {
			//	Toast.makeText(getApplicationContext(), "Please turn on Wi-Fi/3g", Toast.LENGTH_LONG).show();
		} 
		if(ap.getmonth()==12&&ap.getdate()==31)
		{
			year1=ap.getyear()+1;
			month1=1;
			date1=1;
		}
		else if(ap.getdate()==30&&ap.getmonth()!=12)
		{
			year1=ap.getyear();
			month1=ap.getmonth()+1;
			date1=1;
		}
		else
		{
			year1=ap.getyear();
			month1=ap.getmonth();
			date1=ap.getdate()+1;
		}
		ap.setyear(year1);
		ap.setmonth(month1);
		ap.setdate(date1);
		t.setText(String.valueOf(month1)+"-"+String.valueOf(date1)+"-"+String.valueOf(year1));
		img_url=ap.getfeed()+year1+"/"+month1+"/"+date1;
		webView.setOnTouchListener(new View.OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) { 
				return false;
			}
		});
		wS= webView.getSettings();

		webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		wS.setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB
		wS.setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
		wS.setAllowFileAccess( true );
		wS.setAppCacheEnabled( true );
		wS.setJavaScriptEnabled( true );
		wS.setCacheMode( wS.LOAD_DEFAULT ); // load online by default
		//	if (!isOnline(c) ) { // loading offline
		//		wS.setCacheMode( wS.LOAD_CACHE_ELSE_NETWORK );
		//	}
		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setInitialScale(110);
		wS.setUseWideViewPort(true);
		progressBar.setVisibility(View.VISIBLE);
		ref.cancel(true);
		ref=new AsyncTaskRunner();
		ref.execute(); 
		//	Toast.makeText(getApplicationContext(), dat.toString(), Toast.LENGTH_LONG).show();
	}
	public synchronized void loadwebviewbck()
	{
		if (!isOnline() ) {
			//	Toast.makeText(getApplicationContext(), "Please turn on Wi-Fi/3g", Toast.LENGTH_LONG).show();
		} 
		if(ap.getmonth()==1&&ap.getdate()==1)
		{
			year1=ap.getyear()-1;
			month1=12;
			date1=31;
		}
		else if(ap.getdate()==1)
		{
			year1=ap.getyear();
			month1=ap.getmonth()-1;
			date1=30;
		}
		else
		{
			year1=ap.getyear();
			month1=ap.getmonth();
			date1=ap.getdate()-1;
		}
		ap.setyear(year1);
		ap.setmonth(month1);
		ap.setdate(date1);
		t.setText(String.valueOf(month1)+"-"+String.valueOf(date1)+"-"+String.valueOf(year1));
		img_url=ap.getfeed()+year1+"/"+month1+"/"+date1;

		webView.setOnTouchListener(new View.OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) { 
				return false;
			}
		});
		wS= webView.getSettings();
		webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		wS.setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB
		wS.setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
		wS.setAllowFileAccess( true );
		wS.setAppCacheEnabled( true );
		wS.setJavaScriptEnabled( true );
		wS.setCacheMode( wS.LOAD_DEFAULT ); // load online by default
		//	if (!isOnline(c) ) { // loading offline
		//		wS.setCacheMode( wS.LOAD_CACHE_ELSE_NETWORK );
		//	}

		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		//webView.set
		webView.getSettings().setBuiltInZoomControls(true);
		wS.setUseWideViewPort(true);
		webView.setInitialScale(100);
		progressBar.setVisibility(View.VISIBLE);

		ref.cancel(true);
		ref=new AsyncTaskRunner();
		ref.execute(); 
	}
	public synchronized void loadwebviewshuffle()
	{
		if (!isOnline() ) {
			//		Toast.makeText(getApplicationContext(), "Please turn on Wi-Fi/3g", Toast.LENGTH_LONG).show();
		} 
		f++;
		Random r = new Random();
		year1=r.nextInt(1995-1985+1) + 1985;
		int year2=r.nextInt(2013-2007+1) + 2007;
		month1=r.nextInt(12-1+1)+1;
		date1=r.nextInt(30-1+1)+1;
		if(f%2==0)
		{
			ap.setyear(year1);
			ap.setmonth(month1);
			ap.setdate(date1);
			t.setText(String.valueOf(month1)+"-"+String.valueOf(date1)+"-"+String.valueOf(year1));
			img_url=ap.getfeed()+year1+"/"+month1+"/"+date1;
		}
		if(f%2==1)
		{
			ap.setyear(year2);
			ap.setmonth(month1);
			ap.setdate(date1);
			t.setText(String.valueOf(month1)+"-"+String.valueOf(date1)+"-"+String.valueOf(year2));
			img_url=ap.getfeed()+year2+"/"+month1+"/"+date1;

		}
		webView.setOnTouchListener(new View.OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) { 
				return false;
			}
		});
		wS= webView.getSettings();
		webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		wS.setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB
		wS.setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
		wS.setAllowFileAccess( true );
		wS.setAppCacheEnabled( true );
		wS.setJavaScriptEnabled( true );
		wS.setCacheMode( wS.LOAD_DEFAULT ); // load online by default
		//	if (!isOnline(c) ) { // loading offline
		//		wS.setCacheMode( wS.LOAD_CACHE_ELSE_NETWORK );
		//	}
		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		//webView.set
		webView.getSettings().setBuiltInZoomControls(true);
		wS.setUseWideViewPort(true);
		webView.setInitialScale(100);
		progressBar.setVisibility(View.VISIBLE);

		ref.cancel(true);
		ref=new AsyncTaskRunner();
		ref.execute(); 

	}
	public synchronized void loadwebviewend()
	{
		if (!isOnline() ) {
			//Toast.makeText(getApplicationContext(), "Please turn on Wi-Fi/3g", Toast.LENGTH_LONG).show();
		} 
		Calendar now = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		String nowDate = formatter.format(now.getTime());
		String[] separateCurrentDate = nowDate.split("-");
		String year = separateCurrentDate[0];
		String month = separateCurrentDate[1];
		String day = separateCurrentDate[2];
		year1 = Integer.parseInt(year);
		month1 = Integer.parseInt(month);
		date1 = Integer.parseInt(day);
		ap.setyear(year1);
		ap.setmonth(month1);
		ap.setdate(date1);
		t.setText(String.valueOf(month1)+"-"+String.valueOf(date1)+"-"+String.valueOf(year1));
		img_url=ap.getfeed()+year1+"/"+month1+"/"+date1;
		webView.setOnTouchListener(new View.OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) { 
				return false;
			}
		});
		wS= webView.getSettings();
		webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		wS.setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB
		wS.setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
		wS.setAllowFileAccess( true );
		wS.setAppCacheEnabled( true );
		wS.setJavaScriptEnabled( true );
		wS.setCacheMode( wS.LOAD_DEFAULT ); // load online by default
		//	if (!isOnline(c) ) { // loading offline
		//		wS.setCacheMode( wS.LOAD_CACHE_ELSE_NETWORK );
		//	}
		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		//webView.set
		webView.getSettings().setBuiltInZoomControls(true);
		wS.setUseWideViewPort(true);
		webView.setInitialScale(100);
		progressBar.setVisibility(View.VISIBLE);

		ref.cancel(true);
		ref=new AsyncTaskRunner();
		ref.execute(); 
	}
	public synchronized void loadwebviewstart()
	{
		if (!isOnline() ) {
			//	Toast.makeText(getApplicationContext(), "Please turn on Wi-Fi/3g", Toast.LENGTH_LONG).show();
		} 
		year1=1985;
		month1=11;
		date1=18;
		ap.setyear(year1);
		ap.setmonth(month1);
		ap.setdate(date1);
		t.setText(String.valueOf(month1)+"-"+String.valueOf(date1)+"-"+String.valueOf(year1));
		img_url=ap.getfeed()+year1+"/"+month1+"/"+date1;
		webView.setOnTouchListener(new View.OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) { 
				return false;
			}
		});
		wS= webView.getSettings();
		webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		wS.setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB
		wS.setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
		wS.setAllowFileAccess( true );
		wS.setAppCacheEnabled( true );
		wS.setJavaScriptEnabled( true );
		wS.setCacheMode( wS.LOAD_DEFAULT ); // load online by default
		//	if (!isOnline(c) ) { // loading offline
		//		wS.setCacheMode( wS.LOAD_CACHE_ELSE_NETWORK );
		//	}
		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		//webView.set
		webView.getSettings().setBuiltInZoomControls(true);
		wS.setUseWideViewPort(true);
		webView.setInitialScale(100);
		progressBar.setVisibility(View.VISIBLE);

		ref.cancel(true);
		ref=new AsyncTaskRunner();
		ref.execute(); 
	}
	/****************END FUNCTIONS TO NAVIGATE**************************/
	/****ANIMATOR****/
	public class ZoomOutPageTransformer implements ViewPager.PageTransformer  // Animate swipes
	{
		private static final float MIN_SCALE = 0.85f;
		private static final float MIN_ALPHA = 0.5f;
		public void transformPage(View view, float position) 
		{  //Code to animater transitions of fragments
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();
			if (position < -1) { 
				view.setAlpha(0);
			}
			else if (position <= 1) 
			{ 
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horzMargin = pageWidth * (1 - scaleFactor) / 2;
				if (position < 0)
				{
					view.setTranslationX(horzMargin - vertMargin / 2);
				} 
				else 
				{
					view.setTranslationX(-horzMargin + vertMargin / 2);
				}
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);
				view.setAlpha(MIN_ALPHA +(scaleFactor - MIN_SCALE) /(1 - MIN_SCALE) * (1 - MIN_ALPHA));
			} else
			{ 
				view.setAlpha(0);
			}
		}
	}
	/****************MENU OPTIONS************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mainabcdl, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items

		switch (item.getItemId()) {
		case R.id.feeed:
			if(isOnline())
			{
				CharSequence colors[] = new CharSequence[] {"C&H", "Garfield", "Doonesbury", "Non Sequitur","Peanuts", "FoxTrot", "Pearls Before Swine", "Dilbert Classics","Luann","Big Nate","For Better or For Worse","Stone Soup","Marmaduke","The Argyle Sweater","Cul de Sac","Jump Start","Close To Home","Rose is Rose","F minus","Brevity","Eric The Circle","9 Chicweed Lane","Red and Rover","B.C.","Wizard of ID","Last Kiss","Basic Instructions","Dark Side of the Horse","The K Chronicles","Cathy","Ziggy","Herman","Pickles","Get Fuzzy","Adam@Home","That is Priceless","Dick Tracy","PibGorn"};

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Choose Comic");
				builder.setItems(colors, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which)
						{
						case 0:
							ap.setfeed("http://www.gocomics.com/calvinandhobbes/");
							break;
						case 1:
							ap.setfeed("http://www.gocomics.com/garfield/");
							break;
						case 2:
							ap.setfeed("http://www.gocomics.com/doonesbury/");
							break;
						case 3:
							ap.setfeed("http://www.gocomics.com/nonsequitur/");
							break;
						case 4:
							ap.setfeed("http://www.gocomics.com/peanuts/");
							break;
						case 5:
							ap.setfeed("http://www.gocomics.com/foxtrot/");
							break;
						case 6:
							ap.setfeed("http://www.gocomics.com/pearlsbeforeswine/");
							break;
						case 7:
							ap.setfeed("http://www.gocomics.com/dilbert-classics/");
							break;
						case 8:
							ap.setfeed("http://www.gocomics.com/luann/");
							break;
						case 9:
							ap.setfeed("http://www.gocomics.com/bignate/");
							break;
						case 10:ap.setfeed("http://www.gocomics.com/forbetterorforworse/");
							break;
						case 11:ap.setfeed("http://www.gocomics.com/stonesoup/");
							break;
						case 12:ap.setfeed("http://www.gocomics.com/theargylesweater/");
							break;
						case 13:ap.setfeed("http://www.gocomics.com/culdesac/");
							break;
						case 14:ap.setfeed("http://www.gocomics.com/jumpstart/");
							break;
						case 15:ap.setfeed("http://www.gocomics.com/closetohome/");
							break;
						case 16:ap.setfeed("http://www.gocomics.com/marmaduke/");
							break;
						case 17:ap.setfeed("http://www.gocomics.com/roseisrose/");
							break;
						case 18:ap.setfeed("http://www.gocomics.com/fminus/");
							break;
						case 19:ap.setfeed("http://www.gocomics.com/brevity/");
							break;
						case 20:ap.setfeed("http://www.gocomics.com/eric-the-circle/");
							break;
						case 21:ap.setfeed("http://www.gocomics.com/9chickweedlane/");
							break;
						case 22:ap.setfeed("http://www.gocomics.com/redandrover/");
							break;
						case 23:ap.setfeed("http://www.gocomics.com/bc/");
							break;
						case 24:ap.setfeed("http://www.gocomics.com/wizardofid/");
							break;
						case 25:ap.setfeed("http://www.gocomics.com/lastkiss/");
							break;
						case 26:ap.setfeed("http://www.gocomics.com/basicinstructions/");
							break;
						case 27:ap.setfeed("http://www.gocomics.com/darksideofthehorse/");
							break;
						case 28:ap.setfeed("http://www.gocomics.com/thekchronicles/");
							break;
						case 29:ap.setfeed("http://www.gocomics.com/herman/");
							break;
						case 30:ap.setfeed("http://www.gocomics.com/pickles/");
							break;
						case 31:ap.setfeed("http://www.gocomics.com/getfuzzy/");
							break;
						case 32:ap.setfeed("http://www.gocomics.com/adamathome/");
							break;
						case 33:ap.setfeed("http://www.gocomics.com/that-is-priceless/");
							break;
						case 34:ap.setfeed("http://www.gocomics.com/dicktracy/");
							break;
						case 35:ap.setfeed("http://www.gocomics.com/dinosaur-comics/");
							break;
						case 36:ap.setfeed("http://www.gocomics.com/pibgorn/");
							break;
						case 37:ap.setfeed("http://www.gocomics.com/frazz/");
							break;
						case 38:ap.setfeed("http://www.gocomics.com/lio/");
							break;
						}
						startvp();
					}
				});
				builder.show();

			}
			else
			{
				Toast.makeText(getApplicationContext(), "Disconnected from the internet!", Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.clndr:
			Intent i= new Intent(getApplicationContext(),Comfdwdop.class);
			startActivity(i);
			return true;
		case R.id.dwnloadd:
			new RetrieveFeedTask().execute();
			return true;
		case R.id.glry:
			Intent i1 = new Intent(getApplicationContext(),GalActvvvvv.class);
			startActivity(i1);
			return true;
		case R.id.shr:
			if (!isOnline() ) {
				Toast.makeText(getApplicationContext(), "Please turn on Wi-Fi/3g", Toast.LENGTH_LONG).show();
			} 
			else
				new ShareTask().execute();
			return true;
		case R.id.hlp:
			Intent i3= new Intent(getApplicationContext(),ComFedtr.class);
			startActivity(i3);
			return true;

		case R.id.immrsve:
			Intent i4= new Intent(getApplicationContext(),ImmerseHoJa.class);
			startActivity(i4);
			return true;

		case R.id.rte:


			//Amazon :
			final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
		//	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + appPackageName)));
			//Google Play :
			//final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
			//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
			try {
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
			} catch (android.content.ActivityNotFoundException anfe) {
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
/*	class AttemptLogin extends AsyncTask<String, String, String>  // Thread that sends the data for verification
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
			Log.d("URL--------->",file_url);
			ap.setfeed(url);
			startvp();
	
			//Toast.makeText(getApplicationContext(), String.valueOf(id), Toast.LENGTH_LONG).show();
			//Toast.makeText(getApplicationContext(), String.valueOf(url), Toast.LENGTH_LONG).show();
		}
	}*/

	/***************DOWNLOAD TASK********************/

	class RetrieveFeedTask extends AsyncTask<Void, Void, Void> {
		//	ProgressDialog dialog = null;


		protected void onPostExecute() {

			super.onPostExecute(null);
			//		dialog.dismiss();
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			//dialog  = new ProgressDialog (MainActivity.this);
			//	dialog.setMessage("Downloading image to /CAHRerun");
			//	dialog.show();
			Toast.makeText(getApplicationContext(), "Downloading image to /CAHRerun", Toast.LENGTH_LONG).show();
		}
		@Override
		protected Void doInBackground(Void... params) {

			try {
				URL url = new URL(ap.getdat());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				if(myBitmap!=null)
				{
					try {
						File sdCard = Environment.getExternalStorageDirectory();
						File dir = new File (sdCard.getAbsolutePath() + "/CAHRerun");
						Random generator = new Random();
						StringBuilder randomStringBuilder = new StringBuilder();
						int randomLength = generator.nextInt(8);
						char tempChar;
						for (int i = 0; i < randomLength; i++){
							tempChar = (char) (generator.nextInt(96) + 32);
							randomStringBuilder.append(tempChar);
						}
						String s =randomStringBuilder.toString()+".png";
						File file = new File(dir,s);
						out = new FileOutputStream(file);
						myBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try{
							out.close();
						} catch(Throwable ignore) {}
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

	}
	/***************DOWNLOAD TASK for share********************/

	class ShareTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog = null;


		protected void onPostExecute() {

			super.onPostExecute(null);

			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//dialog.cancel();
			//sharei();
		}

		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(MainWaliActi.this);
			pDialog.setMessage("loading apps..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}
		@Override
		protected Void doInBackground(Void... params) {

			try {
				URL url = new URL(ap.getdat());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				if(myBitmap!=null)
				{
					try {
						File sdCard = Environment.getExternalStorageDirectory();
						File dir = new File (sdCard.getAbsolutePath());
						String s ="temp_image_gigabyte.jpeg";
						File file = new File(dir,s);
						if(file.exists()) file.delete();
						out = new FileOutputStream(file);
						myBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
						Intent share = new Intent(Intent.ACTION_SEND);
						share.setType("image/jpeg");
						share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temp_image_gigabyte.jpeg"));
						startActivity(Intent.createChooser(share, "Share Image"));
						pDialog.cancel();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try{
							//		out.close();
						} catch(Throwable ignore) {}
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
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

	private void GetZoom(){
		try{
			SharedPreferences settings = getSharedPreferences(PREFS_Zoom,0);
			zoomlevel = settings.getString("zoom_level","");     

			if (zoomlevel.length() >0)   
			{
				Default_zoomlevel = Integer.parseInt(zoomlevel); 
				//		Toast.makeText(getApplicationContext(), ">0", Toast.LENGTH_LONG).show();
			}
			else
			{
				Default_zoomlevel =100;
				//		Toast.makeText(getApplicationContext(), "100", Toast.LENGTH_LONG).show();
			}
		}catch(Exception ex){
			Log.e("******ZOOM ! ", "Exception GetZoom()  ::"+ex.getMessage());          
		}
	}
	private void SaveZoom(){
		try{
			//Toast.makeText(getApplicationContext(), "save zoom", Toast.LENGTH_LONG).show();
			SharedPreferences settings = getSharedPreferences(PREFS_Zoom,0);            
			SharedPreferences.Editor editor = settings.edit();    
			Default_zoomlevel = (int) (webView.getScale() *100);
			editor.putString("zoom_level",""+ Default_zoomlevel);   
			editor.commit();

		}catch(Exception ex){
			Log.e("******ZOOM ! ", "Exception SaveZoom()  ::"+ex.getMessage());    
			//Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}





}
