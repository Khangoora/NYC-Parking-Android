package com.example.jaskirat.alternate_parking.adapter;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jaskirat.alternate_parking.R;
import com.example.jaskirat.alternate_parking.client.SearchRequestManager;
import com.example.jaskirat.alternate_parking.config.Constants;
import com.example.jaskirat.alternate_parking.json.PlaceJSONParser;
import com.example.jaskirat.alternate_parking.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pchekuri on 8/4/14.
 */
public class LocationSearchAdapter extends ArrayAdapter<LocationSearchAdapter.PlaceResult> implements Filterable {

    private ArrayList<PlaceResult> resultList;
    private boolean enabled = true;
    private static final String REFERENCE_COLUMN_NAME = "suggest_intent_extra_data";
    private static final String PARAMETER_LIMIT = "limit";
    private static final String LIMIT = "5";

    public LocationSearchAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.location_search_item, null);
        }
        PlaceResult result = null;
        try {
            result = resultList.get(position);
        } catch(IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        if(result != null) {
            TextView mDescriptionTextView = (TextView) v.findViewById(R.id.tv_location_search_description);
            mDescriptionTextView.setText(result.getDescription());
            TextView mSubDescriptionTextView = (TextView) v.findViewById(R.id.tv_location_search_subdescription);
            String subDescription = result.getSubDescription();
            if(subDescription != null && subDescription.length() > 0) {
                mSubDescriptionTextView.setText(subDescription);
                mSubDescriptionTextView.setVisibility(View.VISIBLE);
            } else {
                mSubDescriptionTextView.setVisibility(View.GONE);
            }
            ImageView ivLocationIndicator = (ImageView) v.findViewById(R.id.iv_location_icon);
            if(!result.isRecentSuggestion()) {
                ivLocationIndicator.setImageDrawable(getContext().getResources().getDrawable(R.drawable.marker_location));
            } else {
                ivLocationIndicator.setImageDrawable(getContext().getResources().getDrawable(R.drawable.recent_location));
            }
        }
        return v;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public PlaceResult getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null && enabled) {
                    // Retrieve the autocomplete results.
                    if(constraint.length() > 2) {
                        resultList = autocomplete(constraint);
                    } else {
                        resultList = getRecentSearchSuggestions(constraint);
                    }
                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    private ArrayList<PlaceResult> autocomplete(CharSequence charInput) {
        String input = charInput.toString();
        ArrayList<PlaceResult> resultList = null;

        String jsonResults = SearchRequestManager.getPlaces(input);

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults);
            List<HashMap<String, String>> resultsMap = (new PlaceJSONParser()).parse(jsonObj);
            resultList = new ArrayList<PlaceResult>();
            for (int i = 0; i < resultsMap.size(); i++) {
                HashMap<String, String> result = resultsMap.get(i);
                String description = result.get("description");
                String subdescription = result.get("sub_description");
                String reference = result.get("reference");
                PlaceResult newResult = new PlaceResult(description, subdescription, reference, false);
                resultList.add(newResult);
            }
        } catch (JSONException e) {
            LogUtil.d(LogUtil.RetroFit, "Cannot process JSON results " + e);
        }

        return resultList;
    }

    public class PlaceResult {

        private String description;
        private String subDescription;
        private String reference;
        private boolean recentSuggestion;

        public PlaceResult(String description, String subDescription, String reference, boolean recentSuggestion) {
            this.description = description;
            this.subDescription = subDescription;
            this.reference = reference;
            this.recentSuggestion = recentSuggestion;
        }

        public String getDescription() {
            return description;
        }

        public String getSubDescription() {
            return subDescription;
        }

        public String getReference() {
            return reference;
        }

        public boolean isRecentSuggestion() {
            return recentSuggestion;
        }

    }

    public void setEnabled(boolean enabled) {
        if(this.enabled != enabled) {
            this.enabled = enabled;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    private ArrayList<PlaceResult> getRecentSearchSuggestions(CharSequence charInput) {
        String query = charInput.toString();
        ArrayList<PlaceResult> resultList = new ArrayList<PlaceResult>();
        Cursor defaultSuggestionsCursor = getContext().getContentResolver().query(buildPlacesProviderUri(), null, null, new String[]{query}, null);
        for(defaultSuggestionsCursor.moveToFirst(); !defaultSuggestionsCursor.isAfterLast(); defaultSuggestionsCursor.moveToNext()) {
            String description = defaultSuggestionsCursor.getString(defaultSuggestionsCursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
            String subDescription = defaultSuggestionsCursor.getString(defaultSuggestionsCursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2));
            String reference = defaultSuggestionsCursor.getString(defaultSuggestionsCursor.getColumnIndex(REFERENCE_COLUMN_NAME)).trim();
            PlaceResult result = new PlaceResult(description, subDescription, reference, true);
            resultList.add(result);
        }
        return resultList;
    }

    private Uri buildPlacesProviderUri() {
        Uri baseUri = Uri.parse(Constants.SEARCH_AUTHORITY);
        return baseUri.buildUpon().appendPath(SearchManager.SUGGEST_URI_PATH_QUERY)
                .appendQueryParameter(PARAMETER_LIMIT, LIMIT).build();
    }


}
