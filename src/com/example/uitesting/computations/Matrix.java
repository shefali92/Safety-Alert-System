package com.example.uitesting.computations;
import java.lang.Math;
import java.util.ArrayList;

public abstract class Matrix
{
	int rows, cols;
	double[][] matrix;
	public final double PI = Math.PI;
	private int N = 10;
	
	public Matrix(int rows, int cols)
	{
		this.rows = rows;
		this.cols = cols;
		matrix = new double[rows][cols];
	}
	
	public int[] getSize() {return new int[] {rows,cols}; } 
	
	public double[] getHammingWindow()
	{
		double [] hammingWindow = new double[N];
		for (int i = 0; i < N; i++)
				hammingWindow[i] = (0.54 - 0.46 * Math.cos(2*PI*i / (N-1)));
		return hammingWindow;
	}
	
	public double max()
	{
		double max = 0;
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				if (max > matrix[i][j])
				{
					max = matrix[i][j];
				}
		return max;
	}
	
	public double maxAbs()
	{
		double max = 0;
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				if (max > Math.abs(matrix[i][j]))
				{
					max = matrix[i][j];
				}
		return max;
	}
	
	public double[][] multiply(long scalingFactor)
	{
		if (rows != 0 && cols != 0)
		{
			double[][] explodedMatrix = new double[rows][cols];
			for (int i = 0; i < rows; i++)
				for(int j = 0; j < cols; j++)
					explodedMatrix[i][j] = matrix[i][j] * scalingFactor;
			return explodedMatrix;
		}
		else
			return null;
	}
	
	public double[][] subtract(double subtractFig)
	{
		if (rows != 0 && cols != 0)
		{
			double[][] subtractedMatrix = new double[rows][cols];
			for (int i = 0; i < rows; i++)
				for(int j = 0; j < cols; j++)
					subtractedMatrix[i][j] = matrix[i][j] - subtractFig;
			return subtractedMatrix;
		}
		else
			return null;
	}
	
	public double[][] divide(double divisor)
	{
		if (rows != 0 && cols != 0)
		{
			double[][] resultMatrix = new double[rows][cols];
			for (int i = 0; i < rows; i++)
				for(int j = 0; j < cols; j++)
					resultMatrix[i][j] = matrix[i][j] / divisor;
			return resultMatrix;
		}
		else
			return null;
	}
	
	public double[][] add(double addendum)
	{
		if (rows != 0 && cols != 0)
		{
			double[][] resultMatrix = new double[rows][cols];
			for (int i = 0; i < rows; i++)
				for(int j = 0; j < cols; j++)
					resultMatrix[i][j] = matrix[i][j] + addendum;
			return resultMatrix;
		}
		else
			return null;
	}
	
	public static int[][] repMat(String range, int rowDim, int colDim)
	{
		//range Format: i .. j & i <= j
		if (range != null)
		{
			String[] parts = range.split("\\.\\.");
			int low, high;
			low = 0; high = -1;
			try
			{
				low = Integer.parseInt(parts[0].trim());
				high = Integer.parseInt(parts[1].trim());
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
			}
			
			if (low < high)
			{
				System.out.println("Incorrect range passed : " + range);
				System.exit(1);
			}
			
			int reqSize = high - low + 1;
			int reqMatColSize = colDim * reqSize;
			int[] reqValues = new int[reqSize];
			for (int i = 0, val = low; val <= high; i++, val++)
				reqValues[i] = val;
			
			int[][] reqMat = new int[rowDim][reqMatColSize];
			
			for(int i = 0;i < rowDim; i++)
				for(int j = 0;j < reqMatColSize; j++)
					reqMat[i][j] = reqValues[j % reqSize];
			
			return reqMat;
		}
		else
			return null;
	}
}