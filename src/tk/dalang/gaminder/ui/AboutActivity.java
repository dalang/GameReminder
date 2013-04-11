/**
 * Copyright (C) 2013 HalZhang
 */

package tk.dalang.gaminder.ui;

import tk.dalang.gaminder.R;
import tk.dalang.gaminder.utils.AppUtils;
import tk.dalang.gaminder.utils.CalendarUtils;
import tk.dalang.gaminder.utils.MyCalendar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * GameReminder
 * <p>
 * 设置
 * </p>
 * 
 * @author <a href="http://weibo.com/iDalang">dalang</a>
 * @version Apr 8, 2013
 */
public class AboutActivity extends SherlockPreferenceActivity implements OnPreferenceChangeListener {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ListPreference listPreference = (ListPreference) findPreference(getString(R.string.pref_key_auto_fresh));
        listPreference.setOnPreferenceChangeListener(this);
        listPreference.setSummary(listPreference.getEntry());
        
        ListPreference calendarlistPref = (ListPreference)findPreference(getString(R.string.pref_key_default_calendar));
        calendarlistPref.setOnPreferenceChangeListener(this);

        MyCalendar calendars[] = CalendarUtils.getCalendars(this);

        CharSequence[] cEntries = new String[calendars.length];
        CharSequence[] cEntryValues = new String[calendars.length];
        for (int i = 0; i < calendars.length; i++)
        {
        	cEntries[i] = calendars[i].name;
        	cEntryValues[i] = calendars[i].id;
        }
        calendarlistPref.setEntries(cEntries);
        calendarlistPref.setEntryValues(cEntryValues);
        
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
    	String default_calendar_id =pre.getString(getResources().getString(R.string.pref_key_default_calendar), "0");
    	
    	if (!default_calendar_id.equals("0")) {
    		for (int i = 0; i < cEntryValues.length; i++)
    		{
    			if (cEntryValues[i].equals(default_calendar_id)) {
    				calendarlistPref.setSummary(cEntries[i]); 
    				break;
    			}
    		}
    	}
    	
        Preference versionPref = findPreference(getString(R.string.pref_key_version));
        versionPref.setSummary(getString(R.string.pref_summary_version,
                AppUtils.getVersionName(getApplicationContext())));
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        if (getString(R.string.pref_key_auto_fresh).equals(key)) {
        	EasyTracker.getTracker().sendEvent("preference_change_action",
                    "preference_change_auto_fresh",
                    String.format("auto_fresh_%1$s", (String) newValue), 0L);
        	ListPreference listPreference = (ListPreference) preference;
            preference.setSummary(listPreference.getEntries()[listPreference
                    .findIndexOfValue((String) newValue)]);
        } else if (getString(R.string.pref_key_default_calendar).equals(key)) {
            EasyTracker.getTracker().sendEvent("preference_change_action",
                    "preference_change_default_calendar",
                    String.format("default_calendar_%1$s", String.valueOf(newValue)), 0L);
        	ListPreference listPreference = (ListPreference) preference;
            preference.setSummary(listPreference.getEntries()[listPreference
                    .findIndexOfValue((String) newValue)]);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

}
