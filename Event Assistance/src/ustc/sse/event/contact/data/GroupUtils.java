package ustc.sse.event.contact.data;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
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
	
	public List<Group> getAllGroups() {
		ContentResolver cr = activity.getContentResolver();
		List<Group> groups = new ArrayList<Group>();
		Cursor cur = null;
		
		Uri uri = Groups.CONTENT_URI;
		String[] projection = {Groups._ID, Groups.TITLE, Groups.SUMMARY_COUNT, 
				Groups.SUMMARY_WITH_PHONES, Groups.NOTES};
		String sortOrder = " " + Groups.TITLE + " DESC ";
		
		cur = cr.query(uri, projection, null, null, sortOrder);
		activity.startManagingCursor(cur);
		
		if (cur != null && cur.moveToFirst()) {
			int groupIdIndex = cur.getColumnIndex(Groups._ID);
			int titleIndex = cur.getColumnIndex(Groups.TITLE);
			int summaryCountIndex = cur.getColumnIndex(Groups.SUMMARY_WITH_PHONES);
			int summaryCountWithPhoneIndex = cur.getColumnIndex(Groups.SUMMARY_WITH_PHONES);
			int notesIndex = cur.getColumnIndex(Groups.NOTES);
			
			do {
				Group g = new Group();
				g.setGroupId(cur.getLong(groupIdIndex));
				g.setNote(cur.getString(notesIndex));
				g.setSummaryCount(cur.getInt(summaryCountIndex));
				g.setSummaryCountWithPhone(cur.getInt(summaryCountWithPhoneIndex));
				g.setTitle(cur.getString(titleIndex));
				
				groups.add(g);
				
			} while (cur.moveToNext());
		}
		
		return groups;
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
}
