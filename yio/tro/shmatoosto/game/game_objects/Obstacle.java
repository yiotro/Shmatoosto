package yio.tro.shmatoosto.game.game_objects;

import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

public class Obstacle {


    ObjectsLayer objectsLayer;
    public RectangleYio position;


    public Obstacle(ObjectsLayer objectsLayer) {
        this.objectsLayer = objectsLayer;

        position = new RectangleYio();
    }


    public void applyCollisionLines() {
        objectsLayer.addCollisionLine(position.x, position.y, position.x + position.width, position.y);
        objectsLayer.addCollisionLine(position.x, position.y, position.x, position.y + position.height);
        objectsLayer.addCollisionLine(position.x + position.width, position.y + position.height, position.x + position.width, position.y);
        objectsLayer.addCollisionLine(position.x + position.width, position.y + position.height, position.x, position.y + position.height);
    }


    public boolean isInCollisionWith(Obstacle anotherObstacle) {
        RectangleYio aPos = anotherObstacle.position;
        if (position.x > aPos.x + aPos.width) return false;
        if (position.x + position.width < aPos.x) return false;
        if (position.y > aPos.y + aPos.height) return false;
        if (position.y + position.height < aPos.y) return false;

        return true;
    }


    public void limitBy(RectangleYio limit) {
        if (position.x + position.width > limit.x + limit.width) {
            position.width = limit.x + limit.width - position.x;
        }

        if (position.x < limit.x) {
            position.x = limit.x;
        }

        if (position.y + position.height > limit.y + limit.height) {
            position.height = limit.y + limit.height - position.y;
        }

        if (position.y < limit.y) {
            position.y = limit.y;
        }
    }


    public boolean isVertical() {
        return position.height > position.width;
    }
}
