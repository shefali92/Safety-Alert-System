package com.example.uitesting.computations;
import java.util.ArrayList;

/**
 * @author Ganesh Tiwari
 * @reference 'A New Silence Removal and Endpoint Detection Algorithm for Speech
 *            and Speaker Recognition Applications' by IIT, Khragpur
 */
public class EndPointDetection {
	private float[] originalSignal; // input
	private float[] silenceRemovedSignal;// output
	private int samplingRate;
	private int firstSamples;
	private int samplePerFrame;
	private int frameSize;

	public EndPointDetection(float[] originalSignal, int samplingRate) {
		this.originalSignal = originalSignal;
		this.samplingRate = samplingRate;
		samplePerFrame = this.samplingRate / 1000;
		firstSamples = samplePerFrame * 200;// according to formula
	}

	public float[] doEndPointDetectionX() {
		//1. http://read.pudn.com/downloads99/sourcecode/others/404673/audioProcessing/epdByVolZcr.m__.htm
		//2. http://mirlab.org/jang/books/audiosignalprocessing/epdTimeDomain.asp?title=6-2%20EPD%20in%20Time%20Domain%20(%BA%DD%C2I%B0%BB%B4%FA%A1G%AE%C9%B0%EC%AA%BA%A4%E8%AAk)
		return null;
	}
	
	public float[] doEndPointDetection() {
		// for identifying each sample whether it is voiced or unvoiced
		float[] voiced = new float[originalSignal.length];
		float sum = 0;
		double sd = 0.0;
		double m = 0.0;
		// 1. calculation of mean
		for (int i = 0; i < firstSamples; i++) {
			sum += originalSignal[i];
		}
		m = sum / firstSamples;// mean
		sum = 0;// reuse var for S.D.

		// 2. calculation of Standard Deviation
		for (int i = 0; i < firstSamples; i++) {
			sum += Math.pow((originalSignal[i] - m), 2);
		}
		sd = Math.sqrt(sum / firstSamples);
		// 3. identifying one-dimensional Mahalanobis distance function
		// i.e. |x-u|/s greater than ####3 or not,
		for (int i = 0; i < originalSignal.length; i++) {
			if ((Math.abs(originalSignal[i] - m) / sd) > 0.5) { // 0.3
																// =THRESHOLD..
																// adjust value
																// yourself
				voiced[i] = 1;
			} else {
				voiced[i] = 0;
			}
		}
		// 4. calculation of voiced and unvoiced signals
		// mark each frame to be voiced or unvoiced frame
		int frameCount = 0;
		int usefulFramesCount = 1;
		int count_voiced = 0;
		int count_unvoiced = 0;
		int voicedFrame[] = new int[originalSignal.length / samplePerFrame];
		// the following calculation truncates the remainder
		int loopCount = originalSignal.length
				- (originalSignal.length % samplePerFrame);
		for (int i = 0; i < loopCount; i += samplePerFrame) {
			count_voiced = 0;
			count_unvoiced = 0;
			for (int j = i; j < i + samplePerFrame; j++) {
				if (voiced[j] == 1) {
					count_voiced++;
				} else {
					count_unvoiced++;
				}
			}
			if (count_voiced > count_unvoiced) {
				usefulFramesCount++;
				voicedFrame[frameCount++] = 1;
			} else {
				voicedFrame[frameCount++] = 0;
			}
		}
		// 5. silence removal
		silenceRemovedSignal = new float[originalSignal.length]; //silenceRemovedSignal will be 0-padded in the ending
		int k = 0;
		for (int i = 0; i < frameCount; i++) {
			if (voicedFrame[i] == 1) {
				for (int j = i * samplePerFrame; j < i * samplePerFrame
						+ samplePerFrame; j++) {
					silenceRemovedSignal[k++] = originalSignal[j];
				}
			}
		}
		// end
		return silenceRemovedSignal;
	}
}