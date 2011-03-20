/**
 * 
 */
package ustc.sse.assistant.test.provider;

import ustc.sse.assistant.event.provider.EventAssistant;
import ustc.sse.assistant.event.provider.EventAssistant.EventContact;
import ustc.sse.assistant.event.provider.EventContactProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

/**
 * @author AlexanderLee
 *
 */
public class EventContactProviderTestCase extends
		ProviderTestCase2<EventContactProvider> {
	
	private EventContactProvider provider;

	public EventContactProviderTestCase() {
		super(EventContactProvider.class, EventAssistant.EVENT_CONTACT_AUTHORITY);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		provider = this.getProvider();
	}
	
	public void testPrecondition() {
		assertNotNull(provider);
	}
	
	public void testInsert() {
		ContentValues values = new ContentValues();
		values.put(EventContact.DISPLAY_NAME, "lijian");
		values.put(EventContact.EVENT_ID, 1);
		values.put(EventContact.CONTACT_ID, 2);
		
		Uri uri = provider.insert(EventContact.CONTENT_URI, values);
		
		assertNotNull(uri);
	}
	
	public void testQuery() {
		insertRows(5, getContentValues());
		Uri uri = ContentUris.withAppendedId(EventContact.CONTENT_URI, 1);
		Cursor cursor = provider.query(uri, null, null, null, null);
		assertEquals(5, cursor.getCount());
		
		cursor = provider.query(uri, null, EventContact.DISPLAY_NAME + " = ? ", new String[]{"lijian"}, null);
	
		assertEquals(5, cursor.getCount());
	}
	
	public void testDelete() {
		insertRows(10, getContentValues());
		Uri uri = ContentUris.withAppendedId(EventContact.CONTENT_URI, 1);
		int count = provider.delete(uri, null, null);
		
		assertEquals(10, count);
	}
	
	public void testUpdate() {
		try {
			provider.update(null, null, null, null);
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}
	
	private void insertRows(int count, ContentValues values) {
		for (int i = 0; i < count; i++) {
			provider.insert(EventContact.CONTENT_URI, values);
		}
	}
	
	private ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put(EventContact.DISPLAY_NAME, "lijian");
		values.put(EventContact.EVENT_ID, 1);
		values.put(EventContact.CONTACT_ID, 2);
		
		return values;
	}

}
