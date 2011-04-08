/**
 * 
 */
package ustc.sse.assistant.backup.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Struct;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ustc.sse.assistant.event.EventUtils;
import ustc.sse.assistant.event.data.EventContactEntity;
import ustc.sse.assistant.event.data.EventEntity;
import ustc.sse.assistant.event.provider.EventAssistant;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import ustc.sse.assistant.event.provider.EventAssistant.EventContact;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.util.Xml;

/**
 * @author 李健
 *
 */
public class XmlToEvent {
	public static final String TAG = "XmlToEvent";
	
	private ArrayList<EventEntity> events = new ArrayList<EventEntity>();
	private EventEntity currentEvent = null;
	
	private Context ctx;
	private File backupFile;
	private boolean restoreContacts = false;
	private boolean deleteOriginalData = true;
	private InputStream inputStream;
	
	public XmlToEvent(Context context, File backupFile, boolean restoreContacts) throws FileNotFoundException {
		this.ctx = context;
		this.backupFile = backupFile;
		this.restoreContacts = restoreContacts;
		this.inputStream = new FileInputStream(backupFile);
	}
	
	public XmlToEvent(Context context, File backupFile, boolean restoreContacts, boolean deleteOriginal) throws FileNotFoundException {
		this(context, backupFile, restoreContacts);
		this.deleteOriginalData = deleteOriginal;
		
	}
	public XmlToEvent(Context context, InputStream is, boolean restoreContacts, boolean deleteOriginal) {
		this.ctx = context;
		this.restoreContacts = restoreContacts;
		if (is == null) throw new NullPointerException("Inputstream can't be null");
		this.inputStream = is;
	}
	
	public boolean restore() {
		if (backupFile.exists()) {
			InputStream is;
			try {
				is = new BufferedInputStream(inputStream);
				XmlPullParser xpp = Xml.newPullParser();
				xpp.setInput(is, null);
				
				int eventType = xpp.getEventType();
				boolean done = false;
				while (!done) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT :
						Log.i(TAG, "Xml document start");
						break;
					case XmlPullParser.END_DOCUMENT :
						done = true;
						Log.i(TAG, "Xml document end");
						break;
					case XmlPullParser.START_TAG :
						processStartTag(xpp);
						break;
					case XmlPullParser.END_TAG :
						processEndTag(xpp);
						break;
					}
					if (!done) {
						eventType = xpp.next();
					}
				}
					
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (deleteOriginalData) {
				//first delete all info in database
				ContentResolver cr = ctx.getContentResolver();
				cr.delete(EventContact.CONTENT_URI, null, null);
				cr.delete(Event.CONTENT_URI, null, null);
			}
			saveToDB();
			return true;
		}//end if
		
