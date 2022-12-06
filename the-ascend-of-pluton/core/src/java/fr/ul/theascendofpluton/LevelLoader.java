package fr.ul.theascendofpluton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import fr.ul.theascendofpluton.Exceptions.LevelLoadException;
import fr.ul.theascendofpluton.model.*;

import java.util.*;
import java.util.Map;

public class LevelLoader {
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private Joueur joueur;

    public Animation<TextureRegion> flyAnimation;
    private Map<String, Sprite> spriteHashMap;
    private Map<String, Vector2> spriteOffsets;

    private GameWorld gameWorld;

    private static LevelLoader INSTANCE = new LevelLoader();

    private LevelLoader() {

        Texture zomTexture = new Texture(Gdx.files.internal("zombies.png"));
        Sprite zombieSprite = new Sprite(zomTexture, 0, 0, 64, 64);
        zombieSprite.setSize(16, 16);

        Sprite zombieBossSprite = new Sprite(zomTexture, 704, 0, 64, 64);

        Texture appleTexture = new Texture(Gdx.files.internal("apple.png"));
        Sprite appleSprite = new Sprite(appleTexture, 0, 0, 64, 64);
        appleSprite.setSize(16, 16);

        Sprite playerSprite = new Sprite();
        playerSprite.setSize(24, 24);

        Texture portalTexture = new Texture(Gdx.files.internal("portal.png"));
        Sprite portalSprite = new Sprite(portalTexture, 0, 0, 64, 64);
        portalSprite.setSize(32, 32);

        Texture batTexture = new Texture(Gdx.files.internal("bat.png"));
        TextureRegion[][] textureRegions = TextureRegion.split(batTexture, batTexture.getWidth() / 2,
                batTexture.getHeight());
        TextureRegion[] flyFrames = new TextureRegion[2];
        System.arraycopy(textureRegions[0], 0, flyFrames, 0, 2);
        flyAnimation = new Animation<>(0.5f, flyFrames);

        Sprite batSprite = new Sprite();
        batSprite.setSize(16,16);

        spriteOffsets = new HashMap<>();
        spriteHashMap = new HashMap<>();
        spriteHashMap.put(Zombie.class.getSimpleName(), zombieSprite);
        spriteHashMap.put(Apple.class.getSimpleName(), appleSprite);
        spriteHashMap.put(Joueur.class.getSimpleName(), playerSprite);
        spriteHashMap.put("ZombieBoss", zombieBossSprite);
        spriteHashMap.put("Portal", portalSprite);
        spriteHashMap.put(Bat.class.getSimpleName(), batSprite);
    }

    public static LevelLoader getInstance() {
        return INSTANCE;
    }

