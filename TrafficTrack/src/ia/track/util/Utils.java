package ia.track.util;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

public class Utils {
	public static String getStringPreference(String key, Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String res = pref.getString(key, "NOPREF");
		return res;
	}

	public static Boolean getBooleanPreference(String key, Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Boolean res = pref.getBoolean(key, false);
		return res;
	}

	public static void setPreference(String key, String value, Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}


	public static LinkedList<TimeStampPoint> extractPoints(String jsonResponseText) {
		LinkedList<TimeStampPoint> result = new LinkedList<TimeStampPoint>();

		jsonResponseText = "{\"Array\":" + jsonResponseText + "}";

		JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(jsonResponseText);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("Array");

			int lengthJsonArr = jsonMainNode.length();

			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

				/******* Fetch node values **********/
				String ID=null;
				Double latitude = 0.0, longitude = 0.0;
				long timestamp=0;
				try {
					latitude = Double.parseDouble(jsonChildNode.optString("lat").toString());
					longitude = Double.parseDouble(jsonChildNode.optString("lon").toString());
					JSONObject pointerPk=jsonChildNode.optJSONObject("positionPK");
					timestamp=Long.parseLong(pointerPk.optString("timestamp"));
					ID=pointerPk.optString("id");
					result.add(new TimeStampPoint(latitude, longitude, timestamp,ID));
				} catch (NumberFormatException ex) {
					Log.e("Parsing errato di latitudine/longitudine", ex.getMessage() + ex.getStackTrace());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	
	public static void showDialog(final String title, final String message, final boolean okButton, final boolean cancelButton, final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message).setTitle(title);
		if (cancelButton) {
			builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				}
			});
		}
		if (okButton) {
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

				}
			});
		}

		AlertDialog dialog = builder.create();
		dialog.show();

	}
	
	
	public static void showDialog(final String title, final String message, final boolean okButton, final boolean cancelButton, final Context context,DialogInterface.OnClickListener OkButton,DialogInterface.OnClickListener AnnullaButton) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message).setTitle(title);
		if (cancelButton) {
			builder.setNegativeButton("Annulla",AnnullaButton);
		}
		if (okButton) {
			builder.setPositiveButton("OK", OkButton);
		}

		AlertDialog dialog = builder.create();
		dialog.show();

	}
	
	public static String getFormattedTime(Calendar cal) {

		return String.format("%02d", (cal.get(Calendar.HOUR_OF_DAY))) + ":"
				+ String.format("%02d", (cal.get(Calendar.MINUTE)));
	}
	
	public static String getFormattedDate(Calendar cal) {

		return String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + "/"
				+ String.format("%04d", (cal.get(Calendar.YEAR))) + " - " + getFormattedTime(cal);
	}


}
