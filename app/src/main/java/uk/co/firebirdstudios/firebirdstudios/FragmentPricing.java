package uk.co.firebirdstudios.firebirdstudios;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tabs.SlidingTabLayout;


public class FragmentPricing extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pricing, container, false);

        ViewPager mPager = (ViewPager) v.findViewById(R.id.pager);
        mPager.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager()));
        SlidingTabLayout mTabs = (SlidingTabLayout) v.findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);
        mTabs.setViewPager(mPager);

        return v;


    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        String[] tabs;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.pricingTabs);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentPricingFragments.getInstance(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class FragmentPricingFragments extends Fragment implements View.OnClickListener {
        public static FragmentPricingFragments getInstance(int position) {
            FragmentPricingFragments fragmentPricingFragments = new FragmentPricingFragments();
            Bundle args = new Bundle();
            args.putInt("position", position);
            fragmentPricingFragments.setArguments(args);
            return fragmentPricingFragments;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View layout;
            Bundle bundle = getArguments();
            if (bundle.getInt("position") == 0) {
                layout = inflater.inflate(R.layout.fragment_fragment_pricing, container, false);
                return layout;
            } else {
                layout = inflater.inflate(R.layout.fragment_equipment, container, false);
                TextView bass_amp = (TextView) layout.findViewById(R.id.bass_amp);
                TextView standard_guitar_amp = (TextView) layout.findViewById(R.id.guitar_amp_standard);
                TextView premium_guitar_amp = (TextView) layout.findViewById(R.id.guitar_amp_premium);
                TextView drum_kit = (TextView) layout.findViewById(R.id.drum_kit);
                TextView cymbals = (TextView) layout.findViewById(R.id.cymbals);
                TextView snare = (TextView) layout.findViewById(R.id.snare);

                bass_amp.setOnClickListener(this);
                standard_guitar_amp.setOnClickListener(this);
                premium_guitar_amp.setOnClickListener(this);
                drum_kit.setOnClickListener(this);
                cymbals.setOnClickListener(this);
                snare.setOnClickListener(this);
                return layout;
            }

        }

        @Override
        public void onClick(View v) {
            ImageDialogFragment imageDialogFragment = new ImageDialogFragment();
            switch (v.getId()) {
                case R.id.bass_amp:

                    imageDialogFragment.newInstance(R.id.bass_amp).show(getActivity().getFragmentManager(), "bass amp");
                    break;
                case R.id.guitar_amp_standard:

                    imageDialogFragment.newInstance(R.id.guitar_amp_standard).show(getActivity().getFragmentManager(), "standard guitar amp");
                    break;
                case R.id.guitar_amp_premium:
                    imageDialogFragment.newInstance(R.id.guitar_amp_premium).show(getActivity().getFragmentManager(), "premium guitar amp");
                    break;
                case R.id.drum_kit:
                    imageDialogFragment.newInstance(R.id.drum_kit).show(getActivity().getFragmentManager(), "drum kit");
                    break;
                case R.id.cymbals:
                    imageDialogFragment.newInstance(R.id.cymbals).show(getActivity().getFragmentManager(), "cymbals");
                    break;
                case R.id.snare:
                    imageDialogFragment.newInstance(R.id.snare).show(getActivity().getFragmentManager(), "snare");
                    break;
            }
        }
    }


}
