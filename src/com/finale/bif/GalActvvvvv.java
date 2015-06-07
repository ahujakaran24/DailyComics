package com.finale.bif;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class GalActvvvvv extends Activity {

	private File root;
	private ArrayList<File> fileList = new ArrayList<File>();
	private LinearLayout view;
	private int posglo=0;
	private String path = "CAHRerun";
	private Bitmap mPlaceHolderBitmap;
	String r;
	private LruCache<String, Bitmap> mMemoryCache;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.gally);
		Intent intent = getIntent();
		//   view = (LinearLayout) findViewById(R.id.view);

		//getting SDcard root path
		root = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath(),path);
	//	Toast.makeText(getApplicationContext(), String.valueOf(root), Toast.LENGTH_LONG).show();
		if (!root.exists()) {
			if (!root.mkdirs()) {
				Toast.makeText(getApplicationContext(), "Unable to create Comics", Toast.LENGTH_SHORT).show();
			}
			else 
			{
				Toast.makeText(getApplicationContext(), "Comics Folder created", Toast.LENGTH_LONG).show();
			}
		}

		getfile(root);


		if(fileList.size()==0)
		{
			Toast.makeText(getApplicationContext(), "No Comic in this folder", Toast.LENGTH_LONG).show();
		}

		Gallery g = (Gallery) findViewById(R.id.gallery1);
		g.setAdapter(new ImageAdapter(this));
		g.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(@SuppressWarnings("rawtypes") AdapterView parent, View v, int position, long id) {
			//	Intent i = new Intent(getApplicationContext(),ZoomView.class);
			//	i.putExtra("uri",root+"/"+fileList.get(position).getName());
			//	startActivity(i);
			}
		});
		g.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {posglo=position;}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};
	}
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent i = new Intent(getApplicationContext(),FolderView.class);
			startActivity(i);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}*/
	public ArrayList<File> getfile(File dir) {
		File listFile[] = dir.listFiles();
		if (listFile != null && listFile.length > 0) {
			for (int i = 0; i < listFile.length; i++) {


				if (listFile[i].getName().endsWith(".png")
						|| listFile[i].getName().endsWith(".jpg")
						|| listFile[i].getName().endsWith(".jpeg")
						|| listFile[i].getName().endsWith(".gif"))

				{
					fileList.add(listFile[i]);

				}


			}
		}
		return fileList;
	}
	public class ImageAdapter extends BaseAdapter {
		MainWaliActi m = new MainWaliActi();
		int mGalleryItemBackground;
		private Context mContext;
		public ImageAdapter(Context c) {
			mContext = c;
			TypedArray a = obtainStyledAttributes(R.styleable.HellGallery);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.HellGallery_android_galleryItemBackground, 0);
			a.recycle();
		}

		public int getCount() {
			return fileList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) 
		{

			ImageView i1 = new ImageView(mContext);

			loadBitmap (root+"/"+fileList.get(position).getName(),i1);
			return i1;


		}
	}

	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private String data ;

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(String... params) {
			data = params[0];
			final BitmapFactory.Options options = new BitmapFactory.Options();
			final double height = options.outHeight;
			final double width = options.outWidth;
			final Bitmap bitmap= decodeSampledBitmapFromResource( data, width, height);
			addBitmapToMemoryCache(String.valueOf(data), bitmap);
			return bitmap;
		}
		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {  if (isCancelled()) {
			bitmap = null;
		}

		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			final BitmapWorkerTask bitmapWorkerTask =
					getBitmapWorkerTask(imageView);
			if (this == bitmapWorkerTask && imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}}
	}
	public static Bitmap decodeSampledBitmapFromResource( String resId,
			double reqWidth, double reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(resId, options);

		// Calculate inSampleSize
		options.inSampleSize = 1;//calculateInSampleSize(options, reqWidth, reqHeight);
		options.inDensity=1;
		options.inTargetDensity=1;
		options.inScaled=true;
		final int height = options.outHeight;
		final int width = options.outWidth;
		if(height>reqHeight||width>reqWidth)
		{
			options.inSampleSize=1;
		}
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile( resId, options);
	}
	public static int calculateInSampleSize(
			BitmapFactory.Options options, double reqWidth, double reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap,
				BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference =
					new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}
	public void loadBitmap(String resId, ImageView imageView) {

		final String imageKey = String.valueOf(resId);
		final Bitmap bitmap = getBitmapFromMemCache(imageKey);

		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			if (cancelPotentialWork(resId, imageView)) {
				final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
				mPlaceHolderBitmap = BitmapFactory.decodeFile(resId);
				final AsyncDrawable asyncDrawable =
						new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
				imageView.setImageDrawable(asyncDrawable);
				task.execute(resId);
			}
		}


	}
	public static boolean cancelPotentialWork(String data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final String bitmapData = bitmapWorkerTask.data;
			if (bitmapData != data) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was cancelled
		return true;
	}
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}
}



