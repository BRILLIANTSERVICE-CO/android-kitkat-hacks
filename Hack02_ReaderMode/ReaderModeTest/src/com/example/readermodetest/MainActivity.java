package com.example.readermodetest;

import java.io.IOException;

import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.ReaderCallback;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity implements ReaderCallback {

	private static final String TAG = "ReaderModeTest";
	NfcAdapter mNfcAdapter;

	private static final byte[] TARGET_AID = {
		(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04,
		(byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
	};

	private static final byte[] SELECT_CMD = {
		(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter != null) {
			mNfcAdapter.enableReaderMode(this, this,
					NfcAdapter.FLAG_READER_NFC_A
							| NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mNfcAdapter != null) {
			mNfcAdapter.disableReaderMode(this);
		}
	}

	@Override
	public void onTagDiscovered(Tag tag) {
		int cmdLength = SELECT_CMD.length;
		int aidLength = TARGET_AID.length;
		byte[] selectCmd = new byte[cmdLength + aidLength + 1];
		byte[] responseCmd;
		String responseString = "";

		IsoDep isoDep = IsoDep.get(tag);
		if (isoDep != null) {
			try {
				isoDep.connect();

				System.arraycopy(SELECT_CMD, 0, selectCmd, 0, cmdLength);
				selectCmd[cmdLength] = (byte) aidLength;
				System.arraycopy(TARGET_AID, 0, selectCmd, cmdLength + 1,
						aidLength);
				responseCmd = isoDep.transceive(selectCmd);

				for (int i = 0; i < responseCmd.length; i++) {
					responseString += String.format("0x%02x ", responseCmd[i]);
				}
				Log.d(TAG, "responseCmd:" + responseString);

				isoDep.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
