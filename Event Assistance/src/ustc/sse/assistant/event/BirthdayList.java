package ustc.sse.assistant.event;

import java.util.Calendar;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.provider.ContactsContract.Data;
import ustc.sse.assistant.contact.data.BirthdayConstant;
import ustc.sse.assistant.R;

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
		Log.i("birthdaylist", fromMonth);
		Log.i("birthdaylist", toMonth);
		Log.i("birthdaylist", fromDay);
		Log.i("birthdaylist", toDay);
		
		int monthOfBirthdayColumun;
		int dayOfBirthdayColumun;
		int contacNameColumn;

		String monthOfBirthday;
		String dayOfBirthday;
		String contactName;
		String birthdayText;

		ContentResolver cr = getContentResolver();
		Uri uri = android.provider.ContactsContract.Data.CONTENT_URI;
		String selection = null;
		
		if(!fromDay.equals(toDay)){
			selection = Data.MIMETYPE + " = ? AND " + BirthdayConstant.MONTH + " = ? AND " + BirthdayConstant.DAY + " >= ?";
		}
		else{
			selection = Data.MIMETYPE + " = ? AND " + BirthdayConstant.MONTH + " = ? AND " + BirthdayConstant.DAY + " = ?";
		}
		
		String[] projection = {BirthdayConstant.YEAR, BirthdayConstant.MONTH, BirthdayConstant.DAY, Data.DISPLAY_NAME, Data.CONTACT_ID };

		String[] selectionArgs = {ustc.sse.assistant.contact.data.BirthdayConstant.TYPE, fromMonth, fromDay};
		Cursor cursor = cr.query(uri, projection, selection, selectionArgs, null);
		startManagingCursor(cursor);

		num = cursor.getCount();
		if (num != 0) {
			birthdayListData = new String[num];

			int i = 0;
			if (cursor.moveToFirst()) {
				monthOfBirthdayColumun = cursor
						.getColumnIndex(ustc.sse.assistant.contact.data.BirthdayConstant.MONTH);
				dayOfBirthdayColumun = cursor
						.getColumnIndex(ustc.sse.assistant.contact.data.BirthdayConstant.DAY);
				contacNameColumn = cursor
						.getColumnIndex(android.provider.ContactsContract.Data.DISPLAY_NAME);

				do {
					monthOfBirthday = String.valueOf(cursor.getInt(monthOfBirthdayColumun) + 1);
					dayOfBirthday = cursor.getString(dayOfBirthdayColumun);
					contactName = cursor.getString(contacNameColumn);
					birthdayText = monthOfBirthday + "月" + dayOfBirthday + "日" + contactName + "过生日";
					birthdayListData[i] = birthdayText;
					i++;
				} while (cursor.moveToNext());
			}

			ArrayAdapter<String> birthdayListAdapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_list_item_1, birthdayListData);
			birthdayListView.setAdapter(birthdayListAdapter);
		}
		
		else {
			ArrayAdapter<String> birthdayListAdapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_list_item_1, new String[]{"本月没有人过生日"});
			birthdayListView.setAdapter(birthdayListAdapter);
		}

	}
}
