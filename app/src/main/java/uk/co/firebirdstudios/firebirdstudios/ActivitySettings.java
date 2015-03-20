package uk.co.firebirdstudios.firebirdstudios;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;


public class ActivitySettings extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button signOut;

    private AuthPreferences authPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authPreferences = new AuthPreferences(this);

        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        signOut = (Button)findViewById(R.id.sign_out_button);
        signOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        authPreferences.clearUser();
        Intent i = new Intent(this, ActivityLogin.class);
        startActivity(i);
        finish();
    }
}