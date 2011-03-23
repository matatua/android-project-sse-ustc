/**
 * 
 */
package ustc.sse.assistant.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ustc.sse.assistant.calendar.utils.MyCalendar;
import ustc.sse.assistant.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * @author 李健
 *
 */
public class EventCalendar extends Activity {
	
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
		List<Map<String, String>> cells = new ArrayList<Map<String, String>>();
		MyCalendar myCalendar = new MyCalendar(currentCalendar);
		String[] gregorianDays = myCalendar.getDays();
		String[] lunarDays = myCalendar.getLunarDays();

		double calendarHeight = calendarGridView.getHeight();
		double cellHeight = calendarHeight/6.0;
		
	

		for (int i = 0; i < gregorianDays.length; i++)
		{
			Map<String, String> cell = new HashMap<String, String>();
			cell.put("textview1", gregorianDays[i]);
//			cell.put("textview2", lunarDays[i]);
			cells.add(cell);
		}
		
//		SimpleAdapter simpleAdapter = new SimpleAdapter(this, cells,
//				R.layout.calendar_cell, new String[]
//				{ "textview1", "textview2" }, new int[]
//				{ R.id.gridview_textview1, R.id.gridview_textview2 });
//		calendarGridView.setAdapter(simpleAdapter);	
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, cells,
				R.layout.calendar_cell, new String[]
				{ "textview1"}, new int[]
				{ R.id.calendar_gridview_textview1});
		calendarGridView.setAdapter(simpleAdapter);
		
		
		TextView tv = (TextView) calendarGridView.findViewById(R.id.calendar_gridview_textview1);
		tv.setHeight((int) cellHeight);
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

}
