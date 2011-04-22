/**
 * 
 */
package ustc.sse.assistant.share;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import ustc.sse.assistant.event.provider.EventAssistant;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html.TagHandler;
import android.util.Log;

/**
 * @author 李健
 *
 */
public class EventBTService {
	public static final UUID MY_UUID = UUID.fromString("ce395900-31a4-41f2-b4f5-79d7ac4189a6");
	
	private Handler handler;
	private Context context;
	
	private BluetoothAdapter btAdapter;
	private int state = STATE_NONE;
	
	private AcceptThread acceptThread;
	private ConnectThread connectThread;
	private ConnectedThread connectedThread;
	
	public static final int STATE_NONE = 1;
	public static final int STATE_LISTENING = 2;
	public static final int STATE_CONNECTING = 3;
	public static final int STATE_ACCEPTING = 4;
	public static final int STATE_CONNECTED = 5;
	
	public EventBTService(Context context, Handler handler) {
		this.handler = handler;
		this.context = context;
		
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	public synchronized void connect(BluetoothDevice btDevice) {
		if (getState() == STATE_CONNECTING) {
			if (connectThread != null) {
				connectThread.cancel();
				connectThread = null;
			}
		}
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}
		
		connectThread = new ConnectThread(btDevice);
		connectThread.start();
		setState(STATE_CONNECTING);
	}
	
	public synchronized void connected(BluetoothSocket btSocket, BluetoothDevice btDevice) {
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}
		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}
		if (acceptThread != null) {
			acceptThread.cancel();
			acceptThread = null;
		}
		
		connectedThread = new ConnectedThread(btSocket);
		connectedThread.start();
		
		Message m = handler.obtainMessage(ShareEvent.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(ShareEvent.DEVICE_NAME, btDevice.getName());
		m.setData(bundle);
		m.sendToTarget();
		setState(STATE_CONNECTED);
		
	}
	
	public synchronized void send(byte[] data) {
		if (state == STATE_CONNECTED) {
			connectedThread.send(data);
		}
	}
	
	public synchronized void startup() {

		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}
		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}
		
		if (acceptThread == null) {
			acceptThread = new AcceptThread();
			acceptThread.start();
		}
		setState(STATE_LISTENING);
		
	}
	
	public synchronized void stop() {
		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}
		if (acceptThread != null) {
			acceptThread.cancel();
			acceptThread = null;
		}

		setState(STATE_NONE);
	}
	
	private synchronized void setState(int state) {
		this.state = state;
	}
	
	public synchronized int getState() {
		return state;
	}
	
	
	private void connectionLost() {
		Message m = handler.obtainMessage(ShareEvent.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(ShareEvent.TOAST, "连接丢失");
		
		m.setData(bundle);
		m.sendToTarget();
		//connection lost so we start again
		startup();		
	}
	
	private void connectionFailed() {
		Message m = handler.obtainMessage(ShareEvent.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(ShareEvent.TOAST, "连接失败");
		
		m.setData(bundle);
		m.sendToTarget();
		
		setState(STATE_LISTENING);
	}

	//this thread is used when trying to connect a remote device
	private class ConnectThread extends Thread {
		private BluetoothDevice btDevice;
		private BluetoothSocket btSocket;
		
		public ConnectThread(BluetoothDevice device) {
			this.btDevice = device;
		}
		
		@Override
		public void run() {
			btAdapter.cancelDiscovery();
			try {
				Message m = handler.obtainMessage(ShareEvent.MESSAGE_TOAST);
				m.getData().putString(ShareEvent.TOAST, "连接中...");
				m.sendToTarget();
				
				btSocket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
				btSocket.connect();
			} catch (IOException e) {
				connectionFailed();
				try {
					btSocket.close();
				} catch (IOException e2) {
					// TODO: handle exception
				}
				EventBTService.this.startup();
				return ;
			}
			
			synchronized (EventBTService.this) {
				connectThread = null;
			}
			
			if (btSocket != null) {
				connected(btSocket, btDevice);
			}
			
		}


		public void cancel() {
			try {
				btSocket.close();
			} catch (IOException e) {
				// TODO: handle exception
			}
		}
		
	}
	
	//this thread runs when a connection has created, and used to 
	//transfer data between devices
	private class ConnectedThread extends Thread {
		private BluetoothSocket btSocket;
		private InputStream inputStream;
		private OutputStream outputStream;
		
		public ConnectedThread (BluetoothSocket bluetoothSocket) {
			this.btSocket = bluetoothSocket;
			
			try {
				inputStream = btSocket.getInputStream();
				outputStream = btSocket.getOutputStream();
			} catch (IOException e) {
				Log.e("EventBTService", "failure in getting input or output stream ", e);
			}
		}
		
		@Override
		public void run() {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			boolean error = false;
			while (true) {
				while (true) {
					try {
						//TODO receive totall xml data
						int length = inputStream.read(data);
						bo.write(data, 0, length);				
						
						if (length < 0) {
							break;
						}
					} catch (IOException e) {
						connectionLost();
						error = true;
						break;
					}
				}
				
				if (error) break;
				//tell the main activity we have received a xml and user can deal with some options
				handler.obtainMessage(ShareEvent.MESSAGE_READ, bo.size(), -1, bo.toByteArray()).sendToTarget();
				

			}
			
		}


		public void send(byte[] data) {
			try {

				outputStream.write(data);
				handler.obtainMessage(ShareEvent.MESSAGE_SEND, -1, -1, data).sendToTarget();
				Message s = handler.obtainMessage(ShareEvent.MESSAGE_DISMISS_PROGRESSBAR);
				s.getData().putString(ShareEvent.PROGRESSBAR_TEXT, "传送完毕");
				s.sendToTarget();
			} catch (Exception e) {
				handler.obtainMessage(ShareEvent.MESSAGE_DISMISS_PROGRESSBAR).sendToTarget();
				Message s = handler.obtainMessage(ShareEvent.MESSAGE_TOAST);
				s.getData().putString(ShareEvent.TOAST, "传输失败");
				s.sendToTarget();
			}
		}
		
		public void cancel() {
			try {
				inputStream.close();
				outputStream.close();
				btSocket.close();
			} catch (IOException e) {
				//do nothing;
			}
		}
	}
	
	//this thread is used to set up a SocketServer waiting for remote connection
	private class AcceptThread extends Thread {
		private BluetoothServerSocket serverSocket;
		private BluetoothSocket btSocket;
		
		public AcceptThread() {
			try {

				serverSocket = btAdapter.listenUsingRfcommWithServiceRecord(
						EventAssistant.TAG, MY_UUID);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		@Override
		public void run() {
			if (serverSocket != null) {
				while (state != STATE_CONNECTED) {
					try {
						btSocket = serverSocket.accept();
					} catch (IOException e) {
						//do something
					}
					if (btSocket != null) {
						synchronized (EventBTService.this) {
							switch (state) {
							case STATE_CONNECTING :
							case STATE_LISTENING :
								connected(btSocket, btSocket.getRemoteDevice());
								Message s = handler.obtainMessage(ShareEvent.MESSAGE_TOAST);
								s.getData().putString(ShareEvent.TOAST, "连接中...");
								s.sendToTarget();
								break;
							case STATE_CONNECTED :
							case STATE_NONE :
								try {
									btSocket.close();
								} catch (IOException e) {
									//Do something
								}
								break;
								
							}
						}
					}
				}
			}
		}
		
		public void cancel() {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
