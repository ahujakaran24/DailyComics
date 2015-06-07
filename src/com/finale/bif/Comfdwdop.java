package com.finale.bif;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class Comfdwdop extends Activity {
	EditText e1,e2,e3;
	int CY,CM,CD,CYtemp;
	int flag1=0, val,flag2=0,flag3=0;
	Button b;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cal);
		b=(Button)findViewById(R.id.b1);



		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DatePicker datePicker;
				datePicker = (DatePicker) findViewById(R.id.dateselector);
				int   day  = datePicker.getDayOfMonth();
				int   month= datePicker.getMonth()+1;
				int   year = datePicker.getYear();

				if((year<1986||year>2014))
				{
					flag1=0;
					Toast.makeText(getApplicationContext(), "Keep Year between 1986-2014 ", Toast.LENGTH_LONG).show();
				}
				else 
				{
					flag1=1;
					CY=year;
					CD=day;
					CM=month;
				}

				if(flag1==1)
				{
					sahihai(CY,CD,CM);
				}
			}
		});



	}
	@Override
	public void onResume()
	{
		super.onResume();
	}
	protected void sahihai(int year,int day,int month) {
		Context context = getApplicationContext();
		Appref appPrefs = new Appref(context);

		appPrefs.setyear(year);
		appPrefs.setmonth(month);
		appPrefs.setdate(day);

		appPrefs.setcal(true);


		finish();

	}



}
