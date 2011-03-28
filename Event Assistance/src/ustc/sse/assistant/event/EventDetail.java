package ustc.sse.assistant.event;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ustc.sse.assistant.R;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import ustc.sse.assistant.event.provider.EventAssistant.EventContact;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author 宋安琪
 *
 */
public class EventDetail extends Activity{
	/** Called when the activity is first created. */
	public static final int CONTACT_REQUEST_CODE = 100;
	
	private Button editButton;
	private Button deleteButton;
	
	private TextView contentTextView;
	private TextView beginTimeTextView;
	private TextView endTimeTextView;
	private TextView locationTextView;
	private TextView contactTextView;
	private TextView noteTextView;
	private TextView todayRemindTimeTextView;
	private TextView priorAlarmDayTextView;
	private TextView priorAlarmRepeatTextView;
	private TextView alarmTypeTextView;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);
       
        initiateWidgets();
	}
    
	private void initiateWidgets() {
		editButton = (Button) findViewById(R.id.event_detail_edit_button);
		deleteButton = (Button) findViewById(R.id.event_detail_delete_button);		
		contentTextView = (TextView) findViewById(R.id.event_detail_content);
		beginTimeTextView = (TextView) findViewById(R.id.event_detail_beginTime);
		endTimeTextView = (TextView) findViewById(R.id.event_detail_endTime);
		locationTextView = (TextView) findViewById(R.id.event_detail_location);
		contactTextView = (TextView) findViewById(R.id.event_detail_contact);
		noteTextView = (TextView) findViewById(R.id.event_detail_note);
		todayRemindTimeTextView = (TextView) findViewById(R.id.event_detail_today_remind_time);
		priorAlarmDayTextView = (TextView) findViewById(R.id.event_detail_prior_alarm_day);
		priorAlarmRepeatTextView = (TextView) findViewById(R.id.event_detail_prior_alarm_repeat);
		alarmTypeTextView = (TextView) findViewById(R.id.event_detail_alarm_type);			
    	
		long eventId = getIntent().getLongExtra(Event._ID, -1);
		ContentResolver cr = getContentResolver();
		
		String[] projection = {Event.CONTENT, Event.BEGIN_TIME, Event.END_TIME, Event.LOCATION, Event.NOTE, Event.ALARM_TIME, Event.PRIOR_ALARM_DAY, Event.PRIOR_REPEAT_TIME, Event.ALARM_TYPE};
		String selection = Event._ID + " = ? ";
		String[] selectionArgs = {String.valueOf(eventId)};		
		String[] eventContactProjection = {EventContact.DISPLAY_NAME};	
		Uri eventContact = ContentUris.withAppendedId(EventContact.CONTENT_URI, eventId);
		
		Cursor cursor = cr.query(Event.CONTENT_URI, projection, selection, selectionArgs, null);
		Cursor eventContactCursor = cr.query(eventContact, eventContactProjection, null, null, null);
		startManagingCursor(cursor);
		startManagingCursor(eventContactCursor);
		
		String content = null;
		String beginTime = null;
		String beginTimeText = null;
		String endTime = null;
		String endTimeText = null;
		String location = null;
		StringBuilder contact = new StringBuilder();
		String contactText = null;
		String note = null;
		String todayRemindTime = null;
		String todayRemindTimeText = null;
		String priorAlarmDay = null;
		String priorAlarmRepeat = null;
		String alarmType = null;
		
		int contentColumn;
		int beginTimeColumn;
		int endTimeColumn;
		int locationColumn;
		int noteColumn;
		int todayRemindTimeColumn;
		int priorAlarmDayColumn;
		int priorAlarmRepeatColumn;
		int alarmTypeColumn;

		if(cursor.moveToFirst()){
		    contentColumn = cursor.getColumnIndex(Event.CONTENT);
		    beginTimeColumn = cursor.getColumnIndex(Event.BEGIN_TIME);
		    endTimeColumn = cursor.getColumnIndex(Event.END_TIME);
		    locationColumn = cursor.getColumnIndex(Event.LOCATION);
		    noteColumn = cursor.getColumnIndex(Event.NOTE);
		    todayRemindTimeColumn = cursor.getColumnIndex(Event.ALARM_TIME);
		    priorAlarmDayColumn = cursor.getColumnIndex(Event.PRIOR_ALARM_DAY);
		    priorAlarmRepeatColumn = cursor.getColumnIndex(Event.PRIOR_REPEAT_TIME);
		    alarmTypeColumn = cursor.getColumnIndex(Event.ALARM_TYPE);

			content = cursor.getString(contentColumn);
			beginTime = cursor.getString(beginTimeColumn);
			endTime = cursor.getString(endTimeColumn);
			location = cursor.getString(locationColumn);
			note = cursor.getString(noteColumn);
			todayRemindTime = cursor.getString(todayRemindTimeColumn);
			priorAlarmDay = String.valueOf(cursor.getInt(priorAlarmDayColumn));
			priorAlarmRepeat = String.valueOf(cursor.getInt(priorAlarmRepeatColumn));
			alarmType = cursor.getString(alarmTypeColumn);
		}

		if (eventContactCursor.moveToFirst()) {
			int contactColumn = eventContactCursor
					.getColumnIndex(EventContact.DISPLAY_NAME);
			do {
				contact.append(eventContactCursor.getString(contactColumn)).append(" ");
			} while (eventContactCursor.moveToNext());
		}
		contactText = contact.toString();
		
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		calendar.setTimeInMillis(Long.valueOf(beginTime));
		beginTimeText = sdf.format(calendar.getTime());
		calendar.setTimeInMillis(Long.valueOf(endTime));
		endTimeText = sdf.format(calendar.getTime());
		calendar.setTimeInMillis(Long.valueOf(todayRemindTime));
		todayRemindTimeText = sdf.format(calendar.getTime());

		if(TextUtils.isEmpty(content)){
			content = "无";
		}
		if(TextUtils.isEmpty(location)){
			location = "无";
		}
		if(TextUtils.isEmpty(contactText)){
			contactText = "无";
		}
		if(TextUtils.isEmpty(note)){
			note = "无";
		}
		
		contentTextView.setText(content);
		beginTimeTextView.setText(beginTimeText);
		endTimeTextView.setText(endTimeText);	
		locationTextView.setText(location);
		noteTextView.setText(note);
		contactTextView.setText(contactText);
		todayRemindTimeTextView.setText(todayRemindTimeText);
		priorAlarmDayTextView.setText(priorAlarmDay);
		priorAlarmRepeatTextView.setText(priorAlarmRepeat);
		alarmTypeTextView.setText(alarmType);
	}
	
}
