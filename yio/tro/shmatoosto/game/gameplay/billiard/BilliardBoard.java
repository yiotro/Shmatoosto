package yio.tro.shmatoosto.game.gameplay.billiard;

import yio.tro.shmatoosto.game.game_objects.ObjectsLayer;
import yio.tro.shmatoosto.game.gameplay.SimResults;
import yio.tro.shmatoosto.game.view.GameView;
import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

import java.util.ArrayList;

public class BilliardBoard {

    BilliardManager billiardManager;
    public RectangleYio position;
    float topBezelHeight;
    public float cRadius;
    public ArrayList<SimResults.BoardLine> boardLines;
    public ArrayList<CircleYio> corners;
    public ArrayList<CircleYio> middleHoles;
    public ArrayList<PointYio> decorativeCircles;
    public RectangleYio topBezelPosition;


    public BilliardBoard(BilliardManager billiardManager) {
        this.billiardManager = billiardManager;
        position = new RectangleYio();
        topBezelHeight = (float) GraphicsYio.convertToWidth(GameView.TOP_BEZEL_HEIGHT) * GraphicsYio.width;
        cRadius = 0.07f * GraphicsYio.width;
        initPosition();
        boardLines = new ArrayList<>();
        corners = new ArrayList<>();
        middleHoles = new ArrayList<>();
        decorativeCircles = new ArrayList<>();
        topBezelPosition = new RectangleYio();
        initMetrics();
    }


    void initCollisionLines() {
        PointYio t1 = new PointYio();
        PointYio t2 = new PointYio();

        for (SimResults.BoardLine boardLine : boardLines) {
            getObjectsLayer().addCollisionLine(boardLine);
        }

        for (CircleYio middleHole : middleHoles) {
            PointYio center = middleHole.center;
            getObjectsLayer().addCollisionLine(center.x, center.y + 0.8 * cRadius, center.x, center.y + 1.2 * cRadius);
            getObjectsLayer().addCollisionLine(center.x, center.y - 0.8 * cRadius, center.x, center.y - 1.2 * cRadius);

            t1.set(center.x, center.y);
            t1.relocateRadial(0.8 * cRadius, middleHole.angle + Math.PI / 2);
            t2.setBy(t1);
            t2.relocateRadial(cRadius / 2, middleHole.angle - 0.12 * Math.PI);
            getObjectsLayer().addCollisionLine(t1.x, t1.y, t2.x, t2.y);

            t1.set(center.x, center.y);
            t1.relocateRadial(0.8 * cRadius, middleHole.angle - Math.PI / 2);
            t2.setBy(t1);
            t2.relocateRadial(cRadius / 2, middleHole.angle + 0.12 * Math.PI);
            getObjectsLayer().addCollisionLine(t1.x, t1.y, t2.x, t2.y);
        }

        for (CircleYio corner : corners) {
            PointYio center = corner.center;

            t1.setBy(center);
            t1.relocateRadial(0.9 * cRadius, corner.angle + Math.PI);
            t2.setBy(t1);
            t2.relocateRadial(0.3 * cRadius, corner.angle + Math.PI);
            getObjectsLayer().addCollisionLine(t1.x, t1.y, t2.x, t2.y);

            t1.setBy(center);
            t1.relocateRadial(0.9 * cRadius, corner.angle - Math.PI / 2);
            t2.setBy(t1);
            t2.relocateRadial(0.3 * cRadius, corner.angle - Math.PI / 2);
            getObjectsLayer().addCollisionLine(t1.x, t1.y, t2.x, t2.y);

            t1.setBy(center);
            t1.relocateRadial(0.9 * cRadius, corner.angle + Math.PI);
            t2.setBy(t1);
            t2.relocateRadial(cRadius, corner.angle + 0.25 * Math.PI);
            getObjectsLayer().addCollisionLine(t1.x, t1.y, t2.x, t2.y);

            t1.setBy(center);
            t1.relocateRadial(0.9 * cRadius, corner.angle - Math.PI / 2);
            t2.setBy(t1);
            t2.relocateRadial(cRadius, corner.angle + 0.25 * Math.PI);
            getObjectsLayer().addCollisionLine(t1.x, t1.y, t2.x, t2.y);
        }
    }


