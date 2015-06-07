package com.finale.bif;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Appref {
	private static final String USER_PREFS = "USER_PREFS";
	private SharedPreferences appSharedPrefs;
	private SharedPreferences.Editor prefsEditor;
	private String feed = "feed";
	private String year = "year";
	private String month="month";
	private String date="date";
	private String pos="pos";
	private String url="URL";
	private String txt="txt";
	private String fav="fav";
	private String rated="rated";
	private String shaker="shaker";
    private String cal = "cal";
    private String dat = "dat";
	
    
	public Appref(Context context){
		this.appSharedPrefs = context.getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE);
		this.prefsEditor = appSharedPrefs.edit();
	}
	public String getfeed() {
		return appSharedPrefs.getString(feed, " ");
	}

	public void setfeed(String _user_id) {
		prefsEditor.putString(feed, _user_id).commit();
	}
	public int getyear() {
		return appSharedPrefs.getInt(year, 1985);
	}

	public void setyear(int _user_id) {
		prefsEditor.putInt(year, _user_id).commit();
	}
	public int getmonth() {
		return appSharedPrefs.getInt(month, 11);
	}
	public void setmonth(int monval) {
		prefsEditor.putInt(month, monval).commit();
	}
	public int getdate() {
		return appSharedPrefs.getInt(date, 18);
	}
	public void setdate(int datval) {
		prefsEditor.putInt(date, datval).commit();
	}
	public int getpos() {
		return appSharedPrefs.getInt(pos, 0);
	}
	public void setpos(int posval) {
		prefsEditor.putInt(pos, posval).commit();
	}
	public String geturl()
	{
		return appSharedPrefs.getString(url, "");

	}
	public void seturl(String url1)
	{
		prefsEditor.putString(url, url1).commit();
	}
	public boolean gettxt() {
		return appSharedPrefs.getBoolean(txt, true);
	}
	public void settxt(boolean txtval) {
		prefsEditor.putBoolean(txt, txtval).commit();
	}
	public int getfav() {
		return appSharedPrefs.getInt(fav, 1);
	}
	public void setfav(int f) {
		prefsEditor.putInt(fav, f).commit();
	}
	public boolean getrated() {
		return appSharedPrefs.getBoolean(rated, true);
	}
	public void setrated(boolean rates) {
		prefsEditor.putBoolean(rated, rates).commit();
	}
	public boolean getshaker() {
		return appSharedPrefs.getBoolean(shaker, true);
	}
	public void setshaker(boolean rates) {
		prefsEditor.putBoolean(shaker, rates).commit();
	}
	public boolean getcal() {
		return appSharedPrefs.getBoolean(cal, false);
	}
	public void setcal(boolean cal1) {
		prefsEditor.putBoolean(cal, cal1).commit();
	}
	public String getdat()
	{
		return appSharedPrefs.getString(dat, " ");

	}
	public void setdat(String url1)
	{
		prefsEditor.putString(dat, url1).commit();
	}
}

