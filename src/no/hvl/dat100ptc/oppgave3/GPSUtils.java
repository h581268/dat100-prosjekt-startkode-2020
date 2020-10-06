package no.hvl.dat100ptc.oppgave3;

import no.hvl.dat100ptc.oppgave1.GPSPoint;
import static java.lang.Math.*;

public class GPSUtils {

	public static double findMax(double[] da) {

		// return Arrays.stream(da).reduce(da[0], (a, b) -> a > b ? a : b);
		double max; 
		
		max = da[0];
		
		for (double d : da) {
			if (d > max) {
				max = d;
			}
		}
		
		return max;
	}

	public static double findMin(double[] da) {

		// return Arrays.stream(da).reduce(da[0], (a, b) -> a < b ? a : b);
		double min;

		min = da[0];
		
		for (double d : da) {
			if (d < min) {
				min = d;
			}
		}
		
		return min;

	}

	public static double[] getLatitudes(GPSPoint[] gpspoints) {
		
		//return Arrays.stream(gpspoints).mapToDouble(point -> point.getLatitude()).toArray();

		double[] latitudes = new double[gpspoints.length];
		for(int i = 0; i < latitudes.length; i++) {
			latitudes[i] = gpspoints[i].getLatitude();
		}
		return latitudes;
	}

	public static double[] getLongitudes(GPSPoint[] gpspoints) {

		//return Arrays.stream(gpspoints).mapToDouble(point -> point.getLongitude()).toArray();
		
		double[] longitudes = new double[gpspoints.length];
		for(int i = 0; i < longitudes.length; i++) {
			longitudes[i] = gpspoints[i].getLongitude();
		}
		return longitudes;

	}

	private static int R = 6371000; // jordens radius

	public static double distance(GPSPoint gpspoint1, GPSPoint gpspoint2) {

		double d, latitude1, longitude1, latitude2, longitude2;
		
		latitude1 = gpspoint1.getLatitude();
		longitude1 = gpspoint1.getLongitude();
		
		latitude2 = gpspoint2.getLatitude();
		longitude2 = gpspoint2.getLongitude();
		
		double lat1rad = toRadians(latitude1);
		double long1rad = toRadians(longitude1);
		
		double lat2rad = toRadians(latitude2);
		double long2rad = toRadians(longitude2);
		
		double deltaLat = lat2rad - lat1rad;
		double deltaLong = long2rad - long1rad;
		
		double a = pow(sin(deltaLat/2), 2) + cos(lat1rad) * cos(lat2rad) * pow(sin(deltaLong/2), 2);
		double c = 2 * atan2(sqrt(a), sqrt(1 - a));
		
		d = R * c;
		
		return d;

	}

	public static double speed(GPSPoint gpspoint1, GPSPoint gpspoint2) {

		int secs;
		double speed;

		secs = gpspoint2.getTime() - gpspoint1.getTime();
		
		speed = ((distance(gpspoint1, gpspoint2)/secs)*3600)/1000;
		return speed;
	}

	public static String formatTime(int secs) {
		
		int hours = secs/3600;
		int minutes = secs/60%60;
		secs = secs%3600%60;

		return "  " + String.format("%02d:%02d:%02d", hours, minutes, secs);

	}
	private static int TEXTWIDTH = 10;

	public static String formatDouble(double d) {

		String str = "";
		String doubleAsString = String.format("%.2f", d);
		for (int i = 0; i < TEXTWIDTH - doubleAsString.length(); i++) {
			str += " ";
		}
	
		str += doubleAsString.replace(',','.');
		return str;
	}
}
