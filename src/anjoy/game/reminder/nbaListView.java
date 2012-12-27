package anjoy.game.reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.lang.String;


import anjoy.game.reminder.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import anjoy.game.reminder.ParserWeb;

import java.util.Calendar; 
import android.widget.Toast;
import android.provider.CalendarContract;

public class nbaListView extends ListActivity{
	private List<Map<String, Object>> nbaData;
	private List<Game> nbaData2;
	private String nbadataStr;
	private String [] splitedStr;
    private DBManager mgr;  
    nbaAdapter2 adapter;

	
	private MyCalendar m_calendars[];
    private String m_selectedCalendarId = "0";
    public static List<Boolean> mChecked;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mgr = new DBManager(this);  
        
        if (mgr.isEmpty()) {
    		try{
    			nbadataStr = ParserWeb.parserSinaHtml("http://nba.sports.sina.com.cn/showtv.php");
//    			nbadataStr = ParserWeb.parserTestHtml("http://10.24.22.208/wiki/Anjoy:Project:GameReminder:test_page");
    		} catch(Exception e) { 
    			Log.d("HTML PARSER","get data error");
    		}
    		
    		if (!nbadataStr.isEmpty())
    		{
    			nbadataStr = nbadataStr.trim();
    			Log.v("nbadataStr", nbadataStr);
    			splitedStr = Convert(nbadataStr);
    		}
    		else
    			Log.v("nbadataStr", "NULL");
    		nbaData2 = getData2(splitedStr);    
    		mgr.add(nbaData2);
        } else {
        	Date curDate = new Date();
        	Date dt = null;
        	try {
				dt = mgr.timeStamp();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
    			Log.d("DB date PARSER","error");
			}
        	
        	if (null != dt) {
        		if (!dt.after(curDate)) {
            		try{
            			nbadataStr = ParserWeb.parserSinaHtml("http://nba.sports.sina.com.cn/showtv.php");
            		} catch(Exception e) { 
            			Log.d("HTML PARSER","get data error");
            		}
            		if (!nbadataStr.isEmpty())
            		{
            			nbadataStr = nbadataStr.trim();
            			Log.v("nbadataStr", nbadataStr);
            			splitedStr = Convert(nbadataStr);
            		}
            		else
            			Log.v("nbadataStr", "NULL");
            		nbaData2 = getData2(splitedStr);
            		mgr.add(nbaData2);
        		} else {
            		// get data from game.db
        			nbaData2 = mgr.query();
            	}
        	}
        }
		
		try {
			getCalendars();
		} catch (Exception e) {
			Log.d("CALENDAR", "get calendar error");
		}
		
