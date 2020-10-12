package no.hvl.dat100ptc.oppgave5;

import javax.swing.JOptionPane;

import easygraphics.EasyGraphics;
import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave3.GPSUtils;
import no.hvl.dat100ptc.oppgave4.GPSComputer;

public class ShowRoute extends EasyGraphics {

	private static int MARGIN = 50;
	private static int MAPXSIZE = 800;
	private static int MAPYSIZE = 800;

	private GPSPoint[] gpspoints;
	private GPSComputer gpscomputer;
	
	public ShowRoute() {

		String filename = JOptionPane.showInputDialog("GPS data filnavn: ");
		gpscomputer = new GPSComputer(filename);

		gpspoints = gpscomputer.getGPSPoints();

	}

	public static void main(String[] args) {
		launch(args);
	}

	public void run() {

		makeWindow("Route", MAPXSIZE + 2 * MARGIN, MAPYSIZE + 2 * MARGIN);

		showRouteMap(MARGIN + MAPYSIZE);
		
		showStatistics();
	}

	// antall x-pixels per lengdegrad
	public double xstep() {

		double maxlon = GPSUtils.findMax(GPSUtils.getLongitudes(gpspoints));
		double minlon = GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints));

		return MAPXSIZE / (Math.abs(maxlon - minlon));
	}

	// antall y-pixels per breddegrad
	public double ystep() {
	
		double maxlat = GPSUtils.findMax(GPSUtils.getLatitudes(gpspoints));
		double minlat = GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints));
				
		return MAPYSIZE / (Math.abs(maxlat - minlat));
		
	}

	public void showRouteMap(int ybase) {
		
		int x, y;
		int firstX = 0;
		int firstY = 0;
		double elevation = 0.0;
		int RADIUS = 4;
		
		setColor(0, 255, 0);
		
		for(int i = 0; i < gpspoints.length; i++) {
			
			x = MARGIN + (int) ((gpspoints[i].getLongitude() - GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints))) * xstep());
			y = ybase - (int) ((gpspoints[i].getLatitude() - GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints))) * ystep());
			
			if(i < 1) {
				// Setter første X og Y
				firstX = x;
				firstY = y;
				// Tegner første sirkel
				fillCircle(firstX, firstY, RADIUS);
			} else {
				
				// Tegn røde prikker og streker dersom stigning, ellers tegn grønne prikker
				if (gpspoints[i].getElevation() > elevation) {
					setColor(255, 0, 0);
				} else if (gpspoints[i].getElevation() < elevation){
					setColor(0, 255, 0);
				}
				elevation = gpspoints[i].getElevation();
				
				// Tegner strekene mellom
				drawLine(firstX, firstY, x, y);
				firstX = x;
				firstY = y;
				
				// Tegn punktene
				fillCircle(x, y, RADIUS);
			}
		}
		moveRider(ybase);
	}
	
	public void moveRider(int ybase) {
		// setSpeed tar kun int mellom 1-10, og hastighetene kommer oppi 49, må derfor skalere ned for å få den innenfor 1-10.
		int SPEED_SCALE = 5;
		int RADIUSRIDER = 5;
		int x, y;
		
		int firstX = MARGIN + (int) ((gpspoints[0].getLongitude() - GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints))) * xstep());
		int firstY = ybase - (int) ((gpspoints[0].getLatitude() - GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints))) * ystep());
		
		setColor(0, 0, 255);
		int rider = fillCircle(firstX, firstY, RADIUSRIDER);
		
		for(int i = 0; i < gpspoints.length; i++) {
			if(i < gpspoints.length - 1) {
				int speed = (int) GPSUtils.speed(gpspoints[i], gpspoints[i + 1])/SPEED_SCALE;
				setSpeed(speed > 0 ? speed : 1);
			}
			x = MARGIN + (int) ((gpspoints[i].getLongitude() - GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints))) * xstep());
			y = ybase - (int) ((gpspoints[i].getLatitude() - GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints))) * ystep());
			
			moveCircle(rider, x, y);
		}
	}

	public void showStatistics() {

		int TEXTDISTANCE = 20;
		int WEIGHT = 80;

		setColor(0,0,0);
		setFont("Courier",12);
		
		String text[] = {	"Total time", 
							"Total distance", 
							"Total elevation", 
							"Max speed", 
							"Average speed", 
							"Energy"};
		
		String statistics[] = {	formatString(GPSUtils.formatTime(gpscomputer.totalTime())),
								formatString(GPSUtils.formatDouble(gpscomputer.totalDistance()/1000)) + " km", 
								formatString(GPSUtils.formatDouble(gpscomputer.totalElevation())) + " m",
								formatString(GPSUtils.formatDouble(gpscomputer.maxSpeed())) + " km/t",
								formatString(GPSUtils.formatDouble(gpscomputer.averageSpeed())) + " km/t", 
								formatString(GPSUtils.formatDouble(gpscomputer.totalKcal(WEIGHT))) + " kcal"};
		
		for(int i = 0; i < statistics.length; i++) {
			drawString(text[i], TEXTDISTANCE, TEXTDISTANCE + i*TEXTDISTANCE);
			drawString(" :" + statistics[i], TEXTDISTANCE*5, TEXTDISTANCE + i*TEXTDISTANCE);
		}
	}
	
	public static String formatString(String s) {
		
		int TEXTWIDTH = 15;
		
		String str = "";
		for (int i = 0; i < TEXTWIDTH - s.length(); i++) {
			str += " ";
		}
		
		return str += s;
	}
}
