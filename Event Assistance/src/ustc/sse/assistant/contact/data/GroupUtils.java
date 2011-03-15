package ustc.sse.assistant.contact.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ustc.sse.assistant.R;
import ustc.sse.assistant.contact.ContactList;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Groups;

/**
 * 
 * @author 李健
 * 
 * This class contain several convenient method for getting group info, updating
 * groups, and deleting groups. 
 *
 */
public class GroupUtils {
	private Activity activity;
	
	public GroupUtils(Activity activity) {
		this.activity = activity;
	}
	
	/**
	 * this method return all the groups with _id, title, summary information.
	 * Note: the first group is a default group added manually which including all the contacts.
	 * @return
	 */
	public List<Group> getAllGroups() {
		ContentResolver cr = activity.getContentResolver();
		List<Group> groups = new ArrayList<Group>();
		Cursor cur = null;
		
		Uri uri = Groups.CONTENT_URI;
		String[] projection = {Groups._ID, Groups.TITLE, Groups.NOTES};
		String sortOrder = " " + Groups.TITLE + " DESC ";
		
		cur = cr.query(uri, projection, null, null, sortOrder);
		activity.startManagingCursor(cur);
		
		//add a default group named All
		Group defaultGroup = new Group();
		defaultGroup.setGroupId(ContactList.DEFAULT_GROUP_ID);
		defaultGroup.setTitle(activity.getString(R.string.contact_list_group_all));
		defaultGroup.setNote("Default group");
		defaultGroup.setSummaryCount(0);
		defaultGroup.setSummaryCountWithPhone(0);
		groups.add(defaultGroup);
		
		if (cur != null && cur.moveToFirst()) {
			int groupIdIndex = cur.getColumnIndex(Groups._ID);
			int titleIndex = cur.getColumnIndex(Groups.TITLE);
			int notesIndex = cur.getColumnIndex(Groups.NOTES);
			
			do {
				Group g = new Group();
				g.setGroupId(cur.getLong(groupIdIndex));
				g.setNote(cur.getString(notesIndex));
				g.setTitle(cur.getString(titleIndex));
				
				groups.add(g);
				
			} while (cur.moveToNext());
		}
		
		return groups;
	}
	
	public Cursor getGroupCursor() {
		ContentResolver cr = activity.getContentResolver();
		Cursor cur = null;
		
		Uri uri = Groups.CONTENT_URI;
		String[] projection = {Groups._ID, Groups.TITLE, Groups.NOTES};
		String sortOrder = " " + Groups.TITLE + " DESC ";
		
		cur = cr.query(uri, projection, null, null, sortOrder);
		activity.startManagingCursor(cur);
		
		return cur;
	}
	
	public Integer getGroupSummaryCountInfo(Group group) {
		ContentResolver cr = activity.getContentResolver();
		Cursor cursor = null;
		if (group.getGroupId() == ContactList.DEFAULT_GROUP_ID) {
			cursor = cr.query(Contacts.CONTENT_URI,
							  new String[]{Contacts._COUNT}, 
							  null, 
							  null, 
							  null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					return cursor.getInt(0);
				}
			}
		}
		
		//other user made group
		Uri contentSummaryUri = Groups.CONTENT_SUMMARY_URI;
		cursor = cr.query(contentSummaryUri, 
								 new String[]{Groups.SUMMARY_COUNT}, 
								 Groups._ID + " = ? ", 
								 new String[]{Long.toString(group.getGroupId())}, 
								 null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				int summaryCountIndex = cursor.getColumnIndex(Groups.SUMMARY_COUNT);				
				return cursor.getInt(summaryCountIndex);
			}
		}
		return null;
	}
	
	public int updateGroup(ContentValues cv) {
		ContentResolver cr = activity.getContentResolver();
		
		ContentUris.withAppendedId(Groups.CONTENT_URI, cv.getAsLong(Groups._ID));
		Uri uri = Groups.CONTENT_URI;
		
		int updatedRow = cr.update(uri, cv, null, null);
		cr.notifyChange(uri, null);
		
		return updatedRow;
	}
	
	public int deleteGroups(List<Integer> groupIds) {
		ContentResolver cr = activity.getContentResolver();
		int deletedRow = 0;
		for (Integer i : groupIds) {
			Uri url = ContentUris.withAppendedId(Groups.CONTENT_URI, i);
			
			deletedRow += cr.delete(url, null, null);
		}
		
		cr.notifyChange(Groups.CONTENT_URI, null);
		return deletedRow;
	}
	
	public Uri addGroup(ContentValues cv) {
		ContentResolver cr = activity.getContentResolver();
		
		Uri url = Groups.CONTENT_URI;
		Uri newItem = cr.insert(url, cv);
		
		cr.notifyChange(url, null);
		
		return newItem;
	}
	
	public static ContentValues groupToContentValues(Group g) {
		ContentValues cv = new ContentValues();
		
		cv.put(Groups._ID, g.getGroupId());
		
		if (g.getTitle() != null) {
			cv.put(Groups.TITLE, g.getTitle());
		}
		if (g.getNote() != null) {
			cv.put(Groups.NOTES, g.getNote());
		}
		
		cv.put(Groups.SUMMARY_COUNT, g.getSummaryCount());
		cv.put(Groups.SUMMARY_WITH_PHONES, g.getSummaryCountWithPhone());
		
		return cv;
	}
	
	public static List<Map<String, Object>> groupsToList(List<Group> groups) {
		List<Map<String, Object>> listOfMaps = new ArrayList<Map<String,Object>>();
		
		for (Group group : groups) {
			Map<String, Object> groupMapping = new HashMap<String, Object>();
			groupMapping.put(Groups._ID, group.getGroupId());
			groupMapping.put(Groups.TITLE, group.getTitle());
			groupMapping.put(Groups.SUMMARY_COUNT, group.getSummaryCount());
			groupMapping.put(Groups.SUMMARY_WITH_PHONES, group.getSummaryCountWithPhone());
			
			listOfMaps.add(groupMapping);			
		}
		return listOfMaps;
	}

	/**
	 * get a group's title by id
	 * @param groupId
	 * @return
	 */
	public Group getGroupById(Long groupId) {
		ContentResolver cr = activity.getContentResolver();
		Group group = null;
		Cursor c = cr.query(Groups.CONTENT_URI,
							 new String[]{Groups._ID, Groups.TITLE}, 
							 Groups._ID + " = ? ", 
							 new String[]{groupId.toString()}, 
							 null);
		
		if (c != null) {
			if (c.moveToFirst()) {
				group = new Group();
				int groupTitleColumnIndex = c.getColumnIndex(Groups.TITLE);
				String groupTitle = c.getString(groupTitleColumnIndex);
				
				group.setTitle(groupTitle);
				group.setGroupId(groupId);
			}
		}
		
		return group;
	}
}
