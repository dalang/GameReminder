package anjoy.game.reminder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "game.db";
	private static final int DATABASE_VERSION = 1;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		arg0.execSQL("CREATE TABLE IF NOT EXISTS game" +  
	                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, guest NVARCHAR, host NVARCHAR, dt VARCHAR, type INTEGER, chanls TEXT, evented BOOLEAN, eventid INTEGER)"); 
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		arg0.execSQL("ALTER TABLE game ADD COLUMN other STRING");  
	}

}
