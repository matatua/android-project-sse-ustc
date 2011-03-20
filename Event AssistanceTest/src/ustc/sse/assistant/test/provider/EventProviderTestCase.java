/**
 * 
 */
package ustc.sse.assistant.test.provider;

import ustc.sse.assistant.event.data.EventEntity;
import ustc.sse.assistant.event.provider.EventAssistant;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import ustc.sse.assistant.event.provider.EventProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

/**
 * @author 李健
 *
 */
public class EventProviderTestCase extends ProviderTestCase2<EventProvider> {

	private EventProvider provider;
	private Uri newUri;
	
	public EventProviderTestCase() {
		super(EventProvider.class, EventAssistant.EVENT_AUTHORITY);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		provider = this.getProvider();

	}
	
	public void testPrecondition() {
		assertNotNull(provider);
	}
	
	public void testInsertEvent() {
		
		ContentValues values = EventEntity.eventToContentValues("alarmTime",
																 "alarmType", 
																 "beginTime", 
																 "endTime", 
																 "createTime", 
																 "lastModifyTime", 
																 "location", 
																 "note",
																 2, 
																 5);
		
		newUri = provider.insert(Event.CONTENT_URI, values);
		assertNotNull(newUri);
	}
	
	public void testQuery() {
		addRows(2, getContentValues());
		Cursor cursor = provider.query(Event.CONTENT_URI, null, null, null, null);
		assertEquals(cursor.getCount(), 2);
	}
	
	public void testUpdate() {
		addRows(3, getContentValues());
		
		ContentValues values = new ContentValues();
		values.put(Event.NOTE, "updatedNote");
		int count = provider.update(Event.CONTENT_URI, values, null, null);
		
		assertEquals(3, count);
	}
	
	public void testDelete() {
		addRows(5, getContentValues());
		int count = provider.delete(Event.CONTENT_URI, null, null);
		assertEquals(5, count);
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	private void addRows(int count, ContentValues values) {
		for (int i = 0; i < count; ++i) {
			provider.insert(Event.CONTENT_URI, values);

		}
	}
	
	private ContentValues getContentValues() {
		ContentValues values = EventEntity.eventToContentValues("alarmTime",
				 "alarmType", 
				 "beginTime", 
				 "endTime", 
				 "createTime", 
				 "lastModifyTime", 
				 "location", 
				 "note",
				 2, 
				 5);
		return values;

	}
	
}
