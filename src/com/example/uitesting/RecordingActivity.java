package com.example.uitesting;

import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow.LayoutParams;

import com.example.uitesting.computations.StorageHandler;

public class RecordingActivity extends Activity {
	private int maxIterationsRequired = 0;
	private boolean isRecordingRequired = false;
	private StorageHandler sth;
	private int RECORDER_SUBACTIVITY = 1;
	private int currentRecorded = 0;
	private Button[] bArr;
	private OnClickListener ocl;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordingcontainer);
		FrameLayout recContainer = (FrameLayout) findViewById(R.id.recordingcontainer);
		
		recContainer.addView(getLayoutInflater().inflate(R.layout.recording_layout,null));
		recContainer.addView(getLayoutInflater().inflate(R.layout.recordersubactivity,null));
		
		final ImageButton recordButton = (ImageButton) findViewById(R.id.imageButton1);
		int recordCounterMessage = R.string.recordCounterMsg;
		sth = StorageHandler.getInstance();
		
		ocl = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				findViewById(R.id.recordersubactivity).setVisibility(View.VISIBLE);
				findViewById(R.id.recording_layout).setVisibility(View.INVISIBLE);
				
				TableLayout tlayout = (TableLayout) findViewById(R.id.recorderSA_tblLayout);
				tlayout.setLayoutParams(new LayoutParams(
						android.widget.TableLayout.LayoutParams.MATCH_PARENT,
						android.widget.TableLayout.LayoutParams.WRAP_CONTENT));
				maxIterationsRequired = getResources().getInteger(
						R.integer.maxIterationsForRecording);
				
				final Button bRecDone = (Button) findViewById(R.id.Button01);
				Button bRec2 = (Button) findViewById(R.id.Button06);
				Button bRec3 = (Button) findViewById(R.id.Button05);
				Button bRec4 = (Button) findViewById(R.id.Button04);
				Button bRec5 = (Button) findViewById(R.id.Button03);
				Button bRec6 = (Button) findViewById(R.id.Button02);
				bArr = new Button[maxIterationsRequired];
				bArr[0] = bRec2;
				bArr[1] = bRec3;
				bArr[2] = bRec4;
				bArr[3] = bRec5;
				bArr[4] = bRec6;

				for (; currentRecorded < bArr.length; currentRecorded++)
					bArr[currentRecorded]
							.setOnClickListener(new OnClickListener() {
								private int recordedNumber = currentRecorded;
								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub

									Computations_ProgDialog cpd = new Computations_ProgDialog(
											RecordingActivity.this,
											"Recording Message & Saving it for Analysis", sth,
											"recordEmergencySymbolVersion", recordedNumber, false);
									cpd.execute(null,null,null);
									
									//Only if there are maxIterationsRecordings Available, enable the Done button
									if (StorageHandler.isEmergencySymbolRecorded())
									{
										bRecDone.setEnabled(true);
										TabActivity ta = (TabActivity) getParent();
										ta.getTabHost().getTabWidget().getChildTabViewAt(1).setEnabled(true);
										ta.getTabHost().getTabWidget().getChildTabViewAt(2).setEnabled(true);
										ta.getTabHost().getTabWidget().getChildTabViewAt(3).setEnabled(true);
									}
								}
							});

				
				if (!sth.isEmergencySymbolRecorded())
					bRecDone.setEnabled(false);
					
				bRecDone.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						findViewById(R.id.recordersubactivity).setVisibility(View.INVISIBLE);
						findViewById(R.id.recording_layout).setVisibility(View.VISIBLE);
					}
				});
			}
		};
		
		findViewById(R.id.recordersubactivity).setVisibility(View.INVISIBLE);
		findViewById(R.id.recording_layout).setVisibility(View.VISIBLE);
		recordButton.setOnClickListener(ocl);
	}
}