    private ObjectsLayer getObjectsLayer() {
        return billiardManager.gameController.objectsLayer;
    }


    private void initMetrics() {
        initCorners();
        initMiddleHoles();
        initLines();
        initDecorativeCircles();
        initTopBezelPosition();
    }


    private void initTopBezelPosition() {
        topBezelPosition.x = 0;
        topBezelPosition.y = position.y + position.height;
        topBezelPosition.width = GraphicsYio.width;
        topBezelPosition.height = topBezelHeight;
    }


    private void initDecorativeCircles() {
        PointYio t = new PointYio();
        for (SimResults.BoardLine boardLine : boardLines) {
            float length = boardLine.one.distanceTo(boardLine.two);
            double a = boardLine.one.angleTo(boardLine.two);
            float delta = length / 4;
            t.setBy(boardLine.one);
            t.relocateRadial(cRadius / 2, a - Math.PI / 2);
            t.relocateRadial(delta, a);
            for (int i = 0; i < 3; i++) {
                PointYio p = new PointYio();
                p.setBy(t);
                decorativeCircles.add(p);
                t.relocateRadial(delta, a);
            }
        }
    }


    private void initLines() {
        // bottom
        addBoardLine().set(position.x + 2 * cRadius, position.y + cRadius, position.x + position.width - 2 * cRadius, position.y + cRadius);

        // right
        addBoardLine().set(position.x + position.width - cRadius, position.y + 2 * cRadius, position.x + position.width - cRadius, position.y + position.height / 2 - cRadius);
        addBoardLine().set(position.x + position.width - cRadius, position.y + position.height / 2 + cRadius, position.x + position.width - cRadius, position.y + position.height - 2 * cRadius);

        // top
        addBoardLine().set(position.x + position.width - 2 * cRadius, position.y + position.height - cRadius, position.x + 2 * cRadius, position.y + position.height - cRadius);

        // left
        addBoardLine().set(position.x + cRadius, position.y + position.height - 2 * cRadius, position.x + cRadius, position.y + position.height / 2 + cRadius);
        addBoardLine().set(position.x + cRadius, position.y + position.height / 2 - cRadius, position.x + cRadius, position.y + 2 * cRadius);
    }


    private void initMiddleHoles() {
        addMiddleHole().set(position.x + cRadius, position.y + position.height / 2, cRadius).setAngle(Math.PI);
        addMiddleHole().set(position.x + position.width - cRadius, position.y + position.height / 2, cRadius).setAngle(0);
    }


    private void initCorners() {
        addCorner().set(position.x + cRadius, position.y + cRadius, cRadius).setAngle(Math.PI);
        addCorner().set(position.x + position.width - cRadius, position.y + cRadius, cRadius).setAngle(-Math.PI / 2);
        addCorner().set(position.x + position.width - cRadius, position.y + position.height - cRadius, cRadius).setAngle(0);
        addCorner().set(position.x + cRadius, position.y + position.height - cRadius, cRadius).setAngle(Math.PI / 2);
    }


    private CircleYio addCorner() {
        CircleYio circleYio = new CircleYio();
        corners.add(circleYio);
        return circleYio;
    }


    private CircleYio addMiddleHole() {
        CircleYio circleYio = new CircleYio();
        middleHoles.add(circleYio);
        return circleYio;
    }


    private SimResults.BoardLine addBoardLine() {
        SimResults.BoardLine boardLine = new SimResults.BoardLine();
        boardLines.add(boardLine);
        return boardLine;
    }


    private void initPosition() {
        position.set(0, 0, GraphicsYio.width, GraphicsYio.height - topBezelHeight);
    }


}
