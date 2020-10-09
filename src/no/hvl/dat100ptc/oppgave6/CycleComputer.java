package no.hvl.dat100ptc.oppgave6;

import javax.swing.JOptionPane;

import easygraphics.*;
import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave2.GPSData;
import no.hvl.dat100ptc.oppgave2.GPSDataFileReader;
import no.hvl.dat100ptc.oppgave3.GPSUtils;
import no.hvl.dat100ptc.oppgave4.GPSComputer;
import no.hvl.dat100ptc.oppgave5.ShowProfile;
import no.hvl.dat100ptc.oppgave5.ShowRoute;

public class CycleComputer extends EasyGraphics {

	private static int SPACE = 10;
	private static int MARGIN = 20;
	
	// FIXME: take into account number of measurements / gps points
	private static int ROUTEMAPXSIZE = 800; 
	private static int ROUTEMAPYSIZE = 400;
	private static int HEIGHTSIZE = 200;
	private static int TEXTWIDTH = 200;

	private GPSComputer gpscomp;
	private GPSPoint[] gpspoints;
	
	private int N = 0;

	private double minlon, minlat, maxlon, maxlat;

	private double xstep, ystep;

	public CycleComputer() {

		String filename = JOptionPane.showInputDialog("GPS data filnavn: ");

		gpscomp = new GPSComputer(filename);
		gpspoints = gpscomp.getGPSPoints();

	}

	public static void main(String[] args) {
		launch(args);
	}

