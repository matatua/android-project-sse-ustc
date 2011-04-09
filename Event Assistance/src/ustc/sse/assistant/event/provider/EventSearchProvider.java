/**
 * 
 */
package ustc.sse.assistant.event.provider;

import java.util.HashMap;

import ustc.sse.assistant.event.provider.EventAssistant.Event;
import ustc.sse.assistant.event.provider.EventAssistant.EventSearch;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author 李健
 *
 */
public class EventSearchProvider extends ContentProvider {

	private EventAssistantDatabaseOpenHelper dbOpenHelper;
	
	public static final String  EVENT_SEARCH_TABLE = "event_search";
	private static final UriMatcher matcher ;
	
	private static final HashMap<String, String> columnProjectionMap;
	
	private static final int SUGGESTION = 1;
	private static final int SEARCH_EVENT = 2;

	static {
		matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(EventAssistant.EVENT_SEARCH_AUTHORITY, "events", SEARCH_EVENT);
		matcher.addURI(EventAssistant.EVENT_SEARCH_AUTHORITY, "events/*", SEARCH_EVENT);
		matcher.addURI(EventAssistant.EVENT_SEARCH_AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SUGGESTION);
		matcher.addURI(EventAssistant.EVENT_SEARCH_AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SUGGESTION);
		
		columnProjectionMap = new HashMap<String, String>();
		columnProjectionMap.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
		columnProjectionMap.put(EventSearch.CONTENT, EventSearch.CONTENT);
		columnProjectionMap.put(EventSearch.LOCATION, EventSearch.LOCATION);
		columnProjectionMap.put(EventSearch.EVENT_ID, EventSearch.EVENT_ID);
		columnProjectionMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, 
								EventSearch.CONTENT + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
		columnProjectionMap.put(SearchManager.SUGGEST_COLUMN_TEXT_2, EventSearch.LOCATION + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_2);
		columnProjectionMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,  EventSearch.EVENT_ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
	}
	
	@Override
	public boolean onCreate() {

		dbOpenHelper = new EventAssistantDatabaseOpenHelper(getContext());
		if (dbOpenHelper != null) {
			return true;
		}
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		switch (matcher.match(uri)) {
		case SUGGESTION :
			return getSuggestions(selectionArgs);
			
		case SEARCH_EVENT :
			return searchEvent(uri, projection, selection, selectionArgs);
			
		}
		throw new IllegalArgumentException("Unsupported URI:" + uri);
	}

	private Cursor searchEvent(Uri uri, String[] projection, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(EVENT_SEARCH_TABLE);
		builder.setProjectionMap(columnProjectionMap);
		
		String searchSelection = EventSearch.CONTENT + " MATCH ? ";
		String[] searchSelectionArgs = {selectionArgs[0] + "*"};
		
		return queryEvent(projection, searchSelection, searchSelectionArgs);
	}

	private Cursor getSuggestions(String[] selectionArgs) {
		String[] projection = {BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1,
								SearchManager.SUGGEST_COLUMN_TEXT_2, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};
		String searchSelection = EventSearch.CONTENT + " MATCH ?";
		String[] searchSelectionArgs = {selectionArgs[0] + "*"};
		
		return queryEvent(projection, searchSelection, searchSelectionArgs);
	}

	private Cursor queryEvent(String[] projection, String searchSelection, String[] searchSelectionArgs) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(EVENT_SEARCH_TABLE);
		builder.setProjectionMap(columnProjectionMap);
		
		return builder.query(db, projection, searchSelection, searchSelectionArgs, null, null, null);
	}

	@Override
	public String getType(Uri uri) {
		switch (matcher.match(uri)) {
		case SUGGESTION :
			return SearchManager.SUGGEST_MIME_TYPE;
		case SEARCH_EVENT :
			return EventSearch.CONTENT_TYPE;
		
		}
		throw new IllegalArgumentException("Unsupported URI:" + uri);
	}

	/**
	 * insert operatio was done by EventProvider
	 * see EventProvider insert
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException("direct event search is unsupported");
	}

	/**
	 * delete was done by a database trigger
	 * see EventAssistantDatabaseOpenHelper
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("direct delete event search upsupported");
	}

	/**
	 * update was done by a database trigger
	 * see EventAssistantDatabaseOpenHelper
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		throw new UnsupportedOperationException("direct update event search upsupported");
	}

}
