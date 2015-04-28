package uk.co.firebirdstudios.firebirdstudios;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;


public class FragmentContactUs extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);
        Button callUs = (Button) v.findViewById(R.id.phoneNumber);
        callUs.setOnClickListener(this);
        Button maps = (Button) v.findViewById(R.id.maps);
        Button facebookLink = (Button) v.findViewById(R.id.facebook_link);
        Button twitterLink = (Button)v.findViewById(R.id.twitter_link);
        maps.setOnClickListener(this);
        facebookLink.setOnClickListener(this);
        twitterLink.setOnClickListener(this);
        return v;


    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.phoneNumber) {
            callStudio();
        }
        if (v.getId() == R.id.maps) {
            showMaps();
        }
        if (v.getId() == R.id.facebook_link) {
            intent = openFacebook(getActivity().getApplicationContext());
            startActivity(intent);
        }
        if (v.getId() == R.id.twitter_link) {
            try {
                Context context = getActivity().getApplicationContext();
                context.getPackageManager().getPackageInfo("com.twitter.android",0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=2699425295"));
                startActivity(intent);
            } catch (Exception e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/firebirdmusicuk"));
                startActivity(intent);
            }
        }
    }

    public void callStudio() {
        Uri phoneNumber = Uri.parse("tel:01179721830");
        PackageManager packageManager = getActivity().getPackageManager();

        Intent callIntent = new Intent(Intent.ACTION_DIAL, phoneNumber);
        List<ResolveInfo> activities = packageManager.queryIntentActivities(callIntent, packageManager.MATCH_DEFAULT_ONLY);
        boolean isSafe = activities.size() > 0;
        if (isSafe) {
            startActivity(callIntent);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "please get a phone app", Toast.LENGTH_SHORT).show();

        }

    }

    public void showMaps() {

        Intent i = new Intent(Intent.ACTION_VIEW);

        Toast.makeText(getActivity().getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
        i.setData(Uri.parse("geo:51.432356,-2.540121?q=firebird+studios"));
        Intent chooser = Intent.createChooser(i, "Launch Maps");
        startActivity(chooser);
    }

    public static Intent openFacebook(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://page/128418193885308")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/FirebirdStudiosUK")); //catches and opens a url to the desired page
        }
    }


}

