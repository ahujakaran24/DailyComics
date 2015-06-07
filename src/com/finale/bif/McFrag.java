package com.finale.bif;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class McFrag extends Fragment {
	public static final String ARG_OBJECT = "object";
	public static WebView webView;
	static Appref ap;
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		ap=new Appref(getActivity());
		View rootView = inflater.inflate(R.layout.fragged, container, false);
		//    ((TextView) rootView.findViewById(android.R.id.text1)).setText(Integer.toString(args.getInt(ARG_OBJECT)));
		return rootView;
	}	
}
