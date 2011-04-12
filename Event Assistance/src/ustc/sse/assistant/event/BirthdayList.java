package ustc.sse.assistant.event;

import java.util.Calendar;

import ustc.sse.assistant.R;
import ustc.sse.assistant.contact.data.BirthdayConstant;
import ustc.sse.assistant.contact.data.ContactUtils;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.QuickContact;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

/**
 * a birthday list 
 * @author 宋安琪、李健
 *
 */
public class BirthdayList extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.birthday_list);

		ListView birthdayListView = (ListView) findViewById(R.id.birthday_list_listView);
		Calendar fromCalendar = (Calendar) getIntent().getSerializableExtra(
				EventList.FROM_CALENDAR);
		Calendar toCalendar = (Calendar) getIntent().getSerializableExtra(
				EventList.TO_CALENDAR);
		String fromYear = String.valueOf(fromCalendar.get(Calendar.YEAR));
		String fromMonth = String.valueOf(fromCalendar.get(Calendar.MONTH));
		String fromDay = String
				.valueOf(fromCalendar.get(Calendar.DAY_OF_MONTH));
		String toYear = String.valueOf(toCalendar.get(Calendar.YEAR));
		String toMonth = String.valueOf(toCalendar.get(Calendar.MONTH));
		String toDay = String.valueOf(toCalendar.get(Calendar.DAY_OF_MONTH));
		String[] birthdayListData = null;
		int num;

		ContentResolver cr = getContentResolver();
		Uri uri = android.provider.ContactsContract.Data.CONTENT_URI;
		String selection = null;
		
		if(!fromDay.equals(toDay)){
			selection = Data.MIMETYPE + " = ? AND " + BirthdayConstant.MONTH + " = ? AND " + BirthdayConstant.DAY + " >= ?";
		}
		else{
			selection = Data.MIMETYPE + " = ? AND " + BirthdayConstant.MONTH + " = ? AND " + BirthdayConstant.DAY + " = ?";
		}
		
		String[] projection = {BirthdayConstant.YEAR, BirthdayConstant.MONTH, BirthdayConstant.DAY, Data.DISPLAY_NAME, Data.CONTACT_ID, Data._ID };

		String[] selectionArgs = {ustc.sse.assistant.contact.data.BirthdayConstant.TYPE, fromMonth, fromDay};
		Cursor cursor = cr.query(uri, projection, selection, selectionArgs, null);
		startManagingCursor(cursor);

		num = cursor.getCount();
		if (num != 0) {
			birthdayListData = new String[num];

		BirthdayListCursorAdapter adapter = new BirthdayListCursorAdapter(this, 
															R.layout.birthday_list_item, 
															cursor);
			
			birthdayListView.setAdapter(adapter);
			birthdayListView.setOnItemClickListener(itemClickListener);
		}
		
		else {
			ArrayAdapter<String> birthdayListAdapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_list_item_1, new String[]{"没有人过生日"});
			birthdayListView.setAdapter(birthdayListAdapter);
		}

	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view,
				int position, long id) {
			Long contactId = (Long) view.getTag();
			//add a dialog show sms and dial option
			String[] projection = {Contacts.LOOKUP_KEY};
			String selection = Contacts._ID + " = ? ";
			String[] selectionArgs = {contactId.toString()};
			Cursor cursor = getContentResolver().query(Contacts.CONTENT_URI, projection, selection, selectionArgs, null);
			
			String lookup = null;
			if (cursor.moveToFirst()) {
				lookup = cursor.getString(0);
			}
			cursor.close();
			if (lookup != null) {
				Uri lookupUri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, lookup + "/" + contactId.toString());
				QuickContact.showQuickContact(BirthdayList.this, view, lookupUri, QuickContact.MODE_MEDIUM, null);
			}

		}

	};
	
	private static class BirthdayListCursorAdapter extends ResourceCursorAdapter {
		private ContactUtils cu;

		public BirthdayListCursorAdapter(Activity activity, int layoutResId, Cursor c) {
			super(activity, layoutResId, c);
			this.cu = new ContactUtils(activity);
			
		}
		
		/**
		 * this method return the contact's id not the id in Data table
		 */
		@Override
		public long getItemId(int position) {
			return super.getItemId(position);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ImageView birtdayContactPortraitImageView = (ImageView) view.findViewById(R.id.birthday_list_item_contact_portrait);
			TextView birthdayContentTextView = (TextView) view.findViewById(R.id.birthday_list_item_content);
					
			long id = cursor.getLong(cursor.getColumnIndex(Data.CONTACT_ID));
			int monthOfBirthday = cursor.getInt(cursor.getColumnIndex(BirthdayConstant.MONTH));
			int dayOfBirthday = cursor.getInt(cursor.getColumnIndex(BirthdayConstant.DAY));
			String contactName = cursor.getString(cursor.getColumnIndex(Data.DISPLAY_NAME));
			
			byte[] bitmapData = cu.getPhoto(id);
			
			if (bitmapData != null) {
				Bitmap photo = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
				birtdayContactPortraitImageView.setImageBitmap(photo);
			}
			view.setTag(id);
			
			Calendar today = Calendar.getInstance();
			int monthOfToday = today.get(Calendar.MONTH);
			int dayOfToday = today.get(Calendar.DAY_OF_MONTH);
			
			Calendar tomorrow = Calendar.getInstance();
			tomorrow.add(Calendar.DAY_OF_YEAR, 1);
			int monthOfTomorrow = tomorrow.get(Calendar.MONTH);
			int dayOfTomorrow = tomorrow.get(Calendar.DAY_OF_MONTH);
			
			if(monthOfBirthday == monthOfToday && dayOfBirthday == dayOfToday){
				birthdayContentTextView.setText("今天" + contactName + "过生日");
				return;
			}
			
			else if(monthOfBirthday == monthOfTomorrow && dayOfBirthday == dayOfTomorrow){
				birthdayContentTextView.setText("明天" + contactName + "过生日");
				return;
			}
			
			else{
				monthOfBirthday ++;
				birthdayContentTextView.setText(monthOfBirthday + "月" + dayOfBirthday + "日" + contactName + "过生日");
			}
		}
	}
}
