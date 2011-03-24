/**
 * author 李健
 * 2011-3-2
 */

package ustc.sse.assistant;

import ustc.sse.assistant.calendar.EventCalendar;
import ustc.sse.assistant.contact.ContactList;
import ustc.sse.assistant.help.HelpActivity;
import ustc.sse.assistant.setting.Setting;
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
    	ImageButton contactImageButton = (ImageButton) findViewById(R.id.contact_imageButton);
    	contactImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent contactIntent = new Intent(MainScreen.this, ContactList.class);
				MainScreen.this.startActivity(contactIntent);
			}
		});
    	
    	ImageButton calendarImageButton = (ImageButton) findViewById(R.id.calendar_imageButton);
    	calendarImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent eventIntent = new Intent(MainScreen.this, EventCalendar.class);
				MainScreen.this.startActivity(eventIntent);
			}
		});
    	
    	ImageButton settingButton = (ImageButton) findViewById(R.id.setting_imageButtojn);
    	settingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent settingIntent = new Intent(MainScreen.this, Setting.class);
				startActivity(settingIntent);
			}
		});
    	ImageButton helpButton = (ImageButton) findViewById(R.id.help_imageButton);
    	helpButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent helpIntent = new Intent(MainScreen.this, HelpActivity.class);
				startActivity(helpIntent);
			}
		});
    	
    }
}