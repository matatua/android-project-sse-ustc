/**
 * 
 */
package ustc.sse.assistant.contact;

import ustc.sse.assistant.R;
import ustc.sse.assistant.contact.data.Contact;
import ustc.sse.assistant.contact.data.ContactUtils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract.RawContacts;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author 李健
 *This activity shows a individual contact's detail information, 
 *including photo, display name, birthday, and note.
 */
public class ContactDetail extends Activity {

	private static final int DEFAULT_GROUP = Menu.FIRST;
	private static final int EDIT_MENU_ITEM_ID = Menu.FIRST;
	private static final int EDIT_MENU_ITEM_ORDER = Menu.FIRST;
	
	private ContactUtils cu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		cu = new ContactUtils(this);
		setContentView(R.layout.contact_detail);
		
	}
	
	/**
	 * load the individual contact here. In case of contact detail has been modified.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		loadContactDetail();
	}
	
	private void loadContactDetail() {
		Long rawContactId = getIntent().getLongExtra(RawContacts._ID, -1L);
		Contact c = cu.getContactById(rawContactId);
		Bitmap photo = BitmapFactory.decodeByteArray(c.getPhoto(), 0, c.getPhoto().length);
		String name = c.getDisplayedName();
		String birthday = c.getBirthday();
		String note = c.getNote();
		
		ImageView photoImageView = (ImageView) findViewById(R.id.contact_detail_image);
		photoImageView.setImageBitmap(photo);
		
		TextView nameTextView = (TextView) findViewById(R.id.contact_detail_name);
		nameTextView.setText(name);
		
		TextView birthdayTextView = (TextView) findViewById(R.id.contact_detail_birthday_text);
		birthdayTextView.setText(birthday);
		
		TextView noteTextView = (TextView) findViewById(R.id.contact_detail_note_text);
		noteTextView.setText(note);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		
		MenuItem editMenuItem = menu.add(DEFAULT_GROUP, 
										EDIT_MENU_ITEM_ID, 
										EDIT_MENU_ITEM_ORDER, 
										R.string.contact_detail_option_menu_edit);
		editMenuItem.setIcon(R.drawable.icon);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(this, ContactEdit.class);
		intent.putExtra(RawContacts._ID, getIntent().getLongExtra(RawContacts._ID, -1L));
		
		switch (item.getItemId()) {
		case EDIT_MENU_ITEM_ID :
			startActivity(intent);
		}
		return true;
	}
}
