package yio.tro.shmatoosto.game.game_objects;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.game.gameplay.billiard.Hole;
import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;
import yio.tro.shmatoosto.stuff.object_pool.ReusableYio;

public class Ball extends GameObject implements ReusableYio, Comparable{


    public static final float WALL_FRICTION = 0.2f;
    public static final float DEFAULT_BOARD_FRICTION = 0.005f;
    public CircleYio position;
    public BColorYio bColorYio;
    public PointYio delta;
    public float collisionRadius;
    public float maxSpeed;
    float cutValue;
    public Hole hole;
    float targetRadius;
    FactorYio appearFactor;
    public double movementAngle;
    PointYio lastPosition;
    public FactorYio selectionFactor;
    public double selectionAngle;
    float boardFriction;
    boolean frozen;
    public double flagDistance;
    public int collisionId;
    public boolean hidden;
    public boolean solid;
    public double mass;
    public PointYio potentialDelta; // used for simulation


    public Ball(ObjectsLayer objectsLayer) {
        super(objectsLayer);

        position = new CircleYio();
        bColorYio = null;
        delta = new PointYio();
        appearFactor = new FactorYio();
        appearFactor.appear(3, 1);
        lastPosition = new PointYio();
        selectionFactor = new FactorYio();
        hole = null;
        frozen = false;
        boardFriction = DEFAULT_BOARD_FRICTION;
        selectionAngle = Yio.getRandomAngle();
        flagDistance = 0; // used for external algorithms
        mass = 0;
        potentialDelta = new PointYio();
    }


    @Override
    public void reset() {
        position.reset();
        bColorYio = null;
        delta.reset();
        collisionRadius = 0;
        hole = null;
        targetRadius = 0;
        appearFactor.reset();
        lastPosition.reset();
        frozen = false;
        setBoardFriction(DEFAULT_BOARD_FRICTION);
        collisionId = 0;
        hidden = false;
        solid = false;
    }


    public void copyFrom(Ball src) {
        appearFactor.copyFrom(src.appearFactor);
        position.center.setBy(src.position.center);
        setRadius(src.targetRadius);
        bColorYio = src.bColorYio;
        collisionRadius = src.collisionRadius;
        delta.setBy(src.delta);
        movementAngle = src.movementAngle;
        maxSpeed = src.maxSpeed;
        cutValue = src.cutValue;
        hole = src.hole;
        targetRadius = src.targetRadius;
        lastPosition.setBy(src.lastPosition);
        boardFriction = src.boardFriction;
        collisionId= src.collisionId;
        hidden = src.hidden;
        solid = src.solid;
        updateRadius();
    }


    public void setColor(BColorYio bColorYio) {
        this.bColorYio = bColorYio;
    }


    public BColorYio getColor() {
        return bColorYio;
    }


    public void setSpeed(double value, double angle) {
        delta.reset();
        delta.relocateRadial(value, angle);
        movementAngle = angle;
    }


    public double getSpeedValue() {
        return delta.getDistance();
    }


    public void updateCollisionRadius() {
        collisionRadius = 0.7f * targetRadius;
    }


    public void stop() {
        delta.reset();
    }


    public boolean isInCollisionWith(Ball otherBall) {
        if (Math.abs(position.center.x - otherBall.position.center.x) > collisionRadius + otherBall.collisionRadius) return false;
        if (Math.abs(position.center.y - otherBall.position.center.y) > collisionRadius + otherBall.collisionRadius) return false;

        double distance = position.center.distanceTo(otherBall.position.center);
        return distance < collisionRadius + otherBall.collisionRadius;
    }


    public void setRadius(double radius) {
        this.targetRadius = (float) radius;
        updateCollisionRadius();
        setMaxSpeed(collisionRadius);
        mass = targetRadius * targetRadius;
    }


