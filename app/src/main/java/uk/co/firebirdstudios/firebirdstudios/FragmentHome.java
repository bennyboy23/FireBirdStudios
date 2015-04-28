package uk.co.firebirdstudios.firebirdstudios;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class FragmentHome extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View layout = inflater.inflate(R.layout.fragment_home, container, false);
        ImageView imageView = (ImageView)layout.findViewById(R.id.homeImage);
        Picasso.with(getActivity().getApplicationContext())
                .load(R.drawable.studioimage)
                .resize(getResources().getDisplayMetrics().widthPixels,500)
                .into(imageView);
        return layout;
    }
}