    /**
     * Charge la TiledMap et créé le renderer
     *
     * @param level_name
     */
    public void load(String level_name) throws LevelLoadException {
        tiledMap = new TmxMapLoader().load("levels/" + level_name + ".tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        World world = new World(new Vector2(), false);
        gameWorld = new GameWorld(world);
        addObjects(world);
        addObstacles(world);
    }

    /**
     *
     * @param vertices représentant un polygone
     * @return le centre du polygone
     */
    private Vector2 getCentroid(float[] vertices) {
        Vector2 centroid = new Vector2();
        return GeometryUtils.polygonCentroid(vertices, 0, vertices.length, centroid);
    }

    /**
     * Ajoute les différents obstacles dans le monde via la tiledMap
     *
     * @param world
     */
    public void addObstacles(World world) {
        for (String layerName : new String[]{"sol", "vide", "puddles"}) {
            TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(layerName);
            for (int i = 0; i < tiledMapTileLayer.getWidth(); i++) {
                for (int j = 0; j < tiledMapTileLayer.getHeight(); j++) {
                    TiledMapTileLayer.Cell cell = tiledMapTileLayer.getCell(i, j);
                    if (cell != null) {
                        Vector2 coords = new Vector2(i * tiledMapTileLayer.getTileWidth(), j * tiledMapTileLayer.getTileHeight());
                        for (PolygonMapObject polygonMapObject : cell.getTile().getObjects().getByType(PolygonMapObject.class)) {
                            float[] verticies = polygonMapObject.getPolygon().getTransformedVertices();
                            switch (layerName) {
                                case "sol":
                                case "vide":
                                    new Obstacle(world, coords, verticies);
                                    break;
                                case "puddles":
                                    new AcidPuddle(world, coords, verticies, 5);
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     *
     * @param world
     * @throws LevelLoadException lorsque le niveau ne remplis pas les critères voulus
     */
    public void addObjects(World world) throws LevelLoadException {
        List<float[]> arrayVerticies = new ArrayList<>();
        List<Vector2> arrayCentroid = new ArrayList<>();
        int countPlayer = 0 , countBosses = 0;
        for (String layerName : new String[]{"Zombies", "Bosses", "Apples", "Joueur"}){
            MapLayer mapLayer = tiledMap.getLayers().get(layerName);
            for (MapObject mapObject : mapLayer.getObjects()) {
                TiledMapTileMapObject tiledMapTileMapObject = (TiledMapTileMapObject) mapObject;
                for (PolygonMapObject polygonMapObject : tiledMapTileMapObject.getTile().getObjects().getByType(PolygonMapObject.class)) {
                    Polygon polygon = polygonMapObject.getPolygon();
                    polygon.setPosition(0, (float) mapObject.getProperties().get("height"));
                    polygon.setScale(tiledMapTileMapObject.getScaleX(), tiledMapTileMapObject.getScaleY());
                    Vector2 centroid = getCentroid(polygon.getTransformedVertices());
                    arrayCentroid.add(centroid.cpy());
                    polygon.translate(-centroid.x, -centroid.y);
                    arrayVerticies.add(polygon.getTransformedVertices());

                }
                Vector2 globalCentroid = arrayCentroid.stream().reduce(new Vector2(), Vector2::add);
                globalCentroid.set(globalCentroid.x / arrayCentroid.size(), globalCentroid.y / arrayCentroid.size());
                if (!spriteOffsets.containsKey(mapObject.getName())) {
                    spriteOffsets.put(mapObject.getName(), globalCentroid.cpy());
                }
                Vector2 coords = globalCentroid.cpy().add(tiledMapTileMapObject.getX(), tiledMapTileMapObject.getY());
                // A modifier si les objets ont plusieurs fixtures.
                switch (layerName){
                    case "Apples":
                        gameWorld.add(new Apple(world, coords, arrayVerticies.get(0), (float)mapLayer.getProperties().get("heal")));
                        break;
                    case "Zombies":
                        gameWorld.add(new Zombie(world, coords, arrayVerticies.get(0)
                                                 , (float) mapLayer.getProperties().get("life")
                                                 , (float) mapLayer.getProperties().get("damage")
                                                 , (float) mapLayer.getProperties().get("monnaie")
                                                )
                                     );
                        break;
                    case "Bosses":
                        switch (mapObject.getName()){
                            case "ZombieBoss":
                                gameWorld.addBoss(new Boss(new Zombie(world, coords, arrayVerticies.get(0)
                                                                 , (float)mapObject.getProperties().get("life")
                                                                 , (float)mapObject.getProperties().get("damage")
                                                                 , (float)mapObject.getProperties().get("monnaie")
                                                                )
                                                          )
                                                 );
                                countBosses++;
                                break;
                            case "BatBoss": // plus tard
                                break;
                            default:
                                throw new LevelLoadException("Objet inconnu: "+ mapObject.getName());
                        }
                        break;
                    case "Joueur":
                        if (joueur == null || joueur.isDead()) {
                            joueur = new Joueur(world, coords, arrayVerticies.get(0)
                                                , (float) mapLayer.getProperties().get("life")
                                                , (float) mapLayer.getProperties().get("strength")
                                                , (float) mapLayer.getProperties().get("range")
                                               );
                        } else {
                            joueur.setTouchPortal(false);
                            joueur.loadInNewWorld(world, coords, arrayVerticies.get(0));
                        }
                        countPlayer++;
                        gameWorld.add(joueur);
                        break;
                }
                arrayVerticies.clear();
                arrayCentroid.clear();
            }
        }
        if (countBosses == 0) {
            throw new LevelLoadException("Le niveau doit contenir au moins un boss");
        }
        if(countPlayer != 1) {
            throw new LevelLoadException("Le niveau doit contenir exactement un joueur");
        }
    }

    public int getLevelWidth() {
        return (int) tiledMap.getProperties().get("width");
    }

    public int getLevelHeight() {
        return (int) tiledMap.getProperties().get("height");
    }

    public void dispose() {
        tiledMap.dispose();
    }

    public TiledMap getMap() {
        return this.tiledMap;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public MapRenderer getRenderer() {
        return tiledMapRenderer;
    }

    public Joueur getPluton() {
        return joueur;
    }
    public Sprite getSprite(String name){
        return spriteHashMap.get(name);
    }
    public Vector2 getSpriteOffset(String name){
        return spriteOffsets.get(name);
    }

}
