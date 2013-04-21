package tk.dalang.gaminder.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tk.dalang.gaminder.R;
import tk.dalang.gaminder.utils.CalendarUtils;
import tk.dalang.gaminder.utils.DBManager;
import tk.dalang.gaminder.utils.DateUtils;
import tk.dalang.gaminder.utils.EventInfo;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * GameReminder
 * <p>
 * Reminder
 * Data Source: Google Calender
 * </p>
 * 
 * @author <a href="http://weibo.com/iDalang">dalang</a>
 * @version Apr, 11 2013
 */
public class ReminderListFragment extends AbsBaseListFragment {
    private static final String LOG_TAG = ReminderListFragment.class.getSimpleName();

    private ReminderAdapter mAdapter;
    private SqlTask mSqlTask;
	private List<EventInfo> mReminderData = new ArrayList<EventInfo>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ReminderAdapter();
    }
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
        setListAdapter(mAdapter);
        if (mSqlTask == null && mAdapter.isEmpty()) {
        	mSqlTask = new SqlTask(SqlTask.TYPE_REFRESH);
        	mSqlTask.execute();
            getPullToRefreshListView().setRefreshing(true);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSqlTask != null) {
        	mSqlTask.cancel(true);
        	mSqlTask = null;
        }
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.fragment_reminder, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
        Log.i(LOG_TAG, "!!!" + position);
        
        final EventInfo eInfo = (EventInfo) mAdapter.getItem(position - 1);
        Log.i(LOG_TAG, eInfo.toString());
        switch (item.getItemId()) {
            case R.id.menu_delete_event:
            	if (CalendarUtils.deleteEvent(getActivity(), eInfo.eventId)) {
            		UpdateEventTask uEvtTask = new UpdateEventTask();
            		uEvtTask.execute(eInfo.eventId);
            		mReminderData.remove(position);
            		mAdapter.notifyDataSetChanged();
				}
            	break;
            case R.id.menu_edit_reminder:
//                openEventEditor(eInfo);
            	break;
            case R.id.menu_edit_event:
            	openEventEditor(eInfo);
                break;
            default:
                break;
        }
        return true;
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        EasyTracker.getTracker().sendEvent("ui_action", "list_item_click",
                "reminder_list_fragment_list_item_click", 0L);
        
        openEventEditor(mReminderData.get(position - 1));
    }
    
    private void openEventEditor(EventInfo eventinfo) {
    	if (eventinfo == null) {
    		return;
    	}
    	
    	Long eventID = eventinfo.eventId;
    	 
        Uri uri = ContentUris.withAppendedId(CalendarUtils.getEventsUri(), eventID);
        Intent intent = new Intent(Intent.ACTION_EDIT)
            .setData(uri);
        startActivity(intent);
    }
    
    @Override
    protected void onPullDownListViewRefresh(PullToRefreshListView refreshListView) {
        super.onPullDownListViewRefresh(refreshListView);
        EasyTracker.getTracker().sendEvent("ui_action", "pull_down_list_view_refresh",
                "reminder_list_fragment_pull_down_list_view_refresh", 0L);
        if (mSqlTask != null) {
            return;
        }
        mSqlTask = new SqlTask(SqlTask.TYPE_REFRESH);
        mSqlTask.execute();
    }

    private class UpdateEventTask extends AsyncTask<Long, Void, Boolean> {
        private DBManager mDBMgr = null;

        @Override  
        protected void onPreExecute() {  
            super.onPreExecute();  
            mDBMgr = new DBManager(getActivity());  
        }
        
		@Override
		protected Boolean doInBackground(Long... params) {
			if (params[0] != 0) {
				int updated_rows = mDBMgr.updateEvent(params[0]);
				if (updated_rows > 0)
					return true;
			}
			return false;
		}

        @Override
        protected void onPostExecute(Boolean result) {
            mDBMgr.closeDB();
            mDBMgr = null;
            super.onPostExecute(result);
        }
	}
    
    private class SqlTask extends AsyncTask<Void, Void, Boolean> {
    	
        public static final int TYPE_REFRESH = 1;
        public static final int TYPE_LOADMORE = 2;
        
        private int mType = 0;
        private DBManager mDBMgr = null;

        public SqlTask(int Type) {
        	mType = Type;
        }
        @Override  
        protected void onPreExecute() {  
            super.onPreExecute();  
            mDBMgr = new DBManager(getActivity());  
        }
        
		@Override
		protected Boolean doInBackground(Void... params) {
			if (mType == TYPE_REFRESH && mReminderData.size() > 0) {
				mReminderData.clear();
			}
			String tables[] = {"sinagame", "azhibogame"};
			boolean flag = false;

			for (String table : tables) {
				Cursor c = mDBMgr.queryForEvents(table);
				while (c.moveToNext()) {
					long calendarEventID = c.getLong(c
							.getColumnIndex("eventid"));
					EventInfo eInfo = CalendarUtils.getEvent(getActivity(),
							calendarEventID);
					if (eInfo != null) {
						mReminderData.add(eInfo);
						flag = true;
					}
				}
			}
			Collections.sort(mReminderData);
			return flag;
		}

        @Override
        protected void onPostExecute(Boolean result) {
        	if (result) {
                onDataFirstLoadComplete();
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_LONG).show();
            }
        	
            mSqlTask = null;
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
            mSqlTask = null;
            mDBMgr.closeDB();
            mDBMgr = null;
            super.onCancelled();
        }
	}
    
    @Override
    public int getContentViewId() {
        return R.layout.ptr_list_layout;
    }
    
	public final class ViewHolder {
		public TextView datetimeTxt;
		public TextView timeTxt;
		public TextView titleTxt;
		public TextView descTxt;
	}
	
    private class ReminderAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mReminderData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mReminderData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.reminder_list_item, null);
				holder.titleTxt = (TextView)convertView.findViewById(R.id.reminder_item_title);
				holder.descTxt = (TextView)convertView.findViewById(R.id.reminder_item_description);
				holder.datetimeTxt = (TextView)convertView.findViewById(R.id.reminder_item_datetime);
				holder.timeTxt = (TextView)convertView.findViewById(R.id.reminder_item_time);
				convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
			}
			
			EventInfo eInfo = mReminderData.get(arg0);
			holder.titleTxt.setText(eInfo.title);
			if (eInfo.description.contains("www.")) {
				String source = "";
				String chanls[] = eInfo.description.split(";");
				for (String chanl : chanls) {
					String chanldetails[] = chanl.split(":");
					if (chanldetails.length == 2) {
						source += "<a target=\"_blank\" href=\"http://" + chanldetails[1] + "\">" + 
								chanldetails[0] + "</a>  ";
					}
				}
				holder.descTxt.setText(Html.fromHtml(source));
			} else {
				holder.descTxt.setText(eInfo.description.replaceAll("\n", "  "));
			}
			holder.datetimeTxt.setText(eInfo.startDT);
			int rTime = Integer.parseInt(eInfo.reminderTime);
			String rTimeStr = "";
			if (rTime / 1440 > 0)
				rTimeStr += rTime / 1440 + "天";
			if ((rTime % 1440) / 60 > 0)
				rTimeStr += (rTime % 3600) / 60 + "小时";
			if ((rTime % 1440) % 60 > 0)
				rTimeStr += (rTime % 1440) % 60 + "分钟";
			holder.timeTxt.setText(rTimeStr);
			
			return convertView;
		}
    }

}
