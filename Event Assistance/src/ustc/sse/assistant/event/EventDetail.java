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
 * @author 宋安琪
 *
 */
public class EventDetail extends Activity{
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
        setContentView(R.layout.event_detail);
       
	}
}
