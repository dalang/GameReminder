
package tk.dalang.gaminder.ui;

import tk.dalang.gaminder.R;
import tk.dalang.gaminder.fragments.ReminderListFragment;
import tk.dalang.gaminder.fragments.SinaListFragment;
import tk.dalang.gaminder.utils.ActivityUtils;
import tk.dalang.gaminder.utils.AppUtils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.viewpagerindicator.TitlePageIndicator;

/**
 * GameReminder
 * <p>
 * </p>
 * @author <a href="http://weibo.com/iDalang">dalang</a>
 * @version Apr 9, 2013
 */

public class MainActivity extends BaseFragmentActivity {
    private ViewPager mViewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Intent mFeedbackEmailIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.titles);
        indicator.setViewPager(mViewPager);

        mFeedbackEmailIntent = createEmailIntent();

    }

    private Intent createEmailIntent() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {
            "dalang1987@gmail.com"
        });
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.app_name)).append(" v")
                .append(AppUtils.getVersionName(getApplicationContext())).append("反馈");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, builder.toString());
        emailIntent.setType("message/rfc822");
        return emailIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.menu_feedback:
                if(ActivityUtils.isIntentAvailable(getApplicationContext(), mFeedbackEmailIntent)){
                    startActivity(mFeedbackEmailIntent);
                }else{
                    Toast.makeText(getApplicationContext(), R.string.error_noemailapp, Toast.LENGTH_LONG).show();
                }
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private String[] titles;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            titles = getResources().getStringArray(R.array.section_titles);
        }

        @Override
        public Fragment getItem(int arg0) {
            Fragment fragment = null;
            switch (arg0) {
                case 0:
                    fragment = new SinaListFragment();
                    break;
                case 1:
                    fragment = new ReminderListFragment();
                    break;
                    
                default:
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

    }

}
