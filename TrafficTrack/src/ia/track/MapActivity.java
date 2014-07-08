package ia.track;

import ia.track.R;
import ia.track.util.GMapV2GetRouteDirection;
import ia.track.util.PositionDetail;
import ia.track.util.RequestToServer;
import ia.track.util.TimeStampPoint;
import ia.track.util.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.w3c.dom.Document;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

import android.location.Location;
import android.location.LocationManager;
import android.net.NetworkInfo.DetailedState;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * 
 */
public class MapActivity extends FragmentActivity {

	private GMapV2GetRouteDirection v2GetRouteDirection;
	private Hashtable<String, LinkedList<TimeStampPoint>> Tablepositions;
	private GoogleMap mGoogleMap;
	private boolean refreshInterval = false;
	private long valuelastime = 1;
	private MarkerOptions markerOptions;
	private Context context;
	private int map_mode = 0;
	private long fromTime = 0, toTime = 0;
	private int zoom;

	public static final float[] COLOUR_VETT = new float[] { BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_MAGENTA,
			BitmapDescriptorFactory.HUE_CYAN, BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_ORANGE,
			BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_ROSE, BitmapDescriptorFactory.HUE_YELLOW };

	private static final int FAVORITE_RESULT_CODE = 1;

	private BroadcastReceiver refreshMapReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("refreshMapReceiver", "Ho ricevuto una richiesta di refresh");
			refreshMap();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		setToTime(System.currentTimeMillis());
		valuelastime = 3600000;
		setFromTime(getToTime() - valuelastime);

		context = this;

		Log.e("MAP_ACTIVITY", "CREATE");
		Tablepositions = new Hashtable<String, LinkedList<TimeStampPoint>>();

		v2GetRouteDirection = new GMapV2GetRouteDirection();
		SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mGoogleMap = supportMapFragment.getMap();

		// Enabling MyLocation in Google Map
		mGoogleMap.setMyLocationEnabled(true);
		mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
		mGoogleMap.getUiSettings().setCompassEnabled(true);
		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
		mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
		mGoogleMap.setTrafficEnabled(true);
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
		try {
			map_mode = Integer.parseInt(Utils.getStringPreference("map_mode", this));
		} catch (NumberFormatException e) {
			map_mode = 4;
		}
		mGoogleMap.setMapType(map_mode);
		markerOptions = new MarkerOptions();

