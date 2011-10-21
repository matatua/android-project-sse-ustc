package ustc.sse.assistant.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ustc.sse.assistant.R;
import ustc.sse.assistant.contact.data.Contact;
import ustc.sse.assistant.contact.data.ContactUtils;
import ustc.sse.assistant.contact.data.Group;
import ustc.sse.assistant.contact.data.GroupUtils;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Groups;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleExpandableListAdapter;

/**
 * 
 * @author 李健
 *
 * this class is used when associate contacts to a particular event.
 */
public class ContactSelection_deprecated extends ExpandableListActivity {

	private GroupUtils groupUtils;
	private ContactUtils contactUtils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		groupUtils = new GroupUtils(this);
		contactUtils = new ContactUtils(this);
	}  
	
	@Override
	protected void onStart() {
		super.onStart();
		
		initialAllMembersGroup();
		initialAdapter();
		//initialCheckBox();
		
	}

	/**
	 * initiate a group view titled All, 
	 * this group contain all the members
	 */
	private void initialAllMembersGroup() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * initiate the list adpter with all group and corresponding group members
	 */
	private void initialAdapter() {
		List<Group> groups = groupUtils.getAllGroups();
		List<Map<String, Object>> groupData = GroupUtils.groupsToList(groups);
		int expandedGroupLayout = R.layout.contact_list_selection_group;
		int collapsedGroupLayout = R.layout.contact_list_selection_group;
		String[] groupFrom = {Groups._ID, 
							  Groups.TITLE, 
							  };
		int[] groupTo = {R.id.contact_selection_group_hidden_id, 
						R.id.contact_selection_group_title, 
						};
		
		List<List<Map<String, Object>>> childData = getChildData(groups);
		int childLayout = R.layout.contact_list_selection_child;
		String[] childFrom = {Contacts._ID, 
							  Contacts.DISPLAY_NAME};
		int[] childTo = {R.id.contact_selection_child_hidden_id, 
						 R.id.contact_selection_child_name};
		
		SimpleExpandableListAdapter adapter = 
			new SimpleExpandableListAdapter(this,
											groupData, 
											expandedGroupLayout, 
											collapsedGroupLayout, 
											groupFrom, 
											groupTo, 
											childData, 
											childLayout, 
											childFrom, 
											childTo);
		
		this.setListAdapter(adapter);
		
	}
	
	private List<List<Map<String, Object>>> getChildData(List<Group> groups) {
		List<List<Map<String, Object>>> childData = new ArrayList<List<Map<String,Object>>>();
		
		for (Group g : groups) {
			List<Contact> contacts = contactUtils.getContactsBasicInfoByGroup(g);

			List<Map<String, Object>> oneGroup = new ArrayList<Map<String,Object>>();
			
			for (Contact c : contacts) {
				Map<String, Object> childMapping = new HashMap<String, Object>();
				childMapping.put(Contacts._ID, c.getContactId());
				childMapping.put(Contacts.DISPLAY_NAME, c.getDisplayedName());
				
				oneGroup.add(childMapping);
			}
			
			childData.add(oneGroup);
		}
		
		return childData; 
	}

	static public class ContactsExpandableListAdapter extends SimpleExpandableListAdapter {

		public ContactsExpandableListAdapter(Context context,
				List<? extends Map<String, ?>> groupData,
				int expandedGroupLayout, int collapsedGroupLayout,
				String[] groupFrom, int[] groupTo,
				List<? extends List<? extends Map<String, ?>>> childData,
				int childLayout, int lastChildLayout, String[] childFrom,
				int[] childTo) {
			
			super(context, groupData, expandedGroupLayout, collapsedGroupLayout, groupFrom,
					groupTo, childData, childLayout, lastChildLayout, childFrom, childTo);
		}
		
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			return super.getChildView(groupPosition, childPosition, isLastChild,
					convertView, parent);
		}
		
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			return super.getGroupView(groupPosition, isExpanded, convertView, parent);
		}
		
	}
	
}
