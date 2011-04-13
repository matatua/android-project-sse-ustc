/**
 * 
 */
package ustc.sse.assistant.event.broadcast;

import java.util.Calendar;

import ustc.sse.assistant.R;
import ustc.sse.assistant.contact.data.BirthdayConstant;
import ustc.sse.assistant.event.BirthdayList;
import ustc.sse.assistant.event.EventList;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;

/**
 * @author 李健
 * this broadcast will fire when phone boots up and notify every half day
 */
public class BirthdayBroadcastReceiver extends BroadcastReceiver {

	public static final int BIRTHDAY_NOTIFICATION_ID = 200;
	public static final String ACTION_BIRTHDAY_NOTIFICATION = "action_birthday_notification";
	
	private boolean needNotification = false;
	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
				intent.getAction().equals(ACTION_BIRTHDAY_NOTIFICATION)) {
			notifyBirthday(context);
			setAlarm(context);
		} 
	}
	
	private void setAlarm(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		//check if current time is after 12 o'clock, then set the trigger time
		//to be tommorow's noon;
		Calendar triggerCalendar = Calendar.getInstance();
		if (triggerCalendar.get(Calendar.HOUR_OF_DAY) >= 12) {
			triggerCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		triggerCalendar.set(Calendar.HOUR_OF_DAY, 12);
		triggerCalendar.set(Calendar.MINUTE, 0);
		triggerCalendar.set(Calendar.SECOND, 0);
		long triggerAtTime = triggerCalendar.getTimeInMillis();
		
		Intent intent = new Intent(context, BirthdayBroadcastReceiver.class);
		intent.setAction(ACTION_BIRTHDAY_NOTIFICATION);
		PendingIntent operation = PendingIntent.getBroadcast(context, 0, intent, 0);
		
		am.set(AlarmManager.RTC_WAKEUP, triggerAtTime, operation);
		
	}

	private void notifyBirthday(final Context context) {

		// check contacts whose birthday is today
		Calendar today = Calendar.getInstance();
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.setTimeInMillis(today.getTimeInMillis());
		tomorrow.add(Calendar.DAY_OF_MONTH, 1);
		Calendar nextTomorrow = Calendar.getInstance();
		nextTomorrow.setTimeInMillis(tomorrow.getTimeInMillis());
		nextTomorrow.add(Calendar.DAY_OF_MONTH, 1);

		ContentResolver cr = context.getContentResolver();
		String[] projection = { Data.DISPLAY_NAME, Data.LOOKUP_KEY,
				BirthdayConstant.MONTH, BirthdayConstant.DAY };
		String selection = Data.MIMETYPE + " = ? AND (("
				+ BirthdayConstant.MONTH + " = ? AND " + BirthdayConstant.DAY
				+ " = ?) OR " + " ( " + BirthdayConstant.MONTH + " = ? AND "
				+ BirthdayConstant.DAY + " = ?) OR" + " ( "
				+ BirthdayConstant.MONTH + " = ? AND " + BirthdayConstant.DAY
				+ " = ?))";

		String[] selectionArgs = { BirthdayConstant.TYPE,
				String.valueOf(today.get(Calendar.MONTH)),
				String.valueOf(today.get(Calendar.DAY_OF_MONTH)),
				String.valueOf(tomorrow.get(Calendar.MONTH)),
				String.valueOf(tomorrow.get(Calendar.DAY_OF_MONTH)),
				String.valueOf(nextTomorrow.get(Calendar.MONTH)),
				String.valueOf(nextTomorrow.get(Calendar.DAY_OF_MONTH)) };

		Cursor cursor = cr.query(Data.CONTENT_URI, projection, selection,
				selectionArgs, null);
		// if no people have birthday within three days, ignore notification
		if (cursor.getCount() <= 0) {
			needNotification = false;
			return;
		}

		int displayNameColumnIndex = cursor
				.getColumnIndex(Contacts.DISPLAY_NAME);
		int lookupColumnIndex = cursor.getColumnIndex(Contacts.LOOKUP_KEY);
		int monthColumnIndex = cursor.getColumnIndex(BirthdayConstant.MONTH);
		int dayColumnIndex = cursor.getColumnIndex(BirthdayConstant.DAY);
		if (cursor.moveToFirst()) {
			String displayName = cursor.getString(displayNameColumnIndex);
			String monthStr = cursor.getString(monthColumnIndex);
			String dayStr = cursor.getString(dayColumnIndex);
			// prepare notification
			String contentText = displayName + "等" + cursor.getCount()
					+ "人在近三天过生日";
			Intent intent = new Intent(context, BirthdayList.class);
			// TODO set proper from and to calendar for BirthdayList
			intent.putExtra(EventList.FROM_CALENDAR, today);
			intent.putExtra(EventList.TO_CALENDAR, nextTomorrow);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					intent, 0);
			// set notification properties
			Notification notification = new Notification(
					R.drawable.notification, "生日提醒", System.currentTimeMillis());
			notification.defaults |= notification.DEFAULT_SOUND;
			notification.flags |= notification.FLAG_AUTO_CANCEL;
			notification.setLatestEventInfo(context, "事件助手", contentText,
					contentIntent);
			// notify the notification
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(BIRTHDAY_NOTIFICATION_ID, notification);

			needNotification = true;
		}

	}

}
