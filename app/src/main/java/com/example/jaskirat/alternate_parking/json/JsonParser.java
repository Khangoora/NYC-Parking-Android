package com.example.jaskirat.alternate_parking.json;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Intent;

import com.example.jaskirat.alternate_parking.R;
import com.example.jaskirat.alternate_parking.background.LocationInsertTask;
import com.example.jaskirat.alternate_parking.bus.BusProvider;
import com.example.jaskirat.alternate_parking.database.RecentSearchesDB;
import com.example.jaskirat.alternate_parking.event.GeocodeEvent;
import com.example.jaskirat.alternate_parking.event.PlaceEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JsonParser {
    public void parsePlaceDetails(JSONObject jsonObject) throws JSONException {
        String lat = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getString("lat");
        String lng = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getString("lng");
        String reference = jsonObject.getJSONObject("result").getString("reference");
        String address = jsonObject.getJSONObject("result").getString("formatted_address");
        String name = jsonObject.getJSONObject("result").getString("name");
        String results[] = address.split(",\\s*");
        String description = results[0];

        if(name != null) {
            description = name;
        }

        String sub_description = "";

        if(results.length > 2) {
            sub_description += results[1] + ", " + results[2];
        }
        BusProvider.getInstance().register(this);
        BusProvider.getInstance().post(new PlaceEvent(Double.parseDouble(lat), Double.parseDouble(lng), description, address));
        BusProvider.getInstance().unregister(this);

        saveRecentSearch(description, sub_description, reference);

    }

    private void saveRecentSearch(String description, String sub_description, String reference) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SearchManager.SUGGEST_COLUMN_TEXT_1, description);
        contentValues.put(SearchManager.SUGGEST_COLUMN_ICON_1, Integer.toString(R.drawable.recent_location));
        contentValues.put(SearchManager.SUGGEST_COLUMN_TEXT_2, sub_description);
        contentValues.put(RecentSearchesDB.PLACE_DETAILS, "");
        contentValues.put(SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA, reference);

        LocationInsertTask insertTask = new LocationInsertTask();
        insertTask.execute(contentValues);
    }

    public void parseAddress(JSONObject jsonObject) throws JSONException {
        String short_address = null;
        String status = jsonObject.getString("status");
        try {
            JSONArray result = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            short_address = result.getJSONObject(0).getString("short_name") + " " + result.getJSONObject(1).getString("short_name");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BusProvider.getInstance().register(this);
        BusProvider.getInstance().post(new GeocodeEvent(Boolean.parseBoolean(status), short_address));
        BusProvider.getInstance().unregister(this);
    }
}
