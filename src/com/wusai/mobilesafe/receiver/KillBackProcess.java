package com.wusai.mobilesafe.receiver;

import com.wusai.mobilesafe.utils.ProcessUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillBackProcess extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ProcessUtil.killAllProcess(context);
	}

}
