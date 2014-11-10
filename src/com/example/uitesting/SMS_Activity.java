package com.example.uitesting;

//import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
//import android.widget.EditText;
import android.widget.Toast;
//import android.app.Activity;
//import android.view.Menu;

public class SMS_Activity extends Activity {
	 //Button btnSendSMS;
	    //EditText txtPhoneNo;
	    //EditText txtMessage;
	 TextView t;
	 private String trackedLocation;
	 
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.maingpsui);    
	        trackedLocation = getParent().getIntent().getStringExtra("location_IMEI");
	 
	       // btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
	        t = (TextView) findViewById(R.id.textview10);
	        
	        if (trackedLocation != null)
	        	call_itt(trackedLocation);
	       //txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
	       // txtMessage = (EditText) findViewById(R.id.txtMessage);
	 
	        //btnSendSMS.setOnClickListener(new View.OnClickListener() 
	        //{
	    }
	            public void call_itt(String msg) 
	            {                
	                String[] phoneNo = new String[6];
	                String message = new String();
	                phoneNo[0]="+919772060206";
	                phoneNo[1]="+919772059336";
	                phoneNo[2]="+919460564658";
	                phoneNo[3]="+918233592975";
	                phoneNo[4]="+919782844060";
	                phoneNo[5]="+919462836886";
	                message="Emergency: Help required at "+msg ;
	                sendSMS(phoneNo, message);

	            }
	           
	    
	    
	  //---sends an SMS message to another device---
	    private void sendSMS(String phoneNumber[], String message)
	    {        
	        String SENT = "SMS_SENT";
	        String DELIVERED = "SMS_DELIVERED";
	        String s = new String();
	        s=null;
	        String old = new String();
	        old=null;
	       /* String old1 = new String();
	        old1=null;
	        String s1 = new String();
	        s1=null;*/
	        
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
	                                Toast.LENGTH_LONG).show();
	                        String old1 = t.getText().toString();
	                       String s1 = old1 + " SMS successfully sent \n\n";
	                        t.setText(s1);
	                        break;
	                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
	                        Toast.makeText(getBaseContext(), "Generic failure", 
	                                Toast.LENGTH_LONG).show();
	                        old1 = t.getText().toString();
		                     s1 = old1 + " SMS failed \n\n";
		                        t.setText(s1);
	                        break;
	                    case SmsManager.RESULT_ERROR_NO_SERVICE:
	                        Toast.makeText(getBaseContext(), "No service", 
	                                Toast.LENGTH_LONG).show();
	                       old1 = t.getText().toString();
		                        s1 = old1 + " SMS failed: No service \n\n";
		                        t.setText(s1);
	                        break;
	                    case SmsManager.RESULT_ERROR_NULL_PDU:
	                        Toast.makeText(getBaseContext(), "Null PDU", 
	                                Toast.LENGTH_LONG).show();
	                       old1 = t.getText().toString();
		                        s1 = old1 + " SMS failed: Null PDU \n\n";
		                        t.setText(s1);
	                        break;
	                    case SmsManager.RESULT_ERROR_RADIO_OFF:
	                        Toast.makeText(getBaseContext(), "Radio off", 
	                                Toast.LENGTH_LONG).show();
	                        old1 = t.getText().toString();
		                  s1 = old1 + " SMS failed: Radio off \n\n";
		                        t.setText(s1);
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
	                                Toast.LENGTH_LONG).show();
	                        
	                        break;
	                    case Activity.RESULT_CANCELED:
	                        Toast.makeText(getBaseContext(), "SMS not delivered", 
	                                Toast.LENGTH_LONG).show();
	                        break;                        
	                }
	            }
	        }, new IntentFilter(DELIVERED));        
	 
	        SmsManager sms = SmsManager.getDefault();
	        //String s="Sending message to ";
	        //String log=null;
	        for(int i=0;i<6;i++)

	        {if(i==0)
	        	{s = "Sending message to "+phoneNumber[i]+"\n";
	        t.setText(s);}
	        else
	        {
	        	old = t.getText().toString();
	        	s = old+"Sending message to "+phoneNumber[i]+"\n";
	        	t.setText(s);
	        }
	        sms.sendTextMessage(phoneNumber[i], null, message, sentPI, deliveredPI);
	        }
	    }   
	    
	}
	
	
	

