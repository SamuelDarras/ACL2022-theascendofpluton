package fr.ul.theascendofpluton;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import fr.ul.theascendofpluton.view.GameView;

public class Pluton extends Game {

    public static final float CAMERA_HEIGHT = (32*9)/1.5f;
    public static final float CAMERA_WIDTH = (32*16)/1.5f;
    public SpriteBatch batch;
    public static AssetManager manager;

    @Override
    public void create() {
        batch = new SpriteBatch();

        manager = new AssetManager();
        manager.load("sounds/hurt.ogg", Music.class);
        manager.load("sounds/game_over.wav", Music.class);
        manager.load("sounds/death.ogg", Music.class);
        manager.finishLoading();

        setScreen(new GameView(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        manager.dispose();
        batch.dispose();
    }
}
