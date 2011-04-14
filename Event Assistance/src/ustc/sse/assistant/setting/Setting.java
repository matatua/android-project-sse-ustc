package ustc.sse.assistant.setting;

import java.io.IOException;
import java.io.StringWriter;

import ustc.sse.assistant.R;
import ustc.sse.assistant.backup.util.BackupUtils;
import ustc.sse.assistant.backup.util.EventToXml;
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
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				//here check last automatic backup time
				//if the interval is less than the set one, do backup immediately
				//TODO
				
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
				if (!BackupUtils.writeToBackupFile(sw)) {
					t.show();

				}
			} catch (IOException e) {
				t.show();
			}
		}
	}
}