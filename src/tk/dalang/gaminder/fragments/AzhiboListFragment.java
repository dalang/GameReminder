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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

public class AzhiboListFragment extends AbsGameListFragment implements OnItemLongClickListener{

	private static final String LOG_TAG = AzhiboListFragment.class.getSimpleName();

    private final String mAzhiboURL = "http://www.azhibo.com/nbazhibo";
    private final String mAzhiboTable = "azhibogame";

    
    @Override
    public int getContentViewId() {
        return R.layout.ptr_list_layout;
    }

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void bindViewData(ViewHolder holder, final int position) {
		IGame game = mNbaData.get(position);
		holder.guest_tlogoImg.setBackgroundResource(WebParserUtils.getTeamLogoId((String)game.getGuest()));
		holder.home_tlogoImg.setBackgroundResource(WebParserUtils.getTeamLogoId((String)game.getHost()));
		holder.channelsTxt.setText(game.getChanlsHTML());
		Date beginDate = game.getDate();
		String datestr = new SimpleDateFormat("MM-dd", Locale.CHINA).format(beginDate);
		String timestr = new SimpleDateFormat("HH:mm", Locale.CHINA).format(beginDate);
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
		//else if (curDate.after(beginDate) && curDate.before(endDate))
		else
			holder.statusTxt.setText("赛中");
			
		
		String [] types = getResources().getStringArray(R.array.nba_game_types);
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
					if (CalendarUtils.deleteEvent(getActivity(), mNbaData.get(position).getEventId())) {
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

	@Override
	protected String getUrl() {
		Log.d(LOG_TAG, "azhibourl" + mAzhiboURL);
		return mAzhiboURL;
	}

	@Override
	protected List<IGame> getParseredData(String url) {
		try {
			return WebParserUtils.parserAzhiboHtml(url);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected String getTableName() {
		return mAzhiboTable;
	}

	@Override
	protected int getListViewLayout() {
		return R.layout.azhibo_nba_list_item;
	}

}
