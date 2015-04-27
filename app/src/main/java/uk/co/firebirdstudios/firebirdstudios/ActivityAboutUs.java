package uk.co.firebirdstudios.firebirdstudios;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import tabs.SlidingTabLayout;


public class ActivityAboutUs extends ActionBarActivity {
    public static TypedArray array;
    Toolbar toolbar;
    public static Integer[] imageValues = {
            R.drawable.coffeeplease,
            R.drawable.drumsplussun,
            R.drawable.soundproofing,
            R.drawable.cafecoffee,
            R.drawable.drums_birdseye,
            R.drawable.moredrumming,
            R.drawable.noiseboxes,
            R.drawable.studioimage,
            R.drawable.spares

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        array = obtainStyledAttributes(R.styleable.MyGallery);
        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        SlidingTabLayout mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);
        mTabs.setViewPager(mPager);


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        String[] tabs;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.AboutUsTabs);
        }

        @Override
        public Fragment getItem(int position) {
            return AboutUsFragments.getInstance(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class AboutUsFragments extends Fragment {

        private ImageView charlieImageView;
        private ImageView naomiImageView;

        public static AboutUsFragments getInstance(int position) {

            AboutUsFragments aboutUsFragments = new AboutUsFragments();

            Bundle args = new Bundle();
            args.putInt("position", position);
            aboutUsFragments.setArguments(args);
            return aboutUsFragments;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            final View layout;
            Bundle bundle = getArguments();
            switch (bundle.getInt("position")) {
                case 0:
                    layout = inflater.inflate(R.layout.fragment_owners, container, false);
                    naomiImageView = (ImageView) layout.findViewById(R.id.Naomi);
                    Picasso.with(getActivity().getApplicationContext())
                            .load(R.drawable.naomi)
                            .resize(300, 400)
                            .into(naomiImageView);

                    charlieImageView = (ImageView) layout.findViewById(R.id.Charlie);
                    Picasso.with(getActivity().getApplicationContext())
                            .load(R.drawable.charlie)
                            .resize(300, 400)

                            .into(charlieImageView);
                    return layout;

                case 1:
                    layout = inflater.inflate(R.layout.fragment_studio, container, false);
                    return layout;

                case 2:
                    layout = inflater.inflate(R.layout.fragment_gallery, container, false);
                    Gallery gallery = (Gallery) layout.findViewById(R.id.gallery);
                    gallery.setAdapter(new ImageAdapter(getActivity().getApplicationContext()));
                    gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ImageView imageView = (ImageView) layout.findViewById(R.id.biggerPicture);
                            Picasso.with(getActivity().getApplicationContext()).load(imageValues[position]).into(imageView);
                        }
                    });
                    return layout;
            }
            return null;

        }

        public static class ImageAdapter extends BaseAdapter {
            private Context context;
            private int itemBackground;

            public ImageAdapter(Context c) {
                context = c;
                itemBackground = array.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);

            }

            @Override
            public int getCount() {
                return imageValues.length;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageview = new ImageView(context);
                Picasso.with(context).load(imageValues[position]).into(imageview);
                imageview.setLayoutParams(new Gallery.LayoutParams(200, 200));
                imageview.setPadding(5, 5, 5, 5);
                imageview.setBackgroundResource(itemBackground);
                return imageview;

            }
        }

    }


}
