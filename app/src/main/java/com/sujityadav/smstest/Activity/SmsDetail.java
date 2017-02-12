package com.sujityadav.smstest.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.sujityadav.smstest.Adapter.SmsDetailAdapter;
import com.sujityadav.smstest.Model.Conversations;
import com.sujityadav.smstest.R;

import java.util.ArrayList;
import java.util.List;

public class SmsDetail extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {


    String[] permissions = new String[] {
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
    };

    public static final int MULTIPLE_PERMISSIONS = 15;
    RecyclerView mRecyclerView;
    int _id;
    String title;
    private ArrayList<Conversations> messageBoxObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_detail);

        _id=getIntent().getIntExtra("_id",0);
        title=getIntent().getStringExtra("address");
        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        }
        else{
            getSupportLoaderManager().initLoader(_id, null, this);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse("content://sms/");
        return new CursorLoader(getApplicationContext(),baseUri, null,  "thread_id=" + id, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor1) {
        messageBoxObjects= new ArrayList<>();
        String[] columns = new String[]{"address", "person", "date", "body", "type"};
        if (cursor1.getCount() > 0) {
            String count = Integer.toString(cursor1.getCount());
            while (cursor1.moveToNext()) {
                Conversations messageBoxObject = new Conversations();
                messageBoxObject.setSnippet(cursor1.getString(cursor1.getColumnIndex(columns[3])));
                messageBoxObject.setDate(cursor1.getLong(cursor1.getColumnIndex(columns[2])));
                messageBoxObject.setAddress(cursor1.getString(cursor1.getColumnIndex(columns[0])));
                messageBoxObjects.add(messageBoxObject);

            }

        }


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        SmsDetailAdapter mAdapter = new SmsDetailAdapter(SmsDetail.this,messageBoxObjects);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(SmsDetail.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        else{
            getSupportLoaderManager().initLoader(_id, null, this);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSupportLoaderManager().initLoader(_id, null, this);
                } else {
                    // no permissions granted.
                }
                return;
            }
        }

    }
}
