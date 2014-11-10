package com.example.uitesting;

import android.app.Activity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

public class RecorderSubActivity extends Activity {
	private int maxIterations = 0;
	private int currentRecorded = 0;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordersubactivity);

		TableLayout tlayout = (TableLayout) findViewById(R.id.recorderSA_tblLayout);
		tlayout.setLayoutParams(new LayoutParams(319,
				android.widget.TableLayout.LayoutParams.WRAP_CONTENT));
		maxIterations = getResources().getInteger(
				R.integer.maxIterationsForRecording);
		for (; currentRecorded < maxIterations; currentRecorded++) {
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(319,
					LayoutParams.WRAP_CONTENT));
			Button recButton = new Button(this);
			recButton.setText("Record Version " + (currentRecorded+1));
			tr.addView(recButton);
			tlayout.addView(tr);
		}
	}
}
