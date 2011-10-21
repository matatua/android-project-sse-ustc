package ustc.sse.assistant.setting;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

import ustc.sse.assistant.R;
import ustc.sse.assistant.backup.AutomaticBackupBroadcastReceiver;
import ustc.sse.assistant.backup.BackupRestore;
import ustc.sse.assistant.backup.util.BackupUtils;
import ustc.sse.assistant.backup.util.EventToXml;
import ustc.sse.assistant.event.EventUtils;
import ustc.sse.assistant.event.provider.EventAssistant;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class Setting extends PreferenceActivity {
	
	private ListPreference backupInterval;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        automaticBackup();
    }
    
    private void automaticBackup() {
    	 backupInterval = (ListPreference) findPreference("autoBackupInterval");
    	 backupInterval.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				//here check last automatic backup time
				//if the interval is less than the set one, do backup immediately
				Integer value = Integer.valueOf((String) newValue);
				SharedPreferences sf = Setting.this.getSharedPreferences(EventAssistant.TAG, MODE_WORLD_WRITEABLE);
				
				long lastBackupDate = sf.getLong(BackupRestore.LAST_BACKUP_DATE, -1L);
				if (lastBackupDate != -1L) {
					Calendar lastBackupCalendar = Calendar.getInstance();
					lastBackupCalendar.setTimeInMillis(lastBackupDate);
					Calendar now = Calendar.getInstance();
					
					long actualInterval = now.getTimeInMillis() - lastBackupCalendar.getTimeInMillis();
					long newBackupInterval = EventUtils.dayToTimeInMillisecond(value);
					
					Cursor c = Setting.this.getContentResolver().query(Event.CONTENT_URI, new String[]{Event._COUNT}, null, null, null);
					int count = c.getCount();
					c.close();
					//only when we have events, then do backup, otherwise ignore backup operation
					if (actualInterval >= newBackupInterval && count > 0) {
						
						new Thread(new BackupRunnable()).start();
					} else {
						//else set a proper time to do automatic backup
						AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
						Intent i = new Intent(Setting.this, AutomaticBackupBroadcastReceiver.class);
						i.setAction(BackupRestore.ACTION_AUTOMATIC_BACKUP);
						
						PendingIntent operation = PendingIntent.getBroadcast(Setting.this, 0, i, PendingIntent.FLAG_ONE_SHOT);
						long triggerAtTime = lastBackupDate + newBackupInterval;
				
						am.set(AlarmManager.RTC_WAKEUP, triggerAtTime, operation);
					}
					
					
				}
				
				
				return true;
			}
		});
    }
    
    private class BackupRunnable implements Runnable {
		public void run() {
			Toast t = Toast.makeText(Setting.this, "备份失败", Toast.LENGTH_SHORT);
			EventToXml etx = new EventToXml(Setting.this, null, null);
			try {
				StringWriter sw = etx.generateXml();
				if (BackupUtils.writeToBackupFile(sw)) {
					t.setText("系统已为你自动备份");
					t.show();
					//after backup successfully, record the last_backup_date
					SharedPreferences sf = Setting.this.getSharedPreferences(EventAssistant.TAG, MODE_WORLD_WRITEABLE);				
					Date now = new Date();
					Editor editor = sf.edit();
					editor.putLong(BackupRestore.LAST_BACKUP_DATE, now.getTime());
					editor.commit();
					
				}
			} catch (IOException e) {
				t.show();
			}
		}
	}
}