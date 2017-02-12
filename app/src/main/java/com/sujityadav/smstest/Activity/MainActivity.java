package com.sujityadav.smstest.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.sujityadav.smstest.Adapter.ConversationAdapter;
import com.sujityadav.smstest.Model.Conversations;
import com.sujityadav.smstest.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,SearchView.OnQueryTextListener {

    private ArrayList<Conversations> messageBoxObjects;
    private ArrayList<Conversations> filteredModelList ;
    private RecyclerView mRecyclerView;
    String cursorFilter;
    FloatingActionButton fab;
    public final Uri CONVERSATIONS_CONTENT_PROVIDER = Uri.parse("content://mms-sms/conversations?simple=true");


    public static final int MULTIPLE_PERMISSIONS = 10;

    String[] permissions = new String[] {
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Inbox");
        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MainActivity.this,SendSms.class);
                startActivity(i);
            }
        });
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        }
        else{
            getSupportLoaderManager().initLoader(0, null, this);
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                SearchView searchView = new SearchView(MainActivity.this);
        searchView.setOnQueryTextListener(this);
        item.setActionView(searchView);
                return true;
            case R.id.cloud:
               Intent intent= new Intent(MainActivity.this,GdriveUpload.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//
//        MenuItem item = menu.add("Search");
//        item.setIcon(android.R.drawable.ic_menu_search);
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        SearchView searchView = new SearchView(MainActivity.this);
//        searchView.setOnQueryTextListener(this);
//        item.setActionView(searchView);
//
//        return true;
//    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse("content://mms-sms/conversations?simple=true");

        String[] reqCols = new String[] { "_id", "recipient_ids", "message_count", "snippet", "date", "read" };
        return new CursorLoader(getApplicationContext(),baseUri, reqCols, null, null, "date DESC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case 0:
                messageBoxObjects= new ArrayList<>();

                String[] reqCols = new String[] { "_id", "recipient_ids", "message_count", "snippet", "date", "read" };
//                Cursor cursor = ((CustomCursorRecyclerViewAdapter) mRecyclerView.getAdapter()).getCursor();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Conversations messageBoxObject = new Conversations();
               messageBoxObject.set_id(cursor.getInt(cursor.getColumnIndex(reqCols[0])));
                messageBoxObject.setRecipient_ids(cursor.getString(cursor.getColumnIndex(reqCols[1])));
                messageBoxObject.setMessage_count(cursor.getString(cursor.getColumnIndex(reqCols[2])));
                messageBoxObject.setSnippet(cursor.getString(cursor.getColumnIndex(reqCols[3])));
                messageBoxObject.setDate(cursor.getLong(cursor.getColumnIndex(reqCols[4])));
                messageBoxObject.setRead(cursor.getInt(cursor.getColumnIndex(reqCols[5])));
                messageBoxObject.setAddress(getcontact(cursor.getString(cursor.getColumnIndex(reqCols[1]))));
                messageBoxObjects.add(messageBoxObject);
            }
        }
                filteredModelList=messageBoxObjects;
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
                ConversationAdapter mAdapter = new ConversationAdapter(MainActivity.this,messageBoxObjects);

                mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
//                ((ConversationAdapter) mRecyclerView.getAdapter()).swapdata(messageBoxObjects);



                break;
            default:
                throw new IllegalArgumentException("no loader id handled!");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

            filteredModelList = filter(messageBoxObjects, newText);
            ((ConversationAdapter) mRecyclerView.getAdapter()).swapdata(filteredModelList);



        return true;
    }

    private String getcontact(String _id) {
        ContentResolver cr = getApplicationContext().getContentResolver();
        Cursor pCur = cr.query(
                Uri.parse("content://mms-sms/canonical-addresses"), new String[]{"address"},
                "_id" + " = ?",
                new String[]{_id}, null);

        String contactAddress = null;

        if (pCur != null) {
            if (pCur.getCount() != 0) {
                pCur.moveToNext();
                contactAddress = pCur.getString(pCur.getColumnIndex("address"));
            }
            pCur.close();
        }
        return contactAddress;
    }



    private  ArrayList<Conversations> filter(ArrayList<Conversations> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final ArrayList<Conversations> filteredModelList = new ArrayList<>();
        for (Conversations model : models) {
            final String address = model.getAddress().toLowerCase();
            final String body = model.getSnippet().toLowerCase();
            if (address.contains(lowerCaseQuery)||body.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        else{
            getSupportLoaderManager().initLoader(0, null, this);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSupportLoaderManager().initLoader(0, null, this);
                } else {
                    // no permissions granted.
                }
                return;
            }
        }

    }}
