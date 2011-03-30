/**
 * 
 */
package ustc.sse.assistant.backup;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import ustc.sse.assistant.event.data.EventContactEntity;
import ustc.sse.assistant.event.data.EventEntity;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import ustc.sse.assistant.event.provider.EventAssistant.EventContact;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

/**
 * @author 李健
 *
 */
public class EventToXml { 
	private Context ctx;
	private Calendar from;
	private Calendar to;
	
	private ArrayList<EventEntity> events;
	/**
	 * 
	 * @param context
	 * @param from the event create time
	 * @param to the event create time
	 */
	public EventToXml(Context context, Calendar from, Calendar to) {
		
		this.ctx = context;
		this.from = from;
		this.to = to;
		events = new ArrayList<EventEntity>();
	}
	
	private void generateEntities() {
		ContentResolver cr = ctx.getContentResolver();
		String[] projection = {Event._ID, Event.ALARM_TIME, Event.ALARM_TYPE, Event.BEGIN_TIME,
								Event.CONTENT, Event.CREATE_TIME, Event.END_TIME, Event.LAST_MODIFY_TIME,
								Event.LOCATION, Event.NOTE, Event.PRIOR_ALARM_DAY, Event.PRIOR_REPEAT_TIME};
		String selection = Event.CREATE_TIME + " >= ? AND " + Event.CREATE_TIME + " <= ?";
		String[] selectionArgs = {String.valueOf(from.getTimeInMillis()), String.valueOf(to.getTimeInMillis())};
		Cursor c = cr.query(Event.CONTENT_URI, projection, selection, selectionArgs, null);
		
		int idIndex = c.getColumnIndex(Event._ID);
		int alarmTimeIndex = c.getColumnIndex(Event.ALARM_TIME);
		int alarmTypeIndex = c.getColumnIndex(Event.ALARM_TYPE);
		int beginTimeIndex = c.getColumnIndex(Event.BEGIN_TIME);
		int contentIndex = c.getColumnIndex(Event.CONTENT);
		int createTimeIndex = c.getColumnIndex(Event.CREATE_TIME);
		int endTimeIndex = c.getColumnIndex(Event.END_TIME);
		int lastModifyTimeIndex = c.getColumnIndex(Event.LAST_MODIFY_TIME);
		int locationIndex = c.getColumnIndex(Event.LOCATION);
		int noteIndex = c.getColumnIndex(Event.NOTE);
		int priorAlarmDayIndex = c.getColumnIndex(Event.PRIOR_ALARM_DAY);
		int priorAlarmRepeatIndex = c.getColumnIndex(Event.PRIOR_REPEAT_TIME);
		//create events
		if (c !=null && c.moveToFirst()) {
			do {
				EventEntity ee = new EventEntity();
				ee.id = c.getLong(idIndex);
				ee.alarmTime  = c.getString(alarmTimeIndex);
				ee.alarmType = c.getInt(alarmTypeIndex);
				ee.beginTime = c.getString(beginTimeIndex);
				ee.content = c.getString(contentIndex);
				ee.createTime = c.getString(createTimeIndex);
				ee.endTime = c.getString(endTimeIndex);
				ee.lastModifyTime = c.getString(lastModifyTimeIndex);
				ee.location = c.getString(locationIndex);
				ee.note = c.getString(noteIndex);
				ee.priorAlarmDay = c.getInt(priorAlarmDayIndex);
				ee.priorRepeat = c.getInt(priorAlarmRepeatIndex);
				
				events.add(ee);
			} while (c.moveToNext());
		}
		//
		//create eventscontact associated with created event
		for (EventEntity ee : events) {
			Uri contactsUri = ContentUris.withAppendedId(EventContact.CONTENT_URI, ee.id);
			Cursor contactsCursor = cr.query(contactsUri, null, null, null, null);
			
			if (c != null && c.moveToFirst()) {
				do {
					EventContactEntity ece = new EventContactEntity();
					ece.id = contactsCursor.getLong(contactsCursor.getColumnIndex(EventContact._ID));
					ece.eventId = contactsCursor.getLong(contactsCursor.getColumnIndex(EventContact.EVENT_ID));
					ece.eventContactId = contactsCursor.getLong(contactsCursor.getColumnIndex(EventContact.CONTACT_ID));
					ece.displayName = contactsCursor.getString(contactsCursor.getColumnIndex(EventContact.DISPLAY_NAME));
					
					ee.contacts.add(ece);
				} while (contactsCursor.moveToNext());
			}

		}
		
	}
	
	StringWriter generateXml() {
		generateEntities();
		
		StringWriter sw = new StringWriter();
		XmlSerializer xs = Xml.newSerializer();
		
		try {
			xs.setOutput(sw);
			xs.startDocument("UTF-8", true);
			xs.startTag("", EventXmlConstant.EVENTS);
			for (EventEntity ee : events) {
				xs.startTag("", EventXmlConstant.EVENT);
				
				xs.startTag("", EventXmlConstant.EVENT_ALARM_TIME);
				xs.text(ee.alarmTime);
				xs.endTag("", EventXmlConstant.EVENT_ALARM_TIME);
				
				xs.startTag("", EventXmlConstant.EVENT_ALARM_TYPE);
				xs.text(ee.alarmType.toString());
				xs.endTag("", EventXmlConstant.EVENT_ALARM_TYPE);
				
				
				
				xs.endTag("", EventXmlConstant.EVENT);
			}
			
			xs.endTag("", EventXmlConstant.EVENTS);
			xs.endDocument();
			
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
	}
}
 