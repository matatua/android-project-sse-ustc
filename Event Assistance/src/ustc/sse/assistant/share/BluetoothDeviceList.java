/**
 * 
 */
package ustc.sse.assistant.share;

import java.util.Set;

import ustc.sse.assistant.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 李健
 *
 */
public class BluetoothDeviceList extends Activity {

	protected static final String EXTRA_DEVICE_ADDRESS = "extra_device_address";
	protected static final String TAG = "BluetoothDeviceList";
	private ListView bondedListView;
	private ListView unbondedListView;
	
	private ArrayAdapter<String> bondedArrayAdapter;
	private ArrayAdapter<String> unbondedArrayAdapter;
	
	private Button scanButton;
	
	private BluetoothAdapter btAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.share_bluetooth_device_list);
		
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		
		bondedListView = (ListView) findViewById(R.id.share_bluetooth_device_list_bonded);
		unbondedListView = (ListView) findViewById(R.id.share_bluetooth_device_list_unbonded);
		scanButton = (Button) findViewById(R.id.share_bluetooth_device_scan);
		
		bondedArrayAdapter = new ArrayAdapter<String>(this, R.layout.share_bluetooth_device_list_textview);
		unbondedArrayAdapter = new ArrayAdapter<String>(this, R.layout.share_bluetooth_device_list_textview);
	
		Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
		if (devices.size() > 0) {
			for (BluetoothDevice d : devices) {
				bondedArrayAdapter.add(d.getName() + " " + d.getAddress());
			}
		} 
		bondedListView.setAdapter(bondedArrayAdapter);
		unbondedListView.setAdapter(unbondedArrayAdapter);
		
		bondedListView.setOnItemClickListener(deviceClickListener);
		unbondedListView.setOnItemClickListener(deviceClickListener);	

		TextView noBondedTv = (TextView) getLayoutInflater().inflate(R.layout.share_bluetooth_device_list_textview, bondedListView, false);
		noBondedTv.setText("无已配对设备");
		bondedListView.setEmptyView(noBondedTv);
		
		TextView emptyView = (TextView) getLayoutInflater().inflate(R.layout.share_bluetooth_device_list_textview, unbondedListView, false);
		emptyView.setText("未搜索到设备");
		unbondedListView.setEmptyView(emptyView);

		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(receiver, filter);
		this.registerReceiver(receiver, filter2);
		
		scanButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				doDiscovery();
				v.setEnabled(false);
			}
		});
		
	} 
	
	private void doDiscovery() {
		
		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
		setProgressBarIndeterminateVisibility(true);
		setTitle("搜索中...");
		
		btAdapter.startDiscovery();
	}
	
	private OnItemClickListener deviceClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String info = ((TextView) view).getText().toString();
			String address = info.substring(info.length() - 17);
			
			Intent newIntent = new Intent();
			newIntent.putExtra(EXTRA_DEVICE_ADDRESS, address);
			setResult(RESULT_OK, newIntent);
			
			Log.i(TAG, address);
			finish();
		}
	};
	
	protected void onDestroy() {
		super.onDestroy();
		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
		if (receiver != null) unregisterReceiver(receiver);
	};
	
	private BluetoothBroadcastReceiver receiver = new BluetoothBroadcastReceiver();
	
	private class BluetoothBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					String name = (device.getName() == null ? "无名称" : device.getName());
					unbondedArrayAdapter.add("名称:" + name + "\n" + "MAC:" + device.getAddress());
				}
				
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				setTitle("已搜索完毕");
				Toast.makeText(BluetoothDeviceList.this, "搜索完毕", Toast.LENGTH_SHORT).show();
				setProgressBarIndeterminateVisibility(false);
				scanButton.setEnabled(true);
			}
		}
		
	}
}
