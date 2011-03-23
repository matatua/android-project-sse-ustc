package ustc.sse.assistant.event;

import ustc.sse.assistant.event.data.EventsEntity;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import android.content.ContentValues;
import android.os.Bundle;

/**
 * 
 * @author 李健
 *
 */
public class EventUtils {
	
	public static final long priorRepeatToInterval(int priorRepeat) {
		return (24 * 60 * 60 * 1000) / priorRepeat;
	}
	
	public static final long dayToTimeInMillisecond(int day) {
		return day * 24 * 60  * 60 * 1000;
	}

	public static final long toTimeInMillisecond(int todayRemindTime) {
		switch (todayRemindTime) {
		case EventConstant.EVENT_TODAY_REMIND_TIME_FIFTEEN_MINUTE :
			return 15 * 60 * 1000;
		case EventConstant.EVENT_TODAY_REMIND_TIME_FIVE_MINUTE :
			return 5 * 60 * 1000;
		case EventConstant.EVENT_TODAY_REMIND_TIME_FOURTYFIVE_MINUTE :
			return 45 * 60 * 1000;
		case EventConstant.EVENT_TODAY_REMIND_TIME_ONE_HOUR :
			return 60 * 60 * 1000;
		case EventConstant.EVENT_TODAY_REMIND_TIME_ONE_MINUTE :
			return 60 * 1000;
		case EventConstant.EVENT_TODAY_REMIND_TIME_SIX_HOUR :
			return 6 * 60 * 60 * 1000;
		case EventConstant.EVENT_TODAY_REMIND_TIME_TEN_MINUTE :
			return 10 * 60 * 1000;
		case EventConstant.EVENT_TODAY_REMIND_TIME_THIRTY_MINUTE :
			return 30 * 60 * 1000;
		case EventConstant.EVENT_TODAY_REMIND_TIME_THREE_HOUR :
			return 3 * 60 * 60 * 1000;
		case EventConstant.EVENT_TODAY_REMIND_TIME_TWENTY_MINUTE :
			return 20 * 60 * 1000;
		case EventConstant.EVENT_TODAY_REMIND_TIME_TWO_HOUR :
			return 2 * 60 * 60 * 1000;
		case EventConstant.EVENT_TODAY_REMIND_TIME_NONE :
			return 0;
		
		
		}
		return 0;
	}

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

	public static final EventsEntity createEvent(String content,
													long alarmTime, 
													int alarmType,
													long beginTime,											
													long endTime,
													long createTime,
													long lastModifyTime,
													String location,
													String note,
													int priorAlarmDay,
													int priorRepeatDay) {
		EventsEntity ee = new EventsEntity();
		ee.content = content;
		ee.alarmTime = alarmTime;
		ee.alarmType = alarmType;
		ee.beginTime = beginTime;
		ee.endTime = endTime;
		ee.createTime = createTime;
		ee.lastModifyTime = lastModifyTime;
		ee.location = location;
		ee.note = note;
		ee.priorAlarmDay = priorAlarmDay;
		ee.priorRepeat = priorRepeatDay;
		
		return ee;
	}

	public static final Bundle eventToBundle(String content,
													long alarmTime, 
													int alarmType,
													long beginTime,											
													long endTime,
													long createTime,
													long lastModifyTime,
													String location,
													String note,
													int priorAlarmDay,
													int priorRepeatDay) {
		Bundle bundle = new Bundle();
		bundle.putString(Event.CONTENT, content);
		bundle.putLong(Event.ALARM_TIME, alarmTime);
		bundle.putInt(Event.ALARM_TYPE, alarmType);
		bundle.putLong(Event.BEGIN_TIME, beginTime);
		bundle.putLong(Event.END_TIME, endTime);
		bundle.putLong(Event.CREATE_TIME, createTime);
		bundle.putLong(Event.LAST_MODIFY_TIME, lastModifyTime);
		bundle.putString(Event.LOCATION, location);
		bundle.putString(Event.NOTE, note);
		bundle.putInt(Event.PRIOR_ALARM_DAY, priorAlarmDay);
		bundle.putInt(Event.PRIOR_REPEAT_TIME, priorRepeatDay);
		
		return bundle;
	}

}
