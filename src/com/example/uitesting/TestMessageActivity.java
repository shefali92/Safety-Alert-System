package com.example.uitesting;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.uitesting.computations.StorageHandler;

public class TestMessageActivity extends Activity {
	private StorageHandler sth;
	private ImageButton bTestMessage;
	private Computations_ProgDialog cpd;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testmessage_layout);

		sth = StorageHandler.getInstance();
		
		bTestMessage = (ImageButton) findViewById(R.id.imageButton1);
		bTestMessage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				cpd = new Computations_ProgDialog(
						TestMessageActivity.this, "Recording & Comparing for Match",
						sth, "recordTestSymbolForTesting", null, true);
				cpd.execute(null,null,null);
			}
		});
	}
	
	public void processCompleted(ArrayList<String> bestMatch)
	{
		TextView status = (TextView) findViewById(R.id.textView3);
		TextView matchingPercentage = (TextView) findViewById(R.id.textView5);
		if (bestMatch != null)
		{
			if (Float.parseFloat(bestMatch.get(1)) >= sth.getThresholdMatchCoeff())
			{
				status.setText("MATCHED");
				getParent().getIntent().putExtra("validTrigger", true);
				
				TabActivity tabs = (TabActivity) getParent();
			    tabs.getTabHost().setCurrentTab(2);
			}
			else
				status.setText("FAILED IN MATCHING");
			
			matchingPercentage.setText(bestMatch.get(1).toString());
		}
		else
		{
			status.setText("FAILED IN MATCHING");
			matchingPercentage.setText("0.0");
		}
	}
}