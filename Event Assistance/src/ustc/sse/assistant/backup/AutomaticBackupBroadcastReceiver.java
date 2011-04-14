/**
 * 
 */
package ustc.sse.assistant.backup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author 李健
 * 开机后检查上一次备份时间，如果已到设置的自动备份时间间隔，且自动备份功能
 * 已打开，则开启一个服务自动进行备份
 */
public class AutomaticBackupBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
	}

}
