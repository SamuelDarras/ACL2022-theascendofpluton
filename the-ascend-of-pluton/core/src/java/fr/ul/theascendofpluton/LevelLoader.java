package fr.ul.theascendofpluton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import fr.ul.theascendofpluton.model.Zombie;
import fr.ul.theascendofpluton.model.Obstacle;

import java.util.*;

public class LevelLoader {
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;

    public HashMap<String, Sprite> spriteHashMap;

    public LevelLoader(){
        Texture zombie = new Texture(Gdx.files.internal("zombies.png"));
        Sprite zombieSprite = new Sprite(zombie, 13, 36, 32, 27);
        zombieSprite.setScale(.2f);

        spriteHashMap = new HashMap<>();
        spriteHashMap.put("zombie", zombieSprite);
    }

    public void load(String level_name){
        tiledMap = new TmxMapLoader().load("levels/" + level_name + ".tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    public MapRenderer getRenderer(){
        return tiledMapRenderer;
    }

    public MapObject getPluton(){
        return tiledMap.getLayers().get("Player").getObjects().get("Pluton");
    }
    private void addObstaclesFromLayer(TiledMapTileLayer mapLayer, TiledMapTileSet tiledMapTiles, World world){
        System.out.println("toto");

        for (int i = 0; i < mapLayer.getWidth(); i++) {
            for(int j = 0; j < mapLayer.getHeight(); j++){
                TiledMapTileLayer.Cell cell = mapLayer.getCell(i, j);
                if (cell != null){
                    Array<PolygonMapObject> polygons = cell.getTile().getObjects().getByType(PolygonMapObject.class);
                    for (PolygonMapObject polygonMapObject : polygons){
                        new Obstacle(world, i * mapLayer.getTileWidth(), j * mapLayer.getTileHeight(), polygonMapObject.getPolygon().getTransformedVertices());
                    }
                }
            }
        }
    }
    public void addObstacles(World world){
        TiledMapTileSet t = tiledMap.getTileSets().getTileSet("pluton");
        addObstaclesFromLayer((TiledMapTileLayer)tiledMap.getLayers().get("sol"), t, world);
        addObstaclesFromLayer((TiledMapTileLayer)tiledMap.getLayers().get("vide"), t, world);
    }

    public Set<Zombie> addZombies(World world){
        Set<Zombie> zombies = new HashSet<>();
        MapLayer mapLayerZombies = tiledMap.getLayers().get("Zombies");
        float vie = (float) mapLayerZombies.getProperties().get("vie");;
        float damage = (float) mapLayerZombies.getProperties().get("damage");
        for (MapObject mapObject : mapLayerZombies.getObjects()){
            zombies.add(new Zombie(world, (float) mapObject.getProperties().get("x"), (float) mapObject.getProperties().get("y"), vie, damage));
        }
        return zombies;
    }

    public int  getLevelWidth() {
        return (int) tiledMap.getProperties().get("width");
    }
    
    public int  getLevelHeight() {
        return (int) tiledMap.getProperties().get("height");
    }
}
