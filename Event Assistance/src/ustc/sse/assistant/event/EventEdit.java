package ustc.sse.assistant.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ustc.sse.assistant.R;
import ustc.sse.assistant.contact.ContactSelection;
import ustc.sse.assistant.event.broadcast.EventBroadcastReceiver;
import ustc.sse.assistant.event.provider.EventAssistant;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import ustc.sse.assistant.event.provider.EventAssistant.EventContact;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * 
 * @author 宋安琪、李健
 *
 */
public class EventEdit extends Activity{
	/** Called when the activity is first created. */
	public static final int CONTACT_REQUEST_CODE = 100;
	private long eventId;
	
	private Button cancelButton;
	private Button saveButton;
	private Button beginDateButton;
	private Button endDateButton;
	private Button beginTimeButton;
	private Button endTimeButton;
	private ImageView contactImageButton;
	
	private Spinner prioriAlarmDaySpinner;
	private Spinner prioriAlarmRepeatSpinner;
	private Spinner alarmTypeSpinner;
	private Spinner todayRemindTimeSpinner;
	
	private EditText contentEditText;
	private EditText locationEditText;
	private EditText contactEditText;
	private EditText noteEditText;
	
	private Calendar beginCalendar = Calendar.getInstance();
	private Calendar endCalendar = Calendar.getInstance();
	
	private int priorAlarmDay = EventConstant.EVENT_PRIOR_DAY_NONE;
	private int priorAlarmRepeat = EventConstant.EVENT_PRIOR_REPEAT_ONE;
	private int alarmType = 0;
	private String location = "";
	private String note = "";
	private String content = "";
	private int alarmTime = 0;
	private Map<Long, String> contactData = new HashMap<Long,String>();
	
	public static final String DATE_FORMAT = "yyyy年MM月dd日 EE";
	public static final String TIME_FORMAT = "h:mmaa";
	
