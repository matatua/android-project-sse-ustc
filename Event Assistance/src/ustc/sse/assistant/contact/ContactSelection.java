package ustc.sse.assistant.contact;

import ustc.sse.assistant.R;
import ustc.sse.assistant.contact.data.ContactUtils;
import ustc.sse.assistant.contact.data.Group;
import ustc.sse.assistant.contact.data.GroupUtils;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactSelection extends Activity {
	
	public static final Boolean IS_CONTAIN_GROUP = true;
	public static final String  CONTAIN_GROUP = "CONTAIN_GROUP";
	public static final String GROUP_ID	= "GROUP_ID";
	private static final int WAITING_CONTACT_ID = 100;
	private static final String SELECTED_CONTACT = "SELECTED_CONTACT";
	
	private boolean allSelected = false;
	private ContactUtils contactUtils;
	private GroupUtils groupUtils;
	private ListView listView;
	private Button selectAllButton;
	private Button addButton;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_selection);
		contactUtils = new ContactUtils(this);
		groupUtils = new GroupUtils(this);
		listView = (ListView) findViewById(R.id.contact_selection_list_view);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ContactSelection.this.addButton.setText("Add (" + ContactSelection.this.listView.getCheckedItemIds().length + ")");
				if (ContactSelection.this.listView.getCheckedItemIds().length != 
						ContactSelection.this.listView.getAdapter().getCount()) {
					allSelected = false;
					ContactSelection.this.selectAllButton.setText("Select all");
				} else {
					allSelected = true;
					ContactSelection.this.selectAllButton.setText("cancel selections");

				}
			}
		});
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		initButtons();
		/*if (containGrouop()) {
			initGroups();
		}	*/
		initContacts();
		//prepareCheckedItem();
	}

	private void initButtons() {
		selectAllButton = (Button) findViewById(R.id.contact_selection_select_all);
		addButton = (Button) findViewById(R.id.contact_selection_add);
		selectAllButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//select all the contact
				if (!allSelected) {
					for (int i = 0; i < ContactSelection.this.listView.getAdapter().getCount(); i++) {
						ContactSelection.this.listView.setItemChecked(i, true);
						allSelected = true;
						selectAllButton.setText("Cancel selections");
					}
				} else {
					for (int i = 0; i < ContactSelection.this.listView.getAdapter().getCount(); i++) {
						ContactSelection.this.listView.setItemChecked(i, false); 
						allSelected = false;
						selectAllButton.setText("Select all");
					}
				}
				ContactSelection.this.addButton.setText("Add (" + ContactSelection.this.listView.getCheckedItemIds().length + ")");

			}
		});
		
		addButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ContactSelection.this.setResult(RESULT_OK, getIntent());
				ContactSelection.this.finish();
				
			}
		});
		
	}

/*	private void prepareCheckedItem() {
		long[] checkedItemId = getIntent().getLongArrayExtra(SELECTED_CONTACT);
		if (checkedItemId != null) {
			Map<Long, Integer> contactIdPositionMap = ((ContactSelectionListAdapter) listView.getAdapter()).getContactIdPositionMap();
			for (long id : checkedItemId) {
				Integer position = contactIdPositionMap.get(id);
				listView.setItemChecked(position, true);
			}
		}
	}*/

	/**
	 * here we store the selectedContact map in intent.
	 * This map may be used.
	 */
	@Override
	protected void onStop() {
		super.onStop();
		//execute when the activity not contain group
		/*if (null != getIntent() && getIntent().getBooleanExtra(CONTAIN_GROUP, false)) {		
			
			getIntent().putExtra(SELECTED_CONTACT, listView.getCheckedItemIds());
		
		}*/
	}
	
	/*
	private boolean containGrouop() {
		Intent intent = getIntent();
		if (intent != null) {
			return intent.getBooleanExtra(CONTAIN_GROUP, true);
			
		}
		return true;
	}*/
	
/*	private void initGroups() {
		
		List<Group> groups = groupUtils.getAllGroups();
		for (Group g : groups) {
			//make sure that the default group doesn't appear in this screen
			if (g.getGroupId() != ContactList.DEFAULT_GROUP_ID) {
				View groupView = getLayoutInflater().inflate(R.layout.contact_selection_group_view, null);
				TextView titleTv = (TextView) groupView.findViewById(R.id.contact_selection_group_title);
				titleTv.setText(g.getTitle());
				groupView.setTag(g.getGroupId());
				
				groupView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ContactSelection.this, ContactSelection.class);
						intent.putExtra(CONTAIN_GROUP, false);
						intent.putExtra(GROUP_ID, (Long) v.getTag());		
						intent.putExtra(SELECTED_CONTACT, ContactSelection.this.listView.getCheckedItemIds());
						ContactSelection.this.startActivityForResult(intent, WAITING_CONTACT_ID);
					}
				});
				
				listView.addHeaderView(groupView);
			}
		}
	}*/
	
	
	private void initContacts() {
		Cursor contactsCursor = null;
		SimpleCursorAdapter cursorAdapter = null;
		
		if (getIntent() != null && getIntent().getBooleanExtra(CONTAIN_GROUP, false)) {
			Group g = new Group(getIntent().getLongExtra(GROUP_ID, ContactList.DEFAULT_GROUP_ID));
			contactsCursor = contactUtils.getContactsByGroup(g);
			cursorAdapter = new SimpleCursorAdapter(this, 
													android.R.layout.simple_list_item_multiple_choice,
													contactsCursor, 
													new String[]{android.provider.ContactsContract.Data.DISPLAY_NAME},
													new int[]{android.R.id.text1});
		} else {
			contactsCursor = contactUtils.getAllContactsBasicInfoCursor();
			cursorAdapter = new SimpleCursorAdapter(this, 
													android.R.layout.simple_list_item_multiple_choice,
													contactsCursor, 
													new String[]{Contacts.DISPLAY_NAME,},
													new int[]{android.R.id.text1});
		}
		
		listView.setAdapter(cursorAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setFocusable(false);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	/*	if (requestCode == WAITING_CONTACT_ID && data != null) {
			long[] checkedItemId = data.getLongArrayExtra(SELECTED_CONTACT);
			Map<Long, Integer> idPositionMap = ((ContactSelectionListAdapter) listView.getAdapter()).getContactIdPositionMap();

			for (long id : checkedItemId) {
				int position = idPositionMap.get(id);
				listView.setItemChecked(position, true);
			}
					
		}*/
	}
	
	/*public static class ContactSelectionListAdapter extends SimpleCursorAdapter {

		private Map<Long, Integer> contactIdPositionMap = new HashMap<Long, Integer>();
		private int currentPosition;
		
		public ContactSelectionListAdapter(Context context, int layout,
				Cursor c, String[] from, int[] to) {
			super(context, layout, c, from, to);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			currentPosition = position;
			return super.getView(position, convertView, parent);
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			Long contactId = null;
			if (cursor.getColumnIndex(Contacts._ID) != -1) {
				contactId = cursor.getLong(cursor.getColumnIndex(Contacts._ID));
			} else {
				contactId = cursor.getLong(cursor.getColumnIndex(android.provider.ContactsContract.Data.CONTACT_ID));
			}
			contactIdPositionMap.put(contactId, currentPosition);
			return super.newView(context, cursor, parent);
		}
		
		@Override
		protected void onContentChanged() {
			contactIdPositionMap.clear();
			super.onContentChanged();
		}

		public Map<Long, Integer> getContactIdPositionMap() {
			return contactIdPositionMap;
		}
		
	}*/
}
