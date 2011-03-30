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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
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
 * @author 李健
 *
 */
public class EventAdd extends Activity{
	/** Called when the activity is first created. */
	public static final int CONTACT_REQUEST_CODE = 100;
	
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
        setContentView(R.layout.event_add);
        
        initiateWidgets();
        
    }

	private void initiateWidgets() {
		contactImageButton = (ImageView) findViewById(R.id.event_add_contact_add_imageView);
		cancelButton = (Button) findViewById(R.id.event_add_cancel_button);
		saveButton = (Button) findViewById(R.id.event_add_save_button);
		beginDateButton = (Button) findViewById(R.id.event_add_beginTime_date_button);
		endDateButton = (Button) findViewById(R.id.event_add_endTime_date_button);
		beginTimeButton = (Button) findViewById(R.id.event_add_beginTime_time_button);
		endTimeButton = (Button) findViewById(R.id.event_add_endTime_time_button);
		
		prioriAlarmDaySpinner = (Spinner) findViewById(R.id.event_add_priori_alarm_day_spinner);
		prioriAlarmRepeatSpinner = (Spinner) findViewById(R.id.event_add_priori_alarm_repeat_spinner);
		alarmTypeSpinner = (Spinner) findViewById(R.id.event_add_alarm_type_spinner);
		todayRemindTimeSpinner = (Spinner) findViewById(R.id.event_add_today_remind_time_spinner);
		
		contentEditText = (EditText) findViewById(R.id.event_add_content_editText);
		locationEditText = (EditText) findViewById(R.id.event_add_location_editText);
		contactEditText = (EditText) findViewById(R.id.event_add_contact_editText);
		noteEditText = (EditText) findViewById(R.id.event_add_note_editText);
		
		initiateContactImageView();
		initiateButtons();
		
		//initiate spinners
		initiateSpinner();
		//load default preference and set the corresponding spinner
		initiatePreference();
		
	}

	private void initiatePreference() {
		//TODO load preference and initiate the related value.
	}

	private void initiateContactImageView() {
		contactImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EventAdd.this, ContactSelection.class);
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
		for (int i = 0; i < todayRemindTimeStrArray.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", todayRemindTimeStrArray[i]);
			map.put("value", todayRemindTimeIntArray[i]);
			todayRemindTimeDate.add(map);
			
		}
		
		SimpleAdapter adapter = new EventAddSimpleAdapter(this, 
													todayRemindTimeDate, 
													android.R.layout.simple_spinner_dropdown_item, 
													new String[]{"name"}, 
													new int[] {android.R.id.text1});
		todayRemindTimeSpinner.setAdapter(adapter);
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
		for (int i = 0; i < alarmTypeStringArray.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", alarmTypeStringArray[i]);
			map.put("value", alarmTypeIntArray[i]);
			alarmTypeData.add(map);
		}
		SpinnerAdapter alarmTypeAdapter = new SimpleAdapter(this, 
				alarmTypeData, 
				R.layout.event_add_spinner_item,
				new String[]{"name", "value"}, 
				new int[]{R.id.event_add_spinner_textview1, R.id.event_add_spinner_textview2});

		alarmTypeSpinner.setAdapter(alarmTypeAdapter);
		alarmTypeSpinner.setPromptId(R.string.event_alarm_type);
		alarmTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv = (TextView) view.findViewById(R.id.event_add_spinner_textview2);
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
		for (int i = 0; i < prioriRepeatStringArray.length; i++) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("name", prioriRepeatIntArray[i]);
			map.put("value", prioriRepeatIntArray[i]);
			prioriAlarmRepeatData.add(map);
		}
		SpinnerAdapter prioriAlarmRepeatAdapter = new SimpleAdapter(this, 
				prioriAlarmRepeatData, 
				R.layout.event_add_spinner_item,
				new String[]{"name", "value"}, 
				new int[]{R.id.event_add_spinner_textview1, R.id.event_add_spinner_textview2});
		prioriAlarmRepeatSpinner.setAdapter(prioriAlarmRepeatAdapter);
		prioriAlarmRepeatSpinner.setPromptId(R.string.event_repeat);
		prioriAlarmRepeatSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv = (TextView) view.findViewById(R.id.event_add_spinner_textview2);
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
		for (int i = 0; i < prioriDayStringArray.length; i++) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("name", prioriDayIntArray[i]);
			map.put("value", prioriDayIntArray[i]);
			prioriAlarmDayData.add(map);
		}
		SpinnerAdapter prioriAlarmDayAdapter = new SimpleAdapter(this, 
														prioriAlarmDayData, 
														R.layout.event_add_spinner_item,
														new String[]{"name", "value"}, 
														new int[]{R.id.event_add_spinner_textview1, R.id.event_add_spinner_textview2});
		prioriAlarmDaySpinner.setAdapter(prioriAlarmDayAdapter);
		prioriAlarmDaySpinner.setPromptId(R.string.event_prior_alarm_day);
		prioriAlarmDaySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv = (TextView) view.findViewById(R.id.event_add_spinner_textview2);
				priorAlarmDay = Integer.valueOf(tv.getText().toString());
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void initiateButtons() {
		//initiate buttons
		Calendar calendar = Calendar.getInstance();
		CharSequence currentDate = DateFormat.format(DATE_FORMAT, calendar);
		beginDateButton.setText(currentDate);
		endDateButton.setText(currentDate);
		CharSequence currentTime = DateFormat.format(TIME_FORMAT, calendar);
		beginTimeButton.setText(currentTime);
		endTimeButton.setText(currentTime);
		
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
			EventAdd.this.finish();
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
			EventAdd.this.finish();
		}

		private void startPriorAlarmService() {
			if (priorAlarmDay == EventConstant.EVENT_PRIOR_DAY_NONE) {
				return ;
			}
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			long priorRemindTimeInMillisecond = EventUtils.dayToTimeInMillisecond(priorAlarmDay);
			long triggerAtTime = beginCalendar.getTimeInMillis() - priorRemindTimeInMillisecond;
			
			Intent intent = new Intent(EventAdd.this, EventBroadcastReceiver.class);
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
			PendingIntent operation = PendingIntent.getBroadcast(EventAdd.this, 0, intent, 0);
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
			
			Intent intent = new Intent(EventAdd.this, EventBroadcastReceiver.class);
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
			PendingIntent operation = PendingIntent.getBroadcast(EventAdd.this, 0, intent, 0);
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

	private static class EventAddSimpleAdapter extends SimpleAdapter {

		public EventAddSimpleAdapter(Context context,
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
