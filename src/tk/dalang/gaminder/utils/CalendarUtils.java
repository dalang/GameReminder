package tk.dalang.gaminder.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import tk.dalang.gaminder.elements.Game;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.widget.Toast;

/**
 * GameReminder
 * <p>
 * method to work with google calendar
 * </p>
 * @author <a href="http://weibo.com/iDalang">dalang</a>
 * @version Apr 9, 2013
 */
public class CalendarUtils {
	private static final String calendarPath = "content://com.android.calendar/calendars";
	private static final String eventsPath = "content://com.android.calendar/events";
	private static final String remindersPath = "content://com.android.calendar/reminders";
//	private static final String calAlertsPath = "content://com.android.calendar/calendar_alerts";
//	private static final String eventsExtPropPath = "content://com.android.calendar/extendedproperties";
	
	public static Uri getEventsUri() {
		return Uri.parse(eventsPath);
	}
	public static MyCalendar[] getCalendars(Context context) {
		
    	Uri l_calendars;
//    	l_calendars = CalendarContract.Calendars.CONTENT_URI;
    	l_calendars = Uri.parse(calendarPath);
    	MyCalendar tmpCalendars[] = null;
    	String[] l_projection = new String[] {
    	       CalendarContract.Calendars._ID,
    	       CalendarContract.Calendars.ACCOUNT_NAME,
    	       CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
    	       CalendarContract.Calendars.NAME,
    	       CalendarContract.Calendars.CALENDAR_COLOR
    	};
    	Cursor l_managedCursor = context.getContentResolver().query(l_calendars, l_projection, null, null, null);	//all calendars
    	if (l_managedCursor.moveToFirst()) {
    		tmpCalendars = new MyCalendar[l_managedCursor.getCount()];
    		String l_calName;
    		String l_calId;
    		int l_cnt = 0;
    		int l_nameCol = l_managedCursor.getColumnIndex(l_projection[2]);
    		int l_idCol = l_managedCursor.getColumnIndex(l_projection[0]);
    		do {
    			l_calName = l_managedCursor.getString(l_nameCol);
    			l_calId = l_managedCursor.getString(l_idCol);
    			tmpCalendars[l_cnt] = new MyCalendar(l_calName, l_calId);
    			++l_cnt;
    		} while (l_managedCursor.moveToNext());
    	}
    	l_managedCursor.close();
    	
    	return tmpCalendars;
    }
	
	public static EventInfo getEvent(Context context, long eventId) {
		String title;
		String description;
		long timestamp;
		String datetime;
		String method;
		String minutes;
		
		Cursor eCursor = context.getContentResolver().query(Uri.parse(eventsPath),
				new String[] { Events._ID, Events.TITLE, Events.DESCRIPTION, Events.DTSTART }, "_id = ?",
				new String[] { "" + eventId }, (String) null);

		Cursor rCursor = context.getContentResolver().query(Uri.parse(remindersPath),
				new String[] { Reminders.METHOD, Reminders.MINUTES }, "event_id = ?",
				new String[] { "" + eventId }, (String) null);
		
		if (eCursor.moveToNext()) {
			title = eCursor.getString(eCursor.getColumnIndex(Events.TITLE));
			description = eCursor.getString(eCursor.getColumnIndex(Events.DESCRIPTION));
			timestamp = eCursor.getLong(eCursor.getColumnIndex(Events.DTSTART));
			Date date = new Date (timestamp);
			datetime =DateUtils.getDateTimeString(date);
			
			if (rCursor.moveToNext()) {
				method = rCursor.getString(rCursor.getColumnIndex(Reminders.METHOD));
				minutes = rCursor.getString(rCursor.getColumnIndex(Reminders.MINUTES));
				return new EventInfo(eventId, title, description, "", datetime, minutes);
			}
		}
		return null;
	}
	
	public static boolean addEvent(Context context, Game game, int minutes, String selectedCalId) {
		Calendar cal = Calendar.getInstance();
		String datetime[] = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(game.dt).split(" ");
		String date[] = datetime[0].split("-");
		String time[] = datetime[1].split(":");   	
		cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1])-1, Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), 0);
		long start = cal.getTime().getTime();   

		// event insert
		ContentValues l_event = new ContentValues();
		l_event.put(CalendarContract.Events.CALENDAR_ID, selectedCalId);
		l_event.put(CalendarContract.Events.TITLE, game.guest+" VS "+game.host);
		l_event.put(CalendarContract.Events.DESCRIPTION, game.chanls);
		l_event.put(CalendarContract.Events.EVENT_LOCATION, "@"+game.host);
		l_event.put(CalendarContract.Events.DTSTART, start);
		l_event.put(CalendarContract.Events.DTEND, start + 2*60*60*1000);
		l_event.put(CalendarContract.Events.ALL_DAY, 0);
		// status: 0~ undefined; 1~ yes;  2~ no;
		l_event.put(CalendarContract.Events.SELF_ATTENDEE_STATUS, 1);
		//status: 0~ tentative; 1~ confirmed; 2~ canceled
		l_event.put(CalendarContract.Events.STATUS, 1);
		//0~ false; 1~ true
		l_event.put(CalendarContract.Events.HAS_ALARM, 1);

//		Uri l_eventUri = CalendarContract.Events.CONTENT_URI;
		Uri l_eventUri = Uri.parse(eventsPath);
		TimeZone timeZone = TimeZone.getDefault();
		l_event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
		Uri l_uri_event = context.getContentResolver().insert(l_eventUri, l_event);
		if (l_uri_event == null) {
			Toast.makeText(context.getApplicationContext(), "add event failed", Toast.LENGTH_SHORT).show();		
			return false;
		}
		
		// reminder insert
//		Uri l_reminderUri = CalendarContract.Reminders.CONTENT_URI;
		Uri l_reminderUri = Uri.parse(remindersPath);
		ContentValues l_reminder = new ContentValues();
		l_reminder.put( "event_id", Long.parseLong(l_uri_event.getLastPathSegment()));
		l_reminder.put( "method", 1 );
		l_reminder.put( "minutes", minutes );
		Uri l_uri_reminder = context.getContentResolver().insert(l_reminderUri, l_reminder);
		if (l_uri_reminder == null) {
			Toast.makeText(context.getApplicationContext(), "add reminder failed", Toast.LENGTH_SHORT).show();		
			return false;
		}
		
		game.isEvent = true;
		game.eventId = Long.parseLong(l_uri_event.getLastPathSegment());
		Toast.makeText(context.getApplicationContext(), "add event success", Toast.LENGTH_SHORT).show();		
		return true;
	}  

	/*delete an event from calendar*/
	public static boolean deleteEvent(Context context, long eventId) {
//		Uri l_eventUri = CalendarContract.Events.CONTENT_URI;
		Uri l_eventUri = Uri.parse(eventsPath);

		Uri l_deleteUri = Uri.withAppendedPath(l_eventUri, String.valueOf(eventId));
		int deleted_rows = context.getContentResolver().delete(l_deleteUri, null, null);
		if (deleted_rows > 0) {
			Toast.makeText(context.getApplicationContext(), "delete event success", Toast.LENGTH_SHORT).show();
			return true;
		} else {
			Toast.makeText(context.getApplicationContext(), "delete event failed", Toast.LENGTH_SHORT).show();
			return false;
		}
	}  

}
