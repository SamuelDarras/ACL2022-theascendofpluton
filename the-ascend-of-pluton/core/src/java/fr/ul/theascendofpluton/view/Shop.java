package fr.ul.theascendofpluton.view;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import fr.ul.theascendofpluton.Pluton;

public class Shop {
    public static SpriteBatch batch;
    public static BitmapFont font;
    private OrthographicCamera camera;

    private boolean show = false;
    private final Texture textureVie;
    private final Texture textureCoin;
    private final Texture textureStrength;
    private final Texture textureShop;

    private float MENU_WIDTH;
    private float MENU_HEIGHT;

    public Shop(float screenWidth, float screenHeight) {

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.position.set(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f, 0);

        MENU_WIDTH = Gdx.graphics.getWidth()/2f;
        MENU_HEIGHT = Gdx.graphics.getHeight()/2f;

        batch = new SpriteBatch();
        font = new BitmapFont();
        textureVie = new Texture(Gdx.files.internal("vie.png"));
        textureCoin = new Texture(Gdx.files.internal("coin.png"));
        textureStrength = new Texture(Gdx.files.internal("sword.png"));
        textureShop = new Texture(Gdx.files.internal("shop.png"));
    }

    public void toggle() {
        show = !show;
    }

    public void render() {
        if (show) {
            batch.begin();
                batch.draw(textureShop    , camera.position.x - MENU_WIDTH/2f, camera.position.y - MENU_HEIGHT/2f, MENU_WIDTH, MENU_HEIGHT);
                batch.draw(textureVie     , camera.position.x - MENU_WIDTH/2f + textureVie.getWidth()/4f     , camera.position.y - MENU_HEIGHT/2f + (1.5f * textureVie.getHeight()/4f)     ,textureVie.getWidth()/4f     ,textureVie.getHeight()/4f);
                batch.draw(textureStrength, camera.position.x + MENU_WIDTH/2f - textureStrength.getWidth()/4f, camera.position.y - MENU_HEIGHT/2f + (1.3f * textureStrength.getHeight()/6f),textureStrength.getWidth()/6f,textureStrength.getHeight()/6f);

                Pluton.font.draw(batch, "Magasin", camera.position.x - 20, camera.position.y + MENU_HEIGHT/3f);

                Pluton.font.draw(batch, "+ 10 Vies max", camera.position.x - MENU_WIDTH/2 + textureVie.getWidth()/4f      , camera.position.y - MENU_HEIGHT/2f + textureVie.getHeight()/3f);
                Pluton.font.draw(batch, "Prix : 5"     , camera.position.x - MENU_WIDTH/2 + textureVie.getWidth()/4f      , camera.position.y - MENU_HEIGHT/2f + textureVie.getHeight()/3f - (Pluton.font.getCapHeight()*2));
                Pluton.font.draw(batch, "[ I ]"        , camera.position.x - MENU_WIDTH/2 + textureVie.getWidth()/4f + 30 , camera.position.y - MENU_HEIGHT/2f + textureVie.getHeight()/3f - (Pluton.font.getCapHeight()*4));

                Pluton.font.draw(batch, "+ 1 Strengh", camera.position.x + MENU_WIDTH/2 - textureStrength.getWidth()/4f     , camera.position.y - MENU_HEIGHT/2f + textureStrength.getHeight()/5f);
                Pluton.font.draw(batch, "Prix : 10"  , camera.position.x + MENU_WIDTH/2 - textureStrength.getWidth()/4f     , camera.position.y - MENU_HEIGHT/2f + textureStrength.getHeight()/5f - (Pluton.font.getCapHeight()*2));
                Pluton.font.draw(batch, "[ O ]"      , camera.position.x + MENU_WIDTH/2 - textureStrength.getWidth()/4f + 30, camera.position.y - MENU_HEIGHT/2f + textureStrength.getHeight()/5f - (Pluton.font.getCapHeight()*4));
            batch.end();
        }

    }

}
