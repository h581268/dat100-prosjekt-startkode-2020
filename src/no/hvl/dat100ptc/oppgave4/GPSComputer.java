package no.hvl.dat100ptc.oppgave4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import no.hvl.dat100ptc.oppgave1.GPSPoint;
import no.hvl.dat100ptc.oppgave2.GPSData;
import no.hvl.dat100ptc.oppgave2.GPSDataFileReader;
import no.hvl.dat100ptc.oppgave3.GPSUtils;

public class GPSComputer {
	
	private GPSPoint[] gpspoints;
	
	public GPSComputer(String filename) {

		GPSData gpsdata = GPSDataFileReader.readGPSFile(filename);
		gpspoints = gpsdata.getGPSPoints();

	}

	public GPSComputer(GPSPoint[] gpspoints) {
		this.gpspoints = gpspoints;
	}
	
	public GPSPoint[] getGPSPoints() {
		return this.gpspoints;
	}
	
	// beregn total distances (i meter)
	public double totalDistance() {
				
		double distance = 0;

		for(int i = 0; i < gpspoints.length - 1; i++) {
			distance += GPSUtils.distance(gpspoints[i], gpspoints[i + 1]);
		}
		
		return distance;

	}

	// beregn totale høydemeter (i meter)
	public double totalElevation() {
		
		// return Arrays.stream(gpspoints).mapToDouble(point -> point.getElevation()).reduce(0, (a, b) -> a + b);

		double elevation = 0.0;
		
		for(GPSPoint point: gpspoints) {
			elevation = point.getElevation() > elevation ? point.getElevation() : elevation;
		}
		
		return elevation;

	}

	// beregn total tiden for hele turen (i sekunder)
	public int totalTime() {

		return gpspoints[gpspoints.length - 1].getTime() - gpspoints[0].getTime();
	}
		
	// beregn gjennomsnitshastighets mellom hver av gps punktene

	public double[] speeds() {
		
		double[] speeds = new double[gpspoints.length - 1];
		
		for(int i = 0; i < gpspoints.length - 1; i++) {
			speeds[i] = GPSUtils.speed(gpspoints[i], gpspoints[i + 1]);
		}
		
		return speeds;

	}
	
	public double maxSpeed() {

		return GPSUtils.findMax(speeds());
		
	}
	
	// Oppg. 6a
	public double[] climbs() {
		// Bruker liste siden vi ikke vet antall stigninger i en løype, og lister er dynamisk i størrelse.
		ArrayList<Double> climbs = new ArrayList<>();
		
		for(int i = 0; i < gpspoints.length - 1; i++) {
			// Finner avstanden mellom 2 punkt
			double distance = GPSUtils.distance(gpspoints[i], gpspoints[i + 1]);
			// Finner stigningsforskjellen mellom 2 punkt
			double elevation = gpspoints[i + 1].getElevation() - gpspoints[i].getElevation();
			// Finner hosliggende katet i en rettvinklet trekant
			double adjacent = Math.sqrt(Math.pow(distance, 2) - Math.pow(elevation, 2));
			// Finner vinkelen i trekanten
			double angle = Math.atan(elevation/adjacent);
			
			// Dersom det er en stigningsforskjell som er i positiv retning.
			if ( elevation > 0) {
				// Legg til stigning i % til listen med stigninger.
				climbs.add(Math.tan(Math.toRadians(angle))*100);
			}
		}
		// Converterer listen til en array og returnerer
		return climbs.stream().mapToDouble(climb -> climb).toArray();
	}
	
	// Oppg. 6a
	public double maxClimb() {
		
		double[] climbs = climbs();
		
		return Arrays.stream(climbs).reduce(climbs[0], (a, b) -> a > b ? a : b);
		
	}

	public double averageSpeed() {
		
		return totalDistance() / totalTime() * 3600.0 / 1000.0;
	}

	/*
	 * bicycling, <10 mph, leisure, to work or for pleasure 4.0 bicycling,
	 * general 8.0 bicycling, 10-11.9 mph, leisure, slow, light effort 6.0
	 * bicycling, 12-13.9 mph, leisure, moderate effort 8.0 bicycling, 14-15.9
	 * mph, racing or leisure, fast, vigorous effort 10.0 bicycling, 16-19 mph,
	 * racing/not drafting or >19 mph drafting, very fast, racing general 12.0
	 * bicycling, >20 mph, racing, not drafting 16.0
	 */

	// conversion factor m/s to miles per hour
	public static double MS = 2.236936;

	// beregn kcal gitt weight og tid der kjøres med en gitt hastighet
	public double kcal(double weight, int secs, double speed) {

		// MET: Metabolic equivalent of task angir (kcal x kg-1 x h-1)
		double met = 0;		
		double speedmph = speed * MS;

		if (speedmph < 10.0) {
			met = 4.0;
		} else if(speedmph < 12.0) {
			met = 6.0;
		} else if(speedmph < 14.0) {
			met = 8.0;
		} else if(speedmph < 16.0) {
			met = 10.0;
		} else if(speedmph < 20.0) {
			met = 12.0;
		} else {
			met = 16.0;
		}
		
		return met * weight * secs/3600.0;
	}

	public double totalKcal(double weight) {
		
		return kcal(weight, totalTime(), averageSpeed());
	}
	
	private static double WEIGHT = 80.0;
	
	public void displayStatistics() {

		System.out.println("==============================================");
		System.out.println("Total time\t:" + GPSUtils.formatTime(totalTime()));
		System.out.println("Total distance\t:" + GPSUtils.formatDouble(totalDistance()/1000) + " km");
		System.out.println("Total elevation\t:" + GPSUtils.formatDouble(totalElevation()) + " m");
		System.out.println("Max Stigning\t:" + GPSUtils.formatDouble(maxClimb()) + " %"); // Oppgave 6a
		System.out.println("Max speed\t:" + GPSUtils.formatDouble(maxSpeed()) + " km/t");
		System.out.println("Average speed\t:" + GPSUtils.formatDouble(averageSpeed()) + " km/t");
		System.out.println("Energy\t\t:" + GPSUtils.formatDouble(totalKcal(WEIGHT)) + " kcal");
		System.out.println("==============================================");

	}

}
