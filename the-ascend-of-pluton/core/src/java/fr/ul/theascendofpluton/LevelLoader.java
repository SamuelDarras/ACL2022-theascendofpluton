package fr.ul.theascendofpluton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import fr.ul.theascendofpluton.helper.MapGraph;
import fr.ul.theascendofpluton.mapElement.Edge;
import fr.ul.theascendofpluton.mapElement.Node;
import fr.ul.theascendofpluton.model.AcidPuddle;
import fr.ul.theascendofpluton.model.Zombie;
import fr.ul.theascendofpluton.view.GameView;
import fr.ul.theascendofpluton.model.Obstacle;

import java.util.*;
import java.util.Map;

public class LevelLoader {
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;

    //graph map for A*
    public MapGraph mapGraph;

    private GameView gv;
    public Map<String, Sprite> spriteHashMap;

    public List<Node> tmpNode;
    public List<Edge> tmpVertice;

    public LevelLoader(GameView gv) {
        this.gv = gv;

        mapGraph = new MapGraph();

        Texture zomTexture = new Texture(Gdx.files.internal("zombies.png"));
        Sprite zombie = new Sprite(zomTexture, 13, 36, 32, 27);
        zombie.setScale(.3f);

        spriteHashMap = new HashMap<>();
        spriteHashMap.put("zombie", zombie);

        tmpNode = new ArrayList<>();
        tmpVertice = new ArrayList<>();
    }

    /**
     * Charge la TiledMap et créé le renderer
     * @param level_name
     */
    public void load(String level_name){
        tiledMap = new TmxMapLoader().load("levels/" + level_name + ".tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        createMapGraph((TiledMapTileLayer)tiledMap.getLayers().get("sol"));
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
    public Set<Zombie> addZombies(World world){
        Set<Zombie> zombies = new HashSet<>();
        MapLayer mapLayerZombies = tiledMap.getLayers().get("Zombies");
        float vie = (float) mapLayerZombies.getProperties().get("vie");
        ;
        float damage = (float) mapLayerZombies.getProperties().get("damage");
        for (MapObject mapObject : mapLayerZombies.getObjects()) {
            zombies.add(new Zombie(world, (float) mapObject.getProperties().get("x"),
                    (float) mapObject.getProperties().get("y"), vie, damage, gv));
        }
        return zombies;
    }

    public void createMapGraph(TiledMapTileLayer mapLayer){
        float[] cellCenter = new float[2];

        for (int i = 0; i < mapLayer.getWidth(); i++) {
            for (int j = 0; j < mapLayer.getHeight(); j++) {
                TiledMapTileLayer.Cell cell = mapLayer.getCell(i, j);
                if (cell != null){
                    cellCenter[0] = i*mapLayer.getTileWidth() + mapLayer.getTileWidth()/2f;
                    cellCenter[1] = j*mapLayer.getTileHeight() + mapLayer.getTileHeight()/2f;

                    Array<Array<Vector2>> cellPolygon = new Array<>();
                    Array<Vector2> tmp = new Array<>();

                    float[] vertices;

                    for (PolygonMapObject polygonMapObject : cell.getTile().getObjects().getByType(PolygonMapObject.class)){
                        vertices = polygonMapObject.getPolygon().getTransformedVertices();

                        for (int v=0; v<vertices.length; v+=2){
                            tmp.add(new Vector2(vertices[v]+i*mapLayer.getTileWidth(), vertices[v+1]+j*mapLayer.getTileHeight()));
                        }

                        cellPolygon.add(tmp);
                    }

                    for (float[] pt : divide(cellCenter, 2, 32, 32))
                        if (cellPolygon.size == 0){
                            tmpNode.add(new Node(pt[0], pt[1]));
                            mapGraph.addNode(pt[0]+"-"+pt[1], new Node(pt[0], pt[1]));
                        }
                        else {
                            for (Array<Vector2> polygon : cellPolygon) {
                                if (!Intersector.isPointInPolygon(polygon, new Vector2(pt[0], pt[1]))) {
                                    tmpNode.add(new Node(pt[0], pt[1]));
                                    mapGraph.addNode(pt[0] + "-" + pt[1], new Node(pt[0], pt[1]));
                                }
                            }
                        }
                }
            }
        }
        /*
        for (int i = 0; i < mapLayer.getWidth(); i++) {
            for (int j = 0; j < mapLayer.getHeight(); j++) {
                TiledMapTileLayer.Cell cell = mapLayer.getCell(i, j);
                if (cell != null){
                    Node node = mapGraph.nodes.get(i*mapLayer.getWidth()+"-"+j*mapLayer.getTileHeight());
                    for (int k = i-1; k < i+2; k++){
                        for (int l = j-1; k < j+2; j++){
                            TiledMapTileLayer.Cell neighborCell = mapLayer.getCell(k, l);
                            //System.out.println(node + " -- " + neighborCell + " -- " + mapGraph.nodes.get(k+"-"+l));
                            if (neighborCell != null){
                                mapGraph.connectNodes(node, mapGraph.nodes.get(k+"-"+l));
                            }

                        }
                    }
                }
            }
        }

         */
    }

    private Set<float[]> divide(float[] center, int factor, int width, int height){
        Set<float[]> list = new HashSet<>();
        list.add(center);

        for (int i=0; i<factor; i++){
            width /= 2;
            height /= 2;
            list = divideAux(list, width, height);
        }

        return list;
    }

    private Set<float[]> divideAux(Set<float[]> list, float width, float height){
        Set<float[]> listCpy = new HashSet<>();
        listCpy.addAll(list);

        for (float[] pts : listCpy){
            list.add(new float[]{pts[0] - width/2, pts[1] + height/2});
            list.add(new float[]{pts[0] - width/2, pts[1] - height/2});

            list.add(new float[]{pts[0] + width/2, pts[1] + height/2});
            list.add(new float[]{pts[0] + width/2, pts[1] - height/2});
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
}
