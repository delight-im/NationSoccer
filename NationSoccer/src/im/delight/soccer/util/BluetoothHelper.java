package im.delight.soccer.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

public class BluetoothHelper {
	
	// CLASS CONSTANTS BEGIN
	/** Unique request code that will be delivered to Activity.onActivityResult() after Bluetooth has been enabled (or request has been cancelled) */
	public static final int REQUEST_ENABLE_BLUETOOTH = 379242304;
	/** Unique request code that will be delivered to Activity.onActivityResult() after device discovery has been enabled (or request has been cancelled) */
	public static final int REQUEST_ENABLE_DISCOVERY = 379242305;
	/** Determines how long the device should be made visible to other devices for discovery (in seconds) */
	public static final int DISCOVERABILITY_DURATION = 600;
	/** Charset that is used throughout the whole Bluetooth connection for reading and writing */
	public static final String CHARSET = "utf-8";
	// CLASS CONSTANTS END
	// BLUETOOTH HELPER DATA BEGIN
	private static BluetoothHelper mInstance;
	private final UUID mAppUUID;
	private final String mAppName;
	private final BluetoothAdapter mBluetoothAdapter;
	private BluetoothCallback mCallback;
	private Set<BluetoothDevice> mDiscoveryDevices;
	private Activity mActivity;
	// BLUETOOTH HELPER DATA END
	// SERVICES BEGIN
	private ConnectionManager mConnectionManager;
	private ReceiveIncomingConnection mReceiveIncomingConnection;
	private SendOutgoingConnection mSendOutgoingConnection;
	// SERVICES END
	// BROADCAST RECEIVERS BEGIN
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        final String action = intent.getAction();
	        if (action != null) {
		        if (action.equals(BluetoothDevice.ACTION_FOUND)) { // discovery has found a new device
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // get device object from Intent
					if (mCallback == null) {
						throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
					}
					else {
						mDiscoveryDevices.add(device);
					}
		        }
		        else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) { // discovery has started
		        	mDiscoveryDevices = new HashSet<BluetoothDevice>();
		        }
		        else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) { // discovery has started
		        	selectDeviceToConnectTo(mDiscoveryDevices, mActivity);
		        }
	        }
	    }
	};
	// BROADCAST RECEIVERS END
	
	public static interface BluetoothCallback {
		/** Callback that delivers new messages from the remote client to this device (always called on the UI thread) */
		public void onMessage(String message);
		public void onError(Exception exception);
		/** Callback indicating that Bluetooth has successfully connected to the remote client (always called on the UI thread) */
		public void onConnected();
	}
	
	public class ReceiveIncomingConnection extends Thread {

	    private BluetoothServerSocket mServerSocket;
	 
	    public ReceiveIncomingConnection() {
	        try {
	        	mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(mAppName, mAppUUID);
	        }
	        catch (Exception e) {
	        	mServerSocket = null;
				if (mCallback == null) {
					throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
				}
				else {
					mCallback.onError(e);
				}
	        }
	    }
	 
	    public void run() {
	        BluetoothSocket socket = null;
	        while (true) { // keep listening for incoming connections until exception or cancelled or socket is returned successfully
	            try {
	                socket = mServerSocket.accept();
	            }
	            catch (Exception e) {
					if (mCallback == null) {
						throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
					}
					else {
						mCallback.onError(e);
					}
	                break; // stop listening for incoming connections
	            }
	            if (socket != null) { // if connection has been accepted
			        mConnectionManager = new ConnectionManager(socket);
			        mConnectionManager.start();
	                try {
						mServerSocket.close(); // done listening for incoming connections so close the server socket
					}
	                catch (Exception e) {
						if (mCallback == null) {
							throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
						}
						else {
							mCallback.onError(e);
						}
					}
	                break; // stop listening for incoming connections
	            }
	        }
	    }

	    public void cancel() {
	        try {
	            mServerSocket.close();
	        }
	        catch (Exception e) {
				if (mCallback == null) {
					throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
				}
				else {
					mCallback.onError(e);
				}
	        }
	    }
	}
	
	public class SendOutgoingConnection extends Thread {
	    private BluetoothSocket mSocket;
	 
	    public SendOutgoingConnection(BluetoothDevice device) {
	        try {
	        	mSocket = device.createRfcommSocketToServiceRecord(mAppUUID);
	        }
	        catch (Exception e) {
	        	mSocket = null;
				if (mCallback == null) {
					throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
				}
				else {
					mCallback.onError(e);
				}
	        }
	    }
	 
	    public void run() {
	        mBluetoothAdapter.cancelDiscovery();
	        try {
	            mSocket.connect();
		        mConnectionManager = new ConnectionManager(mSocket);
		        mConnectionManager.start();
	        }
	        catch (Exception e) {
				if (mCallback == null) {
					throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
				}
				else {
					mCallback.onError(e);
				}
	            try {
	                mSocket.close();
	            }
	            catch (Exception eClose) { }
	        }
	    }

	    public void cancel() {
	        try {
	            mSocket.close();
	        }
	        catch (Exception e) {
				if (mCallback == null) {
					throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
				}
				else {
					mCallback.onError(e);
				}
	        }
	    }
	}
	
	public class ConnectionManager extends Thread {

	    private BluetoothSocket mSocket;
	    private InputStream mInputStream;
	    private OutputStream mOutputStream;
	 
	    public ConnectionManager(BluetoothSocket socket) {
	        mSocket = socket;
	        try {
	        	mInputStream = socket.getInputStream();
	        	mOutputStream = socket.getOutputStream();
				if (mCallback == null) {
					throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
				}
				else {
					mActivity.runOnUiThread(new Runnable() {
						public void run() {
							mCallback.onConnected();
						}
					});
				}
	        }
	        catch (Exception e) {
	        	mInputStream = null;
	        	mOutputStream = null;
				if (mCallback == null) {
					throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
				}
				else {
					mCallback.onError(e);
				}
	        }
	    }
	 
	    public void run() {
	        final byte[] buffer = new byte[1024]; // stream buffer
	        while (true) { // keep listening for incoming data until exception or cancelled
	            try {
	            	final int readBytes = mInputStream.read(buffer); // read from input stream
					if (mCallback == null) {
						throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
					}
					else {
						mActivity.runOnUiThread(new Runnable() {
							public void run() {
								mCallback.onMessage(new String(buffer, 0, readBytes));
							}
						});
					}
	            }
	            catch (Exception e) {
					if (mCallback == null) {
						throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
					}
					else {
						mCallback.onError(e);
					}
	                break;
	            }
	        }
	    }
	    
	    public void send(String message) {
	    	try {
				write(message.getBytes(CHARSET));
			}
	    	catch (UnsupportedEncodingException e) {
				if (mCallback == null) {
					throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
				}
				else {
					mCallback.onError(e);
				}
			}
	    }
	 
	    private void write(byte[] bytes) {
	        try {
	            mOutputStream.write(bytes);
	        }
	        catch (Exception e) {
				if (mCallback == null) {
					throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
				}
				else {
					mCallback.onError(e);
				}
	        }
	    }

	    public void cancel() {
	        try {
	            mSocket.close();
	        }
	        catch (Exception e) {
				if (mCallback == null) {
					throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
				}
				else {
					mCallback.onError(e);
				}
	        }
	    }
	}
	
	public static BluetoothHelper getInstance(String appUUID, String appName) {
		if (mInstance == null) {
			mInstance = new BluetoothHelper(appUUID, appName);
		}
		return mInstance;
	}
	
	public synchronized void setCallback(BluetoothCallback callback) {
		mCallback = callback;
	}
	
	private BluetoothHelper(String appUUID, String appName) {
		mAppUUID = UUID.fromString(appUUID);
		mAppName = appName;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	public synchronized boolean isBluetoothSupported() {
		return mBluetoothAdapter != null;
	}
	
	public synchronized boolean isBluetoothEnabled() {
		return mBluetoothAdapter.isEnabled();
	}
	
	public synchronized void enableBluetooth(Activity activity) {
		if (activity == null) {
			throw new RuntimeException("You may not pass an Activity reference that is null");
		}
		else {
			mActivity = activity;
			activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BLUETOOTH);
		}
	}
	
	public synchronized void enableDiscovery(Activity activity) {
		if (activity == null) {
			throw new RuntimeException("You may not pass an Activity reference that is null");
		}
		else {
			mActivity = activity;
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABILITY_DURATION);
			activity.startActivityForResult(intent, REQUEST_ENABLE_DISCOVERY);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data, Activity activity) {
		if (activity == null) {
			throw new RuntimeException("You may not pass an Activity reference that is null");
		}
		else {
			mActivity = activity;
			if (requestCode == BluetoothHelper.REQUEST_ENABLE_BLUETOOTH) {
				if (resultCode == Activity.RESULT_OK) {
					discoverOrChoosePaired(activity);
				}
				else {
					Toast.makeText(activity, "Bluetooth has not been enabled", Toast.LENGTH_SHORT).show();
				}
			}
			else if (requestCode == BluetoothHelper.REQUEST_ENABLE_DISCOVERY) {
				if (resultCode == Activity.RESULT_OK) {
					startDeviceDiscovery(activity);
				}
				else {
					Toast.makeText(activity, "Discovery has not been enabled", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	public void init(Activity activity) {
		if (activity == null) {
			throw new RuntimeException("You may not pass an Activity reference that is null");
		}
		else {
			mActivity = activity;
			if (isBluetoothEnabled()) {
				discoverOrChoosePaired(activity);
			}
			else {
				enableBluetooth(activity);
			}
		}
	}
	
	private AlertDialog discoverOrChoosePaired(final Activity activity) {
		if (activity == null) {
			throw new RuntimeException("You may not pass an Activity reference that is null");
		}
		else {
			mActivity = activity;
			final CharSequence[] options = new CharSequence[] { "Choose paired device", "Search for devices" };
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
			dialogBuilder.setTitle("What to do?");
			dialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
						startPairedDeviceSelection(activity);
					}
					else if (which == 1) {
						enableDiscovery(activity);
					}
				}
			});
			dialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) { }
			});
			return dialogBuilder.show();
		}
	}
	
	public synchronized AlertDialog startPairedDeviceSelection(Activity activity) {
		if (activity == null) {
			throw new RuntimeException("You may not pass an Activity reference that is null");
		}
		else {
			mActivity = activity;
			return selectDeviceToConnectTo(mBluetoothAdapter.getBondedDevices(), activity);
		}
	}
	
	private AlertDialog selectDeviceToConnectTo(Set<BluetoothDevice> devices, Activity activity) {
		if (activity == null) {
			throw new RuntimeException("You may not pass an Activity reference that is null");
		}
		else {
			mActivity = activity;
			final int deviceCount = devices.size();
			if (deviceCount > 0) {
				final BluetoothDevice[] deviceObjects = new BluetoothDevice[deviceCount];
				final CharSequence[] deviceNames = new CharSequence[deviceCount];
				int counter = 0;
			    for (BluetoothDevice device : devices) { // loop through all devices
			    	deviceObjects[counter] = device;
			    	deviceNames[counter] = device.getName();
			    }
			    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
			    dialogBuilder.setTitle("Select paired device");
			    dialogBuilder.setItems(deviceNames, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (dialog != null) {
							dialog.dismiss();
						}
						if (mCallback == null) {
							throw new RuntimeException("BluetoothCallback must not be null when events are dispatched");
						}
						else {
							startListening();
							startConnecting(deviceObjects[which]);
						}
					}
				});
			    dialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (dialog != null) {
							dialog.dismiss();
						}
					}
				});
			    return dialogBuilder.show();
			}
			else {
				Toast.makeText(activity, "There are no paired devices available right now!", Toast.LENGTH_SHORT).show();
				return null;
			}
		}
	}

	public synchronized void startDeviceDiscovery(Activity activity) {
		if (activity == null) {
			throw new RuntimeException("You may not pass an Activity reference that is null");
		}
		else {
			mActivity = activity;
			activity.registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
			activity.registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
			activity.registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		}
		stopDeviceDiscovery(); // stop any discovery that might still be running
		mBluetoothAdapter.startDiscovery(); // start device discovery
	}
	
	public synchronized void stopDeviceDiscovery() {
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}
	}
	
	public synchronized void startListening() {
		if (mConnectionManager != null) {
			mConnectionManager.cancel();
			mConnectionManager = null;
		}
		mReceiveIncomingConnection = new ReceiveIncomingConnection();
		mReceiveIncomingConnection.start();
	}
	
	public synchronized void startConnecting(BluetoothDevice connectToDevice) {
		if (mConnectionManager != null) {
			mConnectionManager.cancel();
			mConnectionManager = null;
		}
		mSendOutgoingConnection = new SendOutgoingConnection(connectToDevice);
		mSendOutgoingConnection.start();
	}
	
	public void stop(Activity activity) {
		if (mConnectionManager != null) {
			mConnectionManager.cancel();
			mConnectionManager = null;
		}
		if (mSendOutgoingConnection != null) {
			mSendOutgoingConnection.cancel();
			mSendOutgoingConnection = null;
		}
		if (mReceiveIncomingConnection != null) {
			mReceiveIncomingConnection.cancel();
			mReceiveIncomingConnection = null;
		}
		stopDeviceDiscovery();
		if (activity == null) {
			throw new RuntimeException("You may not pass an Activity reference that is null");
		}
		else {
			try {
				activity.unregisterReceiver(mBroadcastReceiver);
			}
			catch (Exception e) { }
			mActivity = null;
		}
	}

}
