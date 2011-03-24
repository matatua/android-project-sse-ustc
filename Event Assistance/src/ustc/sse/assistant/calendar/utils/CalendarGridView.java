package ustc.sse.assistant.calendar.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class CalendarGridView extends GridView {

	public CalendarGridView(Context context) {
		super(context);
		
	}

	public CalendarGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}

	public CalendarGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE
				) {
			return true;
		}
		return super.onTouchEvent(ev);
	}

}