		clearMap();
		GetRouteTask getRoute = new GetRouteTask();
		getRoute.execute();
	}

	private class GetRouteTask extends AsyncTask<String, Void, String> {

		private ProgressDialog Dialog;
		private long fromTime;
		private long toTime;
		private String response = "";
		private int currentColor = 0;

		private Hashtable<String, LinkedList<PositionDetail>> positionDetail;

		public GetRouteTask(long fromTime, long toTime) {
			super();
			positionDetail = new Hashtable<String, LinkedList<PositionDetail>>();
			if (fromTime <= toTime) {
				this.fromTime = fromTime;
				this.toTime = toTime;
			} else {
				this.fromTime = toTime;
				this.toTime = fromTime;
			}

		}

		public GetRouteTask() {
			super();
			positionDetail = new Hashtable<String, LinkedList<PositionDetail>>();

			this.toTime = System.currentTimeMillis();
			valuelastime = 3600000;
			this.fromTime = toTime - valuelastime;
		}

		private void getPoints() throws Exception {
			Resources res = getResources();
			String serverURL = res.getString(R.string.webservice_pointer) + "range/" + fromTime + "/" + toTime;
			Log.e("URL GET", serverURL);
			String response = RequestToServer.sendGet(serverURL);
			LinkedList<TimeStampPoint> list = Utils.extractPoints(response);
			for (TimeStampPoint point : list) {
				LinkedList<TimeStampPoint> ListTmp = Tablepositions.get(point.getID());
				if (ListTmp != null)
					ListTmp.add(point);
				else {
					ListTmp = new LinkedList<TimeStampPoint>();
					ListTmp.add(point);
				}
				Tablepositions.put(point.getID(), ListTmp);
			}
			Log.i("point", "" + Tablepositions);
		}

		@Override
		protected void onPreExecute() {
			Dialog = new ProgressDialog(MapActivity.this);
			Dialog.setMessage("Caricamento del percorso...");
			Dialog.show();
			Tablepositions.clear();
			clearMap();
		}

		@Override
		protected String doInBackground(String... urls) {
			try {
				getPoints();
				Enumeration<LinkedList<TimeStampPoint>> enume = Tablepositions.elements();
				while (enume.hasMoreElements()) {
					// Get All Route values
					LinkedList<TimeStampPoint> position = enume.nextElement();
					if (position.size() >= 2) {
						int map_route_mode=0;
						try {
							map_route_mode = Integer.parseInt(Utils.getStringPreference("map_route_mode", context));
						} catch (NumberFormatException e) {
							map_route_mode = 0;
						}
						if(map_route_mode==1)
							calulateFree(position);
						else
							calculateDrivingRoute(position);
						
							
					} else
						response = "PochiPunti";
				}
			} catch (java.net.SocketTimeoutException e) {
				response = "Error";
				Log.e("MAP_ACTIVITY: eccezione nell'invio del get per la ricezione degli ultimi punti (TIMEOUT)", e.getMessage() + e.getStackTrace().toString());
			} catch (Exception e) {
				response = "Error";
				Log.e("MAP_ACTIVITY: eccezione nell'invio del get per la ricezione degli ultimi punti", e.getMessage() + e.getStackTrace().toString());
			}
			return response;

		}

		private void calulateFree(LinkedList<TimeStampPoint> position) {

			Log.i("POSITION.SIZE", "" + position.size());
			Collections.sort(position);
			for (int counter = 1; counter < position.size(); counter++) {
				PositionDetail detail = new PositionDetail();
				TimeStampPoint fromPosition = position.get(counter - 1);

				TimeStampPoint toPosition = position.get(counter);

				detail.setFromPoint(fromPosition);
				detail.setToPoint(toPosition);

				ArrayList<LatLng> listarray = new ArrayList<LatLng>();
				listarray.add(fromPosition.getLatLng());
				listarray.add(toPosition.getLatLng());
				detail.setPointsLink(listarray);
				detail.calculateDistance();
				LinkedList<PositionDetail> list = positionDetail.get(toPosition.getID());
				if (list != null)
					list.add(detail);
				else {
					list = new LinkedList<PositionDetail>();
					list.add(detail);
				}
				positionDetail.put(toPosition.getID(), list);
			}

			response = "Success";
		}

		private void calculateDrivingRoute(LinkedList<TimeStampPoint> position) throws InterruptedException {
			int step = (int) Math.round(((double) position.size()) / 30);

			if (step < 1)
				step = 1;

			Log.i("POSITION.SIZE", "" + position.size());
			Collections.sort(position);
			for (int counter = step; counter < position.size(); counter += step) {
				PositionDetail detail = new PositionDetail();
				TimeStampPoint fromPosition = position.get(counter - step);
				int i = 0;
				while (true) {

					if (counter + i < position.size()) {

						TimeStampPoint toPosition = position.get(counter + i);

						detail.setFromPoint(fromPosition);
						detail.setToPoint(toPosition);

						Document document = v2GetRouteDirection.getDocument(fromPosition.getLatLng(), toPosition.getLatLng(), v2GetRouteDirection.MODE_DRIVING);

						detail.setDocument(document);
						detail.setPointsLink(v2GetRouteDirection.getDirection(detail.getDocument()));
						Thread.sleep(300);
						detail.calculateDistance();
						if (detail.getSpeed() <= PositionDetail.MAX_SPEED) {
							LinkedList<PositionDetail> list = positionDetail.get(toPosition.getID());
							if (list != null)
								list.add(detail);
							else {
								list = new LinkedList<PositionDetail>();
								list.add(detail);
							}
							positionDetail.put(toPosition.getID(), list);
							counter = counter + i;
							break;
						} else {
							i++;
						}
					} else {
						break;
					}
				}

			}

			response = "Success";
		}

		@Override
		protected void onPostExecute(String result) {
			if (response.equalsIgnoreCase("Success")) {

				Log.i("Contenuto tabella delle posizioni", positionDetail.toString());
				addPolyline();
				zoom = 8;
				try {
					zoom = Integer.parseInt(Utils.getStringPreference("zoom_level", context));
				} catch (NumberFormatException e) {
					zoom = 8;
				}
				lastMoveCamera();
				addAllMarkers();
			} else if (response.equalsIgnoreCase("Error")) {
				Utils.showDialog("Impossibile contattare il server", "Non è stato possibile contattare il server.", true, false, context);
			} else {
				if (!refreshInterval)
					Utils.showDialog("Impossibile tracciare il percorso",
							"La finestra temporale non contiene almeno due punti. Scegli un intervallo differente.", true, false, context);
			}

			Dialog.dismiss();
		}

		private void addPolyline() {
			Enumeration<LinkedList<PositionDetail>> enume = positionDetail.elements();
			while (enume.hasMoreElements()) {
				LinkedList<PositionDetail> positionInfo = enume.nextElement();
				for (PositionDetail detail : positionInfo) {
					for (int i = 1; i < detail.getPointsLink().size(); i++) {
						mGoogleMap.addPolyline(new PolylineOptions().add(detail.getPointsLink().get(i - 1), detail.getPointsLink().get(i)).width(5)
								.color(detail.getColore()));
					}
				}
			}
		}

		private void lastMoveCamera() {
			TimeStampPoint maxValue = new TimeStampPoint(0, 0, 0, "maxValue");
			Enumeration<LinkedList<PositionDetail>> enume = positionDetail.elements();
			while (enume.hasMoreElements()) {
				LinkedList<PositionDetail> position = enume.nextElement();
				for (PositionDetail detail : position) {
					if (detail.getToPoint().getTimestamp() > maxValue.getTimestamp())
						maxValue = detail.getToPoint();
				}
			}
			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(maxValue.getLatLng(), zoom));
		}

		private void addAllMarkers() {
			Enumeration<LinkedList<PositionDetail>> enume = positionDetail.elements();
			while (enume.hasMoreElements()) {
				LinkedList<PositionDetail> positionInfo = enume.nextElement();
				int contatoreMarker = 1;
				GregorianCalendar cal = new GregorianCalendar();
				if (positionInfo.size() >= 1) {
					// from time
					TimeStampPoint detail = positionInfo.getFirst().getFromPoint();
					cal.setTimeInMillis(detail.getTimestamp());
					addMarker(detail.getLatLng(), "Car ID: " + detail.getID() + " Number: " + contatoreMarker, Utils.getFormattedDate(cal),
							COLOUR_VETT[currentColor]);
					contatoreMarker++;
					for (PositionDetail newDetail : positionInfo) {
						// to time
						if ((newDetail.getToPoint().getTimestamp() - detail.getTimestamp()) >= 25000) {
							cal.setTimeInMillis(newDetail.getToPoint().getTimestamp());
							// addMarker(newDetail.getToPoint().getLatLng(),
							// "Car ID: " + newDetail.getToPoint().getID() +
							// " Number: " + contatoreMarker,
							// ""+newDetail.getToPoint().getTimestamp(),COLOUR_VETT[currentColor]);
							addMarker(newDetail.getToPoint().getLatLng(), "Car ID: " + newDetail.getToPoint().getID() + " Number: " + contatoreMarker,
									Utils.getFormattedDate(cal), COLOUR_VETT[currentColor]);
							contatoreMarker++;
							detail=newDetail.getToPoint();
						}
					}
				}

				currentColor = (currentColor + 1) % COLOUR_VETT.length;
			}

		}

		private void addMarker(LatLng point, String title, String text, float color) {
			// add marker to Map
			markerOptions.icon(BitmapDescriptorFactory.defaultMarker(color)).title(title).snippet(text);
			markerOptions.position(point);
			markerOptions.draggable(false);
			mGoogleMap.addMarker(markerOptions);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.refresh_map:
			refreshMap();
			return true;
		case R.id.settings:
			showSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	private void clearMap() {
		mGoogleMap.clear();

	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.e("MAP_ACTIVITY", "STOP");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("MAP_ACTIVITY", "DESTROY");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshMapReceiver);
		Log.e("MAP_ACTIVITY", "PAUSE");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(refreshMapReceiver, new IntentFilter("refreshMapBroadcast"));

		Log.e("MAP_ACTIVITY", "RESUME");
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		Log.e("MAP_ACTIVITY", "RESUME_FRAGMENT");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.e("MAP_ACTIVITY", "START");
	}

	@Override
	protected void onRestart() {

		super.onRestart();
		int type = 4;
		try {
			type = Integer.parseInt(Utils.getStringPreference("map_mode", this));
		} catch (NumberFormatException e) {
			type = 4;
		}
		if (type != map_mode) {
			map_mode = type;
			mGoogleMap.setMapType(map_mode);
		}

		Log.e("MAP_ACTIVITY", "RESTART");

		int color = Color.RED;
		int localZoom = 8;
		try {
			localZoom = Integer.parseInt(Utils.getStringPreference("zoom_level", context));
		} catch (NumberFormatException e) {
			localZoom = 8;
		}
		String mode = "NOPREF";
		mode = Utils.getStringPreference("modalita_percorsi", context);

		if (localZoom != zoom) {
			zoom = localZoom;
			long totime = System.currentTimeMillis();
			setToTime(totime);
			setFromTime(totime - valuelastime);
			clearMap();
		}

		refreshMap();

	}

	public void refreshMap() {
		setToTime(System.currentTimeMillis());
		if (!Utils.isOnline(this)) {
			Utils.showDialog("Attenzione", "Connessione a Internet assente. Per inviare i dati raccolti devi nuovamente accedere alla rete.", true, false, this);
		}

		else {
			GetRouteTask getRoute = new GetRouteTask(getFromTime(), getToTime());
			getRoute.execute();
		}
	}

	@Override
	public void onBackPressed() {
		return;
	}

	public long getFromTime() {
		return fromTime;
	}

	public void setFromTime(long fromTime) {
		this.fromTime = fromTime;
	}

	public long getToTime() {
		return toTime;
	}

	public void setToTime(long toTime) {
		this.toTime = toTime;
	}
}