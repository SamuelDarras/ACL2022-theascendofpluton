package fr.ul.theascendofpluton;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class LevelLoader {
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;

    public LevelLoader(){
    }

    public void load(String level_name){
        tiledMap = new TmxMapLoader().load("levels/" + level_name + ".tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    public MapRenderer getRenderer(){
        return tiledMapRenderer;
    }

    public MapLayer getEntities(){
        return tiledMap.getLayers().get("entities");
    }

    public int  getLevelWidth() {
        return (int) tiledMap.getProperties().get("width");
    }
    
    public int  getLevelHeight() {
        return (int) tiledMap.getProperties().get("height");
    }
}
