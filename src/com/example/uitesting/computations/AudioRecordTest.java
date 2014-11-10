package com.example.uitesting.computations;
/*
 * The application needs to have the permission to write to external storage
 * if the output file is written to the external storage, and also the
 * permission to record audio. These permissions must be set in the
 * application's AndroidManifest.xml file, with something like:
 *
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 *
 */

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class AudioRecordTest
{
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    private AudioRecord mRecorder = null;
    byte[] buffer;
    
    public void startRecording(String mFileName) 
    {
    	this.mFileName = mFileName;
    	try
    	{
    		int bufSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) * 8;
    		
    	   buffer = new byte[bufSize];
	       mRecorder = new AudioRecord(AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_MONO , AudioFormat.ENCODING_PCM_16BIT, buffer.length);
	       if (mRecorder != null)
	       {
	    	   int currRead = 0;
	    	   mRecorder.startRecording();
	    	   while (currRead < buffer.length)
	    	   {
	    		   int read = mRecorder.read(buffer,currRead, bufSize - currRead);
	    		   currRead += read;
	    	   }
	    	   mRecorder.stop();
	    	   mRecorder.release();
	       }
	       else
	       {
	    	   System.out.println("Couldnt init mRecorder");
	    	   System.exit(1);
	       }
	       
	       FileOutputStream fos = new FileOutputStream(mFileName);
	       fos.write(buffer);
	       fos.close();
	       
    	}
    	catch (Exception e)
    	{
    		 FileOutputStream fos;
			try {
				fos = new FileOutputStream(mFileName);
				  fos.write(buffer);
		  	       fos.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
  	     
  	       	e.printStackTrace();
    	}
    }
}
