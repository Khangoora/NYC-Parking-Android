package com.example.jaskirat.alternate_parking.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jaskirat.alternate_parking.R;
import com.example.jaskirat.alternate_parking.util.LogUtil;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;


public class ShareActivity extends Activity {

    private Button btnClose, btnShareFacebook, btnShareEmail,
            btnShareTwitter, btnShareSMS, btnReview;

    private UiLifecycleHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initView();
        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
    }

    private void initView() {
        btnClose = (Button) findViewById(R.id.btn_close);
        btnShareFacebook = (Button) findViewById(R.id.btn_fb_share);
        btnShareEmail = (Button) findViewById(R.id.btn_email_share);
        btnShareTwitter = (Button) findViewById(R.id.btn_twitter_share);
        btnShareSMS = (Button) findViewById(R.id.btn_sms_share);
        btnReview = (Button) findViewById(R.id.btn_leave_review);

        btnClose.setOnClickListener(closeClickListener);
        btnShareFacebook.setOnClickListener(shareFacebookClickListener);
        btnShareEmail.setOnClickListener(shareEmailClickListener);
        btnShareTwitter.setOnClickListener(shareTwitterClickListener);
        btnShareSMS.setOnClickListener(shareSMSClickListener);
        btnReview.setOnClickListener(reviewClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                LogUtil.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                LogUtil.i("Activity", "Success!");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        com.facebook.AppEventsLogger.activateApp(getApplicationContext(), getString(R.string.fb_app_id));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener closeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener shareFacebookClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareFacebook();
        }
    };

    private View.OnClickListener shareEmailClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareEmail();
        }
    };

    private View.OnClickListener shareTwitterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareTwitter();
        }
    };

    private View.OnClickListener shareSMSClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareSMS();
        }
    };

    private View.OnClickListener reviewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            review();
        }
    };

    private void shareFacebook() {
        if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {

            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                    .setName("NYC Parking")
                    .setLink(getString(R.string.play_store_link))
                    .setDescription(getString(R.string.share_message))
                    .build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } else {
            Toast.makeText(this, "no facebook app detected", Toast.LENGTH_LONG).show();
        }
    }

    private void shareTwitter() {
        String tweetUrl = "https://twitter.com/intent/tweet?text= + " + getString(R.string.share_message) + "&url=" + getString(R.string.shortened_url);

        Uri uri = Uri.parse(tweetUrl);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void shareEmail() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message) + " " + getString(R.string.play_store_link));
        startActivity(intent);
    }

    private void shareSMS() {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", getString(R.string.share_message) + " " + getString(R.string.play_store_link));
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
    }

    private void review() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.play_store_link)));
        startActivity(intent);
    }
}
