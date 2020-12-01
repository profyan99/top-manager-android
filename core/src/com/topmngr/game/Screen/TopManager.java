package com.topmngr.game.Screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.topmngr.game.Utils.Assets;
import com.topmngr.game.Utils.NotificationHandler;
import com.topmngr.game.Utils.Settings;


public class TopManager extends Game {
	public static float screenX;
	public static float screenY;
	public static float VIEWPORT_LEFT;
	static float VIEWPORT_RIGHT;
	public static float viewportWidth;

	static NotificationHandler notificationHandler;
	
	@Override
	public void create () {
		screenX = 1280;
		screenY = 720;
		float aspectRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
		viewportWidth = 720 * aspectRatio;

		VIEWPORT_LEFT = (screenX - viewportWidth) / 2f;
		VIEWPORT_RIGHT = VIEWPORT_LEFT + viewportWidth;
		Assets.instance.load(new AssetManager(new InternalFileHandleResolver()));
		Settings.instance.loadSettings();
		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().show(ScreenEnum.INTRO);
	}

	@Override
	public void dispose() {
		Assets.instance.dispose();
		super.dispose();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		Assets.instance.finishLoading();
		super.resume();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		float aspectRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
		viewportWidth = 720 * aspectRatio;

		VIEWPORT_LEFT = (screenX - viewportWidth) / 2;
		VIEWPORT_RIGHT = VIEWPORT_LEFT + viewportWidth;

	}
	public static void setNotificationHandler(NotificationHandler handler) {
		notificationHandler = handler;
	}
}
