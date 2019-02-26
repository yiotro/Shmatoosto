package yio.tro.shmatoosto.game.game_objects;

public interface CollisionListener {


    void onBallsCollided(Ball one, Ball two);


    void onPrecisionFixApplied(double previousAngle, double currentAngle);
}
