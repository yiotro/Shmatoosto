package yio.tro.shmatoosto.game.gameplay;

import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.object_pool.ReusableYio;

public class SimResults implements ReusableYio{


    SimulationManager simulationManager;
    public int scoreDelta;
    public int collisionBeforeFirstScore;


    public SimResults(SimulationManager simulationManager) {
        this.simulationManager = simulationManager;
    }


    public void dispose() {
        simulationManager.disposeResults(this);
    }


    @Override
    public void reset() {
        scoreDelta = 0;
        collisionBeforeFirstScore = 0;
    }


    public boolean equals(SimResults sr) {
        return scoreDelta == sr.scoreDelta;
    }


    @Override
    public String toString() {
        return "[Results: " +
                scoreDelta +
                "]";
    }


    public static class BoardLine {

        public PointYio one;
        public PointYio two;


        public BoardLine() {
            one = new PointYio();
            two = new PointYio();
        }


        public void set(double x1, double y1, double x2, double y2) {
            one.set(x1, y1);
            two.set(x2, y2);
        }
    }
}
