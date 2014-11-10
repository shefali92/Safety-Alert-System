package com.example.uitesting;

import android.app.Activity;
import android.os.Bundle;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.example.uitesting.R;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class GPSLocationActivity extends Activity {

	private LocationManager locationMangaer = null;
	private LocationListener locationListener = null;
	private boolean validTrigger = false;
	private static final String TAG = "Debug";
	private Boolean flag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gpslocation_layout);

		// if you want to lock screen for always Portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		call_it();
	}

	public void call_it() {

		flag = displayGpsStatus();
		if (flag) {

			// Log.v(TAG, "onClick");

			locationListener = new MyLocationListener();

			locationMangaer.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 2000, 0.2f,
					locationListener);

		} else {
			alertbox("Gps Status!!", "Your GPS is: OFF");
		}

	}

	/*----------Method to Check GPS is enable or disable ------------- */
	private Boolean displayGpsStatus() {
		ContentResolver contentResolver = getBaseContext().getContentResolver();
		boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(
				contentResolver, LocationManager.NETWORK_PROVIDER);
		if (gpsStatus) {
			return true;

		} else {
			return false;
		}
	}

	/*----------Method to create an AlertBox ------------- */
	protected void alertbox(String title, String mymessage) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your Device's GPS is Disable")
				.setCancelable(false)
				.setTitle("** Gps Status **")
				.setPositiveButton("Gps On",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// finish the current activity
								// AlertBoxAdvance.this.finish();
								Intent myIntent = new Intent(
										Settings.ACTION_SECURITY_SETTINGS);
								startActivity(myIntent);
								dialog.cancel();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// cancel the dialog box
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// ---sends an SMS message to another device---

	/*----------Listener class to get coordinates ------------- */
	private class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location loc) {
			if (getParent().getIntent().getBooleanExtra("validTrigger",false))
			{
				
				TableRow tr1 = (TableRow) findViewById(R.id.tableRow1);
				TextView tv1 = (TextView) findViewById(R.id.textView1);
				TableRow tr2 = (TableRow) findViewById(R.id.tableRow2);
				TextView tv2 = (TextView) findViewById(R.id.textView2);
				TableRow tr3 = (TableRow) findViewById(R.id.tableRow3);
				TextView tv3 = (TextView) findViewById(R.id.textView3);
				TableRow tr4 = (TableRow) findViewById(R.id.tableRow4);
				TextView tv4 = (TextView) findViewById(R.id.textView4);
				TextView tv5 = (TextView) findViewById(R.id.textView5);
	
				Toast.makeText(
						getBaseContext(),
						"Location changed : Lat: " + loc.getLatitude() + " Lng: "
								+ loc.getLongitude(), Toast.LENGTH_SHORT).show();
				String longitude = "Longitude: " + loc.getLongitude();
				Log.v(TAG, longitude);
				String latitude = "Latitude: " + loc.getLatitude();
				Log.v(TAG, latitude);
	
				/*----------to get City-Name from coordinates ------------- */
				String cityName = null;
				String zone = null;
				String local = null;
				String countryName = null;
				int check = 0;
				Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
				List<Address> addresses;
				try {
					// TelephonyManager tm =
					// (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
	
					/*
					 * addresses = gcd.getFromLocation(loc.getLatitude(),
					 * loc.getLongitude(), 1);
					 */
	
					/*
					 * addresses = gcd.getFromLocation(loc.getLatitude(),
					 * loc.getLongitude(), 1);
					 */
	
					addresses = gcd.getFromLocation(loc.getLatitude(),
							loc.getLongitude(), 1);
					// if (addresses.size() > 0)
					// System.out.println(addresses.get(0).getLocality());
					check = addresses.get(0).getMaxAddressLineIndex();
					if (check >= 0) {
						local = addresses.get(0).getAddressLine(0);
					}
					cityName = addresses.get(0).getLocality();
					zone = addresses.get(0).getSubLocality();
					countryName = addresses.get(0).getCountryName();
					// id=tm.getDeviceId();
	
					if (local != null || cityName != null || zone != null
							|| countryName != null) {
						String AlertMessage = new String();
						AlertMessage = "Emergency: Help required at " + local
								+ ", " + cityName + ", " + zone + ", " + ", "
								+ countryName;
	
						tv2.setText(cityName);
						tv3.setText(zone);
						tv4.setText(countryName);
	
						String locality = getResources().getString(
								R.string.gpsLocationLocality);
						locality += validate(local);
						tv1.setText(locality);
	
						String city = getResources().getString(
								R.string.gpsLocationCity);
						tv2.setText(city + validate(cityName));
	
						String zoneDistrict = getResources().getString(
								R.string.gpsLocationDistrict);
						tv3.setText(zoneDistrict + validate(zone));
	
						String country = getResources().getString(
								R.string.gpsLocationCountry);
						tv4.setText(country + validate(countryName));
	
						String status = getResources().getString(
								R.string.gpsLocationStatus);
						status = status.replaceAll(": NOT INITIALIZED", "");
						tv5.setText(status
								+ " : TRACKED AND INITIATING SMS SENDING TO EMERGENCY SERVICES");
	
						// get IMEI no.
						TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
						String imei = tm.getDeviceId();
	
						// sendSMS(phoneNo, AlertMessage);
						getParent().getIntent().putExtra(
								"location_IMEI",
								validateForSMS(local) + ", "
										+ validateForSMS(cityName) + ","
										+ validateForSMS(zone) + ","
										+ validateForSMS(countryName) + ", IMEI:"
										+ validateForSMS(imei));
	
						TabActivity tabs = (TabActivity) getParent();
						tabs.getTabHost().setCurrentTab(3);
	
					}

				} catch (IOException e) {
					e.printStackTrace();
					Intent i = new Intent();
					setResult(RESULT_CANCELED, i);
					finish();
				}
			}
		}

		public String validate(String value) {
			if (value == null)
				return "NOT AVAILABLE";
			return value;
		}

		public String validateForSMS(String value) {
			if (value == null)
				return "";
			return value;
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}
}
