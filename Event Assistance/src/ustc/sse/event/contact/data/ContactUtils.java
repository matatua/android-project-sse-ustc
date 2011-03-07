/**
 * 
 * @author 李健
 *
 */
package ustc.sse.event.contact.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;


/**
 * 
 *This class provides some convenient method for getting, updating 
 *built-in contact's information
 *
 */
public class ContactUtils {
	
	private Activity activity;
	
	public ContactUtils(Activity activity) {
		this.activity = activity;
	}

	public List<Contact> getAllContactsBasicInfo() {
		ContentResolver cr = activity.getContentResolver();
		List<Contact> contacts = new ArrayList<Contact>();
		Cursor cur = null;
		
		Uri uri = ContactsContract.RawContacts.CONTENT_URI;
		String[] projection = {RawContacts._ID};
		
		cur = cr.query(uri, projection, null, null, null);
		
		if (cur.moveToFirst()) {
					
			int rawContactIdIndex = cur.getColumnIndex(RawContacts._ID);
			
			do {
				Contact contact = new Contact();	
				contact.setRawContactId(cur.getLong(rawContactIdIndex));
				contacts.add(contact);
			} while (cur.moveToNext());
			
			
		}
		
		for (Contact c : contacts) {
			c.setDisplayedName(getDisplayName(c.getRawContactId()));
			c.setPhoto(getPhoto(c.getRawContactId()));
		}
		cur.close();
		return contacts;
	}
	
	/**
	 * this method provide the basic information for each contact in a given group,
	 * and this information is used in contact list
	 * @param Group
	 * @return
	 */
	public List<Contact> getContactsBasicInfoByGroup(Group g) {
		ContentResolver cr = activity.getContentResolver();
	
		List<Contact> contacts = new ArrayList<Contact>();
		Cursor cur = null;
		
		Uri dataUri = ContactsContract.Data.CONTENT_URI;
		String[] groupProjection = {Data.RAW_CONTACT_ID, 
									Data.MIMETYPE, 
									GroupMembership.GROUP_ROW_ID};
		
		String selection = Data.MIMETYPE + " = ? AND " 
							+ GroupMembership.GROUP_ROW_ID + " = ? ";
		
		String[] selectionArgs = {GroupMembership.MIMETYPE, 
								  Long.toString(g.getGroupId())};
		
		cur = cr.query(dataUri, groupProjection, selection, selectionArgs, null);
		
		
		if (cur.moveToFirst()) {
	
			int rawContactIdIndex = cur.getColumnIndex(RawContacts._ID);
			
			do {
				Contact contact = new Contact();
				contact.setRawContactId(cur.getLong(rawContactIdIndex));
				contacts.add(contact);
			} while (cur.moveToNext());
		}
		
		for (Contact c : contacts) {
			c.setDisplayedName(getDisplayName(c.getRawContactId()));
			c.setPhoto(getPhoto(c.getRawContactId()));
			
		}
		
		cur.close();
		return contacts;
	}
	
	public Contact getContactById(long rawContactId) {
		String displayName = getDisplayName(rawContactId);
		String note = getNote(rawContactId);
		String phoneNumber = getPhoneAndType(rawContactId);
		byte[] photo = getPhoto(rawContactId);
		String birthday = getBirthday(rawContactId);
		
		Contact c = new Contact();
		c.setBirthday(birthday);
		c.setDisplayedName(displayName);
		c.setNote(note);
		c.setEventType(Integer.toString(Event.TYPE_BIRTHDAY));
		c.setPhoneNumber(phoneNumber);
		c.setPhoneType(Integer.toString(Phone.TYPE_MAIN));
		c.setRawContactId(rawContactId);
		c.setPhoto(photo);
		
		return c;
	}
	
	private String getDisplayName(long rawContactId) {
		ContentResolver cr = activity.getContentResolver();
		Cursor c = null;
		String displayName = null;
		
		
		String[] projection = {Data.RAW_CONTACT_ID, Data.MIMETYPE, StructuredName.DISPLAY_NAME};
		String selection = Data.MIMETYPE + " = ? AND " + Data.RAW_CONTACT_ID + " = ? ";
		String[] selectionArgs = {StructuredName.MIMETYPE, Long.toString(rawContactId)};
		c = cr.query(Data.CONTENT_URI, projection, selection, selectionArgs, null);
		
		int displayNameIndex = c.getColumnIndex(StructuredName.DISPLAY_NAME);
		if (c.moveToFirst()) {
			displayName = c.getString(displayNameIndex);
		}
		c.close();
		
		return displayName;
	}
	
