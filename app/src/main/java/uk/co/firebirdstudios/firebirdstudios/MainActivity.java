package uk.co.firebirdstudios.firebirdstudios;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, EquipmentDialogFragment.equipmentDialogFragmentListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private String[] equipmentArray;
    private String equipmentSelected;
    private AuthPreferences authPreferences;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authPreferences = new AuthPreferences(this);
        equipmentArray = getResources().getStringArray(R.array.equipment_hire);
        setContentView(R.layout.activity_main_app_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                FragmentHome fragmenthome = new FragmentHome();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragmenthome)
                        .commit();
                break;
            case 1:
                if(authPreferences.isLoggedIn()) {
                    FragmentBookAStudio fragmentbookastudio = new FragmentBookAStudio();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, fragmentbookastudio)
                            .commit();
                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Not Logged In");
                    alertDialog.setMessage("Please Log in to send a booking");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Log In",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(),ActivityLogin.class);
                            startActivity(intent);
                        }
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"cancel",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
                break;
            case 2:
                FragmentPricing fragmentpricing = new FragmentPricing();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragmentpricing)
                        .commit();
                break;
            case 3:
                Intent intent = new Intent(this, ActivityAboutUs.class);
                this.startActivity(intent);

                break;
            case 4:
                FragmentContactUs fragmentcontactus = new FragmentContactUs();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragmentcontactus)
                        .commit();
                break;

        }
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ActivitySettings.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    public void TestingToast() {
        Toast.makeText(getApplicationContext(), "YES", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onConfirm(ArrayList<Integer> arrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        if (arrayList.size() != 0) {

            for (int i = 0; i < arrayList.size(); i++) {
                String equipment = equipmentArray[arrayList.get(i)];
                stringBuilder.append(equipment).append("\n");

            }
            equipmentSelected = stringBuilder.toString();
            AuthPreferences authPreferences = new AuthPreferences(getApplicationContext());
            authPreferences.setEquipment(equipmentSelected);
        }
    }

    @Override
    public void onCancel() {

    }
}
