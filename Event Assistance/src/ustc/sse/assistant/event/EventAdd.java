package ustc.sse.assistant.event;

import ustc.sse.assistant.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * 
 * @author 李健
 *
 */
public class EventAdd extends Activity{
	/** Called when the activity is first created. */
	private Button cancelButton;
	private Button saveButton;
	private Button beginTimeButton;
	private Button endTimeButton;
	
	private Spinner prioriAlarmDaySpinner;
	private Spinner prioriAlarmRepeatSpinner;
	private Spinner alarmTypeSpinner;
	
	private EditText contentEditText;
	private EditText locationEditText;
	private EditText contactEditText;
	private EditText noteEditText;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_add);
        
        initiateWidgets();
        
    }

	private void initiateWidgets() {
		cancelButton = (Button) findViewById(R.id.event_add_cancel_button);
		saveButton = (Button) findViewById(R.id.event_add_save_button);
		beginTimeButton = (Button) findViewById(R.id.event_add_beginTime_button);
		endTimeButton = (Button) findViewById(R.id.event_add_endTime_button);
		
		prioriAlarmDaySpinner = (Spinner) findViewById(R.id.event_add_priori_alarm_day_spinner);
		prioriAlarmRepeatSpinner = (Spinner) findViewById(R.id.event_add_priori_alarm_repeat_spinner);
		alarmTypeSpinner = (Spinner) findViewById(R.id.event_add_alarm_type_spinner);
		
		contentEditText = (EditText) findViewById(R.id.event_add_content_editText);
		locationEditText = (EditText) findViewById(R.id.event_add_location_editText);
		contactEditText = (EditText) findViewById(R.id.event_add_contact_editText);
		noteEditText = (EditText) findViewById(R.id.event_add_note_editText);
	}

}
