package ustc.sse.event.contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ustc.sse.event.R;
import ustc.sse.event.contact.data.Contact;
import ustc.sse.event.contact.data.ContactUtils;
import ustc.sse.event.contact.data.Group;
import ustc.sse.event.contact.data.GroupUtils;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ContactList extends Activity {
	
	public static final long NONE_GROUP_ID = -2;
	public static final long DEFAULT_GROUOP = -1;
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
		
		loadLastSelectedGroup();
		prepareGroupListView();
		prepareGroupMemberListView();
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
				iv.setImageResource(resId);
				groupListView.setVisibility(View.VISIBLE);
			}
		});
		
		//when user click one group item, the selector's title will correspondingly change
		//and the contactListView will change too.
		groupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				//change selector indicator image and group title and summary count
				iv.setImageResource(resId);
				//change and set a corresponding members
			
				List<Map<String, Object>> contacts = null;
				if (id < 0) {				
					contacts = ContactUtils.listToMap(cu.getAllContactsBasicInfo());
				} else {
					contacts = ContactUtils.listToMap(cu.getContactsBasicInfoByGroup(new Group(id)));
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
				adapter.setViewBinder(viewBinder);
				
				contactListView.setAdapter(adapter);
				
				groupListView.setVisibility(View.INVISIBLE);
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
	
	private List<Contact> prepareGroupMemberListView() {
		List<Contact> contacts = null;
		
		if (lastSelectedGroup == NONE_GROUP_ID) {
			lastSelectedGroup = DEFAULT_GROUOP;
			contacts = cu.getAllContactsBasicInfo();
		} else {
			contacts = cu.getContactsBasicInfoByGroup(new Group(lastSelectedGroup));
		}
		
		return contacts;
	}
}
