package com.topmngr.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.topmngr.game.Screen.TopManager;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useWakelock = true;

		AdapterAndroid adapterAndroid = new AdapterAndroid(this);
		TopManager.setNotificationHandler(adapterAndroid);

		initialize(new TopManager(), config);
	}
}
