package uk.co.firebirdstudios.firebirdstudios;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;


public class ActivityLogin extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private Toolbar toolbar;
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    public static final String PREFS_NAME ="myPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
        setContentView(R.layout.activity_activity_login);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.skip_login).setOnClickListener(this);


    }
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }
    protected void onStop(){
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }
    public void onConnectionFailed(ConnectionResult result){
        if(!mIntentInProgress){
            mConnectionResult = result;
            if(mSignInClicked){
                resolveSignInError();
            }
        }
    }
    public void onConnected(Bundle connectionHint){
        mSignInClicked = false;
        getProfileInformation();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personId= currentPerson.getId();
                Log.d("id",personId);
                String personEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("Name",personName);
                editor.putString("Email",personEmail);
                editor.putString("Id",personId);
                editor.apply();


            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent)
    {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }
    public void onConnectionSuspended(int cause){
        mGoogleApiClient.connect();
    }
    private void resolveSignInError(){
        if(mConnectionResult.hasResolution()){
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),RC_SIGN_IN,null,0,0,0);
            }catch(IntentSender.SendIntentException e){
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_login, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if((v.getId() == R.id.sign_in_button) && !mGoogleApiClient.isConnecting()){

            int playServices = GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE;
            if (playServices != getResources().getInteger(R.integer.google_play_services_version)) {
                GooglePlayServicesUtil.getErrorDialog(playServices,this,0);
            }else {
                mSignInClicked = true;
                resolveSignInError();
                finish();
            }

        }if(v.getId()==R.id.skip_login){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
