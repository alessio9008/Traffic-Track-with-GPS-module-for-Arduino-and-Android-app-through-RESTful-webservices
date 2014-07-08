package ia.track.util;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class PositionDetail implements Comparable<PositionDetail>{

	private TimeStampPoint fromPoint,toPoint;
	private int colore=1;
	private Document document;
	private ArrayList<LatLng> pointsLink;
	private double distance=0;
	private double speed=0;
	
	public static final double MAX_SPEED=80/3.6;
	public static final double RED_SPEED=15;
	public static final double ORANGE_SPEED=30;
	
	
	public double getDistance() {
		return distance;
	}


	public PositionDetail(TimeStampPoint fromPoint, TimeStampPoint toPoint, int colore, Document document, ArrayList<LatLng> pointsLink) {
		super();
		this.fromPoint = fromPoint;
		this.toPoint = toPoint;
		this.colore = colore;
		this.document = document;
		this.pointsLink = pointsLink;
	}
	
	public PositionDetail(){}

	public TimeStampPoint getFromPoint() {
		return fromPoint;
	}

	public void setFromPoint(TimeStampPoint fromPoint) {
		this.fromPoint = fromPoint;
	}

	public TimeStampPoint getToPoint() {
		return toPoint;
	}

	public void setToPoint(TimeStampPoint toPoint) {
		this.toPoint = toPoint;
	}

	public int getColore() {
		return colore;
	}

	private void setColore(int colore) {
		this.colore = colore;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public ArrayList<LatLng> getPointsLink() {
		return pointsLink;
	}

	public void setPointsLink(ArrayList<LatLng> pointsLink) {
		this.pointsLink = pointsLink;
	}


	

	public double getSpeed() {
		return speed;
	}

	@Override
	public String toString() {
		return "PositionDetail [fromPoint=" + fromPoint + ", toPoint=" + toPoint + ", colore=" + colore + ", document=" + document + ", pointsLink="
				+ pointsLink + ", distance=" + distance + ", speed=" + speed + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromPoint == null) ? 0 : fromPoint.hashCode());
		result = prime * result + ((toPoint == null) ? 0 : toPoint.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PositionDetail other = (PositionDetail) obj;
		if (fromPoint == null) {
			if (other.fromPoint != null)
				return false;
		} else if (!fromPoint.equals(other.fromPoint))
			return false;
		if (toPoint == null) {
			if (other.toPoint != null)
				return false;
		} else if (!toPoint.equals(other.toPoint))
			return false;
		return true;
	}

	@Override
	public int compareTo(PositionDetail another) {
		// TODO Auto-generated method stub
		if(another==null) return -1;
		if(this.fromPoint.getTimestamp()>=another.getToPoint().getTimestamp()) return 1;
		if(this.getFromPoint().compareTo(another.getFromPoint())==0 && this.getToPoint().compareTo(another.getToPoint())==0) return 0;
		return -1;
	}
	
	private void calculateColour(){
		double speedKm=speed*3.6;
		if( speedKm>= 0 && speedKm <RED_SPEED){
			this.setColore(Color.RED);
		}
		else if(speedKm > RED_SPEED && speedKm <=ORANGE_SPEED){
			//ORANGE
			this.setColore(Color.rgb(255, 128, 0));
		}
		else if(speedKm>ORANGE_SPEED && speedKm<=MAX_SPEED*3.6){
			this.setColore(Color.GREEN);
		}
		else{
			this.setColore(Color.BLACK);
		}
	}
	
	public void calculateDistance(){
		distance=0;speed=0;
		if(pointsLink!=null && pointsLink.size()>1){
			for(int i=1; i<pointsLink.size();i++){
				float[] results = new float[1];
				Location.distanceBetween(pointsLink.get(i-1).latitude, pointsLink.get(i-1).longitude, pointsLink.get(i).latitude, pointsLink.get(i).longitude, results);
				distance+=results[0];
			}
			speed=distance/((toPoint.getTimestamp()-fromPoint.getTimestamp())/1000);
			calculateColour();
		}
		else{
			Log.e("Calcolo distanza","Non posso calcolare la distanza");
		}
	}
	
	
	
}
