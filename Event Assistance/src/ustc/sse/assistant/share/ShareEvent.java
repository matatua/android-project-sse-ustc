/**
 * 
 */
package ustc.sse.assistant.share;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

import ustc.sse.assistant.R;
import ustc.sse.assistant.backup.BackupRestore;
import ustc.sse.assistant.backup.util.EventToXml;
import ustc.sse.assistant.backup.util.XmlToEvent;
import ustc.sse.assistant.util.EventAssistantUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 李健
 *
 */
public class ShareEvent extends Activity {
	public static final String TAG = "ShareEvent";
	
	public static final int MESSAGE_READ = 1;
	public static final int MESSAGE_SEND = 2;
	public static final int MESSAGE_TOAST = 0;
	public static final int MESSAGE_DEVICE_NAME = 3;
	public static final String TOAST = "toast";
	public static final int MESSAGE_SHOW_PROGRESSBAR = 4;
	public static final int MESSAGE_DISMISS_PROGRESSBAR = 5;
	public static final int MESSAGE_RELEASE_RECIEVE_LOCK = 6;
	
	public static final String DEVICE_NAME = "device_name";
	
	public static final int REQUEST_ENABLE_BT = 100;
	public static final int REQUEST_CONNECT_DEVICE = 101;
	private static final int RESTORE_OPTION = 10;
	private static final int PROCESSING = 9;

	public static final String PROGRESSBAR_TEXT = "progressbar_text";

	

	
	
	private ListView listView;
	private BluetoothAdapter btAdapter;
	private byte[] receivedData;
	private String currentDeviceName;
	
	private Semaphore receiveSemaphore = new Semaphore(1);
	
