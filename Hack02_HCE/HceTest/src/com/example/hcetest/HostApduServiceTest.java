package com.example.hcetest;

import java.util.Arrays;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

public class HostApduServiceTest extends HostApduService {
	private static final String TAG = "HostApduServiceTest";
	private static final byte[] RESPONSE_OK = {
		(byte) 0x90, (byte) 0x00
	};
	private static final byte[] RESPONSE_NG = {
		(byte) 0x00, (byte) 0x00
	};
	private static final byte[] SELECT_CMD = {
		(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00
	};

	@Override
	public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
		byte[] ret = RESPONSE_NG;
		String apduString = "";
		int cmdLength = SELECT_CMD.length;
		byte[] commandApduHeader = new byte[cmdLength];

		for (int i = 0; i < commandApdu.length; i++) {
			apduString += String.format("0x%02x ", commandApdu[i]);
		}
		Log.d(TAG, "commandApdu:" + apduString);

		System.arraycopy(commandApdu, 0, commandApduHeader, 0, cmdLength);
		if (Arrays.equals(commandApduHeader, SELECT_CMD)) {
			ret = RESPONSE_OK;
		}

		return ret;
	}

	@Override
	public void onDeactivated(int reason) {
	}
}