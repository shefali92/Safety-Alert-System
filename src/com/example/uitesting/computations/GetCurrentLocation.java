package com.example.uitesting.computations;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.example.uitesting.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
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
import android.widget.Toast;


public class GetCurrentLocation extends Activity implements OnClickListener {

	private LocationManager locationMangaer = null;
	private LocationListener locationListener = null;

	private Button btnGetLocation = null;
	private EditText editLocation = null;
	private ProgressBar pb = null;

	private static final String TAG = "Debug";
	private Boolean flag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maingpsui);

		// if you want to lock screen for always Portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


		locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

	}

	@Override
	public void onClick(View v) {
		flag = displayGpsStatus();
		if (flag) {

			Log.v(TAG, "onClick");

			editLocation
					.setText("Please!! move your device to see the changes in coordinates."
							+ "\nWait..");

			pb.setVisibility(View.VISIBLE);
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
	
	
	 //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber[], String message)
    {        
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
 
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
 
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
 
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));
 
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));        
 
        SmsManager sms = SmsManager.getDefault();
        for(int i=0;i<1;i++)
        {
        sms.sendTextMessage(phoneNumber[i], null, message, sentPI, deliveredPI);
        }
    }   
    
    

	/*----------Listener class to get coordinates ------------- */
	private class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location loc) {

			editLocation.setText("");
			pb.setVisibility(View.INVISIBLE);
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
			String local=null;
			String id=null;
			Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
			List<Address> addresses;
			try {
				TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
				//String strphonenumber = tm.getLine1Number();
				//System.out.println(strphonenumber);
				
				addresses = gcd.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
				if (addresses.size() > 0)
					System.out.println(addresses.get(0).getLocality());
				local = addresses.get(0).getLocality();
				cityName = addresses.get(0).getCountryName();
				id=tm.getDeviceId();
				
				if(local!=null || id!=null)
				{String AlertMessage=new String();
				AlertMessage = "Emergency: Help required at " + local + " " + cityName+" IMEI is "+id;
				String[] phoneNo = new String[1];
                for(int i=0;i<1;i++)
                {phoneNo[i]=new String();}
				//System.out.println(cityName);
				phoneNo[0]="+919772060206";
				/*phoneNo[1]="+918233592975";
				phoneNo[2]="+919460564658";
				phoneNo[3]="+919772059336";
				phoneNo[4]="+919782844060";*/
                sendSMS(phoneNo, AlertMessage);
                
                Intent i = new Intent();
                setResult(RESULT_OK,i);
                finish();
				}
				
				
				
			} catch (IOException e) {
				e.printStackTrace();
				Intent i = new Intent();
                setResult(RESULT_CANCELED,i);
                finish();
			}

			String s = longitude + "\n" + latitude
					+ "\n\nMy Currrent City is: " + cityName;
			editLocation.setText(s);
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