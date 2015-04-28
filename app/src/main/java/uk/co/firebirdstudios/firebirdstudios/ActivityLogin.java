package uk.co.firebirdstudios.firebirdstudios;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.squareup.picasso.Picasso;

public class ActivityLogin extends ActionBarActivity implements View.OnClickListener {

    private static final int AUTHORIZATION_CODE = 1993;
    private static final int ACCOUNT_CODE = 1601;

    private AuthPreferences authPreferences;
    private AccountManager accountManager;
    private ConnectionChecker connectionChecker;
    boolean connection = false;
    private Toolbar toolbar;

    private final String SCOPE = "https://mail.google.com/ https://www.googleapis.com/auth/userinfo.profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountManager = AccountManager.get(this);
        connectionChecker = new ConnectionChecker(this);
        authPreferences = new AuthPreferences(this);
        //If logged in the app will transport straight to the main activity
        if (authPreferences.getUser() != null
                && authPreferences.getToken() != null) {
            doCoolAuthenticatedStuff();
        } else {
            setContentView(R.layout.activity_activity_login);
            SignInButton logIn = (SignInButton)findViewById(R.id.sign_in_button);

            loadLogo();
            logIn.setOnClickListener(this);
            findViewById(R.id.skip_login).setOnClickListener(this);

        }

    }
    //This is how the Logo is loaded into the imageview on the login page
    public void loadLogo() {
        ImageView logo = (ImageView) findViewById(R.id.logo);
        /* Picasoo is an open source software found at
           http://square.github.io/picasso/
            this is use as the Image loader for
         */
        Picasso.with(this)
                .load(R.drawable.firebird_logo_medium)
                .resize(this.getResources().getDisplayMetrics().widthPixels,700)
                .into(logo);
    }
    /*the following code has been taken from
    http://blog.tomtasche.at/2013/05/google-oauth-on-android-using.html an
    and modified for this app
     */
    private void doCoolAuthenticatedStuff() {
        authPreferences.loggedIn();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
    /*the following code has been taken from
    http://blog.tomtasche.at/2013/05/google-oauth-on-android-using.html an
    and modified for this app
    this is used to access google features from the users selected account
     */
    private void chooseAccount() {

        Intent intent = AccountManager.newChooseAccountIntent(null, null,
                new String[]{"com.google"}, false, null, null, null, null);
        startActivityForResult(intent, ACCOUNT_CODE);

    }

    private void requestToken() {
        Account userAccount = null;
        String user = authPreferences.getUser();
        for (Account account : accountManager.getAccountsByType("com.google")) {
            if (account.name.equals(user)) {
                userAccount = account;

                break;
            }
        }

        accountManager.getAuthToken(userAccount, "oauth2:" + SCOPE, null, this,
                new OnTokenAcquired(), null);
    }

    /*the following code has been taken from
    http://blog.tomtasche.at/2013/05/google-oauth-on-android-using.html an
    and modified for this app
    this is used to invalidate the Token aquired for the account
     */
    protected void invalidateToken() {

        AccountManager accountManager = AccountManager.get(this);
        accountManager.invalidateAuthToken("com.google",
                authPreferences.getToken());

        authPreferences.setToken(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == AUTHORIZATION_CODE) {
                requestToken();
            } else if (requestCode == ACCOUNT_CODE) {
                String accountName = data
                        .getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                authPreferences.setUser(accountName);

                // invalidate old tokens which might be cached. we want a fresh
                // one, which is guaranteed to work
                invalidateToken();

                requestToken();
            }
        }
    }

    @Override
    /*
    this is where the app decides which function to deploy
    depending on which button is pressed
     */
    public void onClick(View v) {
        if (v.getId() == R.id.skip_login) {
            authPreferences.notLoggedIn();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            //here we make a call to the connection checker to ensure the user is connected before allowing the user to log in
            connection = connectionChecker.isConnected();
            if (connection) {

                chooseAccount();
            } else {
                Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Bundle bundle = result.getResult();

                Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (launch != null) {
                    startActivityForResult(launch, AUTHORIZATION_CODE);
                } else {
                    String token = bundle
                            .getString(AccountManager.KEY_AUTHTOKEN);

                    authPreferences.setToken(token);

                    doCoolAuthenticatedStuff();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
