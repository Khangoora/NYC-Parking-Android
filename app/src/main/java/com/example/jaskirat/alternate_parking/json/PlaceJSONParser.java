package com.example.jaskirat.alternate_parking.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PlaceJSONParser {

    public List<HashMap<String, String>> parse(JSONObject jsonObject) {
        JSONArray jPlaces = null;

        try {
            jPlaces = jsonObject.getJSONArray("predictions");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jPlaces);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();

        if (jPlaces == null)
            return placesList;

        int placesCount = jPlaces.length();

        HashMap<String, String> place = null;

        for (int i = 0; i < placesCount; i++) {
            try {
                place = getPlace((JSONObject) jPlaces.get(i));
                placesList.add(place);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject jPlace) {
        HashMap<String, String> place = new HashMap<String, String>();

        String id = "";
        String reference = "";
        String description = "";
        String sub_description = "";

        try {
            String tmp = jPlace.getString("description");
            String results[] = tmp.split(",\\s*");

            description = results[0];
            sub_description = results[1] + (results.length > 2 ? ", " + results[2] : "");

            id = jPlace.getString("id");
            reference = jPlace.getString("reference");

            place.put("description", description);
            place.put("sub_description", sub_description);
            place.put("_id", id);
            place.put("reference", reference);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return place;
    }
}
