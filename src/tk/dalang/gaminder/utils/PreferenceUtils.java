/**
 * Copyright (C) 2013 HalZhang
 */

package tk.dalang.gaminder.utils;

import tk.dalang.gaminder.R;
import android.content.Context;
import android.preference.PreferenceManager;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 17, 2013
 */
public class PreferenceUtils {

    /**
     * 获取自动更新时间
     * @param context
     * @return
     */
    public static String getAutoFresh(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getString(R.string.pref_key_auto_fresh),
                context.getString(R.string.default_auto_fresh));
    }


}
