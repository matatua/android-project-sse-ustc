package ustc.sse.assistant.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ustc.sse.assistant.R;
import ustc.sse.assistant.contact.data.ContactUtils;
import ustc.sse.assistant.contact.data.Group;
import ustc.sse.assistant.contact.data.GroupUtils;
import android.app.Activity;
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
	public static final String  CONTAIN_GROUP = "contain_group";
	public static final String GROUP_ID	= "gorup_id";
	public static final String SELECTED_CONTACT_IDS = "selected_contact_ids";
	public static final String SELECTED_CONTACT_DISPLAY_NAME = "selected_contact_display_name";
	
	private boolean allSelected = false;
	private ContactUtils contactUtils;
	private ListView listView;
	private Button selectAllButton;
	private Button addButton;
	
	private Map<Integer, Long> checkedItemPositions;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_selection);
		contactUtils = new ContactUtils(this);
		new GroupUtils(this);
		checkedItemPositions = new HashMap<Integer, Long>();
		
		listView = (ListView) findViewById(R.id.contact_selection_list_view);
		listView.setSmoothScrollbarEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ContactSelection.this.addButton.setText("Add (" + ContactSelection.this.listView.getCheckedItemIds().length + ")");
				//if not all items are checked
				if (ContactSelection.this.listView.getCheckedItemIds().length != 
						ContactSelection.this.listView.getAdapter().getCount()) {
					allSelected = false;
					ContactSelection.this.selectAllButton.setText("Select all");
				} else {
					//if all items are checked
					allSelected = true;
					ContactSelection.this.selectAllButton.setText("cancel selections");
				}
				
				//put or remove the clicked item
				if (checkedItemPositions.containsKey(position)) {
					checkedItemPositions.remove(position);
				} else {
					checkedItemPositions.put(position, id);
				}
				
			}
		});
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		initButtons();
	
		initContacts();
	
	}

	private void initButtons() {
		selectAllButton = (Button) findViewById(R.id.contact_selection_select_all);
		addButton = (Button) findViewById(R.id.contact_selection_add);
		selectAllButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				//select all the contact
				if (!allSelected) {
					for (int i = 0; i < ContactSelection.this.listView.getAdapter().getCount(); i++) {
						listView.setItemChecked(i, true);
						Long id = listView.getItemIdAtPosition(i);
						checkedItemPositions.put(i, id);
						allSelected = true;
						selectAllButton.setText("Cancel selections");
					}
				} else {
					for (int i = 0; i < ContactSelection.this.listView.getAdapter().getCount(); i++) {
						ContactSelection.this.listView.setItemChecked(i, false); 
						checkedItemPositions.clear();
						allSelected = false;
						selectAllButton.setText("Select all");
					}
				}
				ContactSelection.this.addButton.setText("Add (" + ContactSelection.this.listView.getCheckedItemIds().length + ")");

			}
		});
		
		addButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				List<Long> checkedItemIds = new ArrayList<Long>();
				List<String> checkedItemsDisplayName = new ArrayList<String>();
				Iterator<Entry<Integer, Long>> checkedItemsPositionIter = checkedItemPositions.entrySet().iterator();
				while (checkedItemsPositionIter.hasNext()) {
					Entry<Integer, Long> entry = checkedItemsPositionIter.next();
					Integer position = entry.getKey();
					Long id = entry.getValue();
					Cursor cur = (Cursor) listView.getItemAtPosition(position);
					String name = cur.getString(cur.getColumnIndex(Contacts.DISPLAY_NAME));
					checkedItemIds.add(id);
					checkedItemsDisplayName.add(name);
				}
				long[] ids = new long[checkedItemIds.size()];
				String[] names = new String[checkedItemsDisplayName.size()];
				for (int i = 0; i < checkedItemIds.size(); i++) {
					ids[i] = checkedItemIds.get(i);
					names[i] = checkedItemsDisplayName.get(i);
				}
				getIntent().putExtra(SELECTED_CONTACT_IDS, ids);
				getIntent().putExtra(SELECTED_CONTACT_DISPLAY_NAME, names);
				ContactSelection.this.setResult(RESULT_OK, getIntent());
				ContactSelection.this.finish();
				
			}
		});
		
	}

	/**
	 * here we store the selectedContact map in intent.
	 * This map may be used.
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}
	
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
}
