/**
 * 
 */
package ustc.sse.assistant.event.provider;

import java.util.HashMap;

import ustc.sse.assistant.event.provider.EventAssistant.Event;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author 李健
 *
 */
public class EventProvider extends ContentProvider {
	
	public static final String TAG = "EventAssistantProvider";
	
	public static final String DATABASE_NAME = "event_assistant.db";
	public static final int DATABASE_VERSION = 1;
	public static final String EVENT_TABLE_NAME = "events";
	public static final String EVENT_CONTACT_TABLE_NAME = "event_contact";
		
	public static final HashMap<String, String> eventsProjectionMap;
	
	public static final UriMatcher uriMatcher;
	
	public static final int EVENTS = 1;
	public  static final int EVENT_ID = 2;
	
	
	static public class EventAssistantDatabaseOpenHelper extends SQLiteOpenHelper {

		public EventAssistantDatabaseOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE " + EVENT_TABLE_NAME + "("  
			+ Event._ID + " INTEGER PRIMARY KEY,"
			+ Event.ALARM_TIME + " TEXT,"
			+ Event.ALARM_TIME + " TEXT,"
			+ Event.BEGIN_TIME + " TEXT,"
			+ Event.CONTENT + "TEXT," 
			+ Event.CREATE_TIME + " TEXT,"
			+ Event.END_TIME + " TEXT,"
			+ Event.LAST_MODIFY_TIME + " TEXT,"
			+ Event.LOCATION + " TEXT," 
			+ Event.NOTE + " TEXT,"
			+ Event.PRIOR_ALARM_DAY + " INTEGER,"
			+ Event.PRIOR_REPEAT_TIME + " INTEGER,"
			+ ");";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			  Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
	                    + newVersion + ", which will destroy all old data");
	            db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME);
	            onCreate(db);			
		}
		
	}
	
	private EventAssistantDatabaseOpenHelper openHelper;
	
	@Override
	public boolean onCreate() {
		openHelper = new EventAssistantDatabaseOpenHelper(this.getContext());
		return true;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(EVENT_TABLE_NAME);
		switch (uriMatcher.match(uri)) {
		case EVENTS :
			builder.setProjectionMap(eventsProjectionMap);
			break;
			
		case EVENT_ID :
			builder.setProjectionMap(eventsProjectionMap);
			builder.appendWhere(Event._ID + " = " + uri.getPathSegments().get(1));
			break;
		default :
			throw new IllegalArgumentException("UNKNOWN URI:" + uri);
		}
		
		SQLiteDatabase db = openHelper.getReadableDatabase();
		
		String orderBy = sortOrder;
		if (TextUtils.isEmpty(orderBy)) {
			orderBy = Event.DEFAULT_SORT_ORDER;
		}
		
		Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, orderBy);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case EVENTS :
			return Event.CONTENT_TYPE;
		case EVENT_ID :
			return Event.CONTENT_ITEM_TYPE;
		default :
			throw new IllegalArgumentException("UNKNOWN URI:" + uri);
		}
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (uriMatcher.match(uri) != EVENTS) {
			throw new IllegalArgumentException("UNKNOWN URI " + uri);
		}
		
		if (initialValues == null) {
			throw new NullPointerException("InitialValues is null");
		}
		
		SQLiteDatabase db = openHelper.getWritableDatabase();
		long rowId = db.insert(EVENT_TABLE_NAME, Event.NOTE, initialValues);
		
		if (rowId > 0) {
			Uri eventUri = ContentUris.withAppendedId(Event.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(eventUri, null);
		}
		
		throw new SQLException("Insert fail");
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		SQLiteDatabase db = openHelper.getWritableDatabase();
		
		switch (uriMatcher.match(uri)) {
		case EVENTS :
			count = db.delete(EVENT_TABLE_NAME, selection, selectionArgs);
			break;
		case EVENT_ID :
			String eventId = uri.getPathSegments().get(1);
			count = db.delete(EVENT_TABLE_NAME, Event._ID + " = " + eventId 
							+ (!TextUtils.isEmpty(selection) ? "AND (" + selection + ")" : ""), 
							selectionArgs);
			break;
		default :
			throw new IllegalArgumentException("UNKNOWN URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		SQLiteDatabase db = openHelper.getWritableDatabase();
		
		switch (uriMatcher.match(uri)) {
		case EVENTS :
			count = db.update(EVENT_TABLE_NAME, values, selection, selectionArgs);
			break;
		case EVENT_ID :
			String eventId = uri.getPathSegments().get(1);
			count = db.update(EVENT_TABLE_NAME, values, Event._ID + " = " + eventId 
							+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), 
							selectionArgs);
			break;
		default :
			throw new IllegalArgumentException("UNKNOWN URI " + uri);

		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(EventAssistant.AUTHORITY, "events", EVENTS);
		uriMatcher.addURI(EventAssistant.AUTHORITY, "events/#", EVENT_ID);
		
		eventsProjectionMap = new HashMap<String, String>();
		eventsProjectionMap.put(Event._ID, Event._ID);
		eventsProjectionMap.put(Event.ALARM_TIME, Event.ALARM_TIME);
		eventsProjectionMap.put(Event.ALARM_TYPE, Event.ALARM_TYPE);
		eventsProjectionMap.put(Event.BEGIN_TIME, Event.BEGIN_TIME);
		eventsProjectionMap.put(Event.CONTENT, Event.CONTENT);
		eventsProjectionMap.put(Event.CREATE_TIME, Event.CREATE_TIME);
		eventsProjectionMap.put(Event.END_TIME, Event.END_TIME);
		eventsProjectionMap.put(Event.LAST_MODIFY_TIME, Event.LAST_MODIFY_TIME);
		eventsProjectionMap.put(Event.LOCATION, Event.LOCATION);
		eventsProjectionMap.put(Event.NOTE, Event.NOTE);
		eventsProjectionMap.put(Event.PRIOR_ALARM_DAY, Event.PRIOR_ALARM_DAY);
		eventsProjectionMap.put(Event.PRIOR_REPEAT_TIME, Event.PRIOR_REPEAT_TIME);
		
	}

}
