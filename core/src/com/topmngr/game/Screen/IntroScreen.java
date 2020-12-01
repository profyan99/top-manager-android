package com.topmngr.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.topmngr.game.Utils.Assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.topmngr.game.Screen.TopManager.*;


class IntroScreen implements Screen {
    private Texture logo;
    private Stage stage;
    private Table loadingTable;
    private Image loadingImg;

    IntroScreen() {

    }
    @Override
    public void show() {
        OrthographicCamera camera = new OrthographicCamera(screenX, screenY);
        camera.position.set(new Vector3(screenX/2,screenY/2,0));
        Viewport viewport = new FillViewport(screenX, screenY, camera);

        stage = new Stage(viewport);
        logo = Assets.instance.loadTexture("logo.png");

        loadingTable = new Table();
        loadingTable.setSize(viewportWidth,screenY);
        loadingTable.setPosition(VIEWPORT_LEFT,0);


        if(Assets.instance.isLoaded("mainUI.pack", TextureAtlas.class)) {
            loadingImg = new Image(Assets.instance.bgloading);
            loadingImg.setOrigin(64,64);
            loadingTable.add(loadingImg).width(128).height(128).center();
        }
        loadingTable.addAction(alpha(0));

        Image logoImage = new Image(logo);
        logoImage.setPosition(screenX/2 - logoImage.getHeight()/2, screenY/2 - logoImage.getHeight()/2);

        logoImage.addAction(sequence(alpha(0),fadeIn(1f), delay(1f), parallel(fadeOut(1f), run(new Runnable() {
            @Override
            public void run() {
                goToLoading();
            }
        }))));

        stage.addActor(logoImage);
        stage.addActor(loadingTable);
    }
    private void goToLoading() {
        loadingImg.addAction(forever(rotateBy(-90,0.5f)));
        loadingTable.addAction(sequence(fadeIn(1f), delay(2f), run(new Runnable() {
            @Override
            public void run() {
                ScreenManager.getInstance().show(ScreenEnum.MENU);
            }
        })));
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        logo.dispose();
        stage.dispose();
    }
}
