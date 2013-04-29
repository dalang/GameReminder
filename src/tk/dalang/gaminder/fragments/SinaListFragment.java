/**
 * Copyright (C) 2013 HalZhang
 */

package tk.dalang.gaminder.fragments;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tk.dalang.gaminder.R;
import tk.dalang.gaminder.elements.IGame;
import tk.dalang.gaminder.utils.CalendarUtils;
import tk.dalang.gaminder.utils.WebParserUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * GameReminder
 * <p>
 * Sina NBA TV Data Source: http://nba.sports.sina.com.cn/showtv.php
 * </p>
 * 
 * @author <a href="http://weibo.com/iDalang">dalang</a>
 * @version Apr, 11 2013
 */
public class SinaListFragment extends AbsGameListFragment implements
		OnItemLongClickListener {

	private static final String LOG_TAG = SinaListFragment.class
			.getSimpleName();

	private final String mSinaURL = "http://nba.sports.sina.com.cn/showtv.php";
	private final String mSinaTable = "sinagame";

	// @Override
	// protected void onListItemClick(ListView l, View v, int position, long id)
	// {
	// EasyTracker.getTracker().sendEvent("ui_action", "list_item_click",
	// "sina_list_fragmentt_list_item_click", 0L);
	// // TODO check why -1 here!!!
	// Game entity = (Game) mAdapter.getItem(position - 1);
	// }

	@Override
	public int getContentViewId() {
		return R.layout.ptr_list_layout;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
//		EasyTracker.getTracker().sendEvent("ui_action", "list_item_long_click",
//				"sina_list_fragmentt_list_item_long_click", 0L);
//		Game entity = (Game) mAdapter.getItem(position - 1);
		return true;
	}

	protected String getTableName() {
		return mSinaTable;
	}

	@Override
	protected String getUrl() {
		return mSinaURL;
	}

	@Override
	protected List<IGame> getParseredData(String url) {
		try {
			return WebParserUtils.parserSinaHtml(url);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected int getListViewLayout() {
		return R.layout.nba_list_item;
	}

	@Override
	protected void bindViewData(ViewHolder holder, final int position) {
		IGame game = mNbaData.get(position);
		holder.guest_tlogoImg.setBackgroundResource(WebParserUtils
				.getTeamLogoId((String) game.getGuest()));
		holder.home_tlogoImg.setBackgroundResource(WebParserUtils
				.getTeamLogoId((String) game.getHost()));
		holder.channelsTxt.setText(game.getChanls());
		Date beginDate = game.getDate();
		String datestr = new SimpleDateFormat("MM-dd", Locale.CHINA)
				.format(beginDate);
		String timestr = new SimpleDateFormat("HH:mm", Locale.CHINA)
				.format(beginDate);
		holder.dateTxt.setText(datestr);
		holder.timeTxt.setText(timestr);

		Calendar cal = Calendar.getInstance();
		cal.setTime(beginDate);
		cal.add(Calendar.MINUTE, 135); // 目前時間加2.25小時
		Date endDate = cal.getTime();
		Date curDate = new Date();
		if (curDate.after(endDate))
			holder.statusTxt.setText("完场");
		else if (curDate.before(beginDate))
			holder.statusTxt.setText("未赛");
		// else if (curDate.after(beginDate) && curDate.before(endDate))
		else
			holder.statusTxt.setText("赛中");

		String[] types = getResources().getStringArray(R.array.nba_game_types);
		holder.typeTxt.setText(types[game.getType()]);

		if (game.getIsEvent()) {
			holder.markBtn.setImageResource(R.drawable.star_orange_full);
		} else {
			holder.markBtn.setImageResource(R.drawable.star_blank);
		}

		holder.markBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mNbaData.get(position).getIsEvent()) {
					settingDialog(position);
				} else {
					if (CalendarUtils.deleteEvent(getActivity(),
							mNbaData.get(position).getEventId())) {
						mNbaData.get(position).setIsEvent(false);
						mNbaData.get(position).setEventId(0);
						SQLiteEventTask updateEventTask = new SQLiteEventTask();
						updateEventTask.execute(mNbaData.get(position));
						callNotifyDataSetChanged();
					}
				}
			}
		});
	}
}
