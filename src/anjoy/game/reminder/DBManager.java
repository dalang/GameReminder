package anjoy.game.reminder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;
	
	public DBManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}
	
	public void add(List<Game> games) {
		db.beginTransaction();
		try {
			for (Game game: games) {
				db.execSQL("INSERT INTO game VALUES(null, ?, ?, ?, ?, ?, ?, ?)", new Object[]{game.guest, game.host, getDateTimeString(game.dt), game.type, game.chanls, game.isEvent, game.eventId});
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public List<Game> query() {
		ArrayList<Game> games = new ArrayList<Game>();
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
	
	public void updateEvent(Game game) {  
        ContentValues cv = new ContentValues();  
        cv.put("evented", game.isEvent);  
    	cv.put("eventid", game.eventId);  

        db.update("game", cv, "dt = ? AND guest = ?", new String[]{getDateTimeString(game.dt), game.guest});  
		Log.v("++++++update event in game.db", game.dt.toString());
    } 

	public Cursor queryTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM game", null);
		return c;
	}
	
	public Boolean isEmpty() {
		Cursor c = db.rawQuery("SELECT * FROM game", null);
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
		Cursor c = db.rawQuery("SELECT MAX(dt) FROM game", null);
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
	
	public static String getDateTimeString(Date day) {
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String dateString = formatter.format(day);
	    return dateString.trim();
	} /* getDateTimeString() */
	
	public void closeDB() {
		db.close();
	}
}
