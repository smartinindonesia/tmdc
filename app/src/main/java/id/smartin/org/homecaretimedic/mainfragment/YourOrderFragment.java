package id.smartin.org.homecaretimedic.mainfragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.smartin.org.homecaretimedic.R;
import id.smartin.org.homecaretimedic.mainfragment.yourorderchildfragment.ActiveOrder;
import id.smartin.org.homecaretimedic.mainfragment.yourorderchildfragment.HistoryOrder;

/**
 * Created by Hafid on 8/22/2017.
 */

public class YourOrderFragment extends Fragment {
    public static final String TAG = "[YourOrderFragment]";

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_your_order, container, false);
        ButterKnife.bind(this, layoutView);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        // Inflate the layout for this fragment
        return layoutView;
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            Log.d(TAG, "Visible now");
            if (isVisibleToUser && isResumed()) {
                Log.d(TAG, "Not visible anymore.  Stopping audio.");
                // TODO stop audio playback
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        YourOrderFragment.ViewPagerAdapter adapter = new YourOrderFragment.ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ActiveOrder(), "Active Order");
        adapter.addFragment(new HistoryOrder(), "History Order");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
