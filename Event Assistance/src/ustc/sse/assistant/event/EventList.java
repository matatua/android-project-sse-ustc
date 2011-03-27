/**
 * 
 */
package ustc.sse.assistant.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ustc.sse.assistant.R;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

/**
 * a event list 
 * @author 李健
 *
 */
public class EventList extends Activity {
	
	public static final String FROM_CALENDAR = "begin_calendar";
	public static final String TO_CALENDAR = "end_calendar";
	
	public static final String DATE_FORMAT = "yyyy年MM月dd日";
	
	private static final int HEADER_FOOTER_NUMBER = 1;
	
	private ListView listView;
	private Button selectButton;
	private Button deleteButton;
	private LinearLayout buttonBar;
	private Calendar todayCalendar = Calendar.getInstance();
	private Calendar fromCalendar;
	private Calendar toCalendar;
	
	private Set<Long> selectedItemIds = new HashSet<Long>();
	private List<ImageView> imageViewList = new ArrayList<ImageView>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list);
		
		getWidgets();
		initiateEvents();
		initiateBirthdayInfo();
		initiateButtons();
	}


	private void initiateButtons() {
		selectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int count = listView.getAdapter().getCount() - HEADER_FOOTER_NUMBER;
				int size = selectedItemIds.size();
				//all items selected
				if ( size < count) {
					selectButton.setText(getString(R.string.event_list_deselect_button));
					for (ImageView iv : imageViewList) {
						iv.setSelected(true);
						selectedItemIds.add((Long) iv.getTag());
					}
					
				} else if (size >= count){
					//when all items was selected, then deselect all items
					selectButton.setText(getString(R.string.event_list_select_button));
					for (ImageView iv : imageViewList) {
						iv.setSelected(false);
						selectedItemIds.remove((Long) iv.getTag());
						
					}
					buttonBar.setVisibility(View.INVISIBLE);
				}
				
				deleteButton.setText(getString(R.string.event_list_delete_button) + "(" + selectedItemIds.size() + ")");
			}
		});
		
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}


	private void getWidgets() {
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
			//Toast.makeText(EventList.this, String.valueOf(id), Toast.LENGTH_SHORT).show();
			//TODO start event detail
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
		String selection = Event.BEGIN_TIME + " >=  ? AND " + Event.BEGIN_TIME + " <= ? ";
		String[] selectionArgs = {String.valueOf(fromCalendar.getTimeInMillis()),
									String.valueOf(toCalendar.getTimeInMillis())};
		Cursor cursor = cr.query(Event.CONTENT_URI, projection, selection, selectionArgs, null);
		EventListCursorAdapter adapter = new EventListCursorAdapter(this, 
														R.layout.event_list_item, 
														cursor,
														imageViewListener,
														imageViewList);
		
		TextView tv = new TextView(this);
		tv.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, 50));
		tv.setText("查看更多事件");
		listView.addFooterView(tv);	
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(listViewItemListener);
		
		
	}

	private void initiateBirthdayInfo() {
		// TODO Auto-generated method stub
		
	}
	
	//this listener is used by the check image view only
	private OnClickListener imageViewListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int count = listView.getAdapter().getCount() - HEADER_FOOTER_NUMBER;
			int size = selectedItemIds.size();

			//Toast.makeText(EventList.this, v.toString() + " " + v.getTag() + " " + v.isSelected(), Toast.LENGTH_SHORT).show();
			if (v.isSelected()) {
				if (size == count) {
					selectButton.setText(getString(R.string.event_list_select_button));
				}
				//if size equal 1, here we hide the button bar
				if (size == 1) {
					buttonBar.setVisibility(View.INVISIBLE);
				}
				v.setSelected(false);
				selectedItemIds.remove(v.getTag());
			} else {
				
				if (size + 1 == count) {
					selectButton.setText(getString(R.string.event_list_deselect_button));
				}
				
				v.setSelected(true);
				selectedItemIds.add((Long) v.getTag());
								
				//show the button bar
				if (size == 0) {
					buttonBar.setVisibility(View.VISIBLE);
				}
			}
			
			deleteButton.setText(getString(R.string.event_list_delete_button) + "(" + selectedItemIds.size() + ")");
			
		}
	
	};

	private static class EventListCursorAdapter extends ResourceCursorAdapter {
		private Context context;
		private OnClickListener imageViewListener;
		private List<ImageView> imageViews;

		public EventListCursorAdapter(Context context, int layout, Cursor c, OnClickListener l, List<ImageView> imageViews) {
			super(context, layout, c);
			this.context = context;
			this.imageViewListener = l;
			this.imageViews = imageViews;
			
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return super.getView(position, null, parent);
		     
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
			
			checkImageView.setOnClickListener(imageViewListener);
			Drawable drawable = context.getApplicationContext().getResources().getDrawable(R.drawable.indicator_check_mark_light);
			checkImageView.setImageDrawable(drawable.mutate());
			checkImageView.setTag(id); 
			imageViews.add(checkImageView);
			
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
