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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import fr.ul.theascendofpluton.model.*;
import com.badlogic.gdx.utils.Array;
import fr.ul.theascendofpluton.helper.MapGraph;
import fr.ul.theascendofpluton.mapElement.Edge;
import fr.ul.theascendofpluton.mapElement.Node;
import fr.ul.theascendofpluton.model.AcidPuddle;
import fr.ul.theascendofpluton.model.Zombie;
import fr.ul.theascendofpluton.view.GameView;
import fr.ul.theascendofpluton.model.Obstacle;

import java.io.*;
import java.util.*;
import java.util.Map;

public class LevelLoader {
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private Joueur joueur;

    //graph map for A*
    public MapGraph mapGraph;

    private GameView gv;
    public Map<String, Sprite> spriteHashMap;
    public Map<String, Vector2> spriteOffsets;

    private GameWorld gameWorld;

    private static LevelLoader INSTANCE = new LevelLoader();

    private LevelLoader() {

        mapGraph = new MapGraph();

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

        createMapGraph(level_name, (TiledMapTileLayer)tiledMap.getLayers().get("sol"));

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
                gameWorld.add(new Zombie(world, coords, polygonVerticies, life, damage, monnaie));
            }
        });

        MapLayer mapLayerApples = tiledMap.getLayers().get("Apple");
        float heal = (float)mapLayerApples.getProperties().get("heal");
        Map<Vector2, List<float[]>> mapApples = new HashMap<>(getPolygones(mapLayerApples));
        mapApples.forEach((coords, value)->{
            for(float[] polygonVerticies : value){
                gameWorld.add(new Apple(world, coords, polygonVerticies, heal));
            }
        });

        MapLayer mapLayerJoueur = tiledMap.getLayers().get("Joueur");
        MapObject mapObjectJoueur = mapLayerJoueur.getObjects().get("Pluton");
        Map<Vector2, List<float[]>> mapPlayer = new HashMap<>(getPolygones(mapLayerJoueur));
        mapPlayer.forEach((coords, value)->{
            joueur = new Joueur(world, coords, value.get(0), (float)mapObjectJoueur.getProperties().get("life"));
            gameWorld.setJoueur(joueur);
            gameWorld.add(joueur);
        });
    }

    public void createMapGraph(String level_name, TiledMapTileLayer mapLayer){

        Vector2 cellCenter;
        Map<String, List<Node>> nodeByCell = new HashMap<>();
        int factor = 2;
        float dstNodes = 1f/factor * 25;

        for (int i = 0; i < mapLayer.getWidth(); i++) {
            for (int j = 0; j < mapLayer.getHeight(); j++) {
                TiledMapTileLayer.Cell cell = mapLayer.getCell(i, j);
                if (cell != null){

                    cellCenter = new Vector2(i*mapLayer.getTileWidth() + mapLayer.getTileWidth()/2f, j*mapLayer.getTileHeight() + mapLayer.getTileHeight()/2f);

                    Array<Array<Vector2>> cellPolygon = new Array<>();
                    Array<Vector2> tmp = new Array<>();

                    float[] vertices;

                    for (PolygonMapObject polygonMapObject : cell.getTile().getObjects().getByType(PolygonMapObject.class)){
                        vertices = polygonMapObject.getPolygon().getTransformedVertices();

                        for (int v=0; v<vertices.length; v+=2){
                            tmp.add(new Vector2(vertices[v]+i*mapLayer.getTileWidth(), vertices[v+1]+j*mapLayer.getTileHeight()));
                        }

                        cellPolygon.add(new Array<>(tmp));
                        tmp.clear();
                    }

                    Set<Vector2> subPoint = divide(cellCenter, factor, 32, 32);

                    List<Node> tmpNodeListByCell = new ArrayList<>();
                    for (Vector2 pt : subPoint) {
                        Node addedNode = new Node(pt.x, pt.y);
                        if (cellPolygon.size == 0) {
                            mapGraph.addNode(pt.x + "-" + pt.y, addedNode);
                            tmpNodeListByCell.add(addedNode);
                        } else {
                            boolean addable = true;
                            for (Array<Vector2> polygon : cellPolygon) {
                                if (Intersector.isPointInPolygon(polygon, pt)) {
                                    addable = false;
                                }
                            }
                            if(addable){
                                mapGraph.addNode(pt.x + "-" + pt.y, addedNode);
                                tmpNodeListByCell.add(addedNode);
                            }
                        }
                    }
                    nodeByCell.put(i+"-"+j, tmpNodeListByCell);
                }
            }
        }

        nodeByCell.forEach((key, nodeList) -> {
            for (int i = 0; i<nodeList.size(); i++){
                Node nodeSrc = nodeList.get(i);
                for (int j = i; j<nodeList.size(); j++){
                    Node nodeDst = nodeList.get(j);
                    if(nodeSrc.dst(nodeDst) <= dstNodes){
                        mapGraph.connectNodes(nodeSrc, nodeDst);
                    }
                }
            }
        });
        Set<String> keys = nodeByCell.keySet();
        Set<String> tmpKeys = new HashSet<>(keys);
        final int[][] checkedCoords = {{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
        for (String key : keys){
            String[] coords = key.split("-");
            List<Node> nodeListSrc = nodeByCell.get(key);
            for (int i = 0 ; i<checkedCoords.length ; i++){
                String cellKey = (Integer.valueOf(coords[0]) + checkedCoords[i][0])+"-"+(Integer.valueOf(coords[1]) + checkedCoords[i][1]);
                if(tmpKeys.contains(cellKey)){
                    List<Node> nodeListDst = nodeByCell.get(cellKey);
                    for (int j = 0; j < nodeListSrc.size(); j++){
                        for (int k = 0; k < nodeListDst.size(); k++){
                            Node nodeSrc = nodeListSrc.get(j);
                            Node nodeDst = nodeListDst.get(k);
                            if(nodeSrc.dst(nodeDst) <= dstNodes){
                                mapGraph.connectNodes(nodeSrc, nodeDst);
                            }
                        }
                    }
                }

            }
            tmpKeys.remove(key);
        }

    }

    private Set<Vector2> divide(Vector2 center, int factor, int width, int height){
        Set<Vector2> list = new HashSet<>();
        list.add(center);

        for (int i=0; i<factor; i++){
            width /= 2;
            height /= 2;
            list = divideAux(list, width, height);
        }

        return list;
    }

    private Set<Vector2> divideAux(Set<Vector2> list, float width, float height){
        Set<Vector2> listCpy = new HashSet<>();
        listCpy.addAll(list);

        for (Vector2 pts : listCpy){
            list.add(new Vector2(pts.x - width/2, pts.y + height/2));
            list.add(new Vector2(pts.x - width/2, pts.y - height/2));

            list.add(new Vector2(pts.x + width/2, pts.y + height/2));
            list.add(new Vector2(pts.x + width/2, pts.y - height/2));
        }

        listCpy.removeAll(list);

        return list;
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
