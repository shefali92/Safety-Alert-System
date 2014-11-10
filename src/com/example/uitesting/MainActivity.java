package com.example.uitesting;

import com.example.uitesting.computations.StorageHandler;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity {

	private boolean firstRun = false;
	private boolean isRecordingRequired = false;
	private StorageHandler sth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testing);
		
		TabHost tabHost = getTabHost();
		 
        // Tab for RecordingMessageActivity
        TabSpec recMessageSpec = tabHost.newTabSpec("Record Message");
        // setting Title and Icon for the Tab
        recMessageSpec.setIndicator("Record Message", getResources().getDrawable(R.drawable.icon_photos_tab));
        Intent photosIntent = new Intent(this, RecordingActivity.class);
        recMessageSpec.setContent(photosIntent);
 
        // Tab for TestMessageActivity
        TabSpec testMessageSpec = tabHost.newTabSpec("Test Message");
        testMessageSpec.setIndicator("Test Message", getResources().getDrawable(R.drawable.icon_photos_tab));
        Intent songsIntent = new Intent(this, TestMessageActivity.class);
        testMessageSpec.setContent(songsIntent);
 
        // Tab for GPSLocationActivity
        TabSpec gpsLocationSpec = tabHost.newTabSpec("GPS Location");
        gpsLocationSpec.setIndicator("GPS Location", getResources().getDrawable(R.drawable.icon_photos_tab));
        Intent videosIntent = new Intent(this, GPSLocationActivity.class);
        gpsLocationSpec.setContent(videosIntent);
        
        // Tab for SendSMSLoggingActivity
        TabSpec sendSmsLoggingSpec = tabHost.newTabSpec("SMS Log");
        sendSmsLoggingSpec.setIndicator("SMS Log", getResources().getDrawable(R.drawable.icon_photos_tab));
        Intent videosIntent1 = new Intent(this, SMS_Activity.class);
        sendSmsLoggingSpec.setContent(videosIntent1);
        
        // Tab for AboutActivity
        TabSpec aboutSpec = tabHost.newTabSpec("About");
        aboutSpec.setIndicator("About", getResources().getDrawable(R.drawable.icon_photos_tab));
        Intent aboutIntent = new Intent(this, AboutActivity.class);
        aboutSpec.setContent(aboutIntent);
 
        // Adding all TabSpec to TabHost
        tabHost.addTab(recMessageSpec); 
        tabHost.addTab(testMessageSpec); 
        tabHost.addTab(gpsLocationSpec); 
        tabHost.addTab(sendSmsLoggingSpec);
        tabHost.addTab(aboutSpec);
        
        sth = StorageHandler.getInstance();
        sth.setMaxIterationsRequired(getResources().getInteger(R.integer.maxIterationsForRecording));
        TypedValue tempVal = new TypedValue();
        getResources().getValue(R.fraction.thresholdMatchingCoeff, tempVal, true);
        sth.setThresholdMatchCoeff(tempVal.getFloat());
        sth.setUpFolderStructure();
        
        isRecordingRequired = StorageHandler.isEmergencySymbolRecorded();
        
        if (!isRecordingRequired)
        {
        	tabHost.getTabWidget().getChildAt(1).setEnabled(false);
        	tabHost.getTabWidget().getChildAt(2).setEnabled(false);
        	tabHost.getTabWidget().getChildAt(3).setEnabled(false);
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
