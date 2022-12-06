package fr.ul.theascendofpluton;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import fr.ul.theascendofpluton.view.TitleView;

public class Pluton extends Game {

    private String[] levelNameList = new String[]{"plutonV2"};
    private static int levelIdx;
    public static final float CAMERA_HEIGHT = (32*9)/1.5f;
    public static final float CAMERA_WIDTH = (32*16)/1.5f;
    public static SpriteBatch batch;
    public static BitmapFont font;
    public static BitmapFont finalScreenFont;
    public static AssetManager manager;
    public static Texture positionPoint;

    private static String playerName = "";

    @Override
    public void create() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fillCircle(0, 0, 1);
        positionPoint = new Texture(pixmap);

        FreeTypeFontGenerator fGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Comic_Sans_MS_Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fparams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fparams.size = (Gdx.graphics.getHeight() * 60) / 1024; //28
        fparams.color = new Color(1,1,0, 0.75f);
        fparams.borderColor = Color.BLACK;
        fparams.borderWidth = (Gdx.graphics.getWidth() * 3) / 1024f;
        finalScreenFont = fGen.generateFont(fparams);


        batch = new SpriteBatch();
        font = new BitmapFont();
        manager = new AssetManager();
        manager.load("sounds/hurt.ogg", Music.class);
        manager.load("sounds/game_over.wav", Music.class);
        manager.load("sounds/game_win.wav", Music.class);
        manager.load("sounds/death.ogg", Music.class);
        manager.load("sounds/heal.wav", Music.class);
        manager.load("sounds/wrong.wav", Music.class);
        manager.finishLoading();

        levelIdx = 0;

        setScreen(new TitleView(this));
    }
    @Override
    public void dispose() {
        super.dispose();
        manager.dispose();
        batch.dispose();
        LevelLoader.getInstance().dispose();
    }

    public static String getPlayerName() {
        return playerName;
    }
    public static void setPlayerName(String playerName) {
        Pluton.playerName = playerName;
    }

    public String getCurrentLevel(){
        return levelNameList[levelIdx];
    }

    /**
     * change l'index du niveau de façon cyclique
     */
    public void nextLevel(){
        levelIdx = levelIdx + 1 == levelNameList.length ? 0 : levelIdx + 1;
    }
    public void resetLevel(){
        levelIdx = 0;
    }

    public static int getLevelIdx() {
        return levelIdx;
    }

    public static void setLevelIdx(int levelIdx) {
        Pluton.levelIdx = levelIdx;
    }
}
