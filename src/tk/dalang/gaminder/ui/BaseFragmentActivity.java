/**
 * Copyright (C) 2013 HalZhang
 */
package tk.dalang.gaminder.ui;

import tk.dalang.gaminder.R;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;


import android.os.Bundle;

/**
 * GameReminder
 * <p>
 * </p>
 * @author <a href="http://weibo.com/iDalang">dalang</a>
 * @version Apr 5, 2013
 */
public class BaseFragmentActivity extends SherlockFragmentActivity {
    
    @Override
    protected void onCreate(Bundle arg0) {
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
        super.onCreate(arg0);
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
