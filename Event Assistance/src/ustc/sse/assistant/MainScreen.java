/**
 * author 李健
 * 2011-3-2
 */

package ustc.sse.assistant;

import ustc.sse.assistant.contact.ContactList;
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
        
        initialImageButtons();
    }
    
    private void initialImageButtons() {
    	ImageButton contactImageButton = (ImageButton) findViewById(R.id.contact_imageButton);
    	contactImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent contactIntent = new Intent(MainScreen.this, ContactList.class);
				MainScreen.this.startActivity(contactIntent);
			}
		});
    }
}