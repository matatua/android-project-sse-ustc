/**
 * 
 */
package ustc.sse.assistant.test.provider;

import ustc.sse.assistant.event.provider.EventAssistant;
import ustc.sse.assistant.event.provider.EventAssistant.EventContact;
import ustc.sse.assistant.event.provider.EventContactProvider;
import android.content.ContentResolver;
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
	
	private ContentResolver cr;

	public EventContactProviderTestCase() {
		super(EventContactProvider.class, EventAssistant.EVENT_CONTACT_AUTHORITY);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cr = this.getMockContentResolver();
	}
	
	public void testPrecondition() {
		assertNotNull(cr);
	}
	
	public void testInsert() {
		ContentValues values = new ContentValues();
		values.put(EventContact.DISPLAY_NAME, "lijian");
		values.put(EventContact.EVENT_ID, 1);
		values.put(EventContact.CONTACT_ID, 2);
		
		Uri uri = cr.insert(EventContact.CONTENT_URI, values);
		
		assertNotNull(uri);
	}
	
	public void testQuery() {
		insertRows(5, getContentValues());
		Uri uri = ContentUris.withAppendedId(EventContact.CONTENT_URI, 1);
		Cursor cursor = cr.query(uri, null, null, null, null);
		assertEquals(5, cursor.getCount());
		
		cursor = cr.query(uri, null, EventContact.DISPLAY_NAME + " = ? ", new String[]{"lijian"}, null);
	
		assertEquals(5, cursor.getCount());
	}
	
	public void testDelete() {
		insertRows(10, getContentValues());
		Uri uri = ContentUris.withAppendedId(EventContact.CONTENT_URI, 1);
		int count = cr.delete(uri, null, null);
		
		assertEquals(10, count);
	}
	
	public void testUpdate() {
		try {
			cr.update(EventContact.CONTENT_URI, null, null, null);
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}
	
	private void insertRows(int count, ContentValues values) {
		for (int i = 0; i < count; i++) {
			cr.insert(EventContact.CONTENT_URI, values);
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
