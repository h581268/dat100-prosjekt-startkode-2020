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

		return ROUTEMAPXSIZE / (Math.abs(maxlon - minlon));
	}

	public double ystep() {
				
		return ROUTEMAPYSIZE / (Math.abs(maxlat - minlat));
	}
	
	public void drawRoute(int ybase) {
		
		int x, y;
		int firstX = 0;
		int firstY = 0;
		double elevation = 0;
		int RADIUS = 4;
		
		setColor(0, 255, 0);
		
		for(int i = 0; i < gpspoints.length; i++) {
			x = MARGIN + (int) ((gpspoints[i].getLongitude() - GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints))) * xstep);
			y = ybase - (int) ((gpspoints[i].getLatitude() - GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints))) * ystep/1.5);
			
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
		startSimulation(ybase);
	}
	
	public void startSimulation(int ybase) {
		int SPEED_SCALE = 10;
		int RADIUSRIDER = 5;
		int TEXTDISTANCE = 20;
		
		int x, y;
		int xElevation = MARGIN;
		double distance = 0;
		
		setColor(0, 0, 255);
		
		// Finner x og y for første punkt
		int firstX = MARGIN + (int) ((gpspoints[0].getLongitude() - GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints))) * xstep);
		int firstY = ybase - (int) ((gpspoints[0].getLatitude() - GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints))) * ystep/1.5);
		
		// Prikk som simulerer syklist i løypen
		int riderId = fillCircle(firstX, firstY, RADIUSRIDER);
		
		// For løkke for å tegne rytter langs rute sammen med stigning.
		for(int i = 0; i < gpspoints.length; i++) {
			
			int elevation = (int) (gpspoints[i].getElevation());
			
			// If setning for å sjekke at man ikke er på slutten av ruten, 
			// dersom man ikke er på slutten av ruten, skriv statistikk for rytter og sett hastigheten for animasjonen.
			if(i < gpspoints.length - 1) {
				
				setColor(0,0,0);
				setFont("Courier",12);

				int timeText = drawString("Time", TEXTDISTANCE, TEXTDISTANCE + 0*TEXTDISTANCE);
				int timeValue = drawString(" :" + ShowRoute.formatString(GPSUtils.formatTime(gpspoints[i].getTime())), TEXTDISTANCE*5, TEXTDISTANCE + 0*TEXTDISTANCE);
				int elevationText = drawString("Elevation", TEXTDISTANCE, TEXTDISTANCE + 1*TEXTDISTANCE);
				int elevationValue = drawString(" :" + ShowRoute.formatString(GPSUtils.formatDouble(gpspoints[i].getElevation())) + " m", TEXTDISTANCE*5, TEXTDISTANCE + 1*TEXTDISTANCE);
				int distanceText = drawString("Distance", TEXTDISTANCE, TEXTDISTANCE + 2*TEXTDISTANCE);
				int distanceValue = drawString(" :" + ShowRoute.formatString(GPSUtils.formatDouble(distance += GPSUtils.distance(gpspoints[i], gpspoints[i + 1])/1000)) + " km", TEXTDISTANCE*5, TEXTDISTANCE + 2*TEXTDISTANCE);
				int speedText = drawString("Speed", TEXTDISTANCE, TEXTDISTANCE + 3*TEXTDISTANCE);
				int speedValue = drawString(" :" + ShowRoute.formatString(GPSUtils.formatDouble(GPSUtils.speed(gpspoints[i], gpspoints[i + 1]))) + " km/t", TEXTDISTANCE*5, TEXTDISTANCE + 3*TEXTDISTANCE);
				
				setVisible(timeText, false);
				setVisible(timeValue, false);
				setVisible(elevationText, false);
				setVisible(elevationValue, false);
				setVisible(distanceText, false);
				setVisible(distanceValue, false);
				setVisible(speedText, false);
				setVisible(speedValue, false);
				
				// Setter "hastighet", men siden setSpeed kun er 1-10, må den skaleres, 
				// velger da å "bremse" med å endre farten (pause) etterhvert som elevasjonen stiger eller faller.
				int speed = (int) GPSUtils.speed(gpspoints[i], gpspoints[i + 1])/SPEED_SCALE;
				setSpeed(speed > 0 ? speed : 1);
				pause(elevation);
			}
			
			// Skalerer long og lat til en pixel på skjermen ved hjelp at ystep og xstep.
			x = MARGIN + (int) ((gpspoints[i].getLongitude() - GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints))) * xstep);
			y = ybase - (int) ((gpspoints[i].getLatitude() - GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints))) * ystep/1.5);
			
			setColor(0, 0, 255);
			// Tegner elevasjonslinje
			drawLine(xElevation + i*2, ybase/2, xElevation+i*2, ybase/2-elevation);
			// Flytter rytteren langs ruten
			moveCircle(riderId, x, y);
		}
		
		// Skriver sluttresultatet på skjermen ved endt økt.
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
