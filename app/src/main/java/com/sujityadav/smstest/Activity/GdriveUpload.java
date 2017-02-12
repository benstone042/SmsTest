package com.sujityadav.smstest.Activity;

import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.sujityadav.smstest.R;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class GdriveUpload extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private static final int RESOLVE_CONNECTION_REQUEST_CODE =100 ;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gdrive_upload);
        getSupportActionBar().setTitle("Uploading...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
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
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {

                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                writer.write(readSmsInbox().toString());
                                writer.close();
                            } catch (IOException e) {

                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("New file")
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();

                            // create a file on root folder
                            Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                    .createFile(mGoogleApiClient, changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    showMessage("Created a file with content: " + result.getDriveFile().getDriveId());
                    finish();
                }
            };

    private void showMessage(String s) {

        Toast.makeText(GdriveUpload.this,s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESOLVE_CONNECTION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }




            private Cursor tryOpenContactsCursorById(long contactId) {
                try {
                    String[] PROJECTION = {ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY,};
                    return getContentResolver().query(
                            ContactsContract.RawContacts.CONTENT_URI.buildUpon()
                                    .appendPath(Long.toString(contactId)).build(),
                            PROJECTION, null, null, null);

                } catch (Exception e) {

                    return null;
                }
            }

            private Cursor tryOpenContactsCursorByAddress(String phoneNumber) {
                try {
                    String[] PROJECTION = {ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY,};
                    return getContentResolver().query(
                            ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon()
                                    .appendPath(Uri.encode(phoneNumber)).build(),
                            PROJECTION, null, null, null);

                } catch (Exception e) {

                    return null;
                }
            }

            private Cursor getSmsInboxCursor() {
                String[] PROJECTION = {
                        Telephony.Sms._ID,
                        Telephony.Sms.ADDRESS,
                        Telephony.Sms.PERSON,
                        Telephony.Sms.THREAD_ID,
                        Telephony.Sms.BODY,
                        Telephony.Sms.DATE,
                        Telephony.Sms.TYPE,
                        Telephony.Sms.READ,
                        Telephony.Sms.DATE_SENT,
                        Telephony.Sms.ERROR_CODE,
                        Telephony.Sms.STATUS,
                        Telephony.Sms.SUBJECT,
                        Telephony.Sms.PROTOCOL,
                        Telephony.Sms.SERVICE_CENTER

                };
                String WHERE_INBOX = Telephony.Sms.TYPE + " = "
                        + Telephony.Sms.MESSAGE_TYPE_INBOX;
                try {
                    return getContentResolver().query(
                            Uri.parse("content://sms"),
                            PROJECTION, WHERE_INBOX, null,
                            "date DESC");
                } catch (Exception e) {

                    return null;
                }

            }

            private StringBuilder readSmsInbox() {
                Cursor cursor = getSmsInboxCursor();
                StringBuilder messages = new StringBuilder();
                String and = "";

                if (cursor != null) {
                    final String[] columns = cursor.getColumnNames();
                    while (cursor.moveToNext()) {
                        messages.append(and);
                        long contactId = cursor.getLong(2);
                        String address = cursor.getString(1);

                        final Map msgMap = new HashMap(columns.length);
                        for (int i = 0; i < columns.length; i++) {
                            String value = cursor.getString(i);
                            msgMap.put(columns[i], cursor.getString(i));
                            messages.append(columns[i]);
                            messages.append("=");
                            messages.append(value);
                            messages.append(";");
                        }
                        and = "\n";

                        String displayName = address;

                        if (contactId > 0) {
                            // Retrieve from Contacts with contact id
                            Cursor contactCursor = tryOpenContactsCursorById(contactId);
                            if (contactCursor != null) {
                                if (contactCursor.moveToFirst()) {
                                    displayName = contactCursor.getString(0);
                                } else {
                                    contactId = 0;
                                }
                                contactCursor.close();
                            }
                        } else {
                            if (contactId <= 0) {
                                // Retrieve with address
                                Cursor contactCursor = tryOpenContactsCursorByAddress(address);
                                if (contactCursor != null) {
                                    if (contactCursor.moveToFirst()) {
                                        displayName = contactCursor
                                                .getString(0);
                                    }
                                    contactCursor.close();
                                }
                            }
                        }

                        messages.append("displayName");
                        messages.append("=");
                        messages.append(displayName);
                        messages.append(";");
                    }
                }
                return messages;
            }


        }
