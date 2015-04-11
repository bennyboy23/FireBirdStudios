package uk.co.firebirdstudios.firebirdstudios;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import tabs.SlidingTabLayout;

/**
 * Created by Benjy on 23/12/14.
 */
public class ActivityAboutUs extends ActionBarActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
            private final WeakReference<ImageView> imageViewReference;
            private int data = 0;

            public BitmapWorkerTask(ImageView imageview) {
                imageViewReference = new WeakReference<>(imageview);
            }

            @Override
            protected Bitmap doInBackground(Integer... params) {
                data = params[0];
                return decodeBitmapFromResource(getResources(), data, 100, 100);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (imageViewReference != null && bitmap != null) {
                    final ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }

        public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }

        public static Bitmap decodeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        }

        public void loadBitmap(int resId, ImageView imageView) {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(resId);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View layout;
            Bundle bundle = getArguments();

            if (bundle.getInt("position") == 0) {
                layout = inflater.inflate(R.layout.fragment_owners, container, false);
                naomiImageView = (ImageView)layout.findViewById(R.id.Naomi);
                loadBitmap(R.drawable.naomi,naomiImageView);
                charlieImageView = (ImageView) layout.findViewById(R.id.Charlie);
                loadBitmap(R.drawable.charlie, charlieImageView);
                return layout;
            } else if (bundle.getInt("position") == 1) {
                layout = inflater.inflate(R.layout.fragment_studio, container, false);
                return layout;
            } else {
                layout = inflater.inflate(R.layout.fragment_gallery, container, false);
                return layout;
            }


        }

    }
}
