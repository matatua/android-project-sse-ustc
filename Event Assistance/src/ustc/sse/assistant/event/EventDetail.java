package ustc.sse.assistant.event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import ustc.sse.assistant.R;
import ustc.sse.assistant.event.provider.EventAssistant;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import ustc.sse.assistant.event.provider.EventAssistant.EventContact;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author 宋安琪
 *
 */
public class EventDetail extends Activity{
	/** Called when the activity is first created. */
	private static final int DELETE_DIALOG = 100;
	long eventId;
	
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
        getWidgets();
        initiateWidgets();
        initiateButtons();
	}
 
    private void getWidgets() {
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
    }
    
	private void initiateWidgets() {
		eventId = getIntent().getLongExtra(Event._ID, -1);
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
		int todayRemindTime = 0;
		String todayRemindTimeText = null;
		String priorAlarmDay = null;
		String priorAlarmRepeat = null;
		int alarmType = 0;
		String alarmTypeText = null;
		String[] todayRemindTimeStrArray = getApplicationContext().getResources().getStringArray(R.array.entries_list_event_add_today_remind_time);
		String[] alarmTypeStringArray = getApplicationContext().getResources().getStringArray(R.array.entries_list_alarm_type);
		
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
			todayRemindTime = Integer.valueOf(cursor.getString(todayRemindTimeColumn));
			priorAlarmDay = String.valueOf(cursor.getInt(priorAlarmDayColumn));
			priorAlarmRepeat = String.valueOf(cursor.getInt(priorAlarmRepeatColumn));
			alarmType = Integer.valueOf(cursor.getString(alarmTypeColumn));
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
		
		todayRemindTimeText = todayRemindTimeStrArray[todayRemindTime];
	    alarmTypeText = alarmTypeStringArray[alarmType];
					
		contentTextView.setText(content);
		beginTimeTextView.setText(beginTimeText);
		endTimeTextView.setText(endTimeText);	
		locationTextView.setText(location);
		noteTextView.setText(note);
		contactTextView.setText(contactText);
		todayRemindTimeTextView.setText(todayRemindTimeText);
		priorAlarmDayTextView.setText(priorAlarmDay);
		priorAlarmRepeatTextView.setText(priorAlarmRepeat);
		alarmTypeTextView.setText(alarmTypeText);
	}
	
	private void initiateButtons() {
		editButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(EventDetail.this, EventEdit.class);
				i.putExtra(Event._ID, eventId);
				startActivity(i);
				finish();
			}
		});
		
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(DELETE_DIALOG);
				//Toast.makeText(EventDetail.this, "delete", Toast.LENGTH_SHORT).show();	
			}
		});	
	}
	
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DELETE_DIALOG :
			
			 DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE :
							deleteEvent();
							dialog.cancel();
							break;
						case DialogInterface.BUTTON_NEGATIVE :
							dialog.cancel();
							break;
						}
						
					}
				
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.event_detail_delete_dialog_title);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setMessage(R.string.event_detail_delete_dialog_message);
			builder.setPositiveButton(R.string.event_detail_delete_dialog_affirm, dialogListener);	
			builder.setNegativeButton(R.string.event_detail_delete_dialog_cancel, dialogListener);		
			return builder.create();
		}		
		return null;		
	}
	
	private void deleteEvent() {
		ContentResolver cr = getContentResolver();
		ArrayList<ContentProviderOperation> eventOps = new ArrayList<ContentProviderOperation>();
		ArrayList<ContentProviderOperation> eventContactOps = new ArrayList<ContentProviderOperation>();

		eventOps.add(ContentProviderOperation.newDelete(Event.CONTENT_URI).withSelection(Event._ID + " = ?",
						new String[] { String.valueOf(eventId) }).build());

		Uri eventContactUri = ContentUris.withAppendedId(EventContact.CONTENT_URI, eventId);
		eventContactOps.add(ContentProviderOperation.newDelete(eventContactUri).build());

		try {
			ProgressDialog dialog = ProgressDialog.show(this, null, "删除中...",true, false);
			cr.applyBatch(EventAssistant.EVENT_AUTHORITY, eventOps);
			cr.applyBatch(EventAssistant.EVENT_CONTACT_AUTHORITY,eventContactOps);
			dialog.cancel();

		} catch (RemoteException e) {
			e.printStackTrace();
			Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
			Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
		} finally {
			this.finish();
		}
		
	}
}
