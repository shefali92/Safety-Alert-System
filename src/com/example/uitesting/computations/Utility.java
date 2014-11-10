package com.example.uitesting.computations;
public class Utility 
{
	public double hz2mel(double hzValue)
	{
		return 1127 * Math.log(1 + hzValue / 100);
	}	
	
	public double mel2hz(double melValue)
	{
		return 700 * Math.exp(melValue / 1127) - 700;
	}
}
