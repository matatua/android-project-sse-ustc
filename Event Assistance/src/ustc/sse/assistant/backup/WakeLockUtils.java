/**
 * 
 */
package ustc.sse.assistant.backup;

import ustc.sse.assistant.event.provider.EventAssistant;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 * @author 李健
 *
 */
public class WakeLockUtils {
	private static WakeLock wakeLock;

	public static final void getWakeLock(Context context) {
		if (wakeLock == null) {
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, EventAssistant.TAG);
			wakeLock.setReferenceCounted(true);
			wakeLock.acquire();
		} else {
			wakeLock.acquire();
		}
	}
	
	public static final void releaseWakeLock() {
		if (wakeLock != null) {
			wakeLock.release();
		}
	}
}
