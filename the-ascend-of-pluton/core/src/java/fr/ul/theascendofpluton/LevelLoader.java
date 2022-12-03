package fr.ul.theascendofpluton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.io.Input;

import fr.ul.theascendofpluton.model.*;
import fr.ul.theascendofpluton.view.GameView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map;

public class LevelLoader {
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private Joueur joueur;
    public Map<String, Sprite> spriteHashMap;
    public Map<String, Vector2> spriteOffsets;

    private GameWorld gameWorld;

    private static LevelLoader INSTANCE = new LevelLoader();

    private LevelLoader() {

        Texture zomTexture = new Texture(Gdx.files.internal("zombies.png"));
        Sprite zombieSprite = new Sprite(zomTexture, 0, 0, 64, 64);
        zombieSprite.setSize(16,16);

        Texture appleTexture = new Texture(Gdx.files.internal("apple.png"));
        Sprite appleSprite = new Sprite(appleTexture,0,0,64,64);
        appleSprite.setSize(16,16);

        Sprite playerSprite = new Sprite();
        playerSprite.setSize(24, 24);

        spriteOffsets = new HashMap<>();
        spriteHashMap = new HashMap<>();
        spriteHashMap.put(Zombie.class.getSimpleName(), zombieSprite);
        spriteHashMap.put(Apple.class.getSimpleName(), appleSprite);
        spriteHashMap.put(Joueur.class.getSimpleName(), playerSprite);
    }

    public static LevelLoader getInstance() {
        return INSTANCE;
    }

    /**
     * Charge la TiledMap et créé le renderer
     * @param level_name
     */
    public void load(String level_name){
        tiledMap = new TmxMapLoader().load("levels/" + level_name + ".tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        World world = new World(new Vector2(), false);
        gameWorld = new GameWorld(world);
        addObjects(world);
        addObstacles(world);
    }

    public MapRenderer getRenderer() {
        return tiledMapRenderer;
    }

    public Joueur getPluton() {
        return joueur;
    }

    public Vector2 getCentroid (float[] vertices) {
        Vector2 centroid = new Vector2();
        return GeometryUtils.polygonCentroid(vertices, 0, vertices.length, centroid);
    }

    private Map<Vector2, List<float[]>> getPolygones(MapLayer mapLayer){
        Map<Vector2, List<float[]>> map = new HashMap<>();
        if(mapLayer instanceof TiledMapTileLayer){
            TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) mapLayer;
            for (int i = 0; i < tiledMapTileLayer.getWidth(); i++) {
                for (int j = 0; j < tiledMapTileLayer.getHeight(); j++) {
                    TiledMapTileLayer.Cell cell = tiledMapTileLayer.getCell(i, j);
                    if (cell != null){
                        List<float[]> arrayVerticies = new ArrayList<>();
                        for (PolygonMapObject polygonMapObject : cell.getTile().getObjects().getByType(PolygonMapObject.class)){
                            arrayVerticies.add(polygonMapObject.getPolygon().getTransformedVertices());
                        }
                        map.put(new Vector2(i * tiledMapTileLayer.getTileWidth(), j * tiledMapTileLayer.getTileHeight()), new ArrayList<>(arrayVerticies));
                    }
                }
            }
        }
        else {
            List<float[]> arrayVerticies = new ArrayList<>();
            List<Vector2> arrayCentroid = new ArrayList<>();
            for(MapObject mapObject : mapLayer.getObjects()){
                TiledMapTileMapObject tiledMapTileMapObject = (TiledMapTileMapObject) mapObject;
                for(PolygonMapObject polygonMapObject: tiledMapTileMapObject.getTile().getObjects().getByType(PolygonMapObject.class)){
                    Polygon polygon = polygonMapObject.getPolygon();
                    polygon.setPosition(0,(float)mapObject.getProperties().get("height"));
                    polygon.setScale(tiledMapTileMapObject.getScaleX(), tiledMapTileMapObject.getScaleY());
                    Vector2 centroid = getCentroid(polygon.getTransformedVertices());
                    arrayCentroid.add(centroid.cpy());
                    polygon.translate(-centroid.x, -centroid.y);
                    arrayVerticies.add(polygon.getTransformedVertices());

                }
                Vector2 globalCentroid = arrayCentroid.stream().reduce(new Vector2(), Vector2::add);
                globalCentroid.set(globalCentroid.x/arrayCentroid.size(), globalCentroid.y/arrayCentroid.size());
                if(!spriteOffsets.containsKey(mapLayer.getName())){
                    spriteOffsets.put(mapLayer.getName(), globalCentroid.cpy());
                }
                Vector2 coords = globalCentroid.cpy().add(tiledMapTileMapObject.getX(), tiledMapTileMapObject.getY());
                map.put(coords, new ArrayList<>(arrayVerticies));
                arrayVerticies.clear();
                arrayCentroid.clear();
            }
        }
        return map;
    }

