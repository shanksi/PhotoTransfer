package shanksi.phototransfer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class PhotoDisplayActivity
        extends AppCompatActivity
        implements FileInfoExtractor.FileStatusListener {
    private final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;
    private PhotoListDisplayAdapter adapter;
    private ArrayList<FileInfoExtractor> imageUris;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);
                // MY_PERMISSIONS_REQUEST_READ_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Starting the transfer", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                EditText t = (EditText) findViewById(R.id.editText);

                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (mgr != null) mgr.hideSoftInputFromWindow(t.getWindowToken(), 0);

                new FileCopier(getBaseContext()).CopyFiles(imageUris, t.getText().toString().trim());
            }
        });

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
            OpenSettings();
        }
    }

    private void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            ArrayList<Uri> uris = new ArrayList<>();
            uris.add(imageUri);
            handleImages(uris);
        }
    }

    private void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        handleImages(uris);
    }

    private void handleImages(ArrayList<Uri> uris) {
        if (uris != null) {
            imageUris = map(uris);
            ListView l = (ListView) findViewById(R.id.listView);
            adapter = new PhotoListDisplayAdapter(this, imageUris);
            l.setAdapter(adapter);
        }
    }

    private ArrayList<FileInfoExtractor> map(ArrayList<Uri> uriList) {
        ArrayList<FileInfoExtractor> newList = new ArrayList<>();
        for (Uri uri : uriList) {
            newList.add(new FileInfoExtractor(uri, this, this));
        }
        return newList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return OpenSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean OpenSettings() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        return true;
    }

    @Override
    public void onStatusChanged(FileInfoExtractor fileInfoExtractor) {
        adapter.notifyDataSetChanged();
    }
}
