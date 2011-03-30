/**
 * 
 */
package ustc.sse.assistant.backup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import ustc.sse.assistant.R;
import ustc.sse.assistant.backup.util.EventToXml;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author 李健
 *
 */
public class BackupRestore extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.backup_restore);
		Button backup = (Button) findViewById(R.id.generate_xml);
		Button restore = (Button) findViewById(R.id.restore_xml);
		
		backup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EventToXml etx = new EventToXml(BackupRestore.this, null, null);
				StringWriter sw = etx.generateXml();
				File root = Environment.getExternalStorageDirectory();
				File backDir = new File(root, "/eventassistant");
				
				try {
					if (!backDir.exists()) {
						backDir.mkdirs();
					}
					
					File backFile = new File(backDir, "backup.xml");
					if (!backFile.exists()) {
						backFile.createNewFile();
					}
					
					FileOutputStream fs = new FileOutputStream(backFile);
					byte[] byteArray = new byte[sw.getBuffer().length()];
					fs.write(sw.toString().getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		restore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
}
