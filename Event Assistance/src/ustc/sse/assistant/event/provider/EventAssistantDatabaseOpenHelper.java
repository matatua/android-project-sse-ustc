package ustc.sse.assistant.event.provider;

import ustc.sse.assistant.event.provider.EventAssistant.Event;
import ustc.sse.assistant.event.provider.EventAssistant.EventContact;
import ustc.sse.assistant.event.provider.EventAssistant.EventSearch;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EventAssistantDatabaseOpenHelper extends SQLiteOpenHelper {

	public EventAssistantDatabaseOpenHelper(Context context) {
		super(context, EventProvider.DATABASE_NAME, null, EventProvider.DATABASE_VERSION);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
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
		
		String sqlEventSearch = "CREATE VIRTUAL TABLE " + EventSearchProvider.EVENT_SEARCH_TABLE
								+ " USING fts3 ( " + EventSearch.CONTENT + " , "
								+ EventSearch.LOCATION + " , "
								+ EventSearch.EVENT_ID + " );";
		
		String eventSearchUpdateTrigger = "CREATE TRIGGER update_event_search AFTER UPDATE OF "
											+ " " + Event.CONTENT + " , " + Event.LOCATION 
											+ " ON " + EventProvider.EVENT_TABLE_NAME + " BEGIN "
											+ " UPDATE " + EventSearchProvider.EVENT_SEARCH_TABLE 
											+ " SET " + EventSearch.CONTENT + " = new." + Event.CONTENT
											+ " , " + EventSearch.LOCATION + " = new." + Event.LOCATION
											+ " WHERE " + EventSearch.EVENT_ID + " = old." + Event._ID + " ; "
											+ " END;";
		String eventSearchDeleteTrigger = "CREATE TRIGGER delete_event_search AFTER DELETE ON " + EventProvider.EVENT_TABLE_NAME
											+ " BEGIN " + "DELETE FROM " + EventSearchProvider.EVENT_SEARCH_TABLE 
											+ " WHERE " + EventSearch.EVENT_ID + " = " + "old." + Event._ID + " ; "
											+ " END;";
		db.execSQL(sqlEvent);
		db.execSQL(sqlEventContact);
		db.execSQL(sqlEventSearch);
		db.execSQL(eventSearchUpdateTrigger);
		db.execSQL(eventSearchDeleteTrigger);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		  Log.w(EventProvider.TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + EventProvider.EVENT_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + EventContactProvider.EVENT_CONTACT_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + EventSearchProvider.EVENT_SEARCH_TABLE);
            onCreate(db);			
	}
	
}