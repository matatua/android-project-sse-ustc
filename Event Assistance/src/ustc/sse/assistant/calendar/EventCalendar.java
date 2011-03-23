/**
 * 
 */
package ustc.sse.assistant.calendar;

import java.util.Calendar;

import ustc.sse.assistant.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
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
		//
		
	}
	
	/**
	 * direction true : increment all calendar's month by one
	 * direction false : decrease all calendar's month by one
	 * @param direction
	 */
	private void rollCalendarsMonth(boolean direction) {
			preCalendar.roll(Calendar.MONTH, direction);
			curCalendar.roll(Calendar.MONTH, direction);
			nextCalendar.roll(Calendar.MONTH, direction);
		
	}

	private void setAllTabText() {
		preMonthTextView.setText(DateFormat.format(DATE_FORMAT_MONTH, preCalendar));
		curMonthTextView.setText(DateFormat.format(DATE_FORMAT_YEAR_MONTH, curCalendar));
		nextMonthTextView.setText(DateFormat.format(DATE_FORMAT_MONTH, nextCalendar));
	}
}
