package com.example.uitesting.computations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

// This class is responsible for fetching the standard symbols and returning it to the 
// main program. It also gives methods to save new symbols + store comparison data too.

public class StorageHandler {
	private static String standardSymbolsDirectory = "/mnt/sdcard/AndroidSoundAnalyzer/standard";
	private static String testSymbolDirectory = "/mnt/sdcard/AndroidSoundAnalyzer/samples";
	private static String coeffsDirectory = "/mnt/sdcard/AndroidSoundAnalyzer/coeffs";
	private static String standardSymbolBaseName = "emergency";
	private String testSymbolName = "testMessage";
	private String testSymbol = "/mnt/sdcard/AndroidSoundAnalyzer/samples/emergency_testb.raw";

	private boolean standard = false;
	private static StorageHandler sth = new StorageHandler();
	private static int maxIterationsForRecordingStandardSymbol;
	private static float thresholdMatchCoeff;
	private static HashMap<String, ArrayList<float[]>> standardSymbolVersions;  // hashmap
																				// containing
																				// the
																				// standard
																				// symbol
																				// versions
																				// whenever
																				// the
																				// recordEmergencySymbolVersion
																				// is
																				// called,
																				// refresh
																				// this
																				// hashmap

	private AudioRecordTest art;

	private StorageHandler() {
		art = new AudioRecordTest();
	}

	public void setUpFolderStructure()
	{
		boolean success = false;
		File f = new File(standardSymbolsDirectory);
		success = f.mkdirs();
		if (!success && !f.isDirectory())
		{
			throw new Error(standardSymbolsDirectory + " couldnt be created");
		}
		
		f= new File(coeffsDirectory);
		success = f.mkdirs();
		if (!success && !f.isDirectory())
		{
			throw new Error(coeffsDirectory + " couldnt be created");
		}
		
		f = new File(testSymbolDirectory);
		success = f.mkdirs();
		if (!success && !f.isDirectory())
		{
			throw new Error(testSymbolDirectory + " couldnt be created");
		}
	}
	
	public void setMaxIterationsRequired(int maxIterationsRequired) {
		this.maxIterationsForRecordingStandardSymbol = maxIterationsRequired;
	}
	
	public void setThresholdMatchCoeff(float thresholdMatchCoeff)
	{
		this.thresholdMatchCoeff = thresholdMatchCoeff;
	}
	
	public float getThresholdMatchCoeff()
	{
		return thresholdMatchCoeff;
	}

	public void recordEmergencySymbolVersion(Integer symbolVersion) {
		// record + save mfcc_coeff for this symbolversion
		String file = standardSymbolsDirectory + "/" + standardSymbolBaseName
				+ "_" + symbolVersion + ".raw";
		art.startRecording(file);
		ArrayList<float[]> mfcc_coeffs = MainComputations.createCoeffs(file);
		recordCoeffs(mfcc_coeffs, file);
		refreshStandardSymbolMap(file,mfcc_coeffs);
	}
	
	public void refreshStandardSymbolMap(String file, ArrayList<float[]> mfcc_coeffs)
	{
		if (standardSymbolVersions == null)
		{
			standardSymbolVersions = new HashMap<String, ArrayList<float[]>>();
			standardSymbolVersions.put(file, mfcc_coeffs);
		}
		else
			standardSymbolVersions.put(file, mfcc_coeffs);
	}

	public ArrayList<String> recordTestSymbolForTesting(Void v)
	{
		String file = testSymbolDirectory + "/" + testSymbolName + ".raw";
		art.startRecording(file);
		ArrayList<float[]> mfcc_coeffs = MainComputations.createCoeffs(file);
		recordCoeffs(mfcc_coeffs, file);
		ArrayList<String> bestMatch = MainComputations.getBestMatch(mfcc_coeffs, standardSymbolVersions);
		/*ArrayList<String> bestMatch = new ArrayList<String>();
		bestMatch.add("ASDF");
		bestMatch.add("0.85");*/
		return bestMatch;
	}

	public static StorageHandler getInstance() {
		return sth;
	}

	public boolean isStandardSymbol() {
		return standard;
	}

	public static boolean isEmergencySymbolRecorded() {
		File file = new File(standardSymbolsDirectory);
		if (file.isDirectory()) {
			if (file.list().length == maxIterationsForRecordingStandardSymbol)
			{
				//we are here means the recording has been done
				//so fetch the hashmap for standard symbols from the disk

				getStandardSymbolsCoeffs();
				return true;
			}
		}
		return false;
	}
	
	private static void getStandardSymbolsCoeffs() {
		if (standardSymbolVersions == null)
			standardSymbolVersions = new HashMap<String,ArrayList<float[]>>();
		
		if (standardSymbolVersions.size() == 0) {
			File[] stdFiles = finder(standardSymbolsDirectory);
			for (File f : stdFiles) {
				String path = f.getPath();
				String symbolName = path.substring(path.lastIndexOf('/') + 1,
						path.lastIndexOf('.'));
				path = coeffsDirectory + path.substring(path.lastIndexOf('/'))
						+ ".coeff";
				StringTokenizer st = null;
				ArrayList<float[]> coeffArray = new ArrayList<float[]>();
				String line = "";
				try {
					BufferedReader br = new BufferedReader(new FileReader(path));
					while ((line = br.readLine()) != null && line.length() > 0) {
						st = new StringTokenizer(line, ",");
						int size = st.countTokens();
						float[] array1D = new float[size];

						for (int i = 0; i < size; i++)
							array1D[i] = Float.parseFloat(st.nextToken());
						coeffArray.add(array1D);
					}
					standardSymbolVersions.put(symbolName, coeffArray);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static File[] finder(String dirName) {
		File dir = new File(dirName);

		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".raw");
			}
		});

	}

	public String getTestSymbol() {
		return testSymbol;
	}

	public void recordCoeffs(ArrayList<float[]> floatArray, String fileName) {
		if (floatArray != null && floatArray.size() > 0) {
			String basePath = coeffsDirectory;
			fileName = fileName.substring(fileName.lastIndexOf('/'));
			try {
				BufferedWriter bw = new BufferedWriter(new PrintWriter(basePath
						+ fileName + ".coeff"));
				for (float[] singleFrame : floatArray) {
					String frameContent = "";
					for (float coeff : singleFrame)
						frameContent += coeff + ",";
					frameContent += "\n";
					bw.write(frameContent);
				}
				bw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
