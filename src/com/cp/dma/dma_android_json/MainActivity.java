package com.cp.dma.dma_android_json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.cp.dma.dma_android_json.weatherobjects_x.RootObject;
import com.google.gson.Gson;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);




	}



	public void getWeatherForecast(View v) {

		boolean val = this.checkWebConnection(); 
		if (val == false) { 
			Toast.makeText(MainActivity.this, " No Internet Connection",Toast.LENGTH_LONG).show();
		} else {

			String measurementType = "metric";
			EditText txtEnterCityName = (EditText)findViewById(R.id.txtEnterCityName);
			String cityName = txtEnterCityName.getText().toString();

			InlineAsyncTaskGetWeather t = new InlineAsyncTaskGetWeather(cityName,measurementType);
			t.execute();
		}

	}
	private void DisplayInfo(String result){

		Log.d("MainActivity displayInfo: ", result);
		
		TextView txtWeatherForecastOutput = (TextView)findViewById(R.id.txtWeatherForecastOutput);
		txtWeatherForecastOutput.setText(result);

		Gson gson = new Gson();
		RootObject ro = gson.fromJson(result, RootObject.class);

		//		try{
		//			JSONObject jsonObject = new JSONObject(result);
		//			JSONArray arr = new JSONArray(jsonObject.getString("geonames"));
		//			for(int a = 0; a<arr.length(); a++){
		//				JSONObject element = arr.getJSONObject(a);
		//				Toast.makeText(getBaseContext(), element.getString("countryCode")+ ("\n") + element.getString("countryName"),Toast.LENGTH_SHORT).show();
		//			}
		//		}
		//		catch (Exception e) {
		//			e.printStackTrace();
		//		}
	}

	public boolean checkWebConnection() { 
		ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo info = mgr.getActiveNetworkInfo();
		if (info.isConnectedOrConnecting()) {
			if (info.isConnected()) {
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class InlineAsyncTaskGetWeather extends AsyncTask<String, Integer, String> {
//http://openweathermap.org/data/2.5/weather?q=melbourne,au&units=metric
		private String TAG = "AsyncTaskService";
		final static String SERVICE_ADDRESS = "http://openweathermap.org/data/2.5/weather?";
		final static String CITY_KEY_TEXT = "q=";
		final static String MEASUREMENTTYPE_KEY_TEXT = "&units=";
		final static String COUNTRY_CODE_TEXT = ",au";
		final static String CITY_KEY_TEXT_TEST = "melbourne";
		final static String MEASUREMENTTYPE_KEY_TEXT_TEST = "metric";

		private String cityName;
		private String measurementType;

		public InlineAsyncTaskGetWeather(String cityName, String measurementType) {
			this.cityName = cityName;
			this.measurementType = measurementType;
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i(TAG, "doInBackground");

			String address = SERVICE_ADDRESS + CITY_KEY_TEXT + cityName + COUNTRY_CODE_TEXT + MEASUREMENTTYPE_KEY_TEXT + measurementType;		
			// String address = "http://api.geonames.org/findNearbyPlaceNameJSON?lat=47.3&lng=9&username=cprach";
			//String newAddress = "http://openweathermap.org/data/2.5/weather?q=" + cityName + "&units=metric"));";
			StringBuilder response = new StringBuilder();
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(address);
			try {
				HttpResponse execute = client.execute(httpGet);
				InputStream content = execute.getEntity().getContent();

				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String bufferString = "";
				while ((bufferString = buffer.readLine()) != null) {
					response.append(bufferString);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return response.toString();

		}

		@Override
		protected void onPostExecute(String result) {
			Log.i(TAG, "onPostExecute");
			//Log.d("RESULT: ", result);
			DisplayInfo(result);
		}

		//    @Override
		//    protected void onPreExecute() {
		//        Log.i(TAG, "onPreExecute");
		//    }

		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.i(TAG, "onProgressUpdate");

		}


	}

}
