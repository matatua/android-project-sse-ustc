package ustc.sse.assistant.event.provider;

import ustc.sse.assistant.event.provider.EventAssistant.Event;
import ustc.sse.assistant.event.provider.EventAssistant.EventContact;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EventAssistantDatabaseOpenHelper extends SQLiteOpenHelper {

	public EventAssistantDatabaseOpenHelper(Context context) {
		super(context, EventProvider.DATABASE_NAME, null, EventProvider.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlEvent = "CREATE TABLE " + EventProvider.EVENT_TABLE_NAME + "("  
		+ Event._ID + " INTEGER PRIMARY KEY,"
		+ Event.ALARM_TIME + " TEXT,"
		+ Event.ALARM_TYPE + " TEXT,"
		+ Event.BEGIN_TIME + " TEXT,"
		+ Event.CONTENT + " TEXT," 
		+ Event.CREATE_TIME + " TEXT,"
		+ Event.END_TIME + " TEXT,"
		+ Event.LAST_MODIFY_TIME + " TEXT,"
		+ Event.LOCATION + " TEXT," 
		+ Event.NOTE + " TEXT,"
		+ Event.PRIOR_ALARM_DAY + " INTEGER,"
		+ Event.PRIOR_REPEAT_TIME + " INTEGER"
		+ ");";
		
		String sqlEventContact = "CREATE TABLE " + EventContactProvider.EVENT_CONTACT_TABLE_NAME + " ( "
							  	 	+ EventContact._ID + " INTEGER PRIMARY KEY,"
							  	 	+ EventContact.CONTACT_ID + " INTEGER,"
							  	 	+ EventContact.EVENT_ID + " INTEGER,"
							  	 	+ EventContact.DISPLAY_NAME + " TEXT"
							  	 	+ ");";
		db.execSQL(sqlEvent);
		db.execSQL(sqlEventContact);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		  Log.w(EventProvider.TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + EventProvider.EVENT_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + EventContactProvider.EVENT_CONTACT_TABLE_NAME);
            onCreate(db);			
	}
	
}