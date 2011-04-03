/**
 * 
 */
package ustc.sse.assistant.backup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

/**
 * @author 李健
 *
 */
public class EventBackupAgent extends BackupAgent {

	private HashMap<String, Integer> existedBackupFileSet = new HashMap<String, Integer>();
	private Set<String> backupedFileSet = new HashSet<String>();
	private File[] existedBackupFiles;
	private boolean isMediaMounted() {
		return Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//load existed backup file
		if (isMediaMounted()) {
			File root = Environment.getExternalStorageDirectory();
			File backupDir = new File(root, BackupRestore.BACKUP_RESTORE_DIR);
			existedBackupFiles = backupDir.listFiles(BackupRestore.BACKUP_FILE_FILTER);
		
			for (int i = 0; i < existedBackupFiles.length; i++) {
				existedBackupFileSet.put(existedBackupFiles[i].getName(), i);
			}
		}
	}
	/* (non-Javadoc)
	 * @see android.app.backup.BackupAgent#onBackup(android.os.ParcelFileDescriptor, android.app.backup.BackupDataOutput, android.os.ParcelFileDescriptor)
	 */
	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput output,
			ParcelFileDescriptor newState) throws IOException {
		
			if (oldState == null) {
				DataOutputStream ds = new DataOutputStream(new FileOutputStream(newState.getFileDescriptor()));
				
				//backup all files
				for (File f : existedBackupFiles) {
					
					doBackup(f, output);
					ds.writeUTF(f.getName() + "\n");
					
				}
				ds.close();
			} else {
				//delete old files and add new files
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(oldState.getFileDescriptor())));
				String oneLine = null;
				while ((oneLine = reader.readLine()) != null) {
					backupedFileSet.add(oneLine);
				}
				
				//check new backup file
				Iterator<Entry<String, Integer>> iter = existedBackupFileSet.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String, Integer> entry = iter.next();
					String name = entry.getKey();
					int position = entry.getValue();
					
					if (!backupedFileSet.contains(name)) {
						File needBackFile = existedBackupFiles[position];
						backupedFileSet.add(name);
						
						doBackup(needBackFile, output);
					}					
				} //end while
				//then check deleted file and delete it in google cloud storage
				for (String name : backupedFileSet) {
					if (existedBackupFileSet.get(name) == null) {
						output.writeEntityHeader(name, -1);
						output.writeEntityData(new byte[0], -1);
						
						backupedFileSet.remove(name);
					}
					
				}
				
				DataOutputStream ds = new DataOutputStream(new FileOutputStream(newState.getFileDescriptor()));
				//at last store new info to newState
				for(String name : backupedFileSet) {
					ds.writeUTF(name + "\n");
				}
				ds.close();
			}
	}

	/**
	 * store file to cloud storage
	 * the header key is the same name of the file
	 * @param f
	 * @param output
	 * @throws IOException
	 */
	private void doBackup(File f, BackupDataOutput output) throws IOException {
		int length = (int) f.length();
		byte[] data = null;
		
		synchronized (BackupLock.lock) {
			BufferedInputStream bi = new BufferedInputStream(
					new FileInputStream(f));
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			int oneByte;
			while ((oneByte = bi.read()) != -1) {
				bo.write(oneByte);
			}
			data = bo.toByteArray();
			output.writeEntityHeader(f.getName(), length);
			output.writeEntityData(data, length);
			bi.close();
			bo.close();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.backup.BackupAgent#onRestore(android.app.backup.BackupDataInput, int, android.os.ParcelFileDescriptor)
	 */
	@Override
	public void onRestore(BackupDataInput input, int appVersion,
			ParcelFileDescriptor newState) throws IOException {
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			DataOutputStream dp = new DataOutputStream(new FileOutputStream(newState.getFileDescriptor()));

			while (input.readNextHeader()) {
				String key = input.getKey();
				int dataSize = input.getDataSize();
				byte[] data = new byte[dataSize];
				input.readEntityData(data, 0, dataSize);
				
				File root = Environment.getExternalStorageDirectory();
				File backupDir = new File(root, BackupRestore.BACKUP_RESTORE_DIR);
				File backupFile = new File(backupDir, key);
				
				synchronized (BackupLock.lock) {
					BufferedOutputStream bo = new BufferedOutputStream(
							new FileOutputStream(backupFile));
					bo.write(data);
					bo.flush();
					bo.close();
				}
				
				dp.writeUTF(key + "\n");						
			}
			dp.close();
			
		}
		
	}

}
