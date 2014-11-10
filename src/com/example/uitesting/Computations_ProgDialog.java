package com.example.uitesting;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Computations_ProgDialog extends AsyncTask<String, String, ArrayList<String>> {
	private ProgressDialog progDialog;
	private String progDialogMsg = "";
	private Context context;
	private Object invokerObject;
	private String methodName;
	private Object param;
	private boolean accessReturnValue;
	private ArrayList<String> returnValue;
	
	public Computations_ProgDialog(Context context, String progDialogMsg, Object invokObject, String methString, Object param, boolean accessReturnValue) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.progDialogMsg = progDialogMsg;
		this.invokerObject = invokObject;
		this.methodName = methString;
		this.param = param;
		this.accessReturnValue = accessReturnValue;
	}
	
	@Override
	protected ArrayList<String> doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			Method m = null;
			if (param != null)
				m = invokerObject.getClass().getMethod(methodName, param.getClass());
			else
				m = invokerObject.getClass().getMethod(methodName, Void.class);
			
			this.returnValue = (ArrayList<String>) m.invoke(invokerObject, param);
			if (accessReturnValue)
				return returnValue;
			return null;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			return returnValue;
		}
	}
	
	 @Override
	 protected void onPreExecute()
	 {
        super.onPreExecute();
        progDialog = new ProgressDialog(context);
        progDialog.setMessage(progDialogMsg);
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(false);
        progDialog.show();
	 }
	 
	 @Override
	 protected void onPostExecute(ArrayList<String> unused)
	 {
		super.onPostExecute(unused);
		progDialog.dismiss();
		try {
			Method m = context.getClass().getMethod("processCompleted", ArrayList.class);
			m.invoke(context, unused);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}
