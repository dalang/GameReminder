package tk.dalang.gaminder.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * GameReminder
 * <p>
 * </p>
 * @author <a href="http://weibo.com/iDalang">dalang</a>
 * @version Apr 9, 2013
 */
public class DBHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "gaminder.db";
	private static final int DATABASE_VERSION = 2;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS sinagame" +  
	                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, guest NVARCHAR, host NVARCHAR, " +
	                "dt VARCHAR, type INTEGER, chanls TEXT, evented BOOLEAN, eventid INTEGER)"); 
		
		db.execSQL("CREATE TABLE IF NOT EXISTS azhibogame" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, guest NVARCHAR, host NVARCHAR, " +
                "dt VARCHAR, type INTEGER, chanls TEXT, evented BOOLEAN, eventid INTEGER)"); 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS azhibogame" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, guest NVARCHAR, host NVARCHAR, " +
                "dt VARCHAR, type INTEGER, chanls TEXT, evented BOOLEAN, eventid INTEGER)");
	}

}
