package ustc.sse.assistant.event;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ustc.sse.assistant.event.data.EventEntity;
import ustc.sse.assistant.event.provider.EventAssistant;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author 李健、宋安琪
 *
 */
public class EventUtils {
	
	public static final void linkifyEventLocation(TextView tv) {
		Pattern pattern = Pattern.compile("\\w+");
		Linkify.addLinks(tv, pattern, "geo:", null, new TransformFilter() {
			
			public String transformUrl(Matcher match, String url) {
				return "0,0?q=" + url;				
			}
		});
	}
	
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
	
	public static final ContentValues eventUpdateToContentValues(String content,
			String alarmTime, String alarmType, String beginTime,
			String endTime, String lastModifyTime,
			String location, String note, Integer priorAlarmDay,
			Integer priorRepeatDay) {

		ContentValues values = new ContentValues();
		values.put(Event.ALARM_TIME, alarmTime);
		values.put(Event.ALARM_TYPE, alarmType);
		values.put(Event.BEGIN_TIME, beginTime);
		values.put(Event.END_TIME, endTime);
		values.put(Event.LAST_MODIFY_TIME, lastModifyTime);
		values.put(Event.LOCATION, location);
		values.put(Event.NOTE, note);
		values.put(Event.PRIOR_ALARM_DAY, priorAlarmDay);
		values.put(Event.PRIOR_REPEAT_TIME, priorRepeatDay);
		values.put(Event.CONTENT, content);

		return values;
	}

	public static final EventEntity createEvent(String content,
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
		EventEntity ee = new EventEntity();
		ee.content = content;
		ee.alarmTime = String.valueOf(alarmTime);
		ee.alarmType = alarmType;
		ee.beginTime = String.valueOf(beginTime);
		ee.endTime = String.valueOf(endTime);
		ee.createTime = String.valueOf(createTime);
		ee.lastModifyTime = String.valueOf(lastModifyTime);
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
	
/*	public static class DeleteEventThread extends HandlerThread {
		private ContentResolver cr;
		private AlarmManager am;
		private List<PendingIntent> pendingIntents;
		
		public DeleteEventThread(ContentResolver cr, AlarmManager am, List<PendingIntent> pendingIntents) {
			this.cr = cr;
			this.am = am;
			this.pendingIntents = pendingIntents;
			
		}
		 (non-Javadoc)
		 * @see android.os.HandlerThread#run()
		 
		@Override
		public void run() {
			super.run();
			try {
				cr.applyBatch(EventAssistant.EVENT_AUTHORITY, eventOps);
				cr.applyBatch(EventAssistant.EVENT_CONTACT_AUTHORITY, eventContactOps);											
			
				} catch (RemoteException e) {
					e.printStackTrace();
					toast.show();
				} catch (OperationApplicationException e) {
					e.printStackTrace();
					toast.show();
				}
				
				for (PendingIntent pi : pendingIntents) {
					am.cancel(pi);
				}	
		}

*/
