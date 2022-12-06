package fr.ul.theascendofpluton.model;

import fr.ul.theascendofpluton.LevelLoader;

/**
 * Encapsule un DamageableObject pour en faire un boss de niveau
 */
public class Boss extends DamageableObject{

    DamageableObject bossObject;
    public Boss(DamageableObject bossObject) {
        super(bossObject.getPosition(), bossObject.getLife(), bossObject.getDamage(), bossObject.getMoney());
        setBody(bossObject.getBody());
        getBody().getFixtureList().get(0).setUserData("boss");
        getBody().setUserData(this);
        this.bossObject = bossObject;
    }
    @Override
    public void update(GameWorld w) {
        if(getLife() <= 0){
            LevelLoader.getInstance().getPluton().receiveMoney(getMoney());
            LevelLoader.getInstance().getGameWorld().removeBoss(this);
        }
        else{
            bossObject.update(w);
        }
    }

    @Override
    public void render(float delta) {
        super.render();
    }

    @Override
    public String getName() {
        return bossObject.getName() + super.getName();
    }


}
