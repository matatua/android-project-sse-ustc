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
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

/**
 * 
 * @author 李健
 *
 * this class is used when associate contacts to a particular event.
 */
public class ContactSelection extends ExpandableListActivity {

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
		initialCheckBox();
		
	}

	/**
	 * initiate a group view titled All, 
	 * this group contain all the members
	 */
	private void initialAllMembersGroup() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * initiate group and child checkbox
	 */
	private void initialCheckBox() {
		ExpandableListView listView = this.getExpandableListView();
		CheckBox groupCheckbox = (CheckBox) listView.findViewById(R.id.contact_selection_group_check);
	
//		groupCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if (isChecked) {
//					Toast.makeText(ContactSelection.this, "Check", Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
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
						R.id.contact_selection_group_capacity};
		
		List<List<Map<String, Object>>> childData = getChildData(groups);
		int childLayout = R.layout.contact_list_selection_child;
		String[] childFrom = {RawContacts._ID, 
								StructuredName.DISPLAY_NAME};
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
				childMapping.put(RawContacts._ID, c.getContactId());
				childMapping.put(StructuredName.DISPLAY_NAME, c.getDisplayedName());
				
				oneGroup.add(childMapping);
			}
			
			childData.add(oneGroup);
		}
		
		return childData; 
	}

	
	
}
