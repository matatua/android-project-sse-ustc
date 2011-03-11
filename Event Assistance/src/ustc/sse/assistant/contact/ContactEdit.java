/**
 * 
 */
package ustc.sse.assistant.contact;

import java.util.Calendar;

import ustc.sse.assistant.R;
import ustc.sse.assistant.contact.data.Contact;
import ustc.sse.assistant.contact.data.ContactUtils;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract.RawContacts;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author 李健
 *This activity show the edit view for individual contact;
 */
public class ContactEdit extends Activity {
	public static final int BIRTHDAY_DATE_PICKER = 0;
	
	private ContactUtils cu;
	private Contact contact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.contact_edit);
		
		cu = new ContactUtils(this);
		loadContactInformation();
		initButtons();
	}
	
	/**
	 * load this contact's information and fill into widgits
	 */
	private void loadContactInformation() {
		Long rawContactId = getIntent().getLongExtra(RawContacts._ID, -1);
		contact = cu.getContactById(rawContactId);
		
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
	
	/**
	 * initiate three buttons. They are save, cancel and the birthday choose button.
	 */
	private void initButtons() {
		Button saveButton = (Button) findViewById(R.id.contact_edit_save_button);
		Button cancelButton = (Button) findViewById(R.id.contact_edit_cancel_button);
		Button birthdayChooserButton = (Button) findViewById(R.id.contact_edit_birthday_button);
		
		saveButton.setOnClickListener(new OnClickListener() {
			//update the information and close this activity
			@Override
			public void onClick(View arg0) {
				cu.updateContact(ContactUtils.contactsIntoList(contact));
				ContactEdit.this.finish();
			}
		});
		//finish this activity
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ContactEdit.this.finish();
			}
		});
		//pop up the date picker dialog
		birthdayChooserButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showDialog(BIRTHDAY_DATE_PICKER);
				
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case BIRTHDAY_DATE_PICKER:
			return createBirthdayDatePickerDialog();
		}
		
		return null;
		
		
	}

	private DatePickerDialog createBirthdayDatePickerDialog() {
		OnDateSetListener callBack = new OnDateSetListener() {
			//set the birthday date picker button and the contact's birthday
			@Override
			public void onDateSet(DatePicker view, int year, int month, int day) {
				Button birthdayChooserButton = (Button) findViewById(R.id.contact_edit_birthday_button);
				Calendar inDate = Calendar.getInstance();
				inDate.set(year, month, day);
				birthdayChooserButton.setText(DateFormat.format("yyyy-MM-dd",
						inDate));
				contact.setBirthday(Long.toString(inDate.getTimeInMillis()).toString());
			}
		};
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Long(contact.getBirthday()));
		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog dialog = new DatePickerDialog(this, callBack, year,
				monthOfYear, dayOfMonth);

		return dialog;
	}

	
	
	
	
}
