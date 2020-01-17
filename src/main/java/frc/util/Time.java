package frc.util;
//TODO: Why?  If we need time as double, cast as double.
//nanoTime?  If we need nano second accuracy, we got issues.
//Maybe we need a syncronized delta(?). 
public class Time {

	public static final long SECOND = 1000000000L;
	private static double delta;
	private static double previousTime = getTime();
	
	public static void update() {
		double currentTime = getTime();
		delta = currentTime - previousTime;
		previousTime = currentTime;
	}
	
	public static double getDelta() {
		return delta;
	}
	
	public static double getTime() {
		return (double)System.nanoTime() / SECOND;
	}
	
}