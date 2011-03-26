/**
 * 
 */
package ustc.sse.assistant.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ustc.sse.assistant.R;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * a event list 
 * @author 李健
 *
 */
public class EventList extends Activity {
	
	public static final String FROM_CALENDAR = "begin_calendar";
	public static final String TO_CALENDAR = "end_calendar";
	
	public static final String DATE_FORMAT = "yyyy年MM月dd日";
	
	private ListView listView;
	private Button selectButton;
	private Button deleteButton;
	private LinearLayout buttonBar;
	private Calendar todayCalendar = Calendar.getInstance();
	private Calendar fromCalendar;
	private Calendar toCalendar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list);
		
		initiateWidgets();
		initiateEvents();
		initiateBirthdayInfo();
	}


	private void initiateWidgets() {
		listView = (ListView) findViewById(R.id.event_list_listView);
		selectButton = (Button) findViewById(R.id.event_list_select_button);
		deleteButton = (Button) findViewById(R.id.event_list_delete_button);
		buttonBar = (LinearLayout) findViewById(R.id.event_list_button_bar);
	}
	
	private OnItemClickListener listViewItemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent i = new Intent(EventList.this, EventDetail.class);
			Toast.makeText(EventList.this, String.valueOf(id), Toast.LENGTH_SHORT).show();
			
		}
	};
	
	private void initiateEvents() {
		fromCalendar = (Calendar) getIntent().getSerializableExtra(FROM_CALENDAR);
		toCalendar = (Calendar) getIntent().getSerializableExtra(TO_CALENDAR);
		//
		//if from equal null, then set the time to today 0:00
		if (fromCalendar == null) {
			fromCalendar = new GregorianCalendar
							(todayCalendar.get(Calendar.YEAR), 
							todayCalendar.get(Calendar.MONTH), 
							todayCalendar.get(Calendar.DAY_OF_MONTH), 
							0, 
							0, 
							0);
		}
		//if to equal null, then set the time to tomorrow 0:00
		if (toCalendar == null) {
			toCalendar = new GregorianCalendar(todayCalendar.get(Calendar.YEAR), 
												todayCalendar.get(Calendar.MONTH), 
												todayCalendar.get(Calendar.DAY_OF_MONTH) + 1, 
												0, 
												0, 
												0);
		}
		ContentResolver cr = getContentResolver();
		String[] projection = {Event._ID, Event.CONTENT, Event.LOCATION, Event.BEGIN_TIME, Event.END_TIME};
		String selection = Event.BEGIN_TIME + " > ? AND " + Event.END_TIME + " < ? ";
		String[] selectionArgs = {String.valueOf(fromCalendar.getTimeInMillis()),
									String.valueOf(toCalendar.getTimeInMillis())};
		Cursor cursor = cr.query(Event.CONTENT_URI, projection, selection, selectionArgs, null);
		EventListCursorAdapter adapter = new EventListCursorAdapter(this, R.layout.event_list_item, cursor);
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(listViewItemListener);
		
		
	}

	private void initiateBirthdayInfo() {
		// TODO Auto-generated method stub
		
	}
	
	private static class EventListCursorAdapter extends ResourceCursorAdapter {
		private Context context;
		
		private OnClickListener checkListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "ImageView clicked" + v.getTag(), Toast.LENGTH_SHORT).show();
				v.setSelected(v.isSelected() ? false : true);			
				v.refreshDrawableState();
				
				//TODO add or remove corresponding event id in the set
			}
		};

		public EventListCursorAdapter(Context context, int layout, Cursor c) {
			super(context, layout, c);
			this.context = context;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView dateTv = (TextView) view.findViewById(R.id.event_list_item_date);
			TextView contentTv = (TextView) view.findViewById(R.id.event_list_item_content);
			TextView locationTv = (TextView) view.findViewById(R.id.event_list_item_location);
			ImageView checkImageView = (ImageView) view.findViewById(R.id.event_list_item_check);
			
			long id = cursor.getLong(cursor.getColumnIndex(Event._ID));
			String content = cursor.getString(cursor.getColumnIndex(Event.CONTENT));
			String location = cursor.getString(cursor.getColumnIndex(Event.LOCATION));
			String beginTimeStr = cursor.getString(cursor.getColumnIndex(Event.BEGIN_TIME));
			String endTimeStr = cursor.getString(cursor.getColumnIndex(Event.END_TIME));
			
			checkImageView.setOnClickListener(checkListener);
			checkImageView.setTag(id);
			
			Calendar begin = Calendar.getInstance();
			begin.setTimeInMillis(Long.valueOf(beginTimeStr));
			Calendar end = Calendar.getInstance();
			end.setTimeInMillis(Long.valueOf(endTimeStr));
			
			dateTv.setText("From " + DateFormat.format(DATE_FORMAT, begin) 
							+ " to " + DateFormat.format(DATE_FORMAT, end));
			contentTv.setText(content);
			locationTv.setText(location);
		}
	}
}
