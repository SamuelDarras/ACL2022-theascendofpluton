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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import fr.ul.theascendofpluton.model.*;
import fr.ul.theascendofpluton.view.GameView;
import sun.tools.jconsole.JConsole;

import java.util.*;
import java.util.Map;

public class LevelLoader {
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;

    private GameView gv;
    private Set<Zombie> zombies;
    private Set<Apple> apples;
    private Joueur joueur;
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

    public Joueur getPluton() {
        return joueur;
    }

    private Map<Vector2, Array<float[]>> getPolygones(MapLayer mapLayer){
        Map<Vector2, Array<float[]>> map = new HashMap<>();
        if(mapLayer instanceof TiledMapTileLayer){
            TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) mapLayer;
            for (int i = 0; i < tiledMapTileLayer.getWidth(); i++) {
                for (int j = 0; j < tiledMapTileLayer.getHeight(); j++) {
                    TiledMapTileLayer.Cell cell = tiledMapTileLayer.getCell(i, j);
                    if (cell != null){
                        Array<float[]> arrayVerticies = new Array<>();
                        for (PolygonMapObject polygonMapObject : cell.getTile().getObjects().getByType(PolygonMapObject.class)){
                            arrayVerticies.add(polygonMapObject.getPolygon().getTransformedVertices());
                        }
                        map.put(new Vector2(i * tiledMapTileLayer.getTileWidth(), j * tiledMapTileLayer.getTileHeight()), arrayVerticies);
                    }
                }
            }
        }
        else {
            for(MapObject mapObject : mapLayer.getObjects()){
                TiledMapTileMapObject tiledMapTileMapObject = (TiledMapTileMapObject) mapObject;
                Array<float[]> arrayVerticies = new Array<>();
                for(PolygonMapObject polygonMapObject: tiledMapTileMapObject.getTile().getObjects().getByType(PolygonMapObject.class)){
                    Polygon polygon = polygonMapObject.getPolygon();
                    polygon.setScale(tiledMapTileMapObject.getScaleX(), tiledMapTileMapObject.getScaleY());
                    arrayVerticies.add(polygon.getTransformedVertices());
                }
                Vector2 coords = new Vector2(tiledMapTileMapObject.getX() - (float)mapLayer.getProperties().get("offsetX"), tiledMapTileMapObject.getY() - (float)mapLayer.getProperties().get("offsetY")); // TODO: deduction automatique du décalage
                map.put(coords, arrayVerticies);
            }
        }
        return map;
    }

//     for (MapObject mapObject : mapLayerApples.getObjects()){
//        TiledMapTileMapObject tiledMapTileMapObject = (TiledMapTileMapObject) mapObject;
//        Polygon polygon = tiledMapTileMapObject.getTile().getObjects().getByType(PolygonMapObject.class).get(0).getPolygon();
//        polygon.setScale(tiledMapTileMapObject.getScaleX(), tiledMapTileMapObject.getScaleY());
//        apples.add(new Apple(world,tiledMapTileMapObject.getX()-(float)mapLayerApples.getProperties().get("offsetX"), tiledMapTileMapObject.getY()-(float)mapLayerApples.getProperties().get("offsetY"), polygon.getTransformedVertices(), heal, gv));
//        // je ne sais pas pourquoi je dois mettre ces offsets
//    }

    /**
     * Ajoute les différents obstacles dans le monde via la tiledMap
     * @param world
     */
    public void addObstacles(World world){
        Map<Vector2, Array<float[]>> mapObstacles = new HashMap<>();
        Map<Vector2, Array<float[]>> mapPuddles = new HashMap<>();
        mapObstacles.putAll(getPolygones(tiledMap.getLayers().get("sol")));
        mapObstacles.putAll(getPolygones(tiledMap.getLayers().get("vide")));
        mapPuddles.putAll(getPolygones(tiledMap.getLayers().get("puddles")));
        mapObstacles.forEach((key, value)->{
            for(float[] verticies : value){
                new Obstacle(world, key, verticies);
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
        MapLayer mapLayerZombies = tiledMap.getLayers().get("Zombies");
        float life = (float)mapLayerZombies.getProperties().get("life");
        float damage = (float)mapLayerZombies.getProperties().get("damage");
        Map<Vector2, Array<float[]>> mapZombies = new HashMap<>(getPolygones(mapLayerZombies));
        mapZombies.forEach((key, value)->{
            for(float[] verticies : value){
                zombies.add(new Zombie(world, key, verticies, life, damage, gv));
            }
        });

        MapLayer mapLayerApples = tiledMap.getLayers().get("Apples");
        float heal = (float)mapLayerApples.getProperties().get("heal");
        Map<Vector2, Array<float[]>> mapApples = new HashMap<>(getPolygones(mapLayerApples));
        mapApples.forEach((coords, value)->{
            for(float[] verticies : value){
                apples.add(new Apple(world, coords, verticies, heal, gv));
            }
        });

        MapLayer mapLayerJoueur = tiledMap.getLayers().get("Player");
        MapObject mapObjectJoueur = mapLayerJoueur.getObjects().get("Pluton");
        Map<Vector2, Array<float[]>> mapPlayer = new HashMap<>(getPolygones(mapLayerJoueur));
        mapPlayer.forEach((coords, value)->{
            joueur = new Joueur(world, coords, value.get(0), (float)mapObjectJoueur.getProperties().get("life"));
        });
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
