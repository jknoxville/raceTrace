package com.example.openracer;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class NewSessionActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_session, menu);
        return true;
    }
}
