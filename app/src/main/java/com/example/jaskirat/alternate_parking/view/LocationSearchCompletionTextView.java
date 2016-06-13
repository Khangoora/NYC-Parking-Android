package com.example.jaskirat.alternate_parking.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.example.jaskirat.alternate_parking.R;
import com.example.jaskirat.alternate_parking.application.ParkingApp;


public class LocationSearchCompletionTextView extends AutoCompleteTextView {

    public LocationSearchCompletionTextView(Context context) {
        super(context);
    }

    public LocationSearchCompletionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocationSearchCompletionTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean enoughToFilter() {
        return (getText().length() > 0);
    }

    @Override
    protected void replaceText(CharSequence text) {
        super.replaceText(ParkingApp.getInstance().getText(R.string.get_address));
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
}
