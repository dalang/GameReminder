package tk.dalang.gaminder.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import tk.dalang.gaminder.R;
import tk.dalang.gaminder.elements.IGame;
import tk.dalang.gaminder.ui.MainActivity;
import tk.dalang.gaminder.utils.CalendarUtils;
import tk.dalang.gaminder.utils.DBManager;
import tk.dalang.gaminder.utils.DateUtils;
import tk.dalang.gaminder.utils.MyCalendar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.infteh.comboseekbar.ComboSeekBar;

public abstract class AbsGameListFragment extends AbsBaseListFragment {
	
	private static final String LOG_TAG = AbsGameListFragment.class.getSimpleName();

	protected List<IGame> mNbaData = new ArrayList<IGame>();

	protected MyCalendar mCalendars[];
    protected String mSelectedCalendarId = "0";    
	protected GameAdapter mAdapter;
	protected AbsTask mSQLiteTask;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new GameAdapter();
		try {
			 mCalendars = CalendarUtils.getCalendars(getActivity());
		} catch (Exception e) {
			Log.d("CALENDAR", "get calendar error");
		}
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
//        getListView().setOnItemLongClickListener(this);
        setListAdapter(mAdapter);
        if (mSQLiteTask == null && mAdapter.isEmpty()) {
        	mSQLiteTask = new AbsTask(AbsTask.TYPE_REFRESH);
			mSQLiteTask.execute(getUrl());
            getPullToRefreshListView().setRefreshing(true);
        }
    }
    
    @Override
    protected void onPullDownListViewRefresh(PullToRefreshListView refreshListView) {
        super.onPullDownListViewRefresh(refreshListView);
        EasyTracker.getTracker().sendEvent("ui_action", "pull_down_list_view_refresh",
                "sina_list_fragmentt_pull_down_list_view_refresh", 0L);
        if (mSQLiteTask != null) {
            return;
        }
        mSQLiteTask = new AbsTask(AbsTask.TYPE_REFRESH);
        mSQLiteTask.execute(getUrl());
    } 
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSQLiteTask != null) {
        	mSQLiteTask.cancel(true);
        	mSQLiteTask = null;
        }
    }

    protected abstract String getUrl();
    protected abstract String getTableName();


    protected class SQLiteEventTask extends AsyncTask<IGame, Void, Boolean> {
        private DBManager mDBMgr = null;

        @Override  
        protected void onPreExecute() {  
            super.onPreExecute();  
            mDBMgr = new DBManager(getActivity());  
        }
        
		@Override
		protected Boolean doInBackground(IGame... params) {
			if (params[0] != null) {
				int updated_rows = mDBMgr.updateEvent(getTableName(), params[0]);
				if (updated_rows > 0)
					return true;
			}
			return false;
		}

        @Override
        protected void onPostExecute(Boolean result) {
            mDBMgr.closeDB();
            mDBMgr = null;
			((MainActivity)getActivity()).setReminderChanged(true);
            super.onPostExecute(result);
        }
	}
	
    protected abstract List<IGame> getParseredData(String url);
    protected class AbsTask extends AsyncTask<String, Void, Boolean> {

        public static final int TYPE_REFRESH = 1;
        public static final int TYPE_LOADMORE = 2;

        private int mType = 0;
        private DBManager mDBMgr = null;
        public AbsTask(int type) {
            mType = type;
        }

        @Override  
        protected void onPreExecute() {  
            super.onPreExecute();  
            mDBMgr = new DBManager(getActivity());  
        }
        
        @Override
        protected Boolean doInBackground(String... params) {
            try {
				Log.d(LOG_TAG, "start parse task");
				if (mDBMgr.isEmpty(getTableName())) {
					Log.d(LOG_TAG, params[0]);
					List<IGame> tmpList = getParseredData(params[0]);
					if (tmpList != null && tmpList.size() > 0) {
						mDBMgr.addList(getTableName(), tmpList);
					}
				} else {
					Date curDate = new Date();
		        	Date dt = mDBMgr.timeStamp(getTableName());
		        	SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());
			    	String autoFresh =pre.getString(getResources().getString(R.string.pref_key_auto_fresh), "0");
			    	if (autoFresh.equals(""))
			    		autoFresh = "0";
			    	int int_autoFresh = Integer.parseInt(autoFresh);
			    	Log.d(LOG_TAG, "max date in sqlite:" + dt.toString());
			    	dt.setDate(dt.getDate() - int_autoFresh);
			    	Log.d(LOG_TAG, "date to be compare:" + dt.toString());
					if (null != dt && dt.before(curDate)) {
						List<IGame> tmpList = getParseredData(params[0]);
						if (tmpList != null && tmpList.size() > 0) {
							mDBMgr.addList(getTableName(), tmpList);
						}
					}
				}
				// TODO make sure db insert will not add redundant item
				if (mType == TYPE_REFRESH && mNbaData.size() > 0) {
					mNbaData.clear();
				}
				mDBMgr.query(getTableName(), mNbaData);
                return true;
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
                EasyTracker.getTracker().sendException("AbsTask", e, false);
                return false;
            }

        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                onDataFirstLoadComplete();
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_LONG).show();
            }
            mSQLiteTask = null;
            getPullToRefreshListView().getLoadingLayoutProxy().setLastUpdatedLabel(
                    DateUtils.getLastUpdateLabel(getActivity()));
            getPullToRefreshListView().onRefreshComplete();
            mDBMgr.closeDB();
            mDBMgr = null;
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            getPullToRefreshListView().onRefreshComplete();
            mSQLiteTask = null;
            mDBMgr.closeDB();
            mDBMgr = null;
            super.onCancelled();
        }

    }
        
    protected class GameAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNbaData.size();
        }

        @Override
        public Object getItem(int position) {
            return mNbaData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(getListViewLayout(),
                        null);
                holder = buildHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            bindViewData(holder, position);
			return convertView;
		} 		
	}

    private ViewHolder buildHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
		holder.guest_tlogoImg = (ImageView)convertView.findViewById(R.id.guest_tlogoImg);
		holder.home_tlogoImg = (ImageView)convertView.findViewById(R.id.home_tlogoImg);
		holder.dateTxt = (TextView)convertView.findViewById(R.id.dateTxt);
		holder.timeTxt = (TextView)convertView.findViewById(R.id.timeTxt);
		holder.statusTxt = (TextView)convertView.findViewById(R.id.statusTxt);
		holder.typeTxt = (TextView)convertView.findViewById(R.id.typeTxt);
		holder.channelsTxt = (TextView)convertView.findViewById(R.id.channelsTxt);
		holder.markBtn = (ImageButton)convertView.findViewById(R.id.markBtn);
        return holder;
    }
    
    protected void callNotifyDataSetChanged()
    {
    	mAdapter.notifyDataSetChanged();
    }
    protected abstract int getListViewLayout();

    protected abstract void bindViewData(ViewHolder holder, int position);

    public static class ViewHolder {
		public ImageView guest_tlogoImg;
		public ImageView home_tlogoImg;
		public TextView dateTxt;
		public TextView timeTxt;
		public TextView statusTxt;
		public TextView typeTxt;
		public TextView channelsTxt;
		public ImageButton markBtn;
	}

    
	public void settingDialog(final int position){

		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View layout = inflater.inflate(R.layout.add_event_dialog, (ViewGroup) getActivity().findViewById(R.id.dialogLayout));
	    final TextView hintTxt = (TextView)layout.findViewById(R.id.seekbar_hint);
		final LinearLayout ll = (LinearLayout)layout.findViewById(R.id.time_comboseekbar);
		ComboSeekBar csb;
		final List<String> labels = Arrays.asList(getResources().getStringArray(R.array.reminder_times));
		final String seekbar_hint = getResources().getString(R.string.seekbar_hint); 

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.reminder_dialog_title))
		.setView(layout)
		.setPositiveButton(getResources().getString(R.string.add_event), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
//					ComboSeekBar mycsb = (ComboSeekBar)layout.findViewById(R.id.time_comboseekbar);
					ComboSeekBar mycsb = (ComboSeekBar)ll.getChildAt(0);
					int minutes = 0;
					if (mycsb != null) {
						String str = labels.get(mycsb.getSelectedId());
						String [] ss = str.split(" ");
						
						if (ss[1].equals("分钟")) {
							minutes = Integer.parseInt(ss[0]);
						} else if (ss[1].equals("小时")) {
							minutes = Integer.parseInt(ss[0]) * 60;
						} else if (ss[1].equals("天")) {
							minutes = Integer.parseInt(ss[0]) * 1440;
						}
					}
					if (CalendarUtils.addEvent(getActivity(), mNbaData.get(position), minutes, mSelectedCalendarId)) {
						SQLiteEventTask updateTask = new SQLiteEventTask();
						updateTask.execute(mNbaData.get(position));
						mAdapter.notifyDataSetChanged();
					} 
				} catch (Exception e) {
					Log.e(LOG_TAG, "add event ERROR");
				}
			}
		}).show();


        csb = new ComboSeekBar(getActivity());
        csb.SetStyle(Color.BLACK, 25, true);

        csb.setAdapter(labels);
        csb.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String seekbar_hintFinal = String.format(seekbar_hint, labels.get(arg2));
				hintTxt.setText(seekbar_hintFinal);
			}
        	
        });
        ll.addView(csb);

		Spinner spinner_calender = (Spinner)layout.findViewById(R.id.spinner_calendar);
		if (mCalendars != null) {
			ArrayAdapter<?> l_arrayAdapter = new ArrayAdapter<Object>(getActivity().getApplicationContext(), R.layout.spinner_item, mCalendars);
			populateCalendarSpinner(spinner_calender, l_arrayAdapter);
		}
	}

	 private void populateCalendarSpinner(Spinner spinner_calender, ArrayAdapter<?> l_arrayAdapter) {
	    	if(l_arrayAdapter == null)
	    		return;
	    	l_arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
	    	spinner_calender.setAdapter(l_arrayAdapter);
	    	
	    	SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    	String default_calendar_id =pre.getString(getResources().getString(R.string.pref_key_default_calendar), "0");

	    	for(int i = 0; i < l_arrayAdapter.getCount(); i++)
	    	{
	    		MyCalendar cal = (MyCalendar) l_arrayAdapter.getItem(i);
	    		if (default_calendar_id.equals(cal.id)) {
	    			spinner_calender.setSelection(i);
	    			break;
	    		}
	    	}
	    	
	    	spinner_calender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	    		@Override
	    		public void onItemSelected(AdapterView<?> p_parent, View p_view,
	    				int p_pos, long p_id) {
	    			mSelectedCalendarId = mCalendars[(int)p_id].id;
	    		}
	    		@Override
	    		public void onNothingSelected(AdapterView<?> arg0) {}
	    	});
	    }
}