	private String getNote(long rawContactId) {
		ContentResolver cr = activity.getContentResolver();
		Cursor c = null;
		String note = null;
		
		String[] projection = {Data.RAW_CONTACT_ID, Data.MIMETYPE, Note.NOTE};
		String selection = Data.MIMETYPE + " = ? AND " + Data.RAW_CONTACT_ID + " = ? ";
		String[] selectionArgs = {Note.MIMETYPE, Long.toString(rawContactId)};
		
		c = cr.query(Data.CONTENT_URI, projection, selection, selectionArgs, null);
		int noteColumnIndex = c.getColumnIndex(Note.NOTE);
		if (c.moveToFirst()) {
			note = c.getString(noteColumnIndex);
		}
		
		c.close();
		return note;
	}
	
	private String getPhoneAndType(long rawContactId) {
		ContentResolver cr = activity.getContentResolver();
		Cursor c = null;
		String phoneNumber = null;
		String phoneType = null;
		
		String[] projection = {Data.RAW_CONTACT_ID, Data.MIMETYPE, Phone.NUMBER, Phone.TYPE};
		String selection = Data.MIMETYPE + " = ? AND " + Data.RAW_CONTACT_ID + " = ? ";
		String[] selectionArgs = {Phone.MIMETYPE, Long.toString(rawContactId)};
		
		c = cr.query(Data.CONTENT_URI, projection, selection, selectionArgs, null);
		int phoneNumberColumnIndex = c.getColumnIndex(Phone.NUMBER);
		//Pick the first Phone number, ignore other phone type and number
		if (c.moveToFirst()) {
			phoneNumber = c.getString(phoneNumberColumnIndex);
		}
		
		c.close();
		return phoneNumber;
	}
	
	private byte[] getPhoto(long rawContactId) {
		ContentResolver cr = activity.getContentResolver();
		Cursor c = null;
		byte[] photo = null;
		
		String[] projection = {Data.RAW_CONTACT_ID, Data.MIMETYPE, Photo.PHOTO};
		String selection = Data.MIMETYPE + " = ? AND " + Data.RAW_CONTACT_ID + " = ? ";
		String[] selectionArgs = {Photo.MIMETYPE, Long.toString(rawContactId)};
		
		c = cr.query(Data.CONTENT_URI, projection, selection, selectionArgs, null);
		int photoColumnIndex = c.getColumnIndex(Photo.PHOTO);
		if (c.moveToFirst()) {
			photo = c.getBlob(photoColumnIndex);
		}
		
		c.close();
		return photo;
	}
	
	private String getBirthday(long rawContactId) {
		ContentResolver cr = activity.getContentResolver();
		Cursor c = null;
		String birthday = null;
		
		String[] projection = {Data.RAW_CONTACT_ID, Data.MIMETYPE, Event.START_DATE, Event.TYPE};
		String selection = Data.MIMETYPE + " = ? AND " 
							+ Data.RAW_CONTACT_ID + " = ? "
							+ Event.TYPE + " = ?";
		String[] selectionArgs = {Event.MIMETYPE, 
								  Long.toString(rawContactId),
								  Integer.toString(Event.TYPE_BIRTHDAY)};
		
		c = cr.query(Data.CONTENT_URI, projection, selection, selectionArgs, null);
		int birthdayColumnIndex = c.getColumnIndex(Event.START_DATE);
		if (c.moveToFirst()) {
			birthday = c.getString(birthdayColumnIndex);
		}
		
		c.close();
		return birthday;
	}
	
	
	/**
	 * this method only update birthday, ignor other field
	 * @param contacts
	 * @return
	 */
	public int updateContact(List<Contact> contacts) {
		ContentResolver cr = activity.getContentResolver();
		int updatedRow = 0;
		Uri uri = ContactsContract.Data.CONTENT_URI;
		
		for (Contact c : contacts) {
			Uri updatedUri = ContentUris.withAppendedId(uri, c.getRawContactId());
			ContentValues values = new ContentValues();
			values.put(Data.MIMETYPE, Event.MIMETYPE);
			values.put(Event.TYPE, Event.TYPE_BIRTHDAY);
			values.put(Event.START_DATE, c.getBirthday());
			
			updatedRow = cr.update(updatedUri, values, null, null);
		}
		
		return updatedRow;
	}

	public static List<Contact> contactsIntoList(Contact... contacts) {
		List<Contact> contactList = new ArrayList<Contact>();
		for (Contact c : contacts) {
			contactList.add(c);
		}
		return contactList;
	}
	
	/**
	 * This method only convert contact's id, photo and name into a list of map;
	 * @param contacts
	 * @return
	 */
	public static List<Map<String, Object>> listToMap(List<Contact> contacts) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
		for (Contact c : contacts) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put(RawContacts._ID, c.getRawContactId());
			m.put(StructuredName.DISPLAY_NAME, c.getDisplayedName());
			m.put(Photo.PHOTO, c.getPhoto());
			
			listMap.add(m);
		}
		
		return listMap;
	}
}
