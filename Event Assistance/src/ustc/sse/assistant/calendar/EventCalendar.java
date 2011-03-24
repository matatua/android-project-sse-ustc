/**
 * 
 */
package ustc.sse.assistant.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ustc.sse.assistant.R;
import ustc.sse.assistant.calendar.utils.MyCalendar;
import ustc.sse.assistant.calendar.utils.SmartDate;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * @author 李健
 *
 */
public class EventCalendar extends Activity {
	public static final String SMART_DATE = "smart_date";
	
	private TextView preMonthTextView;
	private TextView curMonthTextView;
	private TextView nextMonthTextView;
	
	private GridView calendarGridView;
	private Calendar preCalendar;
	private Calendar curCalendar;
	private Calendar nextCalendar;
	
	public static final String DATE_FORMAT_YEAR_MONTH = "yyyy年MM月";
	public static final String DATE_FORMAT_MONTH = "MM月";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		
		initiateCalendars();
		initiateWidget();
		setAllTabText();
	}

	/**
	 * initiate all calendars to original state
	 */
	private void initiateCalendars() {
		preCalendar = Calendar.getInstance();
		curCalendar = Calendar.getInstance();
		nextCalendar =  Calendar.getInstance();
		
		preCalendar.roll(Calendar.MONTH, false);
		nextCalendar.roll(Calendar.MONTH, true);
		
	}

	private void initiateWidget() {
		preMonthTextView = (TextView) findViewById(R.id.calendar_left_tab);
		curMonthTextView = (TextView) findViewById(R.id.calendar_center_tab);
		nextMonthTextView = (TextView) findViewById(R.id.calendar_right_tab);	
		calendarGridView = (GridView) findViewById(R.id.calendar_gridView);
		
		//set onClick listener for tabs
		initiateTabListener();
		//
		initiateCalendarGridView(curCalendar);
	}


	private void initiateTabListener() {
		OnClickListener leftTabListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				rollCalendarsMonth(false);
				initiateCalendarGridView(curCalendar);
				setAllTabText();
			}
		}; 
		preMonthTextView.setOnClickListener(leftTabListener);
		
		OnClickListener centerTabListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar now = Calendar.getInstance();
				if (!(curCalendar.get(Calendar.MONTH) == now.get(Calendar.MONTH))) {
					initiateCalendars();
					initiateCalendarGridView(curCalendar);
					
					setAllTabText();
				}
				
			}
		};
		curMonthTextView.setOnClickListener(centerTabListener);
	
		OnClickListener rightTabListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				rollCalendarsMonth(true);
				initiateCalendarGridView(curCalendar);
				setAllTabText();
			}
		};
		nextMonthTextView.setOnClickListener(rightTabListener);
	}

	/**
	 * using currentCalendar to initiate the gridview
	 * @param currentCalendar
	 */
	private void initiateCalendarGridView(Calendar currentCalendar) {

		double calendarHeight = calendarGridView.getHeight();
		double cellHeight = calendarHeight/6.0;
		
		List<Map<String, SmartDate>> data = new ArrayList<Map<String,SmartDate>>();
		
		ListAdapter adapter = new EventCalendarGridViewAdapter(this, data, (int) cellHeight);

		calendarGridView.setAdapter(adapter);
	}
	
	/**
	 * direction true : increment all calendar's month by one
	 * direction false : decrease all calendar's month by one
	 * @param direction
	 */
	private void rollCalendarsMonth(boolean direction) {
			int amount = 0;
			if (direction){
				amount = 1;
			} else {
				amount = -1;
			}
			preCalendar.add(Calendar.MONTH, amount);
			curCalendar.add(Calendar.MONTH, amount);
			nextCalendar.add(Calendar.MONTH, amount);				
	}

	private void setAllTabText() {
		preMonthTextView.setText(DateFormat.format(DATE_FORMAT_MONTH, preCalendar));
		curMonthTextView.setText(DateFormat.format(DATE_FORMAT_YEAR_MONTH, curCalendar));
		nextMonthTextView.setText(DateFormat.format(DATE_FORMAT_MONTH, nextCalendar));
	}

	
	private static class EventCalendarGridViewAdapter extends BaseAdapter {

		private Context context;
		private List<Map<String, SmartDate>> data;
		private int height;
		
		/**
		 * 
		 * @param ctx
		 * @param data list of map of SmartDate, a map contain a gregorian date and a lunar date
		 * @param height the height of each cell
		 */
		public EventCalendarGridViewAdapter(Context ctx, List<Map<String, SmartDate>> data, int height) {
			this.context = ctx;
			this.data = data;
			this.height = height;
		}
		
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LinearLayout linearLayout = null;
			if (convertView != null && convertView instanceof LinearLayout) {
				linearLayout = (LinearLayout) convertView;
			} else {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				linearLayout = (LinearLayout) inflater.inflate(R.id.calendar_gridView_cell_linearLayout, null);

			}
			linearLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, height));
			
			Map<String, SmartDate> map = data.get(position);
			SmartDate smartDate = map.get(SMART_DATE);
		
			TextView firstTv = (TextView) linearLayout.findViewById(R.id.calendar_gridview_textview1);
			TextView secondTv = (TextView) linearLayout.findViewById(R.id.calendar_gridview_textview2);
			
			//set different color and font, or other attributes using information from SmartDate
			firstTv.setText(smartDate.getGregorianDay().toString());
			secondTv.setText(smartDate.getDisplayText());
			firstTv.setTextColor(smartDate.getGregorianColorResId());
			secondTv.setTextColor(smartDate.getLunarColorResId());
			
			linearLayout.setTag(smartDate);
			return linearLayout;
			
		}
		
	}
}