		adapter = new nbaAdapter2(this, nbaData2);
		setListAdapter(adapter);
	}
	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        mgr.closeDB();  
    }  
	
	public String[] Convert(String str){
		String[] splitedstr = str.split("\\s+"); /*|[ \t\n\x0B\f\r]*/
		for(int i = 0; i< splitedstr.length; i++)
		{
			Log.v("Convert"+i, splitedstr[i]);
		}
		return splitedstr;
	}


	private List<Map<String, Object>> getData(String [] nbadata) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;

		Log.d("SOURCE string length: ", ""+nbadata.length);

		if(nbadata.length > 10)
		{
			int begin = 0;
			while(true)
			{
				Boolean isBegin = false;
				for (int i = nbadata[begin].length(); --i>0;)
				{
					if (Character.isDigit((nbadata[begin].charAt(i))))
					{
						isBegin = true;
						break;
					}
				}
				if (isBegin)
					break;
				begin++;
			}
			for (int i = begin; i < nbadata.length; i += 7)
			{
				map = new HashMap<String, Object>();
				map.put("date", nbadata[i]);
				map.put("time", nbadata[i+1]);
				map.put("status", nbadata[i+2]);
				map.put("type", nbadata[i+3]);
				map.put("guest", nbadata[i+4]);
				map.put("home", nbadata[i+5]);
				String chanls = nbadata[i+6].replace('/', '\n');
				map.put("channels", chanls);
				list.add(map);
			}
			Log.d("SOURCE nbaData size: ", ""+list.size());
		}

		return list;
	}

	private List<Game> getData2(String [] nbadata) {
		List<Game> list = new ArrayList<Game>();
		Game g;

		Log.d("SOURCE string length: ", ""+nbadata.length);

		if(nbadata.length > 10)
		{
			int begin = 0;
			while(true)
			{
				Boolean isBegin = false;
				for (int i = nbadata[begin].length(); --i>0;)
				{
					if (Character.isDigit((nbadata[begin].charAt(i))))
					{
						isBegin = true;
						break;
					}
				}
				if (isBegin)
					break;
				begin++;
			}
			for (int i = begin; i < nbadata.length; i += 7)
			{
				String chanls = nbadata[i+6].replace('/', '\n');		
				int curYear = (Calendar.getInstance()).get(Calendar.YEAR);
				String dtstr = "" + curYear + "-" + nbadata[i] + " " + nbadata[i+1];
				Date date;
				int type;
				if (nbadata[i+3].equals("季前赛"))
					type = 1;
				else if (nbadata[i+3].equals("常规赛"))
					type = 2;
				else if (nbadata[i+3].equals("全明星"))
					type = 3;
				else if (nbadata[i+3].equals("季后赛"))
					type = 4;
				else if (nbadata[i+3].equals("总决赛"))
					type = 5;
				else
					type = 0;
				try {
					date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(dtstr);
					g = new Game(nbadata[i+4], nbadata[i+5], date, type, chanls);
					list.add(g);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("date parser error", dtstr);
				}				
			}
			Log.d("SOURCE nbaData size: ", ""+list.size());
		}

		return list;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.v("mark-button-click", (String)nbaData.get(position).get("channels"));
	}
	
	/*********************************
	 * Setting Dialog
	 */
	public void settingDialog(final int position){
		int advanceTime = 3;
		int maxAdvance = 36;
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View layout = inflater.inflate(R.layout.vlist_setting_dialog, (ViewGroup) findViewById(R.id.vlist_setting_dialog_root_element));
	    
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Reminder setting")
		.setView(layout)
		.setPositiveButton("Add event", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SeekBar sb = (SeekBar)layout.findViewById(R.id.seekBar1);
				try {
					addEvent2(position, sb.getProgress());
					adapter.notifyDataSetChanged();
				} catch (Exception e) {
					Log.e("event ERROR: ", e.toString());
					Toast.makeText(getApplicationContext(), "add event error", Toast.LENGTH_SHORT).show();
				}
			}
		})
		.show();
		
		final String seekbar_hint = getResources().getString(R.string.seekbar_hint); 
		SeekBar sb = (SeekBar)layout.findViewById(R.id.seekBar1);
		sb.setMax(maxAdvance);
		sb.setProgress(advanceTime);
		String seekbar_hintFinal = String.format(seekbar_hint, advanceTime);
		((TextView)layout.findViewById(R.id.tVseekbar_hint)).setText(seekbar_hintFinal);
		
		sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {
		            //add code here
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {
		            //add code here
		    }
		    
		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
	            //Do something here with new value
				//String seekbar_hint = getResources().getString(R.string.seekbar_hint); 
		    	String hintFinal = String.format(seekbar_hint, progress);
		    	((TextView)layout.findViewById(R.id.tVseekbar_hint)).setText(hintFinal); 
	        }
	    });
		
		Spinner spinner_calender = (Spinner)layout.findViewById(R.id.spinner_calendar);
		if (m_calendars != null) {
			ArrayAdapter<?> l_arrayAdapter = new ArrayAdapter<Object>(this.getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, m_calendars);
			populateCalendarSpinner(spinner_calender, l_arrayAdapter);
		}
	}
	
    private void populateCalendarSpinner(Spinner spinner_calender, ArrayAdapter<?> l_arrayAdapter) {
    	if(l_arrayAdapter == null)
    		return;
    	l_arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner_calender.setAdapter(l_arrayAdapter);
    	spinner_calender.setSelection(0);
    	spinner_calender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    		@Override
    		public void onItemSelected(AdapterView<?> p_parent, View p_view,
    				int p_pos, long p_id) {
    			m_selectedCalendarId = m_calendars[(int)p_id].id;
    		}
    		@Override
    		public void onNothingSelected(AdapterView<?> arg0) {}
    	});
    }

	public final class ViewHolder{
		public ImageView guest_tlogoImg;
		public ImageView home_tlogoImg;
		public TextView dateTxt;
		public TextView timeTxt;
		public TextView statusTxt;
		public TextView typeTxt;
		public TextView channelsTxt;
		public ImageButton markBtn;
	}
	
	public class nbaAdapter2 extends BaseAdapter {
		private LayoutInflater mInflater;
		  
		public nbaAdapter2(Context context, List<Game> nbaData){
			this.mInflater = LayoutInflater.from(context);			
	        mChecked = new ArrayList<Boolean>();  
	        for (int i = 0; i < nbaData.size(); i++) {  
	            mChecked.add(false);  
	        }
		}
		
		@Override
		public int getCount() {
			return nbaData2.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}	

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Boolean markflag;
			ViewHolder holder = null;
			if (convertView == null) {
				holder=new ViewHolder();  

				convertView = mInflater.inflate(R.layout.nba_vlist, null);

				holder.guest_tlogoImg = (ImageView)convertView.findViewById(R.id.guest_tlogoImg);
				holder.home_tlogoImg = (ImageView)convertView.findViewById(R.id.home_tlogoImg);
				holder.dateTxt = (TextView)convertView.findViewById(R.id.dateTxt);
				holder.timeTxt = (TextView)convertView.findViewById(R.id.timeTxt);
				holder.statusTxt = (TextView)convertView.findViewById(R.id.statusTxt);
				holder.typeTxt = (TextView)convertView.findViewById(R.id.typeTxt);
				holder.channelsTxt = (TextView)convertView.findViewById(R.id.channelsTxt);
				holder.markBtn = (ImageButton)convertView.findViewById(R.id.markBtn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}

			holder.guest_tlogoImg.setBackgroundResource(getTeamLogoId((String)nbaData2.get(position).guest));
			holder.home_tlogoImg.setBackgroundResource(getTeamLogoId((String)nbaData2.get(position).host));
			holder.channelsTxt.setText((String)nbaData2.get(position).chanls);
			mChecked.set(position, nbaData2.get(position).isEvent);
			markflag = mChecked.get(position);
			Date date = nbaData2.get(position).dt;
			Date curdate = new Date();
			String datestr = new SimpleDateFormat("MM-dd", Locale.US).format(date);
			String timestr = new SimpleDateFormat("HH:mm", Locale.US).format(date);
			Log.v("DT", datestr + "|" + timestr);
			
			holder.dateTxt.setText(datestr);
			holder.timeTxt.setText(timestr);
			Log.v("TYPE", "" + nbaData2.get(position).type);
			switch (nbaData2.get(position).type) {
			case 0:
				break;
			case 1:
				holder.typeTxt.setText("季前賽");
				break;
			case 2:
				holder.typeTxt.setText("常規賽");
				break;
			case 3:
				holder.typeTxt.setText("全明星赛");
				break;
			case 4:
				holder.typeTxt.setText("季后賽");
				break;
			case 5:
				holder.typeTxt.setText("总决赛");
				break;
			}
			
			if (date.before(curdate))
				holder.statusTxt.setText("完场");
			else if (date.after(curdate))
				holder.statusTxt.setText("未赛");
			if (date.equals(curdate))
				holder.statusTxt.setText("ING");
			
			if (markflag) {
				holder.markBtn.setImageResource(R.drawable.star_orange_full);
			} else {
				holder.markBtn.setImageResource(R.drawable.star_blank);
			}
			
			holder.markBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!mChecked.get(position)) {
						settingDialog(position);
					} else {
						deleteEvent(position);
					}
//					mChecked.set(position, !mChecked.get(position));
					notifyDataSetChanged();
				}
			});		
			return convertView;
		} 		
	}

	public class nbaAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		  
		public nbaAdapter(Context context, List<Map<String, Object>> nbaData){
			this.mInflater = LayoutInflater.from(context);
			
	        mChecked = new ArrayList<Boolean>();  
	        for (int i = 0; i < nbaData.size(); i++) {  
	            mChecked.add(false);  
	        }
		}
		
		@Override
		public int getCount() {
			return nbaData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}	

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Boolean markflag = mChecked.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				holder=new ViewHolder();  

				convertView = mInflater.inflate(R.layout.nba_vlist, null);

				holder.guest_tlogoImg = (ImageView)convertView.findViewById(R.id.guest_tlogoImg);
				holder.home_tlogoImg = (ImageView)convertView.findViewById(R.id.home_tlogoImg);
				holder.dateTxt = (TextView)convertView.findViewById(R.id.dateTxt);
				holder.timeTxt = (TextView)convertView.findViewById(R.id.timeTxt);
				holder.statusTxt = (TextView)convertView.findViewById(R.id.statusTxt);
				holder.typeTxt = (TextView)convertView.findViewById(R.id.typeTxt);
				holder.channelsTxt = (TextView)convertView.findViewById(R.id.channelsTxt);
				holder.markBtn = (ImageButton)convertView.findViewById(R.id.markBtn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}

			holder.guest_tlogoImg.setBackgroundResource(getTeamLogoId((String)nbaData.get(position).get("guest")));
			holder.home_tlogoImg.setBackgroundResource(getTeamLogoId((String)nbaData.get(position).get("home")));
			holder.dateTxt.setText((String)nbaData.get(position).get("date"));
			holder.timeTxt.setText((String)nbaData.get(position).get("time"));
			holder.statusTxt.setText((String)nbaData.get(position).get("status"));
			holder.typeTxt.setText((String)nbaData.get(position).get("type"));
			holder.channelsTxt.setText((String)nbaData.get(position).get("channels"));
			
			if (markflag) {
				holder.markBtn.setImageResource(R.drawable.star_orange_full);
			} else {
				holder.markBtn.setImageResource(R.drawable.star_blank);
			}
			
			holder.markBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!mChecked.get(position))
						settingDialog(position);
					mChecked.set(position, !mChecked.get(position));
					notifyDataSetChanged();
				}
			});		
			return convertView;
		} 
	}

	public final int getTeamLogoId(String name) {
		Log.d("TEAM NAME: ", name);
		if(name.equals("老鹰"))
			return R.drawable.teamlogo1;
		else if(name.equals("凯尔特人"))
			return R.drawable.teamlogo2;
		else if (name.equals("黄蜂"))
			return R.drawable.teamlogo3;
		else if (name.equals("公牛"))
			return R.drawable.teamlogo4;
		else if (name.equals("骑士"))
			return R.drawable.teamlogo5;
		else if (name.equals("小牛"))
			return R.drawable.teamlogo6;
		else if (name.equals("掘金"))
			return R.drawable.teamlogo7;
		else if (name.equals("活塞"))
			return R.drawable.teamlogo8;
		else if (name.equals("勇士"))
			return R.drawable.teamlogo9;
		else if (name.equals("火箭"))
			return R.drawable.teamlogo10;
		else if (name.equals("步行者"))
			return R.drawable.teamlogo11;
		else if (name.equals("快船"))
			return R.drawable.teamlogo12;
		else if (name.equals("湖人"))
			return R.drawable.teamlogo13;
		else if (name.equals("热火"))
			return R.drawable.teamlogo14;
		else if (name.equals("雄鹿"))
			return R.drawable.teamlogo15;
		else if (name.equals("森林狼"))
			return R.drawable.teamlogo16;
		else if (name.equals("篮网"))
			return R.drawable.teamlogo17;
		else if (name.equals("尼克斯"))
			return R.drawable.teamlogo18;
		else if (name.equals("魔术"))
			return R.drawable.teamlogo19;
		else if (name.equals("76人"))
			return R.drawable.teamlogo20;
		else if (name.equals("太阳"))
			return R.drawable.teamlogo21;
		else if (name.equals("开拓者"))
			return R.drawable.teamlogo22;
		else if (name.equals("国王"))
			return R.drawable.teamlogo23;
		else if (name.equals("马刺"))
			return R.drawable.teamlogo24;
		else if (name.equals("雷霆"))
			return R.drawable.teamlogo25;
		else if (name.equals("爵士"))
			return R.drawable.teamlogo26;
		else if (name.equals("奇才"))
			return R.drawable.teamlogo27;
		else if (name.equals("猛龙"))
			return R.drawable.teamlogo28;
		else if (name.equals("灰熊"))
			return R.drawable.teamlogo29;
		else if (name.equals("山猫"))
			return R.drawable.teamlogo30;
		else 
			return R.drawable.teamlogo0;
	}

	/**
	 *
	 * Calendar Part
	 */   
	private void getCalendars() {
    	Uri l_calendars = CalendarContract.Calendars.CONTENT_URI;
    	String[] l_projection = new String[] {
    	       CalendarContract.Calendars._ID,
    	       CalendarContract.Calendars.ACCOUNT_NAME,
    	       CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
    	       CalendarContract.Calendars.NAME,
    	       CalendarContract.Calendars.CALENDAR_COLOR
    	};
    	Cursor l_managedCursor = getContentResolver().query(l_calendars, l_projection, null, null, null);	//all calendars
    	if (l_managedCursor.moveToFirst()) {
    		m_calendars = new MyCalendar[l_managedCursor.getCount()];
    		String l_calName;
    		String l_calId;
    		int l_cnt = 0;
    		int l_nameCol = l_managedCursor.getColumnIndex(l_projection[2]);
    		int l_idCol = l_managedCursor.getColumnIndex(l_projection[0]);
    		do {
    			l_calName = l_managedCursor.getString(l_nameCol);
    			l_calId = l_managedCursor.getString(l_idCol);
    			m_calendars[l_cnt] = new MyCalendar(l_calName, l_calId);
    			++l_cnt;
    		} while (l_managedCursor.moveToNext());
    	}
    	l_managedCursor.close();
    }
    
	/*add an event to calendar*/
	private void addEvent2(int position, int progress) {
		Game nbagamedata = nbaData2.get(position);
		Calendar cal = Calendar.getInstance();
		String datetime[] = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(nbagamedata.dt).split(" ");
		Log.v("datetime in addEvent2", datetime[0].toString() + " " + datetime[1].toString());
		String date[] = datetime[0].split("-");
		String time[] = datetime[1].split(":");   	
		cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1])-1, Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), 0);
		Log.v("cal in addEvent2", cal.toString());
		long start = cal.getTime().getTime();   
		
		// event insert
		ContentValues l_event = new ContentValues();
		l_event.put(CalendarContract.Events.CALENDAR_ID, m_selectedCalendarId);
		l_event.put(CalendarContract.Events.TITLE, nbagamedata.guest+" VS "+nbagamedata.host);
		l_event.put(CalendarContract.Events.DESCRIPTION, nbagamedata.chanls);
		l_event.put(CalendarContract.Events.EVENT_LOCATION, "@"+nbagamedata.host);
		l_event.put(CalendarContract.Events.DTSTART, start);
		l_event.put(CalendarContract.Events.DTEND, start + 2*60*60*1000);
		l_event.put(CalendarContract.Events.ALL_DAY, 0);
		// status: 0~ undefined; 1~ yes;  2~ no;
		l_event.put(CalendarContract.Events.SELF_ATTENDEE_STATUS, 1);
		//status: 0~ tentative; 1~ confirmed; 2~ canceled
		l_event.put(CalendarContract.Events.STATUS, 1);
		//0~ false; 1~ true
		l_event.put(CalendarContract.Events.HAS_ALARM, 1);
		
		Uri l_eventUri = CalendarContract.Events.CONTENT_URI;
		TimeZone timeZone = TimeZone.getDefault();
		l_event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
		Uri l_uri = this.getContentResolver().insert(l_eventUri, l_event);
		
		// reminder insert
		Uri l_reminderUri = CalendarContract.Reminders.CONTENT_URI;
		ContentValues l_reminder = new ContentValues();
		l_reminder.put( "event_id", Long.parseLong(l_uri.getLastPathSegment()));
		l_reminder.put( "method", 1 );
		l_reminder.put( "minutes", progress*60 );
		this.getContentResolver().insert(l_reminderUri, l_reminder);
		Log.v("++++++add event to calendar", l_uri.toString());
		nbagamedata.isEvent = true;
		nbagamedata.eventId = Long.parseLong(l_uri.getLastPathSegment());
		mgr.updateEvent(nbagamedata);
		Toast.makeText(getApplicationContext(), "add event success", Toast.LENGTH_SHORT).show();
	}  
	
	/*delete an event from calendar*/
	private void deleteEvent(int position) {
		Game nbagamedata = nbaData2.get(position);
		
		// event delete
		Uri l_eventUri = CalendarContract.Events.CONTENT_URI;
		Uri l_deleteUri = Uri.withAppendedPath(l_eventUri, String.valueOf(nbagamedata.eventId));
		this.getContentResolver().delete(l_deleteUri, null, null);
		Log.v("++++++delete event from calendar", l_deleteUri.toString());
		nbagamedata.isEvent = false;
		nbagamedata.eventId = 0;
		mgr.updateEvent(nbagamedata);
		Toast.makeText(getApplicationContext(), "delete event success", Toast.LENGTH_SHORT).show();
	}  
	
    /*add an event to calendar*/
    private void addEvent(int position, int progress) {
    	Map<String, Object> nbagamedata = nbaData.get(position);
    	Calendar cal = Calendar.getInstance();
    	String date[] = nbagamedata.get("date").toString().split("-");
    	String time[] = nbagamedata.get("time").toString().split(":");   	
    	cal.set(2012, Integer.parseInt(date[0])-1, Integer.parseInt(date[1]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), 0);
    	long start = cal.getTime().getTime();   
    	
    	// event insert
    	ContentValues l_event = new ContentValues();
    	l_event.put(CalendarContract.Events.CALENDAR_ID, m_selectedCalendarId);
    	l_event.put(CalendarContract.Events.TITLE, nbagamedata.get("guest").toString()+" VS "+nbagamedata.get("home"));
    	l_event.put(CalendarContract.Events.DESCRIPTION, "");
    	l_event.put(CalendarContract.Events.EVENT_LOCATION, "@"+nbagamedata.get("home"));
    	l_event.put(CalendarContract.Events.DTSTART, start);
    	l_event.put(CalendarContract.Events.DTEND, start + 2*60*60*1000);
    	l_event.put(CalendarContract.Events.ALL_DAY, 0);
    	// status: 0~ undefined; 1~ yes;  2~ no;
    	l_event.put(CalendarContract.Events.SELF_ATTENDEE_STATUS, 1);
    	//status: 0~ tentative; 1~ confirmed; 2~ canceled
    	l_event.put(CalendarContract.Events.STATUS, 1);
    	//0~ false; 1~ true
    	l_event.put(CalendarContract.Events.HAS_ALARM, 1);

    	Uri l_eventUri = CalendarContract.Events.CONTENT_URI;
    	TimeZone timeZone = TimeZone.getDefault();
    	l_event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
    	Uri l_uri = this.getContentResolver().insert(l_eventUri, l_event);
    	
    	// reminder insert
    	Uri l_reminderUri = CalendarContract.Reminders.CONTENT_URI;
    	ContentValues l_reminder = new ContentValues();
    	l_reminder.put( "event_id", Long.parseLong(l_uri.getLastPathSegment()));
    	l_reminder.put( "method", 1 );
    	l_reminder.put( "minutes", progress*60 );
    	this.getContentResolver().insert(l_reminderUri, l_reminder);
    	
    	Log.v("++++++add event to calendar", l_uri.toString());
		Toast.makeText(getApplicationContext(), "add event success", Toast.LENGTH_SHORT).show();
    }  
}
