package de.janrufmonitor.util.math;

/**
 * Class to determine the distance between two points on earth.
 * 
 * created on 2010/04/02
 * 
 * @author brandt
 *
 */
public class Distance {

	public static void main(String[] args) {
		// Angelbachtal -> Sinsheim
		System.out.println(calculateDistance(49.2225,8.7800,49.2553,8.8772));
	}
	
	
	public static double DegreesInRadiants = 0.0174532925199433;
	public static double EarthRadius = 6.378137000000000E+06;
	public static double EccentricitySquared = 6.694379990141320E-03;

	/**
	 * Calculates the distance between two point on earth, assume the earth is an ellipsoid.
	 * 
	 * @param longitudeStart
	 * @param latitudeStart
	 * @param longitudeEnd
	 * @param latitudeEnd
	 * 
	 * @return distance in kilometer (km)
	 * 
	 */
	public static double calculateDistance(double longitudeStart, double latitudeStart, double longitudeEnd, double latitudeEnd) {
		double dlon = Math.abs(longitudeEnd*DegreesInRadiants - longitudeStart*DegreesInRadiants);
		double dlat = Math.abs(latitudeEnd*DegreesInRadiants - latitudeStart*DegreesInRadiants);
		double averageLatitude = (latitudeEnd*DegreesInRadiants + latitudeStart*DegreesInRadiants)/2;
		double averageLatitudeSin = Math.pow(Math.sin(averageLatitude), 2);
		double rlon = EarthRadius*Math.cos(averageLatitude)/
		Math.sqrt(1 - EccentricitySquared*averageLatitudeSin);
        double rlat = EarthRadius*(1 - EccentricitySquared)/
        Math.pow(1 - EccentricitySquared*averageLatitudeSin, 1.5);
        double x = dlon*rlon;
        double y = dlat*rlat;
        double d = Math.sqrt(x*x + y*y);
        return d/1000;
	}

	public static double calculateDistance(Point start, Point end) {
		return Distance.calculateDistance(start.getLongitude(), start.getLatitude(), end.getLongitude(), end.getLatitude());
	}
}
