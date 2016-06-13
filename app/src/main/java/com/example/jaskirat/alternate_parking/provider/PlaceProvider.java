package com.example.jaskirat.alternate_parking.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import com.example.jaskirat.alternate_parking.R;
import com.example.jaskirat.alternate_parking.client.SearchRequestManager;
import com.example.jaskirat.alternate_parking.config.Constants;
import com.example.jaskirat.alternate_parking.database.RecentSearchesDB;
import com.example.jaskirat.alternate_parking.json.PlaceJSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;


public class PlaceProvider extends ContentProvider {

    public static final String AUTHORITY = Constants.SEARCH_AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/locations");
    private static final int SUGGESTIONS = 1;

    private static final UriMatcher mUriMatcher = buildUriMatcher();

    RecentSearchesDB mSearchDB;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SUGGESTIONS);

        return uriMatcher;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor c = null;
        PlaceJSONParser parser = new PlaceJSONParser();
        String jsonString = "";
        List<HashMap<String, String>> list = null;
        MatrixCursor mCursor = null;
        if (mUriMatcher.match(uri) == SUGGESTIONS) {
            mCursor = new MatrixCursor(new String[]{"_id", SearchManager.SUGGEST_COLUMN_ICON_1, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2, SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA, RecentSearchesDB.PLACE_DETAILS});
            if (selectionArgs[0].length() > 2) {
                parser = new PlaceJSONParser();
                jsonString = SearchRequestManager.getPlaces(selectionArgs[0]);
                try {
                    list = parser.parse(new JSONObject(jsonString));

                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String, String> hMap = (HashMap<String, String>) list.get(i);
                        mCursor.addRow(new String[]{Integer.toString(i), Integer.toString(R.drawable.marker_location), hMap.get("description"), hMap.get("sub_description"), hMap.get("reference"), ""});
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (mCursor.getCount() == 0)
                    mCursor.addRow(new String[]{Integer.toString(0), Integer.toString(R.drawable.search_location), "Current Location", null, null, null});

                c = mCursor;
            } else {
                c = mSearchDB.getAllLocations();
            }

        }
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean onCreate() {
        mSearchDB = new RecentSearchesDB(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = mSearchDB.insert(values);
        Uri result = null;
        if (rowID > 0) {
            result = ContentUris.withAppendedId(CONTENT_URI, rowID);
        } else {
            try {
                throw new SQLException("Failed to insert : " + uri);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
