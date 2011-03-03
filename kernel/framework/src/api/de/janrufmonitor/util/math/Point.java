package de.janrufmonitor.util.math;

public class Point {

	double m_lng;
	double m_lat;
	
	int m_accurance;
	
	public Point() {
		this (0,0,-1);
	}

	public Point(double lng, double lat) {
		this (lng, lat, -1);
	}
	
	public Point(double lng, double lat, int accurance) {
		this.m_lng = lng;
		this.m_lat = lat;
		this.m_accurance = accurance;
	}
	
	public double getLongitude() {
		return m_lng;
	}

	public void setLongitude(double m_lng) {
		this.m_lng = m_lng;
	}

	public double getLatitude() {
		return m_lat;
	}

	public void setLatitude(double m_lat) {
		this.m_lat = m_lat;
	}

	public int getAccurance() {
		return m_accurance;
	}

	public void setAccurance(int m_accurance) {
		this.m_accurance = m_accurance;
	}

	public String toString() {
		return this.m_accurance+";"+this.m_lng+";"+this.m_lat;
	}
}
