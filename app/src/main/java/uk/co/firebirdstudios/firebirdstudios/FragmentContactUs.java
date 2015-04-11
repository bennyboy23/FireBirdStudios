package uk.co.firebirdstudios.firebirdstudios;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Benjy on 23/12/14.
 */
public class FragmentContactUs extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);
        TextView textView = (TextView) v.findViewById(R.id.phoneNumber);
        textView.setOnClickListener(this);
        Button maps = (Button)v.findViewById(R.id.maps);
        maps.setOnClickListener(this);
        return v;


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.phoneNumber) {
            callStudio();
        }if (v.getId() == R.id.maps){
            showMaps();
        }
    }
    public void callStudio(){
        Uri phoneNumber = Uri.parse(getResources().getString(R.string.phoneNumber));
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

        Toast.makeText(getActivity().getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
        i.setData(Uri.parse("geo:51.432356,-2.540121?q=firebird+studios"));
        Intent chooser = Intent.createChooser(i,"Launch Maps");
        startActivity(chooser);
    }
}

