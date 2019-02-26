package yio.tro.shmatoosto.game.game_objects;

import yio.tro.shmatoosto.stuff.containers.posmap.PosMapObjectYio;

public abstract class GameObject extends PosMapObjectYio{

    ObjectsLayer objectsLayer;


    public GameObject(ObjectsLayer objectsLayer) {
        this.objectsLayer = objectsLayer;
    }


    public abstract void move();
}
