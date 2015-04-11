package uk.co.firebirdstudios.firebirdstudios;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class ActivitySettings extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private AuthPreferences authPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authPreferences = new AuthPreferences(this);
        setContentView(R.layout.activity_settings);
        Spinner spinner = (Spinner)findViewById(R.id.locale);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Locales, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button revokeToken = (Button) findViewById(R.id.revokeToken);
        revokeToken.setOnClickListener(this);
        Button signOut = (Button) findViewById(R.id.sign_out_button);
        signOut.setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, ActivityLogin.class);
        switch (v.getId()) {
            case R.id.sign_out_button:
                authPreferences.clearUser();

                startActivity(i);
                finish();
                break;

            case R.id.revokeToken:
                DisconnectFromApp disconnectFromApp = new DisconnectFromApp();
                disconnectFromApp.execute();
                authPreferences.clearUser();
                startActivity(i);
                finish();
                break;
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 1){
            authPreferences.setLocale("zh");
            Toast.makeText(getApplicationContext(),authPreferences.getLocale(),Toast.LENGTH_SHORT).show();
        }else{
            authPreferences.setLocale("en");
            Toast.makeText(getApplicationContext(),authPreferences.getLocale(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private  class DisconnectFromApp extends AsyncTask<Void, Void, Void> {
        String token = authPreferences.getToken();

        String url = "https://accounts.google.com/o/oauth2/revoke";
        String json = "token={"+token+"}";
        @Override
        protected Void doInBackground(Void... params) {
           Log.d("token",token);
           POST(url, json);


            return null;
        }
    }
    public String POST(String url, String json) {
        InputStream inputStream = null;
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new StringEntity(json));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPost.setHeader(HTTP.CONTENT_TYPE,
                "application/json;charset=UTF-8");
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
            }
            Log.d("Response Code", Integer.toString(httpResponse.getStatusLine().getStatusCode()));
        } catch (Exception e) {
            result = e.toString();
        }
        return result;
    }
    protected String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;

    }
}