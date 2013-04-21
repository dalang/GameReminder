package tk.dalang.gaminder.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tk.dalang.gaminder.elements.AzhiboGame;
import tk.dalang.gaminder.elements.Game;
import tk.dalang.gaminder.elements.IGame;
import tk.dalang.gaminder.elements.SinaGame;
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
	
	public void addList(String table, List<IGame> games) {
		db.beginTransaction();
		try {
			Date minDate = new Date();
			minDate.setDate(minDate.getDate() - 1);
			
			for (IGame game: games) {
				if (game.getDate().after(minDate)) {
					// UPDATE (+ INSERT if UPDATE fails). Less code = fewer bugs.
			        ContentValues cv = new ContentValues();  
			    	cv.put("chanls", game.getChanls());  
			        int row = db.update(table, cv, "dt = ? AND guest = ?", new String[]{DateUtils.getDateTimeString(game.getDate()), game.getGuest()});  
			        if (row == 0) {
			        	db.execSQL("INSERT INTO " + table + " VALUES(null, ?, ?, ?, ?, ?, ?, ?)", new Object[]{game.getGuest(), game.getHost(), DateUtils.getDateTimeString(game.getDate()), game.getType(), game.getChanls(), game.getIsEvent(), game.getEventId()});
			        }
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public List<IGame> query(List<IGame> games) {
		Cursor c = queryTheCursor();
		while (c.moveToNext()) {
			SinaGame game = new SinaGame();
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
	
	public void query(String tableName, List<IGame> games) {
		Cursor c = queryTheCursor2(tableName);
		while (c.moveToNext()) {
			IGame game;
			int _id = c.getInt(c.getColumnIndex("_id"));
			String guest = c.getString(c.getColumnIndex("guest"));
			String host = c.getString(c.getColumnIndex("host"));
			int type = c.getInt(c.getColumnIndex("type"));
			String chanls = c.getString(c.getColumnIndex("chanls"));
			Boolean isEvent = (c.getInt(c.getColumnIndex("evented")) != 0);
			Long eventId = c.getLong(c.getColumnIndex("eventid"));
			String originalString = c.getString(c.getColumnIndex("dt"));
			Date dt;
			try {
				Date tmpDt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(originalString);
				dt = tmpDt;

				if (tableName.equals("azhibogame"))
					game = new AzhiboGame(_id, guest, host, dt, type, chanls, isEvent, eventId);
				else 
					game = new SinaGame(_id, guest, host, dt, type, chanls, isEvent, eventId);
				games.add(game);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		c.close();		
	}

	public List<SinaGame> query(Date date, List<SinaGame> games) {
		Cursor c = queryTheCursor2();
		while (c.moveToNext()) {
			SinaGame game = new SinaGame();
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

	public int updateEvent(String table, IGame game) {  
        ContentValues cv = new ContentValues();  
        cv.put("evented", game.getIsEvent());  
    	cv.put("eventid", game.getEventId());  

        return db.update(table, cv, "dt = ? AND guest = ?", new String[]{DateUtils.getDateTimeString(game.getDate()), game.getGuest()});  
    }
	
	public int updateEvent(Game game) {  
        ContentValues cv = new ContentValues();  
        cv.put("evented", game.isEvent);  
    	cv.put("eventid", game.eventId);  

        return db.update("sinagame", cv, "dt = ? AND guest = ?", new String[]{DateUtils.getDateTimeString(game.dt), game.guest});  
    } 

	public int updateEvent(String table, Long eventid) {  
        ContentValues cv = new ContentValues();  
        cv.put("evented", false);  
    	cv.put("eventid", 0);  

        return db.update(table, cv, "eventid = ?", new String[]{"" + eventid});  
    }
	
	public int updateEvent(Long eventid) {  
        ContentValues cv = new ContentValues();  
        cv.put("evented", false);  
    	cv.put("eventid", 0);  

        return db.update("sinagame", cv, "eventid = ?", new String[]{"" + eventid});  
    }
	
	public Cursor queryTheCursor(String table) {
		Cursor c = db.rawQuery("SELECT * FROM " + table + " ORDER BY dt ASC", null);
		return c;
	}
	
	public Cursor queryTheCursor2(String table) {
		Cursor c = db.rawQuery("SELECT * FROM " + table + " WHERE dt > datetime('now','start of day') ORDER BY dt ASC", null);
		return c;
	}
	
	public Cursor queryTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM sinagame ORDER BY dt ASC", null);
		return c;
	}
	
	public Cursor queryTheCursor2() {
		Cursor c = db.rawQuery("SELECT * FROM sinagame WHERE dt > datetime('now','start of day') ORDER BY dt ASC", null);
		return c;
	}
	
	public Cursor queryForEvents(String table) {
		Cursor c = db.rawQuery("SELECT eventid FROM " + table + "  WHERE evented = 1 ORDER BY dt DESC", null);
		return c;
	}
	
	public Cursor queryForEvents() {
		Cursor c = db.rawQuery("SELECT eventid FROM sinagame WHERE evented = 1 ORDER BY dt ASC", null);
		return c;
	}

	public Boolean isEmpty(String table) {
		Cursor c = db.rawQuery("SELECT * FROM " + table, null);
		if (c.getCount() > 0) {
			c.close();
			return false;
		}
		else {
			c.close();
			return true;
		}
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
	
	public Date timeStamp(String table) throws ParseException {
		Cursor c = db.rawQuery("SELECT MAX(dt) FROM " + table, null);
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
