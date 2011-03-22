/**
 * 
 */
package ustc.sse.assistant.test.provider;

import ustc.sse.assistant.event.data.EventEntity;
import ustc.sse.assistant.event.provider.EventAssistant;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import ustc.sse.assistant.event.provider.EventProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

/**
 * @author 李健
 *
 */
public class EventProviderTestCase extends ProviderTestCase2<EventProvider> {

	private ContentResolver cr;
	private Uri newUri;
	
	public EventProviderTestCase() {
		super(EventProvider.class, EventAssistant.EVENT_AUTHORITY);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cr = this.getMockContentResolver();

	}
	
	public void testPrecondition() {
		assertNotNull(cr);
	}
	
	public void testInsertEvent() {
		
		ContentValues values = EventEntity.eventToContentValues("content",
																"alarmTime",
																 "alarmType", 
																 "beginTime", 
																 "endTime", 
																 "createTime", 
																 "lastModifyTime", 
																 "location", 
																 "note",
																 2, 
																 5);
		
		newUri = cr.insert(Event.CONTENT_URI, values);
		assertNotNull(newUri);
	}
	
	public void testQuery() {
		addRows(2, getContentValues());
		Cursor cursor = cr.query(Event.CONTENT_URI, null, null, null, null);
		assertEquals(cursor.getCount(), 2);
	}
	
	public void testUpdate() {
		addRows(3, getContentValues());
		
		ContentValues values = new ContentValues();
		values.put(Event.NOTE, "updatedNote");
		int count = cr.update(Event.CONTENT_URI, values, null, null);
		
		assertEquals(3, count);
	}
	
	public void testDelete() {
		addRows(5, getContentValues());
		int count = cr.delete(Event.CONTENT_URI, null, null);
		assertEquals(5, count);
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	private void addRows(int count, ContentValues values) {
		for (int i = 0; i < count; ++i) {
			cr.insert(Event.CONTENT_URI, values);

		}
	}
	
	private ContentValues getContentValues() {
		ContentValues values = EventEntity.eventToContentValues(
				"content",
				"alarmTime",
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
