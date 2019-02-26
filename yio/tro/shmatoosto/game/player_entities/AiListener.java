package yio.tro.shmatoosto.game.player_entities;

public interface AiListener {


    void onScorePredicted(double score);


    void onTimeTaken(int millis);
}