	private EventBTService service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.share_event);
		initiateListView();
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (btAdapter == null) {
			Toast.makeText(this, "你的设备不支持蓝牙", Toast.LENGTH_SHORT).show();
			this.finish();
		}
		
	}

	/**
	 * 
	 */
	private void initiateListView() {
		listView = (ListView) findViewById(R.id.share_event_listView);
		LinearLayout headerView = (LinearLayout) getLayoutInflater().inflate(R.layout.share_event_header, listView, false);

		listView.addHeaderView(headerView, null, true);
		TextView backupLabel = new TextView(this);
		backupLabel.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
		backupLabel.setGravity(Gravity.CENTER);
		backupLabel.setText("共享已备份文件");
		backupLabel.setBackgroundColor(getResources().getColor(R.color.default_light_grey_bg));
		listView.addHeaderView(backupLabel, null, false);
		
		BackupFileAdapter backupFileAdapter = new BackupFileAdapter(this);
		listView.setAdapter(backupFileAdapter);
		listView.setOnItemClickListener(backupFileClickListener);
	} 
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (btAdapter != null) {
			if (!btAdapter.isEnabled()) {
				Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(i, REQUEST_ENABLE_BT);
			} else {
				if (service == null)
					setupEventBluetoothService();
			}
		}
	}
	
	@Override
	protected synchronized void onResume() {
		super.onResume();
		
		if (service != null) {
			if (service.getState() == EventBTService.STATE_NONE) {
				service.startup();
			}
		}
	}

	private void setupEventBluetoothService() {
		if (service != null) {
			service.stop();
		}
		
		service = new EventBTService(this, handler);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (service != null) 
			service.stop();
	}
	//make this device discoverable
	private void ensureDiscoverable() {
		if (btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) { 
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
			startActivity(discoverableIntent);
		}
			
	}
	//send a event xml file
	private synchronized void send(byte[] data) {
		if (service != null && data.length > 0) {
	
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bos);
				dos.writeLong(data.length);
				dos.write(data);
				
				service.send(bos.toByteArray());
			} catch (IOException e) {
				Log.e(TAG, "send fail", e);
			}
			
			
		}
	}
	//when receive a remote xml file
	private synchronized void receive(final boolean restoreContacts) {
		
		final Toast toast = Toast.makeText(this, "共享成功", Toast.LENGTH_SHORT);
		new Thread() {
			public void run() {
				
				handler.obtainMessage(MESSAGE_SHOW_PROGRESSBAR).sendToTarget();
				InputStream is = new ByteArrayInputStream(receivedData);
				XmlToEvent xte = new XmlToEvent(ShareEvent.this, is, restoreContacts, false);
				boolean success = xte.restore();
				handler.obtainMessage(MESSAGE_DISMISS_PROGRESSBAR).sendToTarget();
				if (success) {
					toast.show();
				} else {
					toast.setText("共享失败");
					toast.show();
				}
				
				handler.obtainMessage(MESSAGE_RELEASE_RECIEVE_LOCK).sendToTarget();
			};
		}.start();
	
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		switch (id) {
		case RESTORE_OPTION :
			return makeRestoreOptionDialog();
		case PROCESSING :
			return makeProcessingDialog();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
		super.onPrepareDialog(id, dialog);
		
		switch (id) {
		case PROCESSING :
			String message = bundle.getString(PROGRESSBAR_TEXT);
			if (null != message) {
				((ProgressDialog) dialog).setMessage(message);
			}
		}
	}
	
	private Dialog makeProcessingDialog() {
		ProgressDialog pd = new ProgressDialog(this);
		pd.setCancelable(false);
		pd.setMessage("共享数据处理中");
		pd.setIndeterminate(true);
		
		return pd;
	}

	private Dialog makeRestoreOptionDialog() {
		android.content.DialogInterface.OnClickListener callBack = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case Dialog.BUTTON_POSITIVE :
					receive(false);
					dialog.dismiss();
					break;
				case Dialog.BUTTON_NEUTRAL :
					receive(true);
					dialog.dismiss();
					break;
				case Dialog.BUTTON_NEGATIVE :
					dialog.dismiss();
					receiveSemaphore.release();
					break;
				}
			}
		};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("共享事件和联系人");
		builder.setMessage("请选择共享选项");
		builder.setPositiveButton("仅事件", callBack);
		builder.setNeutralButton("事件和联系人", callBack);
		builder.setNegativeButton("取消", callBack);
		
		return builder.create();
	}

	private Handler handler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_DEVICE_NAME :
				//TODO set title to device name
				String name = msg.getData().getString(TOAST);
				if (name == null) name = "";
				setTitle(name + "已连接");
				break;
			case MESSAGE_READ :
				
				try {
					receiveSemaphore.acquire();
					byte[] data = (byte[]) msg.obj;
					receivedData = data;

				} catch (InterruptedException e) {
					
				}
				
				break;
			case MESSAGE_SEND :
				break;
			case MESSAGE_TOAST :
				//TODO show toast message
				String text = msg.getData().getString(ShareEvent.TOAST);
				Toast.makeText(ShareEvent.this, text, Toast.LENGTH_SHORT).show();
				break;
				
			case MESSAGE_SHOW_PROGRESSBAR :
				Bundle bundle = msg.getData();
				showDialog(PROCESSING, bundle);
				break;
			case MESSAGE_DISMISS_PROGRESSBAR :
				dismissDialog(PROCESSING);
				break;
			case MESSAGE_RELEASE_RECIEVE_LOCK :
				receiveSemaphore.release();
				break;
			}
			
		};
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE :
			if (resultCode == RESULT_OK) {
				String address = data.getStringExtra(BluetoothDeviceList.EXTRA_DEVICE_ADDRESS);
				BluetoothDevice btDevice = btAdapter.getRemoteDevice(address);
				
				service.connect(btDevice);
			}
			break;
		case REQUEST_ENABLE_BT :
			if (resultCode == RESULT_OK) {
				setupEventBluetoothService();
			} else {
				Toast.makeText(this, "打开蓝牙失败", Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.share_menu, menu);
		MenuItem deviceListItem = menu.findItem(R.id.share_event_menu_device_list);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.share_event_menu_device_list :
			Intent deviceListIntent = new Intent(this, BluetoothDeviceList.class);
			startActivityForResult(deviceListIntent, REQUEST_CONNECT_DEVICE);		
			
		case R.id.share_event_menu_discoverable :
			ensureDiscoverable();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	//
	private OnItemClickListener backupFileClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(final AdapterView<?> parent, View view, final int position,
				long id) {
			if (service == null || service.getState() != EventBTService.STATE_CONNECTED) {
				Toast.makeText(ShareEvent.this, "请先建立蓝牙连接", Toast.LENGTH_SHORT).show();
			} else {
				//start a new thread to load file and execute send
				new Thread() {
					public void run() {
						if (position == 0) {
							//make a new xml and transfer it to remote device
							handler.obtainMessage(MESSAGE_SHOW_PROGRESSBAR).sendToTarget();
							EventToXml etx = new EventToXml(ShareEvent.this, null, null);
							try {
								send(etx.generateXml().toString().getBytes());
							} catch (IOException e) {
								Log.e(TAG, e.getMessage());
								Message m = handler.obtainMessage(MESSAGE_TOAST);
								m.getData().putString(TOAST, "文件生成失败");
								m.sendToTarget();
							}
						} else {
							
							handler.obtainMessage(MESSAGE_SHOW_PROGRESSBAR).sendToTarget();
							File file = (File) parent.getAdapter().getItem(position);
							byte[] buffer = new byte[1024];
							int length = -1;
							try {
								ByteArrayOutputStream bo = new ByteArrayOutputStream();
								FileInputStream fs = new FileInputStream(file);
								while ((length = fs.read(buffer)) > 0) {
									bo.write(buffer, 0, length);
								}
								send(bo.toByteArray());
							} catch (FileNotFoundException e) {
								
							} catch (IOException e) {
								
							}
							
						}
					}
				}.start();
				
			}
			
		}
	};
	
	private static class BackupFileAdapter extends BaseAdapter {

		private File[] backupFiles = new File[0];
		private Context context;
		
		public BackupFileAdapter(Context context) {
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				Toast.makeText(context, "SD卡未准备好", Toast.LENGTH_SHORT).show();
				return ;
			}
			
			this.context = context;
			File root = Environment.getExternalStorageDirectory();
			File backupDir = new File(root, BackupRestore.BACKUP_RESTORE_DIR);
			if (backupDir.exists()) {
				backupFiles = backupDir.listFiles(BackupRestore.BACKUP_FILE_FILTER);
				if (backupFiles == null) {
					backupFiles = new File[0];
				}
			} else {
				backupFiles = new File[0];
			}
		}
		
		@Override
		public int getCount() {
			return backupFiles.length;
		}

		@Override
		public Object getItem(int position) {
			return backupFiles[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.share_event_file_item, parent, false);
			}
			
			TextView timeTv = (TextView) convertView.findViewById(R.id.share_event_file_item_backup_time);
			TextView sizeTv = (TextView) convertView.findViewById(R.id.share_event_file_item_backup_size);
			
			File curFile = backupFiles[position];
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(curFile.lastModified());
			timeTv.setText("备份于：" + DateFormat.format(BackupRestore.DATE_FORMAT, calendar));
			sizeTv.setText("大小：" + EventAssistantUtils.bytesToLargeUnit(curFile.length(), EventAssistantUtils.UNIT_SMART));

			return convertView;
		}
		
	}
}
