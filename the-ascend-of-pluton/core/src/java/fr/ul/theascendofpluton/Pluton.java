package fr.ul.theascendofpluton;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import fr.ul.theascendofpluton.view.GameView;

public class Pluton extends Game {

    public static final float CAMERA_HEIGHT = (32*9)/1.5f;
    public static final float CAMERA_WIDTH = (32*16)/1.5f;
    public static SpriteBatch batch;
    public static BitmapFont font;
    public static AssetManager manager;
    public static Texture positionPoint;

    @Override
    public void create() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fillCircle(0, 0, 1);
        positionPoint = new Texture(pixmap);

        batch = new SpriteBatch();
        font = new BitmapFont();
        manager = new AssetManager();
        manager.load("sounds/hurt.ogg", Music.class);
        manager.load("sounds/game_over.wav", Music.class);
        manager.load("sounds/death.ogg", Music.class);
        manager.load("sounds/heal.wav", Music.class);
        manager.finishLoading();

        setScreen(new GameView(this));
    }
    @Override
    public void dispose() {
        super.dispose();
        manager.dispose();
        batch.dispose();
        LevelLoader.getInstance().dispose();
    }
}
