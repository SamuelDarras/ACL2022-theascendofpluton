package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.math.Vector2;

import fr.ul.theascendofpluton.LevelLoader;

public abstract class DamageableObject extends GameObject{
    private float life;
    private float damage;
    private float money;

    public DamageableObject(Vector2 coords, float life, float damage, float money) {
        super(coords);
        this.life = life;
        this.damage = damage;
        this.money = money;
    }

    public void inflictDamage(DamageableObject o){
        o.receiveDamage(this.damage);
    };

    public void receiveDamage(float n){
        life -= n;
    };

    public float getLife() {
        return life;
    }

    public void setLife(float life) {
        this.life = life;
    }

    public float getDamage() {
        return damage;
    }

    public float getMoney() {
        return money;
    }

}
