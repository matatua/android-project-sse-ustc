/**
 * 
 * @author 李健
 *
 */
package ustc.sse.assistant.contact.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
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
		
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = {Contacts._ID, Contacts.DISPLAY_NAME, Contacts.PHOTO_ID};
		
		cur = cr.query(uri, projection, null, null, null);
		
		if (cur.moveToFirst()) {
					
			int contactIdIndex = cur.getColumnIndex(Contacts._ID);
			int displayNameIndex = cur.getColumnIndex(Contacts.DISPLAY_NAME);
			int photoIdIndex = cur.getColumnIndex(Contacts.PHOTO_ID);
			
			do {
				Contact contact = new Contact();	
				contact.setContactId(cur.getLong(contactIdIndex));
				contact.setDisplayedName(cur.getString(displayNameIndex));
				contact.setPhotoId(cur.getLong(photoIdIndex));
				contacts.add(contact);
			} while (cur.moveToNext());
			
			
		}
		
		for (Contact c : contacts) {
			Long photoId = c.getPhotoId();
			if (photoId == 0 || photoId == null) {
				continue;
			}
			c.setPhoto(getPhoto(photoId));
		}
		
		cur.close();
		return contacts;
	}

	/**
	 * get the cursor including all contacts' primary display name and photo id
	 * @return
	 */
	public Cursor getAllContactsBasicInfoCursor() {
		ContentResolver cr = activity.getContentResolver();
		
		Cursor cur = null;
		
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = {Contacts._ID, Contacts.DISPLAY_NAME, Contacts.PHOTO_ID};
		
		cur = cr.query(uri, projection, null, null, null);
		activity.startManagingCursor(cur);
		return cur;
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
		String[] groupProjection = {Data.DISPLAY_NAME,
									Data.PHOTO_ID,
									Data.CONTACT_ID};
		
		String selection = Data.MIMETYPE + " = ?" + " AND " 
									+ GroupMembership.GROUP_ROW_ID + " = ?";
		
		String[] selectionArgs = {GroupMembership.CONTENT_ITEM_TYPE, Long.toString(g.getGroupId())};
		cur = cr.query(dataUri, groupProjection, selection, selectionArgs, null);
		
		
		if (cur.moveToFirst()) {
	
			int displayNameIndex = cur.getColumnIndex(Data.DISPLAY_NAME);
			int contactIndex = cur.getColumnIndex(Data.CONTACT_ID);
			int photoIdIndex = cur.getColumnIndex(Data.PHOTO_ID);
			
			do {
				Contact contact = new Contact();
				contact.setContactId(cur.getLong(contactIndex));
				contact.setPhotoId(cur.getLong(photoIdIndex));
				contact.setDisplayedName(cur.getString(displayNameIndex));
				contacts.add(contact);
			} while (cur.moveToNext());
		}
		
		for (Contact c : contacts) {
			c.setPhoto(getPhoto(c.getContactId()));
			
		}
		
		cur.close();
		return contacts;
	}
	
	public Contact getContactById(long contactId) {
		String displayName = getDisplayName(contactId);
		String note = getNote(contactId);
		String[] phoneAndType = getPrimaryPhone(contactId);
		byte[] photo = getPhoto(contactId);
		String birthday = getBirthday(contactId);
		
		Contact c = new Contact();
		c.setBirthday(birthday);
		c.setDisplayedName(displayName);
		c.setNote(note);
		c.setEventType(Integer.toString(Event.TYPE_BIRTHDAY));
		c.setPhoneNumber(phoneAndType[0]);
		c.setPhoneType(phoneAndType[1]);
		c.setPhoneType(Integer.toString(Phone.TYPE_MAIN));
		c.setContactId(contactId);
		c.setPhoto(photo);
		
		return c;
	}
	
	private String getDisplayName(long contactId) {
		ContentResolver cr = activity.getContentResolver();
		Cursor c = null;
		String displayName = null;
		
		
		String[] projection = {Contacts.DISPLAY_NAME};
		String selection = Contacts._ID + " = ? ";
		String[] selectionArgs = {Long.toString(contactId)};
		c = cr.query(Contacts.CONTENT_URI, projection, selection, selectionArgs, null);
		
		int displayNameIndex = c.getColumnIndex(Contacts.DISPLAY_NAME);
		if (c.moveToFirst()) {
			displayName = c.getString(displayNameIndex);
		}
		c.close();
		
		return displayName;
	}
	
	private String getNote(long contactId) {
		ContentResolver cr = activity.getContentResolver();
		Cursor c = null;
		String note = null;
		
		String[] projection = {Note.NOTE};
		String selection = Data.MIMETYPE + " = ?" + " AND " + Data.CONTACT_ID + " = ? ";
		String[] selectionArgs = {Note.CONTENT_ITEM_TYPE, Long.toString(contactId)};
		
		c = cr.query(Data.CONTENT_URI, projection, selection, selectionArgs, null);
		int noteColumnIndex = c.getColumnIndex(Note.NOTE);
		if (c.moveToFirst()) {
			note = c.getString(noteColumnIndex);
		}
		
		c.close();
		return note;
	}
	
	private String[] getPrimaryPhone(long contactId) {
		ContentResolver cr = activity.getContentResolver();
		Cursor c = null;
		String phoneNumber = null;
		String phoneType = null;
		
		String[] projection = {Phone.NUMBER, Phone.TYPE};
		String selection = Data.MIMETYPE + " = ? AND " + 
							Data.CONTACT_ID + " = ? " + " AND " +
							Data.IS_PRIMARY + " = ?";
		String[] selectionArgs = {Phone.CONTENT_ITEM_TYPE, 
									Long.toString(contactId),
									Integer.toString(1)};
		
		c = cr.query(Data.CONTENT_URI, projection, selection, selectionArgs, null);
		int phoneNumberColumnIndex = c.getColumnIndex(Phone.NUMBER);
		int phoneTypeColumnIndex = c.getColumnIndex(Phone.TYPE);
		//Pick the first Phone number, ignore other phone type and number
		if (c.moveToFirst()) {
			phoneNumber = c.getString(phoneNumberColumnIndex);
			phoneType = c.getString(phoneTypeColumnIndex);
		}
		
		c.close();
		return new String[]{phoneNumber, phoneType};
	}
	
	private byte[] getPhoto(Long photoId) {
		Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, photoId);
		Uri photoUri = Uri.withAppendedPath(contactUri, android.provider.ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
		byte[] data = null;
		
		 Cursor cursor = activity.getContentResolver().query(photoUri,
		          new String[] {Photo.PHOTO}, null, null, null);
		     if (cursor == null) {
		         return null;
		     }
		     try {
		         if (cursor.moveToFirst()) {
		             data = cursor.getBlob(0);
		            
		         }
		     } finally {
		         cursor.close();
		     }
		     
		     return data;

	}
	
	private String getBirthday(long contactId) {
		ContentResolver cr = activity.getContentResolver();
		Cursor c = null;
		String birthday = null;
		
		String[] projection = {Event.START_DATE, Event.TYPE};
		String selection = Data.MIMETYPE + " = ? AND " 
							+ Data.CONTACT_ID + " = ? " + " AND "
							+ Event.TYPE + " = ?";
		String[] selectionArgs = {Event.CONTENT_ITEM_TYPE, 
								  Long.toString(contactId),
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
	 * @throws OperationApplicationException 
	 * @throws RemoteException 
	 */
	public int updateContact(List<Contact> contacts, boolean newData) throws RemoteException, OperationApplicationException {
	
		int updatedRow = 0;
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		for (Contact c : contacts) {
			//get the first rawcontact id
			Long rawContactId = getRawContactId(c.getContactId()).get(0);
			
			if (newData) {
				ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				          .withValue(Data.RAW_CONTACT_ID, rawContactId)
				          .withValue(Data.MIMETYPE, Event.CONTENT_ITEM_TYPE)
				          .withValue(Event.TYPE, Event.TYPE_BIRTHDAY)
				          .withValue(Event.START_DATE, c.getBirthday())
				          .build());
			} else {
				ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
				          .withSelection(Data.RAW_CONTACT_ID + " = ?", new String[]{rawContactId.toString()})
				          .withSelection(Data.MIMETYPE + " = ? ", new String[]{Event.CONTENT_ITEM_TYPE})
				          .withSelection(Event.TYPE + " = ?", new String[]{Long.toString(Event.TYPE_BIRTHDAY)})
				          .withValue(Event.START_DATE, c.getBirthday())
				          .build());
			}
		}
		
		activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
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
			m.put(Contacts._ID, c.getContactId());
			m.put(Contacts.DISPLAY_NAME, c.getDisplayedName());
			m.put(Photo.PHOTO, c.getPhoto());
			
			listMap.add(m);
		}
		
		return listMap;
	}
	
	public static List<Map<String, Object>> contactsToGroups(List<Contact> contacts) {
		List<Map<String, Object>> groupOfMembers = new ArrayList<Map<String,Object>>();
		
		for (Contact c : contacts) {
			Map<String, Object> contactMapping = new HashMap<String, Object>();
			contactMapping.put(Contacts._ID, c.getContactId());
			contactMapping.put(Contacts.DISPLAY_NAME, c.getDisplayedName());
			
			groupOfMembers.add(contactMapping);
		}
		
		return groupOfMembers;
	}
	
	/**
	 * return all corresponding rawcontact id with a contact id
	 * @param contactId
	 * @return
	 */
	public List<Long> getRawContactId(Long contactId) {
		ContentResolver cr = activity.getContentResolver();
		
		Cursor cursor = cr.query(RawContacts.CONTENT_URI,
				 new String[]{RawContacts._ID},
				 RawContacts.CONTACT_ID + " = ? ",
				 new String[]{contactId.toString()},
				 null);
		
		List<Long> rawContactIds = new ArrayList<Long>();
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					Long rawContactId = cursor.getLong(0);
					rawContactIds.add(rawContactId);
				} while (cursor.moveToNext());
			}
		}
		
		return rawContactIds;
	}

}
