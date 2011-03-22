package ustc.sse.assistant.event.data;

import java.util.List;

import ustc.sse.assistant.contact.data.Contact;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import android.content.ContentValues;

/**
 * 
 * @author 李健
 *
 */
public class EventEntity {
	
	public static final ContentValues eventToContentValues(
													String content,
													String alarmTime, 
													String alarmType,
													String beginTime,											
													String endTime,
													String createTime,
													String lastModifyTime,
													String location,
													String note,
													Integer priorAlarmDay,
													Integer priorRepeatDay
													) {
		
		ContentValues values = new ContentValues();
		values.put(Event.ALARM_TIME, alarmTime);
		values.put(Event.ALARM_TYPE, alarmType);
		values.put(Event.BEGIN_TIME, beginTime);
		values.put(Event.END_TIME, endTime);
		values.put(Event.CREATE_TIME, createTime);
		values.put(Event.LAST_MODIFY_TIME, lastModifyTime);
		values.put(Event.LOCATION, location);
		values.put(Event.NOTE, note);
		values.put(Event.PRIOR_ALARM_DAY, priorAlarmDay);
		values.put(Event.PRIOR_REPEAT_TIME, priorRepeatDay);
		values.put(Event.CONTENT, content);
		
		return values;
	}

}