	public static final int BEGIN_DATE_DIALOG = 0;
	public static final int BEGIN_TIME_DIALOG = 1;
	public static final int END_DATE_DIALOG = 2;
	public static final int END_TIME_DIALOG = 3;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_edit);
        
        initiateWidgets();
        
    }

	private void initiateWidgets() {
		contactImageButton = (ImageView) findViewById(R.id.event_edit_contact_add_imageView);
		cancelButton = (Button) findViewById(R.id.event_edit_cancel_button);
		saveButton = (Button) findViewById(R.id.event_edit_save_button);
		beginDateButton = (Button) findViewById(R.id.event_edit_beginTime_date_button);
		endDateButton = (Button) findViewById(R.id.event_edit_endTime_date_button);
		beginTimeButton = (Button) findViewById(R.id.event_edit_beginTime_time_button);
		endTimeButton = (Button) findViewById(R.id.event_edit_endTime_time_button);
		
		prioriAlarmDaySpinner = (Spinner) findViewById(R.id.event_edit_priori_alarm_day_spinner);
		prioriAlarmRepeatSpinner = (Spinner) findViewById(R.id.event_edit_priori_alarm_repeat_spinner);
		alarmTypeSpinner = (Spinner) findViewById(R.id.event_edit_alarm_type_spinner);
		todayRemindTimeSpinner = (Spinner) findViewById(R.id.event_edit_today_remind_time_spinner);
		
		contentEditText = (EditText) findViewById(R.id.event_edit_content_editText);
		locationEditText = (EditText) findViewById(R.id.event_edit_location_editText);
		contactEditText = (EditText) findViewById(R.id.event_edit_contact_editText);
		noteEditText = (EditText) findViewById(R.id.event_edit_note_editText);
		
		initiateDate();
		initiateContactImageView();
		initiateButtons();
		
		//initiate spinners
		initiateSpinner();
		//load default preference and set the corresponding spinner
		initiatePreference();		
	}


	public void initiateDate(){
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
		
		String beginTime = null;
		String endTime = null;
		StringBuilder contactBuilder = new StringBuilder();
		String contact = null;
		
		int contentColumn;
		int beginTimeColumn;
		int endTimeColumn;
		int locationColumn;
		int noteColumn;
		int alarmTimeColumn;
		int priorAlarmDayColumn;
		int priorAlarmRepeatColumn;
		int alarmTypeColumn;

		if(cursor.moveToFirst()){
		    contentColumn = cursor.getColumnIndex(Event.CONTENT);
		    beginTimeColumn = cursor.getColumnIndex(Event.BEGIN_TIME);
		    endTimeColumn = cursor.getColumnIndex(Event.END_TIME);
		    locationColumn = cursor.getColumnIndex(Event.LOCATION);
		    noteColumn = cursor.getColumnIndex(Event.NOTE);
		    alarmTimeColumn = cursor.getColumnIndex(Event.ALARM_TIME);
		    priorAlarmDayColumn = cursor.getColumnIndex(Event.PRIOR_ALARM_DAY);
		    priorAlarmRepeatColumn = cursor.getColumnIndex(Event.PRIOR_REPEAT_TIME);
		    alarmTypeColumn = cursor.getColumnIndex(Event.ALARM_TYPE);

			content = cursor.getString(contentColumn);
			beginTime = cursor.getString(beginTimeColumn);
			endTime = cursor.getString(endTimeColumn);
			location = cursor.getString(locationColumn);
			note = cursor.getString(noteColumn);
			alarmTime = Integer.valueOf(cursor.getString(alarmTimeColumn));
			priorAlarmDay = cursor.getInt(priorAlarmDayColumn);
			priorAlarmRepeat = cursor.getInt(priorAlarmRepeatColumn);
			alarmType = Integer.valueOf(cursor.getString(alarmTypeColumn));
		}

		if (eventContactCursor.moveToFirst()) {
			int contactColumn = eventContactCursor
					.getColumnIndex(EventContact.DISPLAY_NAME);
			do {
				contactBuilder.append(eventContactCursor.getString(contactColumn)).append(" ");
			} while (eventContactCursor.moveToNext());
		}
		contact = contactBuilder.toString();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Long.valueOf(beginTime));
		beginDateButton.setText(DateFormat.format(DATE_FORMAT, calendar));
		beginTimeButton.setText(DateFormat.format(TIME_FORMAT, calendar));
		calendar.setTimeInMillis(Long.valueOf(endTime));
		endDateButton.setText(DateFormat.format(DATE_FORMAT, calendar));
		endTimeButton.setText(DateFormat.format(TIME_FORMAT, calendar));

		if(!TextUtils.isEmpty(content)){
			contentEditText.setText(content);
		}
		if(!TextUtils.isEmpty(location)){
			locationEditText.setText(location);
		}
		if(!TextUtils.isEmpty(contact)){
			contactEditText.setText(contact);
		}
		if(!TextUtils.isEmpty(note)){
			noteEditText.setText(note);
		}
		
	}
	
	private void initiatePreference() {
		//TODO load preference and initiate the related value.
	}

	private void initiateContactImageView() {
		contactImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EventEdit.this, ContactSelection.class);
				startActivityForResult(intent, CONTACT_REQUEST_CODE);
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == CONTACT_REQUEST_CODE && data != null) {
			StringBuilder sb = new StringBuilder();
			long[] contactIds = data.getLongArrayExtra(ContactSelection.SELECTED_CONTACT_IDS);
			String[] names = data.getStringArrayExtra(ContactSelection.SELECTED_CONTACT_DISPLAY_NAME);
		
			if (contactIds != null && names != null) {
				for (int i = 0; i < contactIds.length; i++) {
					contactData.put(contactIds[i], names[i]);
					sb.append(names[i] + " ");							
				}	
				contactEditText.setText(sb.toString());
			}
		}
	}

	private void initiateSpinner() {
		//prior day spinner
		preparePriorDaySpinner();
		//--------------------------------------------------------------------------
		//prior repeat spinner
		preparePriorRepeatSpinner();
		//---------------------------------------------------------------------------------
		//alarm type spinner
		prepareAlarmTypeSpinner();
		//--------------------------------------------------------------------------------
		//Today remind time spinner
		prepareTodayRemindTime();
		
	}

	private void prepareTodayRemindTime() {
		String[] todayRemindTimeStrArray = getApplicationContext().getResources().getStringArray(R.array.entries_list_event_add_today_remind_time);
		int[] todayRemindTimeIntArray = getApplicationContext().getResources().getIntArray(R.array.entriesvalue_list_event_today_remind_time);
		List<Map<String, Object>> todayRemindTimeDate = new ArrayList<Map<String,Object>>();
		int selectedIndex;
		
		for (int i = 0; i < todayRemindTimeStrArray.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", todayRemindTimeStrArray[i]);
			map.put("value", todayRemindTimeIntArray[i]);
			todayRemindTimeDate.add(map);			
		}
		
		for (selectedIndex = 0; selectedIndex < todayRemindTimeIntArray.length; selectedIndex++){
			if(alarmTime == todayRemindTimeIntArray[selectedIndex]){
				break;
			}
		}
		
		SimpleAdapter adapter = new EventEditSimpleAdapter(this, 
													todayRemindTimeDate, 
													android.R.layout.simple_spinner_dropdown_item, 
													new String[]{"name"}, 
													new int[] {android.R.id.text1});
		todayRemindTimeSpinner.setAdapter(adapter);
		todayRemindTimeSpinner.setSelection(selectedIndex, true);
		todayRemindTimeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				alarmTime = (Integer) view.getTag();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}

	private void prepareAlarmTypeSpinner() {
		String[] alarmTypeStringArray = getApplicationContext().getResources().getStringArray(R.array.entries_list_alarm_type);
		int[] alarmTypeIntArray = getApplicationContext().getResources().getIntArray(R.array.entriesvalue_list_alarm_type);
		List<Map<String, Object>> alarmTypeData = new ArrayList<Map<String,Object>>();
		int selectedIndex;
		
		for (int i = 0; i < alarmTypeStringArray.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", alarmTypeStringArray[i]);
			map.put("value", alarmTypeIntArray[i]);
			alarmTypeData.add(map);
		}
		
		for (selectedIndex = 0; selectedIndex < alarmTypeIntArray.length; selectedIndex++){
			if(alarmType == alarmTypeIntArray[selectedIndex]){
				break;
			}
		}
		
		SpinnerAdapter alarmTypeAdapter = new SimpleAdapter(this, 
				alarmTypeData, 
				R.layout.event_edit_spinner_item,
				new String[]{"name", "value"}, 
				new int[]{R.id.event_edit_spinner_textview1, R.id.event_edit_spinner_textview2});

		alarmTypeSpinner.setAdapter(alarmTypeAdapter);
		alarmTypeSpinner.setPromptId(R.string.event_alarm_type);
		alarmTypeSpinner.setSelection(selectedIndex, true);
		alarmTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv = (TextView) view.findViewById(R.id.event_edit_spinner_textview2);
				alarmType = Integer.valueOf(tv.getText().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void preparePriorRepeatSpinner() {
		String[] prioriRepeatStringArray = getApplicationContext().getResources().getStringArray(R.array.entries_list_priori_repeat);
		int[] prioriRepeatIntArray = getApplicationContext().getResources().getIntArray(R.array.entriesvalue_list_priori_repeat);
		List<Map<String, Integer>> prioriAlarmRepeatData = new ArrayList<Map<String,Integer>>();
		int selectedIndex;
		
		for (int i = 0; i < prioriRepeatStringArray.length; i++) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("name", prioriRepeatIntArray[i]);
			map.put("value", prioriRepeatIntArray[i]);
			prioriAlarmRepeatData.add(map);
		}
		
		for (selectedIndex = 0; selectedIndex < prioriRepeatIntArray.length; selectedIndex++){
			if(priorAlarmRepeat == prioriRepeatIntArray[selectedIndex]){
				break;
			}
		}
		
		SpinnerAdapter prioriAlarmRepeatAdapter = new SimpleAdapter(this, 
				prioriAlarmRepeatData, 
				R.layout.event_edit_spinner_item,
				new String[]{"name", "value"}, 
				new int[]{R.id.event_edit_spinner_textview1, R.id.event_edit_spinner_textview2});
		prioriAlarmRepeatSpinner.setAdapter(prioriAlarmRepeatAdapter);
		prioriAlarmRepeatSpinner.setPromptId(R.string.event_repeat);
		prioriAlarmRepeatSpinner.setSelection(selectedIndex, true);
		prioriAlarmRepeatSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv = (TextView) view.findViewById(R.id.event_edit_spinner_textview2);
				priorAlarmRepeat = Integer.valueOf(tv.getText().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		
		});
	}

	private void preparePriorDaySpinner() {
		String[] prioriDayStringArray = getApplicationContext().getResources().getStringArray(R.array.entries_list_priori_day);
		int[] prioriDayIntArray = getApplicationContext().getResources().getIntArray(R.array.entriesvalue_list_priori_day);		
		List<Map<String, Integer>> prioriAlarmDayData = new ArrayList<Map<String,Integer>>();
		int selectedIndex;
		
		for (int i = 0; i < prioriDayStringArray.length; i++) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("name", prioriDayIntArray[i]);
			map.put("value", prioriDayIntArray[i]);
			prioriAlarmDayData.add(map);
		}
		
		for (selectedIndex = 0; selectedIndex < prioriDayIntArray.length; selectedIndex++){
			if(priorAlarmDay == prioriDayIntArray[selectedIndex]){
				break;
			}
		}
		
		SpinnerAdapter prioriAlarmDayAdapter = new SimpleAdapter(this, 
														prioriAlarmDayData, 
														R.layout.event_edit_spinner_item,
														new String[]{"name", "value"}, 
														new int[]{R.id.event_edit_spinner_textview1, R.id.event_edit_spinner_textview2});
		prioriAlarmDaySpinner.setAdapter(prioriAlarmDayAdapter);
		prioriAlarmDaySpinner.setPromptId(R.string.event_prior_alarm_day);
		prioriAlarmDaySpinner.setSelection(selectedIndex, true);
		prioriAlarmDaySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv = (TextView) view.findViewById(R.id.event_edit_spinner_textview2);
				priorAlarmDay = Integer.valueOf(tv.getText().toString());
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void initiateButtons() {
		//initiate buttons		
		beginDateButton.setOnClickListener(beginDateListener);
		beginTimeButton.setOnClickListener(beginTimeListener);
		endDateButton.setOnClickListener(endDateListener);
		endTimeButton.setOnClickListener(endTimeListener);
		
		cancelButton.setOnClickListener(cancelButtonListener);
		saveButton.setOnClickListener(saveButtonListener);
	}
	
	private OnClickListener cancelButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			EventEdit.this.finish();
		}
	};
	private OnClickListener saveButtonListener = new OnClickListener() {
		//this calendar is used to set createTime and lastModifyTime
		Calendar now = Calendar.getInstance();
		@Override
		public void onClick(View v) {
			//save event and corresponding contacts
			saveEventAndContact();				
			//start alarm service here
			startTodayAlarmService();
			startPriorAlarmService();
			EventEdit.this.finish();
		}

		private void startPriorAlarmService() {
			if (priorAlarmDay == EventConstant.EVENT_PRIOR_DAY_NONE) {
				return ;
			}
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			long priorRemindTimeInMillisecond = EventUtils.dayToTimeInMillisecond(priorAlarmDay);
			long triggerAtTime = beginCalendar.getTimeInMillis() - priorRemindTimeInMillisecond;
			
			Intent intent = new Intent(EventEdit.this, EventBroadcastReceiver.class);
			//this action is set only for distinguish, here action is event plus triggerAtTime
			intent.setAction(Event.TAG + String.valueOf(triggerAtTime));
			intent.putExtra(Event.CONTENT, content);
			intent.putExtra(Event.ALARM_TIME, alarmTime);
			intent.putExtra(Event.ALARM_TYPE, alarmType);
			intent.putExtra(Event.BEGIN_TIME, beginCalendar.getTimeInMillis());
			intent.putExtra(Event.END_TIME, endCalendar.getTimeInMillis());
			intent.putExtra(Event.LOCATION, location);
			intent.putExtra(Event.NOTE, note);
			intent.putExtra(Event.PRIOR_ALARM_DAY, priorAlarmDay);
			intent.putExtra(Event.PRIOR_REPEAT_TIME, priorAlarmRepeat);
			PendingIntent operation = PendingIntent.getBroadcast(EventEdit.this, 0, intent, 0);
			am.setInexactRepeating(AlarmManager.RTC_WAKEUP, 
								triggerAtTime, 
								EventUtils.priorRepeatToInterval(priorAlarmRepeat), 
								operation);
		}

		private void saveEventAndContact() {
			//save all the data and store into database
			//if necessary, start the alarm services
			ContentResolver cr = getContentResolver();
			//store event
			content = contentEditText.getText().toString();
			location = locationEditText.getText().toString();
			note = noteEditText.getText().toString();
			
			ContentValues contentValues = EventUtils.eventToContentValues(
																	content,
																	String.valueOf(alarmTime), 
																	String.valueOf(alarmType), 
																	String.valueOf(beginCalendar.getTimeInMillis()), 
																	String.valueOf(endCalendar.getTimeInMillis()), 
																	String.valueOf(now.getTimeInMillis()), 
																	String.valueOf(now.getTimeInMillis()), 
																	location, 
																	note, 
																	priorAlarmDay, 
																	priorAlarmRepeat);
			Uri newUri = cr.insert(Event.CONTENT_URI, contentValues);
			//store eventcontact
			Long eventId = Long.valueOf(newUri.getPathSegments().get(1));
			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
			Iterator<Entry<Long, String>> iter = contactData.entrySet().iterator();
			
			while (iter.hasNext()) {
				Entry<Long, String> entry = iter.next();
				ContentProviderOperation op = ContentProviderOperation
														.newInsert(EventContact.CONTENT_URI)
														.withValue(EventContact.EVENT_ID, eventId)
														.withValue(EventContact.CONTACT_ID, entry.getKey())
														.withValue(EventContact.DISPLAY_NAME, entry.getValue())
														.build();
				ops.add(op);
			}
						
			try {
				cr.applyBatch(EventAssistant.EVENT_CONTACT_AUTHORITY, ops);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				e.printStackTrace();
			}
		}

		private void startTodayAlarmService() {
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			long remindTimeInMillisecond = EventUtils.toTimeInMillisecond(alarmTime);
			long triggerAtTime = beginCalendar.getTimeInMillis() - remindTimeInMillisecond;
			
			Intent priorIntent = new Intent(EventEdit.this, EventBroadcastReceiver.class);
			priorIntent.setAction(Event.TAG + String.valueOf(now.getTimeInMillis()));
			PendingIntent operaIntent = PendingIntent.getBroadcast(EventEdit.this, 0, priorIntent, 0);
			am.cancel(operaIntent);
			
			Intent intent = new Intent(EventEdit.this, EventBroadcastReceiver.class);
			intent.setAction(Event.TAG + String.valueOf(now.getTimeInMillis()));
			intent.putExtra(Event.CONTENT, content);
			intent.putExtra(Event.ALARM_TIME, alarmTime);
			intent.putExtra(Event.ALARM_TYPE, alarmType);
			intent.putExtra(Event.BEGIN_TIME, beginCalendar.getTimeInMillis());
			intent.putExtra(Event.END_TIME, endCalendar.getTimeInMillis());
			intent.putExtra(Event.LOCATION, location);
			intent.putExtra(Event.NOTE, note);
			intent.putExtra(Event.PRIOR_ALARM_DAY, priorAlarmDay);
			intent.putExtra(Event.PRIOR_REPEAT_TIME, priorAlarmRepeat);
			PendingIntent operation = PendingIntent.getBroadcast(EventEdit.this, 0, intent, 0);
			am.set(AlarmManager.RTC_WAKEUP, triggerAtTime, operation);		
		}
	};
	
	private OnClickListener beginDateListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(BEGIN_DATE_DIALOG);
		}
	};
	private OnClickListener beginTimeListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(BEGIN_TIME_DIALOG);
		}
	};
	private OnClickListener endDateListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(END_DATE_DIALOG);			
		}
	};
	private OnClickListener endTimeListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(END_TIME_DIALOG);
		}
	};

	private DatePickerDialog datePickerDialog;

	private TimePickerDialog timePickerDialog;

	private DatePickerDialog datePickerDialog2;

	private TimePickerDialog timePickerDialog2;
	
	protected Dialog onCreateDialog(int id) {
		Calendar today = Calendar.getInstance();
		OnDateSetListener beginDateCallBack = new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar compareCalendar = Calendar.getInstance();
				compareCalendar.setTimeInMillis(endCalendar.getTimeInMillis());
				compareCalendar.set(year, monthOfYear, dayOfMonth);
				
				if (compareCalendar.after(endCalendar)) {
					endCalendar.set(year, monthOfYear, dayOfMonth);
					endDateButton.setText(DateFormat.format(DATE_FORMAT, endCalendar));
					datePickerDialog.updateDate(year, monthOfYear, dayOfMonth);
				}
				beginCalendar.set(year, monthOfYear, dayOfMonth);
				beginDateButton.setText(DateFormat.format(DATE_FORMAT, beginCalendar));
				
			}
		};
		
		OnTimeSetListener beginTimeCallBack = new OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				Calendar compareCalendar = Calendar.getInstance();
				compareCalendar.setTimeInMillis(endCalendar.getTimeInMillis());
				compareCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				compareCalendar.set(Calendar.MINUTE, minute);
				if (compareCalendar.after(endCalendar)) {
					endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
					endCalendar.set(Calendar.MINUTE, minute);
					endTimeButton.setText(DateFormat.format(TIME_FORMAT, endCalendar));
					timePickerDialog.updateTime(hourOfDay, minute);
				}
						
				beginCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				beginCalendar.set(Calendar.MINUTE, minute);
				beginTimeButton.setText(DateFormat.format(TIME_FORMAT, beginCalendar));
			}
		};
		
		OnDateSetListener endDateCallBack = new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, monthOfYear, dayOfMonth);
				calendar.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY));
				calendar.set(Calendar.MINUTE, endCalendar.get(Calendar.MINUTE));
				if (calendar.after(beginCalendar)) {
					endCalendar.set(year, monthOfYear, dayOfMonth);
					endDateButton.setText(DateFormat.format(DATE_FORMAT, endCalendar));				
				}							
			}
		};
		
		OnTimeSetListener endTimeCallback = new OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(endCalendar.getTimeInMillis());
				calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				calendar.set(Calendar.MINUTE, minute);
				
				if (calendar.after(beginCalendar)) {
					endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
					endCalendar.set(Calendar.MINUTE, minute);
					endTimeButton.setText(DateFormat.format(TIME_FORMAT, endCalendar));

				}							
			}
		};
		switch (id) {
		case BEGIN_DATE_DIALOG :
			datePickerDialog = new DatePickerDialog(this,
														beginDateCallBack, 
														today.get(Calendar.YEAR), 
														today.get(Calendar.MONTH), 
														today.get(Calendar.DAY_OF_MONTH));
			return datePickerDialog;
		case BEGIN_TIME_DIALOG :
			timePickerDialog = new TimePickerDialog(this, 
															beginTimeCallBack, 
															today.get(Calendar.HOUR_OF_DAY), 
															today.get(Calendar.MINUTE), 
															true);
			return timePickerDialog;
		case END_DATE_DIALOG :
			datePickerDialog2 = new DatePickerDialog(this, 
															endDateCallBack, 
															today.get(Calendar.YEAR), 
															today.get(Calendar.MONTH), 
															today.get(Calendar.DAY_OF_MONTH));
			return datePickerDialog2;
		case END_TIME_DIALOG :
			timePickerDialog2 = new TimePickerDialog(this, 
															endTimeCallback, 
															today.get(Calendar.HOUR_OF_DAY), 
															today.get(Calendar.MINUTE), 
															true);
			return timePickerDialog2;
			
		}
		
		return null;
	}

	private static class EventEditSimpleAdapter extends SimpleAdapter {

		public EventEditSimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);
			Map<String, String> map =  (Map<String, String>) getItem(position);
			v.setTag(map.get("value"));
			
			return v;
		}		
	}
	
}
