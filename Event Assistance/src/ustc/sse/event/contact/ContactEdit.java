/**
 * 
 */
package ustc.sse.event.contact;

import java.util.Calendar;
import java.util.Date;

import ustc.sse.event.R;
import ustc.sse.event.contact.data.Contact;
import ustc.sse.event.contact.data.ContactUtils;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract.RawContacts;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author 李健
 *
 */
public class ContactEdit extends Activity {
	private ContactUtils cu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.contact_edit);
		
		cu = new ContactUtils(this);
		loadContactInformation();
	}
	
	private void loadContactInformation() {
		Long rawContactId = getIntent().getLongExtra(RawContacts._ID, -1);
		Contact contact = cu.getContactById(rawContactId);
		
		Bitmap bitmap = BitmapFactory.decodeByteArray(contact.getPhoto(), 0, contact.getPhoto().length);
		ImageView photoImageView = (ImageView) findViewById(R.id.contact_edit_image);
		TextView nameTextView = (TextView) findViewById(R.id.contact_edit_name);
		Button birthdayChooseButton = (Button) findViewById(R.id.contact_edit_birthday_button);
		photoImageView.setImageBitmap(bitmap);
		nameTextView.setText(contact.getDisplayedName());
		
		String birthdayInMilliSecond = contact.getBirthday();
		
		CharSequence birthday = DateFormat.format("yyyy-MM-dd", new Long(birthdayInMilliSecond));
		birthdayChooseButton.setText(birthday);
	}
	
	private void initSaveCancelButtons() {
		Button saveButton = (Button) findViewById(R.id.contact_edit_save_button);
		Button cancelButton = (Button) findViewById(R.id.contact_edit_cancel_button);
		
		
	}
	
	
	
	
}
