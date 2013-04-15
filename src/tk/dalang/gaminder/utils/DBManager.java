package tk.dalang.gaminder.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tk.dalang.gaminder.elements.Game;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * GameReminder
 * <p>
 * SQLite DB Manager
 * </p>
 * @author <a href="http://weibo.com/iDalang">dalang</a>
 * @version Apr 9, 2013
 */
public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;
	
	public DBManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}
	
	public void addList(List<Game> games) {
		db.beginTransaction();
		try {
			Date minDate = new Date();
			minDate.setDate(minDate.getDate() - 1);

			for (Game game: games) {
				if (game.dt.after(minDate)) {
					// UPDATE (+ INSERT if UPDATE fails). Less code = fewer bugs.
			        ContentValues cv = new ContentValues();  
			    	cv.put("chanls", game.chanls);  

			        int row = db.update("sinagame", cv, "dt = ? AND guest = ?", new String[]{DateUtils.getDateTimeString(game.dt), game.guest});  
			        if (row == 0) {
			        	db.execSQL("INSERT INTO sinagame VALUES(null, ?, ?, ?, ?, ?, ?, ?)", new Object[]{game.guest, game.host, DateUtils.getDateTimeString(game.dt), game.type, game.chanls, game.isEvent, game.eventId});
			        }
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public List<Game> query(List<Game> games) {
		Cursor c = queryTheCursor();
		while (c.moveToNext()) {
			Game game = new Game();
			game._id = c.getInt(c.getColumnIndex("_id"));
			game.guest = c.getString(c.getColumnIndex("guest"));
			game.host = c.getString(c.getColumnIndex("host"));
			game.type = c.getInt(c.getColumnIndex("type"));
			game.chanls = c.getString(c.getColumnIndex("chanls"));
			game.isEvent = (c.getInt(c.getColumnIndex("evented")) != 0);
			game.eventId = c.getLong(c.getColumnIndex("eventid"));

			String originalString = c.getString(c.getColumnIndex("dt"));
			Date date;
			try {
				date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(originalString);
				game.dt = date;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			games.add(game);
		}
		c.close();
		return games;
	}
	
	public List<Game> query(Date date, List<Game> games) {
		Cursor c = queryTheCursor(date);
		while (c.moveToNext()) {
			Game game = new Game();
			game._id = c.getInt(c.getColumnIndex("_id"));
			game.guest = c.getString(c.getColumnIndex("guest"));
			game.host = c.getString(c.getColumnIndex("host"));
			game.type = c.getInt(c.getColumnIndex("type"));
			game.chanls = c.getString(c.getColumnIndex("chanls"));
			game.isEvent = (c.getInt(c.getColumnIndex("evented")) != 0);
			game.eventId = c.getLong(c.getColumnIndex("eventid"));

			String originalString = c.getString(c.getColumnIndex("dt"));
			try {
				Date tmpDt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(originalString);
				game.dt = tmpDt;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			games.add(game);
		}
		c.close();
		return games;
	}
	
	public int updateEvent(Game game) {  
        ContentValues cv = new ContentValues();  
        cv.put("evented", game.isEvent);  
    	cv.put("eventid", game.eventId);  

        return db.update("sinagame", cv, "dt = ? AND guest = ?", new String[]{DateUtils.getDateTimeString(game.dt), game.guest});  
    } 

	public int updateEvent(Long eventid) {  
        ContentValues cv = new ContentValues();  
        cv.put("evented", false);  
    	cv.put("eventid", 0);  

        return db.update("sinagame", cv, "eventid = ?", new String[]{"" + eventid});  
    }
	
	public Cursor queryTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM sinagame ORDER BY dt ASC", null);
		return c;
	}
	
	public Cursor queryTheCursor(Date date) {
		Cursor c = db.rawQuery("SELECT * FROM sinagame WHERE dt > datetime('now','start of day') ORDER BY dt ASC", null);
		return c;
	}
	
	public Cursor queryForEvents() {
		Cursor c = db.rawQuery("SELECT eventid FROM sinagame WHERE evented = 1 ORDER BY dt ASC", null);
		return c;
	}
	
	public Boolean isEmpty() {
		Cursor c = db.rawQuery("SELECT * FROM sinagame", null);
		if (c.getCount() > 0) {
			c.close();
			return false;
		}
		else {
			c.close();
			return true;
		}
	}
	
	public Date timeStamp() throws ParseException {
		Cursor c = db.rawQuery("SELECT MAX(dt) FROM sinagame", null);
		Date date = null;
		String datestr = "";
		if (c.moveToNext()) {
			datestr = c.getString(0);
		}
		c.close();
		Log.v("date in DB", datestr);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		date = format.parse(datestr);  
		Log.v("date in DB", date.toString());
		
		return date;
	}
	
	public void closeDB() {
		db.close();
	}
}
