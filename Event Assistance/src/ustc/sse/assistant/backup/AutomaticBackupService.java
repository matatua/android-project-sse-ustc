/**
 * 
 */
package ustc.sse.assistant.backup;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import ustc.sse.assistant.R;
import ustc.sse.assistant.backup.util.BackupUtils;
import ustc.sse.assistant.backup.util.EventToXml;
import ustc.sse.assistant.event.EventUtils;
import ustc.sse.assistant.event.provider.EventAssistant;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author 李健
 * 执行自动备份的服务
 *
 */
public class AutomaticBackupService extends IntentService {
	
	public static final String TAG = "AutomaticBackupService";
	
	private Notification notification;
	private NotificationManager nm;
	
	public AutomaticBackupService() {
		this(TAG);
	}
	
	public AutomaticBackupService(String name) {
		super(name);
		Log.i(TAG, "Initialize AutomaticBackupService");
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		WakeLockUtils.releaseWakeLock();
		
		if (EventUtils.haveEvent(getApplicationContext())) {
			EventToXml etx = new EventToXml(this, null, null);
			try {
				StringWriter writer = etx.generateXml();
				boolean success = BackupUtils.writeToBackupFile(writer);
				if (success) {
					notification = new Notification(R.drawable.notification, "自动备份完成", new Date().getTime());
					Intent i = new Intent(this, BackupRestore.class);
					PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
					notification.setLatestEventInfo(this, "事件助手", "自动备份完成", contentIntent);
					nm.notify(10, notification);
					
					//record last backup date
					SharedPreferences sp = this.getSharedPreferences(EventAssistant.TAG, MODE_WORLD_WRITEABLE);
					sp.edit().putLong(BackupRestore.LAST_BACKUP_DATE, (new Date()).getTime()).commit();
					Log.i(TAG, "automatic backup service success");				
				}
							
			} catch (IOException e) {
				Log.i(TAG, "auto backup fail");
			}
		}
		
	}
	
	

}