	public void run() {

		N = gpspoints.length; // number of gps points

		minlon = GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints));
		minlat = GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints));

		maxlon = GPSUtils.findMax(GPSUtils.getLongitudes(gpspoints));
		maxlat = GPSUtils.findMax(GPSUtils.getLatitudes(gpspoints));

		xstep = xstep();
		ystep = ystep();

		makeWindow("Cycle Computer", 
				2 * MARGIN + ROUTEMAPXSIZE,
				2 * MARGIN + ROUTEMAPYSIZE + HEIGHTSIZE + SPACE);

		bikeRoute();

	}
	
	public void bikeRoute() {
		drawRoute(2 * MARGIN + ROUTEMAPYSIZE + HEIGHTSIZE);
	}
	
	public double xstep() {

		double maxlon = GPSUtils.findMax(GPSUtils.getLongitudes(gpspoints));
		double minlon = GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints));

		return ROUTEMAPXSIZE / (Math.abs(maxlon - minlon));
	}

	public double ystep() {

		double maxlat = GPSUtils.findMax(GPSUtils.getLatitudes(gpspoints));
		double minlat = GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints));
				
		return ROUTEMAPYSIZE / (Math.abs(maxlat - minlat));
	}
	
	public void drawRoute(int ybase) {
		
		int x, y;
		int RADIUS = 4;
		
		// Finner første X og første Y
		int firstX = MARGIN + (int) ((gpspoints[0].getLongitude() - GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints))) * xstep());
		int firstY = ybase - (int) ((gpspoints[0].getLatitude() - GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints))) * ystep()/1.5);

		setColor(0, 255, 0);
		
		// Tegner første sirkel
		fillCircle(firstX, firstY, RADIUS);
		
		double elevation = 0;
		for(int i = 1; i < gpspoints.length; i++) {
			x = MARGIN + (int) ((gpspoints[i].getLongitude() - GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints))) * xstep());
			y = ybase - (int) ((gpspoints[i].getLatitude() - GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints))) * ystep()/1.5);
			
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
		startSimulation(ybase);
	}
	
	public void startSimulation(int ybase) {
		int SPEED_SCALE = 10;
		int RADIUSRIDER = 5;
		int x, y;
		int xElevation = MARGIN;
		double distance = 0;
		
		int TEXTDISTANCE = 20;
		
		String text[] = {	"Time", 
							"Elevation",
							"Distance",
							"Speed"};
		
		int firstX = MARGIN + (int) ((gpspoints[0].getLongitude() - GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints))) * xstep());
		int firstY = ybase - (int) ((gpspoints[0].getLatitude() - GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints))) * ystep()/1.5);
		
		setColor(0, 0, 255);
		int rider = fillCircle(firstX, firstY, RADIUSRIDER);
		
		for(int i = 0; i < gpspoints.length; i++) {
			
			int elevation = (int) (gpspoints[i].getElevation()) > 0 ? (int) (gpspoints[i].getElevation()) : 0;
			
			// Går senere i oppoverbakker. Siden setSpeed kun går 1-10, så må den begrenses med andre metoder, bruker da pause utifra elevasjon.
			if(i < gpspoints.length - 1) {
				
				setColor(0,0,0);
				setFont("Courier",12);
				
				String statistics[] = {	ShowRoute.formatString(GPSUtils.formatTime(gpspoints[i].getTime())), 
										ShowRoute.formatString(GPSUtils.formatDouble(gpspoints[i].getElevation())) + " m",
										ShowRoute.formatString(GPSUtils.formatDouble(distance)) + " km",
										ShowRoute.formatString(GPSUtils.formatDouble(GPSUtils.speed(gpspoints[i], gpspoints[i + 1]))) + " km/t"};

				distance += GPSUtils.distance(gpspoints[i], gpspoints[i + 1])/1000;

				int timeText = drawString(text[0], TEXTDISTANCE, TEXTDISTANCE + 0*TEXTDISTANCE);
				int timeValue = drawString(" :" + statistics[0], TEXTDISTANCE*5, TEXTDISTANCE + 0*TEXTDISTANCE);
				int elevationText = drawString(text[1], TEXTDISTANCE, TEXTDISTANCE + 1*TEXTDISTANCE);
				int elevationValue = drawString(" :" + statistics[1], TEXTDISTANCE*5, TEXTDISTANCE + 1*TEXTDISTANCE);
				int distanceText = drawString(text[2], TEXTDISTANCE, TEXTDISTANCE + 2*TEXTDISTANCE);
				int distanceValue = drawString(" :" + statistics[2], TEXTDISTANCE*5, TEXTDISTANCE + 2*TEXTDISTANCE);
				int speedText = drawString(text[3], TEXTDISTANCE, TEXTDISTANCE + 3*TEXTDISTANCE);
				int speedValue = drawString(" :" + statistics[3], TEXTDISTANCE*5, TEXTDISTANCE + 3*TEXTDISTANCE);
				
				setVisible(timeText, false);
				setVisible(timeValue, false);
				setVisible(elevationText, false);
				setVisible(elevationValue, false);
				setVisible(distanceText, false);
				setVisible(distanceValue, false);
				setVisible(speedText, false);
				setVisible(speedValue, false);
				
				int speed = (int) GPSUtils.speed(gpspoints[i], gpspoints[i + 1])/SPEED_SCALE;
				setSpeed(speed > 0 ? speed : 1);
				pause(elevation);
			}
			
			
			
			x = MARGIN + (int) ((gpspoints[i].getLongitude() - GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints))) * xstep());
			y = ybase - (int) ((gpspoints[i].getLatitude() - GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints))) * ystep()/1.5);
			
			setColor(0, 0, 255);
			drawLine(xElevation + i*2, ybase/2, xElevation+i*2, ybase/2-elevation);
			moveCircle(rider, x, y);
		}
		
		setColor(0,0,0);
		drawString("Total Time", TEXTDISTANCE, TEXTDISTANCE + 0*TEXTDISTANCE);
		drawString(" :" + GPSUtils.formatTime(gpscomp.totalTime()), TEXTDISTANCE*5, TEXTDISTANCE + 0*TEXTDISTANCE);
		drawString("Total Elevation", TEXTDISTANCE, TEXTDISTANCE + 1*TEXTDISTANCE);
		drawString(" :" + GPSUtils.formatDouble(gpscomp.totalElevation()) + " m", TEXTDISTANCE*5, TEXTDISTANCE + 1*TEXTDISTANCE);
		drawString("Total Distance", TEXTDISTANCE, TEXTDISTANCE + 2*TEXTDISTANCE);
		drawString(" :" + GPSUtils.formatDouble(gpscomp.totalDistance()/1000) + " km", TEXTDISTANCE*5, TEXTDISTANCE + 2*TEXTDISTANCE);
		drawString("Avarage speed", TEXTDISTANCE, TEXTDISTANCE + 3*TEXTDISTANCE);
		drawString(" :" + GPSUtils.formatDouble(gpscomp.averageSpeed()) + " km/t", TEXTDISTANCE*5, TEXTDISTANCE + 3*TEXTDISTANCE);
	}
}
