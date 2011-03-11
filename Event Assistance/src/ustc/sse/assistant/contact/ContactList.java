package ustc.sse.assistant.contact;

import java.util.List;
import java.util.Map;

import ustc.sse.assistant.R;
import ustc.sse.assistant.contact.data.Contact;
import ustc.sse.assistant.contact.data.ContactIndividualViewBinder;
import ustc.sse.assistant.contact.data.ContactUtils;
import ustc.sse.assistant.contact.data.Group;
import ustc.sse.assistant.contact.data.GroupUtils;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ContactList extends Activity {
	
	public static final long NONE_GROUP_ID = -2;
	public static final long DEFAULT_GROUP = -1;
	public static final String TAG = "ContactList";
	
	private ContactUtils cu;
	private GroupUtils gu;
	private ListView groupListView;
	private ListView contactListView;
	private SharedPreferences sharedPreference;
	private long lastSelectedGroup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.contact_list);
		cu = new ContactUtils(this);
		gu = new GroupUtils(this);
			
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		loadLastSelectedGroup();
		prepareGroupListView();
		prepareGroupMemberListView();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		saveLastSelectedGroupId();
	}
	
	/**
	 * initiate groupSelector and groupListView's onClickListener
	 */
	private void initListener() {
		final ImageView iv = (ImageView) findViewById(R.id.groupSelectorIndicatorImage);
		LinearLayout groupSelector = (LinearLayout) findViewById(R.id.groupSelector);
		
		//when this selector is clicked, the groupListView will become visible
		groupSelector.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//set the arrow image which directing below
				iv.setImageResource(R.drawable.icon);
				groupListView.setVisibility(View.VISIBLE);
			}
		});
		
		//when user click one group item, the selector's title will correspondingly change
		//and the contactListView will change too.
		groupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				//change selector indicator image and group title and summary count
				iv.setImageResource(R.drawable.icon);
				//change and set a corresponding members
			
				ListAdapter adapter = generateContactListViewAdapter(id);
				contactListView.setAdapter(adapter);
				
				groupListView.setVisibility(View.INVISIBLE);
				lastSelectedGroup = id;
				
			}
		});
		contactListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view, int position,
					long rowId) {
				LinearLayout linearLayout = (LinearLayout) view;
				TextView idTextView = (TextView) linearLayout.findViewById(R.id.hiddenIndividualId);
				Long individualId = new Long(idTextView.getText().toString());
				
				Intent intent = new Intent(ContactList.this, ContactDetail.class);
				intent.putExtra(RawContacts._ID, individualId);
				ContactList.this.startActivity(intent);
			}
		});
	
	}
	
	private void loadLastSelectedGroup() {
		sharedPreference = getPreferences(MODE_WORLD_WRITEABLE);
		lastSelectedGroup = sharedPreference.getLong(Groups._ID, NONE_GROUP_ID);
	}
	
	private void prepareGroupListView() {
		groupListView = (ListView) findViewById(R.id.groupListView);
		
		Cursor c = gu.getGroupCursor();
		int layout = R.layout.contact_list_group_item;
		String[] from = {Groups._ID, Groups.TITLE, Groups.SUMMARY_COUNT};
		int[] to = {R.id.hiddenGroupId, R.id.groupItemText, R.id.groupSummaryCountText};
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, 
															  layout, 
															  c, 
															  from, 
															  to);
		
		groupListView.setAdapter(adapter);
		
		
	}
	
	private void prepareGroupMemberListView() {
		
		ListAdapter adapter = null;
		if (lastSelectedGroup == NONE_GROUP_ID) {
			lastSelectedGroup = DEFAULT_GROUP;			
		} 
		adapter = generateContactListViewAdapter(lastSelectedGroup);
		
		contactListView.setAdapter(adapter);
		
	}
	
	/**
	 * help method used as providing ListAdapter according to a given GroupId
	 * @param groupId
	 * @return
	 */
	private ListAdapter generateContactListViewAdapter(long groupId) {
		List<Map<String, Object>> contacts = null;
		if (groupId == DEFAULT_GROUP) {				
			contacts = ContactUtils.listToMap(cu.getAllContactsBasicInfo());
		} else {
			contacts = ContactUtils.listToMap(cu.getContactsBasicInfoByGroup(new Group(groupId)));
		}
		
		String[] from = {RawContacts._ID, 
						 StructuredName.DISPLAY_NAME,
						 Photo.PHOTO};
		int[] to = {R.id.hiddenIndividualId,
					R.id.contactIndividualName,
					R.id.contactIndividualImage};
		SimpleAdapter adapter = new SimpleAdapter(ContactList.this, 
												  contacts, 
												  R.layout.contact_list_individual, 
												  from, 
												  to);
		adapter.setViewBinder(new ContactIndividualViewBinder());
		return adapter;
	}
	
	/**
	 * help method used to save the selected GroupId
	 */
	private void saveLastSelectedGroupId() {
		getPreferences(MODE_WORLD_WRITEABLE).edit()
											.putLong(Groups._ID, lastSelectedGroup);
		
	}
	
}
