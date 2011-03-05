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
	
	public boolean deleteGroups(List<Integer> groupIds) {
		ContentResolver cr = activity.getContentResolver();
		
		for (Integer i : groupIds) {
			ContentUris.withAppendedId(Groups.CONTENT_URI, i);
			
			cr.delete(url, where, selectionArgs)
		}
	}
	
	public boolean addGroup(ContentValue cv) {
		
	}
}
