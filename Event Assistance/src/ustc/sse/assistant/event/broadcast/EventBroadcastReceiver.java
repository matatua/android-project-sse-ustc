/**
 * 
 */
package ustc.sse.assistant.event.broadcast;

import ustc.sse.assistant.MainScreen;
import ustc.sse.assistant.R.drawable;
import ustc.sse.assistant.event.EventConstant;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * @author 李健
 *
 */
public class EventBroadcastReceiver extends BroadcastReceiver {
	public static final int EVENT_NOTIFICATION_ID = 1000;
	
	private Context ctx;
	private Intent i;
	private Toast toast;
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		ctx = context;
		i = intent;
		
		int alarmType = i.getIntExtra(Event.ALARM_TYPE, EventConstant.EVENT_ALARM_TYPE_RING);
		switch (alarmType) {
		case EventConstant.EVENT_ALARM_TYPE_VIBRATE :
			whenVibrate();
			break;
		case EventConstant.EVENT_ALARM_TYPE_NOTIFICATION :
			whenNotification();
			break;
		case EventConstant.EVENT_ALARM_TYPE_RING :			
		default :
			whenRing();
			
		}
		
		
	}
	
	private void whenVibrate() {
		Vibrator vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = {1000, 2000, 1000, 20000, 1000, 2000};
		vibrator.vibrate(pattern, -1);
		showToast();
	}
	
	private void whenNotification() {
		NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = makeNotification();
		nm.notify(EVENT_NOTIFICATION_ID, notification);
	}
	
	private void whenRing() {
		NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = makeNotification();
		notification.defaults = Notification.DEFAULT_SOUND;
		nm.notify(EVENT_NOTIFICATION_ID, notification);
	}
	
	private Notification makeNotification() {
		String content = i.getStringExtra(Event.CONTENT);
		CharSequence tickerText = "事件：" + content.substring(0, 20);
		CharSequence contentTitle = "你的事件";
		CharSequence contentText = content;
		// TODO start the event detail UI
		Intent intent = new Intent(ctx, MainScreen.class);
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
		Notification notification = new Notification(drawable.notification, tickerText, System.currentTimeMillis());
		notification.setLatestEventInfo(ctx, contentTitle, contentText, contentIntent);
		
		return notification;
	}
	
	private void showToast() {
		String content = i.getStringExtra(Event.CONTENT);

		CharSequence text = "事件：" + content.substring(0, 20);
		if (toast == null) {
			
			Toast.makeText(ctx, text, Toast.LENGTH_LONG);
		} else {
			toast.cancel();
			toast.setText(text);
			toast.show();
			
		}
	}

}
