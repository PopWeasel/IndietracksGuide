package com.roadsidepoppies.indietracks.guide2016;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class InfoActivity extends AppCompatActivity {

    private final static String TAG = "InfoActivity";

    private static Intent intent = new Intent();
    private static final String CURRENT_INFO = "CurrentInfo";
    private int currentItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_main);
        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_INFO)) {
            currentItemId = savedInstanceState.getInt(CURRENT_INFO);
        }
        if (currentItemId < 0)
            displayInfoFragment(InfoFragment.GETTING_THERE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.info_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" Indietracks Info ");
        actionBar.setIcon(R.mipmap.ic_indietracks);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_INFO, currentItemId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.info_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        currentItemId = item.getItemId();
        switch (item.getItemId()) {
            case R.id.info_getting_there:
                displayInfoFragment(InfoFragment.GETTING_THERE);
                return true;
            case R.id.info_taxis:
                displayInfoFragment(InfoFragment.TAXIS);
                return true;
            case R.id.info_about:
                displayInfoFragment(InfoFragment.ABOUT);
                return true;
            case R.id.info_action:
                intent.setAction("com.roadsidepoppies.indietracks.guide2016.INDIETRACKS");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.settings_action:
                intent.setAction("com.roadsidepoppies.indietracks.guide2016.SETTINGS");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case android.R.id.home:
                intent.setAction("com.roadsidepoppies.indietracks.guide2016.INDIETRACKS");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void displayInfoFragment(String infoType) {
        InfoFragment infoFragment = new InfoFragment();
        Bundle arguments = new Bundle();
        arguments.putString(InfoFragment.INFO_TYPE, infoType);
        infoFragment.setArguments(arguments);
        FrameLayout view = (FrameLayout) findViewById(R.id.infoContent);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.infoContent, infoFragment);
        if (getSupportFragmentManager().findFragmentById(R.id.infoContent) != null)
            transaction.addToBackStack(null);
        transaction.commit();
    }
}
