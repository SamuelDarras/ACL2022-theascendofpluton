package fr.ul.theascendofpluton.view;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;

import fr.ul.theascendofpluton.listener.PlayerContactListener;
import fr.ul.theascendofpluton.listener.PlayerControlListener;
import fr.ul.theascendofpluton.model.Zombie;
import fr.ul.theascendofpluton.model.Joueur;
import fr.ul.theascendofpluton.LevelLoader;

public class GameView extends ScreenAdapter {
    private final float CAMERA_HEIGHT = (32*9)/1.5f;
    private final float CAMERA_WIDTH = (32*16)/1.5f;

    Viewport vp;
    OrthographicCamera camera;

    SpriteBatch batch;
    BitmapFont font;
    Texture plutonTexture;
    Sprite plutonSprite;

    Box2DDebugRenderer renderer;
    World world;

    LevelLoader levelLoader;

    Joueur joueur;
    MapObject mapObjectPluton;
    PlayerControlListener c;
    PlayerContactListener contactListener;

    MapObjects zombiesMo;
    Set<Zombie> zombies;

    MiniMap map;

    public GameView() {
        super();

        levelLoader = new LevelLoader(this);
        levelLoader.load("pluton");

        map = new MiniMap(levelLoader.getMap(), levelLoader.getPluton());

        mapObjectPluton = levelLoader.getPluton();
        world = new World(new Vector2(0f, 0f), true);

        levelLoader.addObstacles(world);
        zombies = levelLoader.addZombies(world);

        joueur = new Joueur(world);
        joueur.register((float) mapObjectPluton.getProperties().get("x"),
                (float) mapObjectPluton.getProperties().get("y"),
                (float) mapObjectPluton.getProperties().get("vie"),
                (float) mapObjectPluton.getProperties().get("monnaie"));

        camera = new OrthographicCamera();
        camera.position.x = joueur.getPosition().x;
        camera.position.y = joueur.getPosition().y;

        vp = new FitViewport(CAMERA_WIDTH, CAMERA_HEIGHT, camera);
        vp.apply();

        renderer = new Box2DDebugRenderer();

        batch = new SpriteBatch();
        font = new BitmapFont();
        plutonTexture = new Texture(Gdx.files.internal("pluton.png"));
        plutonSprite = new Sprite(plutonTexture, 0, 0, 32, 32);

        c = new PlayerControlListener(joueur);
        Gdx.input.setInputProcessor(c);
        contactListener = new PlayerContactListener();
        world.setContactListener(contactListener);


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update();
        batch.setProjectionMatrix(camera.combined);

        levelLoader.getRenderer().setView(camera);
        levelLoader.getRenderer().render();

        batch.begin();
        showStats();
        plutonSprite.draw(batch);
            for (Zombie zombie : zombies) {
                Sprite s = levelLoader.spriteHashMap.get("zombie");
                s.setPosition(zombie.getPosition().x - s.getWidth()/2, zombie.getPosition().y - s.getHeight()/2);
                s.draw(batch);
                zombie.update(joueur.getPosition().x, joueur.getPosition().y);
            }
        batch.end();

        renderer.render(world, camera.combined);
        
        map.render();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, levelLoader.getLevelWidth(), levelLoader.getLevelHeight());
        if(joueur == null){
            camera.position.set((float) mapObjectPluton.getProperties().get("x"), (float) mapObjectPluton.getProperties().get("y"), 0);
        }
        else{
            camera.position.set(joueur.getPosition().x,joueur.getPosition().y, 0);
        }
        camera.update();

        levelLoader.getRenderer().setView(camera);

        vp.update(width, height, true);
    }

    @Override
    public void dispose() {
        renderer.dispose();
        world.dispose();
    }

    private void update() {
        plutonSprite.setPosition(joueur.getPosition().x - 16 , joueur.getPosition().y - 16);

        if (!(joueur.getPosition().x + CAMERA_WIDTH/2 > levelLoader.getLevelWidth() * 32 || joueur.getPosition().x - CAMERA_WIDTH/2 < 0))
            camera.position.x = joueur.getPosition().x;

        if (!(joueur.getPosition().y + CAMERA_HEIGHT/2 > levelLoader.getLevelHeight() * 32 || joueur.getPosition().y - CAMERA_HEIGHT/2 < 0))
            camera.position.y = joueur.getPosition().y;

        world.step(Gdx.graphics.getDeltaTime(), 2, 2);
        joueur.update();

        map.update(joueur.getPosition().x, joueur.getPosition().y, camera.viewportWidth, camera.viewportHeight);

        camera.update();
    }

    public void setToDestroy(Zombie zombie) {
        zombie.dispose();
        //À la mort du zombie, le joueur gagne de l'argent
        joueur.receiveMoney(zombie.getMoney());
        System.out.println(joueur.getMoney());
        this.zombies.remove(zombie);
    }

    public void showStats(){

        font.draw(batch, String.valueOf("Vie :"+joueur.getLife()), camera.position.x-168, camera.position.y-72);
        font.draw(batch, String.valueOf("Force :"+joueur.getStrength()), camera.position.x-168, camera.position.y-80);
        font.draw(batch, String.valueOf("Monnaie :"+joueur.getMoney()), camera.position.x-168, camera.position.y-88);

        //batch = new SpriteBatch();
        //
        font.getData().setScale(0.4f, 0.4f);
    }
}