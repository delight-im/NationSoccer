package im.delight.soccer;

import im.delight.soccer.util.BluetoothHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothScreen extends Activity implements BluetoothHelper.BluetoothCallback {
	
	private static final String APP_UUID = "3b8e70f4-4c31-44f1-b66b-47a08af1781d";
	private BluetoothHelper mBluetoothHelper;
	private EditText mNewMessage;
	private Button mButtonSend;
	private TextView mMessageLog;
	private AlertDialog mAlertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth);
		mMessageLog = (TextView) findViewById(R.id.message_log);
		mNewMessage = (EditText) findViewById(R.id.new_message);
		mButtonSend = (Button) findViewById(R.id.button_send);
		mButtonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mNewMessage.getText().toString(); // XXX send this message
			}
		});
		mNewMessage.setEnabled(false);

		mBluetoothHelper = BluetoothHelper.getInstance(APP_UUID, getString(R.string.app_name));
		mBluetoothHelper.setCallback(this);
		if (mBluetoothHelper.isBluetoothSupported()) {
			mBluetoothHelper.init(this);
		}
		else {
			mButtonSend.setEnabled(false);
			mMessageLog.setText("Bluetooth is not supported by your device!");
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mBluetoothHelper.onActivityResult(requestCode, resultCode, data, this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mAlertDialog != null) {
			mAlertDialog.dismiss();
			mAlertDialog = null;
		}
		mBluetoothHelper.stop(this);
	}

	@Override
	public void onMessage(String message) {
		mMessageLog.setText(message+"\n"+mMessageLog.getText().toString());
	}

	@Override
	public void onError(Exception exception) {
		exception.printStackTrace();
		Toast.makeText(this, (exception.getMessage() == null ? "Error" : exception.getMessage()), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnected() {
		mButtonSend.setEnabled(true);
	}

}
