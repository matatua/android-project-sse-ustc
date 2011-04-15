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
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;

/**
 * @author 李健
 * 执行自动备份的服务
 *
 */
public class AutomaticBackupService extends IntentService {
	private Notification notification;
	private NotificationManager nm;
	public AutomaticBackupService(String name) {
		super(name);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		WakeLockUtils.releaseWakeLock();
		
		if (EventUtils.haveEvent(getApplicationContext())) {
			EventToXml etx = new EventToXml(getApplicationContext(), null, null);
			try {
				StringWriter writer = etx.generateXml();
				boolean success = BackupUtils.writeToBackupFile(writer);
				if (success) {
					notification = new Notification(R.drawable.notification, "自动备份完成", new Date().getTime());
					nm.notify(10, notification);
				}
				
			} catch (IOException e) {
				
			}
		}
		
	}
	
	

}
