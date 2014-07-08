/**
 * 
 */
package ia.track.util;

import android.app.UiAutomation.OnAccessibilityEventListener;

public class TimeStampPoint extends Point implements Comparable<TimeStampPoint> {

	private long timestamp;
	private String ID;


	public TimeStampPoint(double lat, double lon, long timestamp, String ID) {
		super(lat, lon);
		this.timestamp = timestamp;
		this.ID=ID;
	}

	public TimeStampPoint() {
		super();
	}
	
	

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public TimeStampPoint(double lat, double lon) {
		super(lat, lon);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeStampPoint other = (TimeStampPoint) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimeStampPoint [timestamp=" + timestamp + ", ID=" + ID + ", toString()=" + super.toString() + "]";
	}

	@Override
	public int compareTo(TimeStampPoint another) {
		// TODO Auto-generated method stub
		if(another==null) return -1;
		else{
			if(this.getTimestamp()==another.getTimestamp()) return 0;
			else if(this.getTimestamp()>=another.getTimestamp()) return 1;
			else return -1;
		}
	}
	
	
	
}
