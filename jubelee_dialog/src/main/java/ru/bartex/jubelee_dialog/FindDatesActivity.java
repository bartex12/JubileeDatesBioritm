package ru.bartex.jubelee_dialog;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class FindDatesActivity extends AppCompatActivity {

    public final String TAG = "33333";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_dates);

        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_dates,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                Log.d(TAG, "Домой");
                Intent intent = new Intent(this,PersonsListActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_settings:
                Log.d(TAG, "OptionsItem = action_settings");
                Intent intentSettings = new Intent(this, PrefActivity.class);
                startActivity(intentSettings);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