    /**
     * Ajoute les différents obstacles dans le monde via la tiledMap
     * @param world
     */
    public void addObstacles(World world){
        Map<Vector2, List<float[]>> mapObstacles = new HashMap<>();
        Map<Vector2, List<float[]>> mapPuddles = new HashMap<>();
        mapObstacles.putAll(getPolygones(tiledMap.getLayers().get("sol")));
        mapObstacles.putAll(getPolygones(tiledMap.getLayers().get("vide")));
        mapPuddles.putAll(getPolygones(tiledMap.getLayers().get("puddles")));
        mapObstacles.forEach((key, value)->{
            for(float[] polygonVerticies : value){
                new Obstacle(world, key, polygonVerticies);
            }
        });

        mapPuddles.forEach((key, value)->{
            for(float[] verticies : value){
                new AcidPuddle(world, key, verticies, 5);
            }
        });
    }

    /**
     * Ajoute au monde les zombies présents dans la tiledMap
     * @param world
     * @return le set contenant les zombies ajoutés au monde.
     */
    public void addObjects(World world){
        MapLayer mapLayerZombies = tiledMap.getLayers().get("Zombie");
        float life = (float)mapLayerZombies.getProperties().get("life");
        float damage = (float)mapLayerZombies.getProperties().get("damage");
        float monnaie = (float)mapLayerZombies.getProperties().get("monnaie");
        Map<Vector2, List<float[]>> mapZombies = new HashMap<>(getPolygones(mapLayerZombies));
        mapZombies.forEach((coords, polygons)->{
            for (float[] polygonVerticies : polygons) {
                gameWorld.add(new Zombie(world, coords, spriteOffsets.get(Zombie.class.getSimpleName()), polygonVerticies, life, damage, monnaie));
            }
        });

        MapLayer mapLayerApples = tiledMap.getLayers().get("Apple");
        float heal = (float)mapLayerApples.getProperties().get("heal");
        Map<Vector2, List<float[]>> mapApples = new HashMap<>(getPolygones(mapLayerApples));
        mapApples.forEach((coords, value)->{
            for(float[] polygonVerticies : value){
                gameWorld.add(new Apple(world, coords, spriteOffsets.get(Apple.class.getSimpleName()), polygonVerticies, heal));
            }
        });

        try {
            Input input = new Input(new FileInputStream("joueur.bin"));
            joueur = GameView.kryo.readObject(input, Joueur.class);
            gameWorld.setJoueur(joueur);
            gameWorld.add(joueur);
            input.close();
        } catch (FileNotFoundException e) {
            MapLayer mapLayerJoueur = tiledMap.getLayers().get("Joueur");
            MapObject mapObjectJoueur = mapLayerJoueur.getObjects().get("Pluton");
            Map<Vector2, List<float[]>> mapPlayer = new HashMap<>(getPolygones(mapLayerJoueur));
            mapPlayer.forEach((coords, value)->{
                joueur = new Joueur(coords, spriteOffsets.get(Joueur.class.getSimpleName()), value.get(0), (float)mapObjectJoueur.getProperties().get("life"));
                gameWorld.setJoueur(joueur);
                gameWorld.add(joueur);
            });
        }
    }

    public int getLevelWidth() {
        return (int) tiledMap.getProperties().get("width");
    }

    public int getLevelHeight() {
        return (int) tiledMap.getProperties().get("height");
    }

    public void dispose(){
        tiledMap.dispose();
    }

    public TiledMap getMap() {
        return this.tiledMap;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }
}
