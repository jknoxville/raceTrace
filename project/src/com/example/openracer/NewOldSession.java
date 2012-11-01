package com.example.openracer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class NewOldSession extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_old_session);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_old_session, menu);
        return true;
    }
    
    //go to set up session screen
    public void newSession(View view) {
    	Intent intent = new Intent(this, NewSessionActivity.class);
    	startActivity(intent);
    }
}
