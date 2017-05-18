package app.pptik.org.radioprrsnibandung;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import app.pptik.org.radioprrsnibandung.customview.Tab;
import app.pptik.org.radioprrsnibandung.model.StasiunRadio;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RadioActivity extends AppCompatActivity {
    private static String TAG = "[RadioActivity]";

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    private StasiunRadio stasiunRadio;
    private BroadCastFragment broadcast;
    private PodcastFragment podcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        stasiunRadio = (StasiunRadio) getIntent().getSerializableExtra("StasiunRadio");
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        Log.i(TAG, stasiunRadio.getNama());
        setupTabIcons();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home){
            if (!podcast.isAnyDownload()) {
                super.onBackPressed();
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcast.stopMediaPlayer();
        podcast.stopMediaPlayer();
    }

    @Override
    public void onBackPressed() {
        if (!podcast.isAnyDownload()) {
            super.onBackPressed();
            finish();
        }
    }

    private void setupTabIcons() {
        List<Tab> tabs = new ArrayList<>();
        tabs.add(new Tab(R.drawable.ic_radio_station, "BroadCast"));
        tabs.add(new Tab(R.drawable.ic_podcast, "PodCast"));
        for (int i = 0; i < tabs.size(); i++) {
            LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tabContent = (TextView) tabLinearLayout.findViewById(R.id.tabContent);
            ImageView icontab = (ImageView) tabLinearLayout.findViewById(R.id.icontab);
            tabContent.setText(tabs.get(i).getName());
            icontab.setImageResource(tabs.get(i).getIcon());
            tabLayout.getTabAt(i).setCustomView(tabLinearLayout);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putSerializable("StasiunRadio", stasiunRadio);

        broadcast = new BroadCastFragment();
        broadcast.setArguments(bundle);
        podcast = new PodcastFragment();
        podcast.setArguments(bundle);

        adapter.addFrag(broadcast, "Broadcast");
        adapter.addFrag(podcast, "Podcast");

        viewPager.setAdapter(adapter);
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

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
