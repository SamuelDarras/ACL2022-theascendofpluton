package fr.ul.theascendofpluton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import fr.ul.theascendofpluton.model.AcidPuddle;
import fr.ul.theascendofpluton.model.Apple;
import fr.ul.theascendofpluton.model.Zombie;
import fr.ul.theascendofpluton.view.GameView;
import fr.ul.theascendofpluton.model.Obstacle;
import sun.tools.jconsole.JConsole;

import java.util.*;
import java.util.Map;

public class LevelLoader {
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;

    private GameView gv;
    private Set<Zombie> zombies;
    private Set<Apple> apples;
    public Map<String, Sprite> spriteHashMap;

    public LevelLoader(GameView gv) {
        this.gv = gv;
        zombies = new HashSet<>();
        apples = new HashSet<>();

        Texture zomTexture = new Texture(Gdx.files.internal("zombies.png"));
        Sprite zombie = new Sprite(zomTexture, 13, 36, 32, 27);
        zombie.setScale(.3f);

        Texture appleTexture = new Texture(Gdx.files.internal("apple.png"));
        Sprite appleSprite = new Sprite(appleTexture,0,0,64,64);
        appleSprite.setScale(.25f);

        spriteHashMap = new HashMap<>();
        spriteHashMap.put("zombie", zombie);
        spriteHashMap.put("apple", appleSprite);

    }

    /**
     * Charge la TiledMap et créé le renderer
     * @param level_name
     */
    public void load(String level_name){
        tiledMap = new TmxMapLoader().load("levels/" + level_name + ".tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    public MapRenderer getRenderer() {
        return tiledMapRenderer;
    }

    public MapObject getPluton() {
        return tiledMap.getLayers().get("Player").getObjects().get("Pluton");
    }

    /**
     * récupère les coordonnées et les sommets des polygones présents dans une couche de la map
     * @param mapLayer couche de la map contenant des tuiles
     */
    private Map<int[], Array<float[]>> getPolygonesFromLayer(TiledMapTileLayer mapLayer){
        Map<int [], Array<float[]>> map = new HashMap<>();
        for (int i = 0; i < mapLayer.getWidth(); i++) {
            for (int j = 0; j < mapLayer.getHeight(); j++) {
                TiledMapTileLayer.Cell cell = mapLayer.getCell(i, j);
                if (cell != null){
                    int[] coords = new int[]{i * mapLayer.getTileWidth(), j * mapLayer.getTileHeight()};
                    Array<float[]> arrayVerticies = new Array<>();
                    for (PolygonMapObject polygonMapObject : cell.getTile().getObjects().getByType(PolygonMapObject.class)){
                        arrayVerticies.add(polygonMapObject.getPolygon().getTransformedVertices());
                    }
                    map.put(coords, arrayVerticies);
                }
            }
        }
        return map;
    }

    /**
     * Ajoute les différents obstacles dans le monde via la tiledMap
     * @param world
     */
    public void addObstacles(World world){
        Map<int[], Array<float[]>> mapObstacles = new HashMap<>();
        Map<int[], Array<float[]>> mapPuddles = new HashMap<>();

        mapObstacles.putAll(getPolygonesFromLayer((TiledMapTileLayer)tiledMap.getLayers().get("sol")));
        mapObstacles.putAll(getPolygonesFromLayer((TiledMapTileLayer)tiledMap.getLayers().get("vide")));
        mapPuddles.putAll(getPolygonesFromLayer((TiledMapTileLayer)tiledMap.getLayers().get("puddles")));

        mapObstacles.forEach((key, value)->{
            for(float[] verticies : value){
                new Obstacle(world, key[0], key[1], verticies);
            }
        });

        mapPuddles.forEach((key, value)->{
            for(float[] verticies : value){
                new AcidPuddle(world, key[0], key[1], verticies, 5);
            }
        });
    }

    /**
     * Ajoute au monde les zombies présents dans la tiledMap
     * @param world
     * @return le set contenant les zombies ajoutés au monde.
     */
    public void addObjects(World world){
        MapLayer mapLayerZombies = tiledMap.getLayers().get("Zombies");
        float vie = (float) mapLayerZombies.getProperties().get("vie");
        float damage = (float) mapLayerZombies.getProperties().get("damage");
        for (MapObject mapObject : mapLayerZombies.getObjects()) {
            zombies.add(new Zombie(world, (float) mapObject.getProperties().get("x"),
                    (float) mapObject.getProperties().get("y"), vie, damage, gv));
        }

        MapLayer mapLayerApples = tiledMap.getLayers().get("Apples");
        TiledMapTileSet appleTileSet = tiledMap.getTileSets().getTileSet("Apple");
        System.out.println(appleTileSet);

        float heal = (float) mapLayerApples.getProperties().get("heal");
        for (MapObject mapObject : mapLayerApples.getObjects()){
            TiledMapTileMapObject tiledMapTileMapObject = (TiledMapTileMapObject) mapObject;
            Polygon polygon = tiledMapTileMapObject.getTile().getObjects().getByType(PolygonMapObject.class).get(0).getPolygon();
            polygon.setScale(tiledMapTileMapObject.getScaleX(), tiledMapTileMapObject.getScaleY());
            apples.add(new Apple(world,tiledMapTileMapObject.getX()-(float)mapLayerApples.getProperties().get("offsetX"), tiledMapTileMapObject.getY()-(float)mapLayerApples.getProperties().get("offsetY"), polygon.getTransformedVertices(), heal, gv));
            // je ne sais pas pourquoi je dois mettre ces offsets
        }

    }

    public int getLevelWidth() {
        return (int) tiledMap.getProperties().get("width");
    }

    public int getLevelHeight() {
        return (int) tiledMap.getProperties().get("height");
    }

    public Set<Zombie> getZombies(){
        return zombies;
    }

    public void dispose(){
        tiledMap.dispose();
    }

    public Set<Apple> getApples() {
        return apples;
    }
}
