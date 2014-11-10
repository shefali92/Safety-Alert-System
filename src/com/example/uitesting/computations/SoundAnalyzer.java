package com.example.uitesting.computations;


public class SoundAnalyzer {
	private Matrix __internalMatrix;
	
	public Matrix premphasis(double alpha, Matrix m) // y = filter(b,a,X) 
									// y = filter(b,a,X) filters the data in vector X
									// with the filter described by numerator coefficient 
									// vector b and denominator coefficient vector a. 
									// If a(1) is not equal to 1, filter normalizes the filter coefficients by a(1). 
									// If a(1) equals 0, filter returns an error.
									// http://www.mathworks.in/help/matlab/ref/filter.html
	{
		return m;
	}
}