		return false;
	}

	private void saveToDB() {
		ContentResolver cr = ctx.getContentResolver();
		//first restore all events info
		for (EventEntity ee : events) {
			ContentValues values = EventUtils.eventToContentValues(
													ee.content, 
													ee.alarmTime, 
													ee.alarmType.toString(), 
													ee.beginTime, 
													ee.endTime, 
													ee.createTime, 
													ee.lastModifyTime, 
													ee.location,
													ee.note, 
													ee.priorAlarmDay,
													ee.priorRepeat);
			
			Uri newEventUri = cr.insert(Event.CONTENT_URI, values);
			long eventId = ContentUris.parseId(newEventUri);
			
			// if restoreContacts is true, then restore contacts info, else return
			if (restoreContacts) {
				for (EventContactEntity ece : ee.contacts) {
					Uri uri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI, ece.displayName);
					Cursor c = cr.query(uri, new String[] {Contacts._ID}, null, null, null);
					long contactId = -1;
					
					if (c.getCount() <= 0) {
						//only when the user hasn't a contact whose name is the same as the ece's
						//we insert into the system ContactContract database
						Account account = AccountManager.get(ctx).getAccounts()[0];
						ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
						ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
														.withValue(RawContacts.ACCOUNT_TYPE, account.type)
														.withValue(RawContacts.ACCOUNT_NAME, account.name)
														.build());
						ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
														.withValueBackReference(Data.RAW_CONTACT_ID, 0)
														.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
														.withValue(Data.IS_PRIMARY, 1)
														.withValue(Data.IS_SUPER_PRIMARY, 1)
														.withValue(StructuredName.DISPLAY_NAME, ece.displayName)
														.build());
						try {
							//apply insert and load corresponding contact's id
							ContentProviderResult[] results = cr.applyBatch(ContactsContract.AUTHORITY, ops);
							Cursor newRawContactCursor = cr.query(results[0].uri, new String[]{RawContacts.CONTACT_ID}, null, null, null);
							if (newRawContactCursor.moveToFirst()) {
								contactId = newRawContactCursor.getLong(0);
							}
						} catch (RemoteException e) {		
							e.printStackTrace();
						} catch (OperationApplicationException e) {
							e.printStackTrace();
						}
						
					} else {
						if (c.moveToFirst()) {
							contactId = c.getLong(c.getColumnIndex(Contacts._ID));
						}
					}
					
					ContentValues eventContactValues = new ContentValues();
					eventContactValues.put(EventContact.CONTACT_ID, contactId);
					eventContactValues.put(EventContact.EVENT_ID, eventId);
					eventContactValues.put(EventContact.DISPLAY_NAME, ece.displayName);
					cr.insert(EventContact.CONTENT_URI, eventContactValues);
					
				}//end foreach on every contact
			}//end if restoreContact
		}//end foreach on events
	
	}

	private void processStartTag(XmlPullParser xpp) {
		String name = xpp.getName();
		if (name.equalsIgnoreCase(EventXmlConstant.EVENT)
				&& null == currentEvent) {
			
			currentEvent = new EventEntity();
		} else if (name.equalsIgnoreCase(EventXmlConstant.EVENT_CONTACT_DISPLAY_NAME)
					&& currentEvent != null) {
			try {
				EventContactEntity ece = new EventContactEntity();
				ece.displayName = xpp.nextText();
				currentEvent.contacts.add(ece);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if (currentEvent != null) {
			try {
				if (name.equalsIgnoreCase(EventXmlConstant.EVENT_ALARM_TIME)) {
					currentEvent.alarmTime = xpp.nextText();

				} else if (name.equalsIgnoreCase(EventXmlConstant.EVENT_ALARM_TYPE)) {
					currentEvent.alarmType = Integer.valueOf(xpp.nextText());

				} else if (name.equalsIgnoreCase(EventXmlConstant.EVENT_BEGIN_TIME)) {
					currentEvent.beginTime = xpp.nextText();
				} else if (name.equalsIgnoreCase(EventXmlConstant.EVENT_CONTENT)) {
					currentEvent.content = xpp.nextText();
				} else if (name.equalsIgnoreCase(EventXmlConstant.EVENT_CREATE_TIME)) {
					currentEvent.createTime = xpp.nextText();
				} else if (name.equalsIgnoreCase(EventXmlConstant.EVENT_END_TIME)) {
					currentEvent.endTime = xpp.nextText();
				} else if (name.equalsIgnoreCase(EventXmlConstant.EVENT_LAST_MODIFY_TIME)) {
					currentEvent.lastModifyTime = xpp.nextText();
				} else if (name.equalsIgnoreCase(EventXmlConstant.EVENT_LOCATION)) {
					currentEvent.location = xpp.nextText();
				} else if (name.equalsIgnoreCase(EventXmlConstant.EVENT_NOTE)) {
					currentEvent.note = xpp.nextText();
				} else if (name.equalsIgnoreCase(EventXmlConstant.EVENT_PRIOR_ALARM_DAY)) {
					currentEvent.priorAlarmDay = Integer.valueOf(xpp.nextText());
				} else if (name.equalsIgnoreCase(EventXmlConstant.EVENT_PRIOR_ALARM_REPEAT)) {
					currentEvent.priorRepeat = Integer.valueOf(xpp.nextText());
				}
				
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void processEndTag(XmlPullParser xpp) {
		if (xpp.getName().equalsIgnoreCase(EventXmlConstant.EVENT)) {
			if (currentEvent != null) {
				events.add(currentEvent);
				currentEvent = null;
			}
		}
	}

	/**
	 * Note: this should be called after restore
	 * @return the events
	 */
	public ArrayList<EventEntity> getEvents() {
		return events;
	}
}
