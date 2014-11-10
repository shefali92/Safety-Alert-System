package com.example.uitesting.computations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class MainComputations {

	private boolean firstRun = true;
	private boolean isRecordingDone = false;
	private int RECORDING_DONE = 1;
	private int GPS_TRIGGERED = 2;
	
	public static ArrayList<float[]> createCoeffs(String filePath)
	{
		int LITTLE_ENDIAN = 1;
		int BIG_ENDIAN = 2;
		int sampleLengthInBytes = 32768;
		
		try {
			InputStream is = new FileInputStream(filePath);
			byte buffer[] = new byte[sampleLengthInBytes];
			is.read(buffer);			
			
			
			//1. first we need to convert this byte data into 2-byte data because
			//size of each sample is 2 bytes
			
			short convertedBuffer[] = convertTo2ByteFormat(buffer,LITTLE_ENDIAN);
			float normalizedArray[] = new float[convertedBuffer.length];
			for(int i = 0; i < convertedBuffer.length; i++) normalizedArray[i] = convertedBuffer[i];
			
			EndPointDetection epd = new EndPointDetection(normalizedArray, 16000);
			normalizedArray = epd.doEndPointDetection();
			
			normalizedArray = normalizeArray(normalizedArray,true); //need to see if this is required... 
											 //it appears epd class is doing it internally
			
			int nnumberofFilters = 24;   
			int nlifteringCoefficient = 22;   
			boolean oisLifteringEnabled = true;
			boolean oisZeroThCepstralCoefficientCalculated = false;   
			int nnumberOfMFCCParameters = 12; //without considering 0-th   
			double dsamplingFrequency = 16000.0;   
			int nFFTLength = 2048;   
			if (oisZeroThCepstralCoefficientCalculated) {   
				//take in account the zero-th MFCC   
				nnumberOfMFCCParameters = nnumberOfMFCCParameters + 1;
			}   
			else {   
        	  nnumberOfMFCCParameters = nnumberOfMFCCParameters;   
			}   
   
			MFCC mfcc = new MFCC(nnumberOfMFCCParameters,   
        		  dsamplingFrequency,   
        		  nnumberofFilters,   
        		  nFFTLength,   
        		  oisLifteringEnabled,   
        		  nlifteringCoefficient,   
        		  oisZeroThCepstralCoefficientCalculated);   
   
			System.out.println(mfcc.toString());   
   
			//divide the original message into frames of size 2048 samples
			//keep fs = 2048, overlap = 1 / 2 of fs = 1024
			
			ArrayList<float[]> framesForMFCC = null;
			ArrayList<float[]> MFCC_Coeffs = new ArrayList<float[]>();
			float overlapFactor = 0.5f;
			int overlapSize = (int) (overlapFactor * nFFTLength);
			framesForMFCC = getFrames(normalizedArray,nFFTLength,overlapSize);
						
          //simulate a frame of speech 
			for(float[] singleFrame : framesForMFCC)
			{
				float[] dparameters = mfcc.getParameters(singleFrame);
				MFCC_Coeffs.add(dparameters);
				/*System.out.println("MFCC parameters:");   
				for (int i = 0; i < dparameters.length; i++) {   
					System.out.print(" " + dparameters[i]);   
				}*/
			}
			return MFCC_Coeffs;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<String> getBestMatch(ArrayList<float[]> testSymbol, HashMap<String, ArrayList<float[]>> standardSymbols)
	{
		if (testSymbol != null && testSymbol.size() > 0 && standardSymbols != null && standardSymbols.size() > 0)
		{
			float bestCorrCoeff = 0f;
			String bestMatch = null;
			ArrayList<String> returnStr = new ArrayList<String>();
			for(String key : standardSymbols.keySet())
			{
				ArrayList<float[]> standardSymbolCoeff = standardSymbols.get(key);
				float corrCoeff = corr2(standardSymbolCoeff,testSymbol);
				if (corrCoeff > bestCorrCoeff)
				{
					bestCorrCoeff = corrCoeff;
					bestMatch = key;
				}
				System.out.println("Symbol : " + key + " CORRCOFF : " + corrCoeff);
			}
			System.out.println("-----------------------------------------------------");
			System.out.println("BEST : " + bestMatch + "     CORRVALUE : " + bestCorrCoeff);
			returnStr.add(bestMatch);
			returnStr.add(Float.toString(bestCorrCoeff));
			return returnStr;
		}
		throw new Error("Invalid values passed to getBestMatch");
	}
	
	private static float corr2(ArrayList<float[]> m1, ArrayList<float[]> m2)
	{
		if (m1 != null && m2 != null && m1.size() == m2.size())
		{
			float meanM1 = mean(m1);
			float meanM2 = mean(m2);
			int rowsize = m1.size();
			int colsize = m1.get(0).length;
			float numerator = 0f, denominator = 0f, denominatorPart1 = 0f, denominatorPart2 = 0f;
			
			for(int i = 0; i < rowsize; i++)
			{
				for (int j = 0; j < colsize; j++)
				{
					float term1 = (m1.get(i)[j] - meanM1);
					float term2 = (m2.get(i)[j] - meanM2);
					numerator +=  term1 * term2;
					denominatorPart1 += term1 * term1;
					denominatorPart2 += term2 * term2;
				}
			}
			denominator = (float) Math.sqrt(denominatorPart1 * denominatorPart2);
			return numerator / denominator;
		}
		throw new Error("Matrices must be of the same size");
	}
	
	private static float mean(ArrayList<float[]> matrix)
	{
		if (matrix != null && matrix.size() > 0)
		{
			float mean = 0f;
			int size = matrix.size();
			
			for (int i = 0; i < size; i++)
				for(float f : matrix.get(i))
					mean += f;
			return mean / size;
		}
		return 0;
	}
	
	private static ArrayList<float[]> getFrames(float[] origArray, int frameSize, int overlapSize)
	{
		//BUFFER2 Frame blocking
		//http://mirlab.org/jang/matlab/toolbox/utility/html/utility/buffer2.html
		
		if (origArray != null && origArray.length > 0)
		{
			int arraySize = origArray.length;
			int stepSize = frameSize - overlapSize;
			int frameCount = (int) Math.floor((arraySize - overlapSize) / stepSize);
			
			ArrayList<float[]> framesArray = new ArrayList<float[]>();
			
			for (int i = 0; i < frameCount; i++)
			{
				float[] singleFrame = new float[frameSize];
				int startIndex = i * stepSize;
				for (int j = 0; j < frameSize; j++)
				{
					singleFrame[j] = origArray[startIndex + j];
				}
				framesArray.add(singleFrame);
			}
			return framesArray;
		}
		return null;
	}
	
	private static float[] normalizeArray(float[] array, boolean doZeroJustification)
	{
		if (array != null && array.length > 0)
		{
			float[] normalizedArray = new float[array.length];
			float sum = 0.0f;
			float mean = 0.0f;
			float divisor = (float) Math.pow(2, 15); //divide by 32768 for converting in range of -1.0 to +1.0
			
			for(int i = 0; i < array.length; i++)
			{
				normalizedArray[i] = array[i] / divisor;
				sum += normalizedArray[i];
			}
			
			if (doZeroJustification == false)
				return normalizedArray;
			
			mean = sum / normalizedArray.length;
			
			for (int i = 0; i < normalizedArray.length; i++)
			{
				normalizedArray[i] -= mean;
			}
			return normalizedArray;
		}
		return null;
	}
	
	private static void writeToFile(short[] array) //for verification that our conversion from byte to short array 
												   //works fine. It worked correctly
	
	{
		ByteBuffer myByteBuffer = ByteBuffer.allocate(array.length * 2);
		myByteBuffer.order(ByteOrder.LITTLE_ENDIAN);

		ShortBuffer myShortBuffer = myByteBuffer.asShortBuffer();
		myShortBuffer.put(array);

		FileChannel out;
		try {
			out = new FileOutputStream("c:\\project\\converted.pcm").getChannel();
			out.write(myByteBuffer);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static short[] convertTo2ByteFormat(byte[] buffer, int endianess)
	{
		//1. LITTLE_ENDIAN
		//2. BIG_ENDIAN
		
		if (buffer != null && buffer.length > 0)
		{
			short[] convertedValueArray = new short[buffer.length / 2];
			for(int i = 0; i < buffer.length;)
			{
				byte lowerByte = 0;
				byte higherByte = 0;
				
				if (endianess == 1)
				{
					lowerByte = buffer[i]; //lower byte is considered having lower value i.e. little-endian
					higherByte = buffer[i+1];
				}
				else if(endianess == 2)
				{
					lowerByte = buffer[i+1]; //lower byte is considered having higher value i.e. big-endian
					higherByte = buffer[i];
				}
				
				short convertedValue = (short) ((higherByte << 8) + lowerByte);
				convertedValueArray[i/2] = convertedValue;
				i += 2;
			}
			return convertedValueArray;
		}
		return null;
	}

}
