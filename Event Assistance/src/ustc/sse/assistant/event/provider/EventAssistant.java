package ustc.sse.assistant.event.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 
 * @author 李健
 *
 */
public class EventAssistant {
	public static final String TAG = "EventAssistant";
	
	public static final String EVENT_AUTHORITY = "ustc.sse.provider.EventAssistant.event";
	public static final String EVENT_CONTACT_AUTHORITY = "ustc.sse.provider.EventAssistant.eventcontact";
	public static final String EVENT_SEARCH_AUTHORITY = "ustc.sse.provider.EventAssistant.search";
	
	public static class Event implements BaseColumns {
		public static final String TAG = "EventAssistant.Event";
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + EVENT_AUTHORITY + "/events");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.EventAssistant.events";
		
		public static final String CONTENT_ITEM_TYPE= "vnd.android.cursor.item/vnd.EventAssistant.events";
		
		public static final String CONTENT = "content";
		
		public static final String BEGIN_TIME = "begin_time";
		
		public static final String END_TIME = "end_time";
		
		public static final String LOCATION = "location";
		
		public static final String NOTE = "note";
		
		public static final String ALARM_TIME = "alarm_time";
		
		public static final String PRIOR_ALARM_DAY = "prior_alarm_time";
	
		public static final String PRIOR_REPEAT_TIME = "prior_repeat_time";
		
		public static final String ALARM_TYPE = "alarm_type";
		
		public static final String CREATE_TIME = "create_time";
		
		public static final String LAST_MODIFY_TIME = "last_modify_time";
		
		public static final String DEFAULT_SORT_ORDER = LAST_MODIFY_TIME + " DESC";
	}
	
	public static class EventContact implements BaseColumns {
		public static final String TAG = "EventAssistant.EventContact";
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + EVENT_CONTACT_AUTHORITY + "/eventcontact");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.EventAssistant.eventcontact";
		
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.EventAssistant.eventcontact";
		
		public static final String EVENT_ID = "event_id";
		
		public static final String CONTACT_ID = "contact_id";
		
		public static final String DISPLAY_NAME = "display_name";
		
		public static final String DEFAULT_SORT_ORDER = _ID + " DESC";

	}
	
	public static class EventSearch {
		public static final String TAG = "EventSearch";
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + EVENT_SEARCH_AUTHORITY + "/events");
		
		public static final String CONTENT = Event.CONTENT;
		
		public static final String LOCATION = Event.LOCATION;
		
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.EventAssistant.search";
		
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.EventAssitant.search";
		
		public static final String EVENT_ID = "event_id";
	}
}