    public float getTargetRadius() {
        return targetRadius;
    }


    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
        cutValue = 0.15f * maxSpeed;
    }


    public void setPosition(double x, double y) {
        position.center.set(x, y);
        updateLastPosition();
    }


    @Override
    protected void updatePosMapPosition() {
        posMapPosition.setBy(position.center);
    }


    public boolean isCue() {
        return getColor() == BColorYio.white;
    }


    public boolean isSoccerBall() {
        return getColor() == BColorYio.soccer;
    }


    @Override
    public void move() {
        checkForSolid();
        movePosition();
        applyFriction();
        moveRadius();
    }


    private void checkForSolid() {
        if (!solid) return;

        delta.reset();
    }


    public void moveSelection() {
        selectionFactor.move();

        if (isSelected()) {
            selectionAngle -= 0.01;
        }
    }


    public void select() {
        selectionFactor.setValues(0.01, 0);
        selectionFactor.appear(3, 1);
    }


    public void deselect() {
        selectionFactor.destroy(1, 2);
    }


    public boolean isSelected() {
        return selectionFactor.get() > 0;
    }


    private void moveRadius() {
        if (!appearFactor.move()) return;

        updateRadius();
    }


    private void updateRadius() {
        position.radius = appearFactor.get() * targetRadius;
    }


    public void limitMaxSpeed() {
        double currentSpeed = getSpeedValue();
        if (currentSpeed < maxSpeed) return;

        double a = getSpeedAngle();

        delta.reset();
        delta.relocateRadial(maxSpeed - 1, a);
    }


    public void setBoardFriction(float boardFriction) {
        this.boardFriction = boardFriction;
    }


    public float getBoardFriction() {
        return boardFriction;
    }


    private void applyFriction() {
        if (solid) return;

        double speedValue = getSpeedValue();
        if (speedValue == 0) return;

        if (speedValue > cutValue) {
            delta.x *= (1 - boardFriction);
            delta.y *= (1 - boardFriction);
        } else {
            speedValue -= boardFriction * cutValue;
            if (speedValue < 0) {
                speedValue = 0;
            }

            delta.reset();
            delta.relocateRadial(speedValue, movementAngle);
        }
    }


    private double getSpeedAngle() {
        return Yio.angle(0, 0, delta.x, delta.y);
    }


    public void updateMovementAngle() {
        movementAngle = getSpeedAngle();
    }


    public void updateMovementAngleByLastPosition() {
        movementAngle = lastPosition.angleTo(position.center);
    }


    public void updateLastPosition() {
        lastPosition.setBy(position.center);
    }


    public void setFrozen(boolean frozen) {
        if (this.frozen == frozen) return;

        this.frozen = frozen;

        if (!frozen) {
            objectsLayer.onBallUnfrozen();
        }
    }


    public boolean isFrozen() {
        return frozen;
    }


    public void limitByScreen() {
        if (position.center.x + collisionRadius > GraphicsYio.width) {
            updateLastPosition();
            position.center.x = GraphicsYio.width - collisionRadius;
            delta.x = -(1 - WALL_FRICTION) * Math.abs(delta.x);
            updateMovementAngle();
        }

        if (position.center.x - collisionRadius < 0) {
            updateLastPosition();
            position.center.x = collisionRadius;
            delta.x = (1 - WALL_FRICTION) * Math.abs(delta.x);
            updateMovementAngle();
        }

        if (position.center.y + collisionRadius > GraphicsYio.height) {
            updateLastPosition();
            position.center.y = GraphicsYio.height - collisionRadius;
            delta.y = -(1 - WALL_FRICTION) * Math.abs(delta.y);
            updateMovementAngle();
        }

        if (position.center.y - collisionRadius < 0) {
            updateLastPosition();
            position.center.y = collisionRadius;
            delta.y = (1 - WALL_FRICTION) * Math.abs(delta.y);
            updateMovementAngle();
        }
    }


    private void movePosition() {
        updateLastPosition();
        if (solid) return;

        position.center.x += delta.x;
        position.center.y += delta.y;
    }


    public void setCollisionId(int collisionId) {
        this.collisionId = collisionId;
    }


    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }


    public void setSolid(boolean solid) {
        this.solid = solid;
    }


    @Override
    public String toString() {
        return "[Ball: " +
                position.center + ", " +
                getColor() +
                "]";
    }


    @Override
    public int compareTo(Object o) {
        return (int) (flagDistance - ((Ball) o).flagDistance);
    }
}
