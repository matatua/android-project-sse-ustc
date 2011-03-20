package ustc.sse.assistant.event.provider;

import java.util.HashMap;

import ustc.sse.assistant.event.provider.EventAssistant.Event;
import ustc.sse.assistant.event.provider.EventAssistant.EventContact;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.SeekBar;

/**
 * this provider support query, insert and delete operation.
 * you can insert one row a time, therefore, if you want to insert 
 * a bulk of rows, you must invoke the insert method several times
 * @author 李健
 *
 */
public class EventContactProvider extends ContentProvider {

	public static final String TAG = "EventContactProvider";
	
	public static final UriMatcher uriMatcher;
	
	public static final HashMap<String, String> eventContactProjectionMap;
	
	public static final String EVENT_CONTACT_TABLE_NAME = "event_contact";
	
	public static int EVENTS = 1;
	
	public static int EVENT_CONTACT = 2;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(EventAssistant.EVENT_CONTACT_AUTHORITY, "eventcontact/#", EVENTS);
		uriMatcher.addURI(EventAssistant.EVENT_CONTACT_AUTHORITY, "eventcontact", EVENT_CONTACT);
		
		eventContactProjectionMap = new HashMap<String, String>();
		eventContactProjectionMap.put(EventContact._ID, EventContact._ID);
		eventContactProjectionMap.put(EventContact.DISPLAY_NAME, EventContact.DISPLAY_NAME);
		eventContactProjectionMap.put(EventContact.EVENT_ID, EventContact.EVENT_ID);
		eventContactProjectionMap.put(EventContact.CONTACT_ID, EventContact.CONTACT_ID);
	}
	
	private EventAssistantDatabaseOpenHelper openHelper;
	
	@Override
	public boolean onCreate() {
		openHelper = new EventAssistantDatabaseOpenHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		
		if (uriMatcher.match(uri) == EVENTS) {
			builder.setProjectionMap(eventContactProjectionMap);
			builder.setTables(EVENT_CONTACT_TABLE_NAME);
			builder.appendWhere(EventContact.EVENT_ID + " = " + uri.getPathSegments().get(1));
			
			String orderBy = sortOrder;
			if (TextUtils.isEmpty(orderBy)) {
				orderBy = EventContact.DEFAULT_SORT_ORDER;
			}
			
			SQLiteDatabase db = openHelper.getReadableDatabase();
			Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, orderBy);
		
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			
			return cursor;
		}
		throw new IllegalArgumentException("UNKNOWN URI " + uri);
	}

	@Override
	public String getType(Uri uri) {
		if (uriMatcher.match(uri) == EVENTS) {
			return EventContact.CONTENT_TYPE;
		} else if (uriMatcher.match(uri) == EVENT_CONTACT) {
			return EventContact.CONTENT_ITEM_TYPE;
		}
		
		throw new IllegalArgumentException("UNKNOWN URI " + uri);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (values == null) {
			throw new NullPointerException("values are not allowed to be null");
		}
		
		if (uriMatcher.match(uri) == EVENT_CONTACT) {
			SQLiteDatabase db = openHelper.getWritableDatabase();
			long rowId = db.insert(EVENT_CONTACT_TABLE_NAME, "EventContact", values);
			
			Uri newUri = ContentUris.withAppendedId(uri, rowId);
			return newUri;
		}
		
		throw new IllegalArgumentException("UNKNOWN URI " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		
		if (uriMatcher.match(uri) == EVENTS) {
			SQLiteDatabase db = openHelper.getWritableDatabase();
			
			count = db.delete(EVENT_CONTACT_TABLE_NAME, 
						EventContact.EVENT_ID + " = " + uri.getPathSegments().get(1) 
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + " )" : ""), 
						selectionArgs);
			
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}
		
		throw new IllegalArgumentException("UNKNOWN URI " + uri); 
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException("Update is not supported");
	}

}
