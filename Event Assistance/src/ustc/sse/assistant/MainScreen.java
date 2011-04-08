/**
 * author 李健
 * 2011-3-2
 */

package ustc.sse.assistant;

import ustc.sse.assistant.backup.BackupRestore;
import ustc.sse.assistant.calendar.EventCalendar;
import ustc.sse.assistant.contact.ContactList;
import ustc.sse.assistant.help.HelpActivity;
import ustc.sse.assistant.setting.Setting;
import ustc.sse.assistant.share.ShareEvent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainScreen extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initImageButtons();
    }
    
    private void initImageButtons() {
    	View contactView = findViewById(R.id.main_screen_contact);
    	contactView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent contactIntent = new Intent(MainScreen.this, ContactList.class);
				MainScreen.this.startActivity(contactIntent);
			}
		});
    	View calendarView = findViewById(R.id.main_screen_calendar);
    	calendarView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent eventIntent = new Intent(MainScreen.this, EventCalendar.class);
				MainScreen.this.startActivity(eventIntent);
			}
		});
    	View settingView = findViewById(R.id.main_screen_setting);
    	settingView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent settingIntent = new Intent(MainScreen.this, Setting.class);
				startActivity(settingIntent);
			}
		});
    	View helpView = findViewById(R.id.main_screen_help);
    	helpView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent helpIntent = new Intent(MainScreen.this, HelpActivity.class);
				startActivity(helpIntent);
			}
		});
    	View backupView = findViewById(R.id.main_screen_backup);
    	backupView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent backupIntent = new Intent(MainScreen.this, BackupRestore.class);
				startActivity(backupIntent);
				
			}
		});
    	View shareView = findViewById(R.id.main_screen_share);
    	shareView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent shareIntent = new Intent(MainScreen.this, ShareEvent.class);
				startActivity(shareIntent);
			}
		});
    	
    }
}