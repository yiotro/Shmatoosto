package yio.tro.shmatoosto.game.gameplay.soccer;

import yio.tro.shmatoosto.game.game_objects.CollisionLine;
import yio.tro.shmatoosto.game.game_objects.ObjectsLayer;
import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;

import java.util.ArrayList;

import static yio.tro.shmatoosto.game.view.GameView.TOP_BEZEL_HEIGHT;

public class SoccerBoard {


    public static final float SIDE_RATIO = 1.5f;
    SoccerManager soccerManager;
    public RectangleYio position;
    public PointYio center;
    float topBezelHeight;
    public ArrayList<RectangleYio> darkAreas;
    public PointYio cLineLeft, cLineRight;
    public RectangleYio topPlayground, bottomPlayground;
    private float dh;
    public float circleRadius;
    public ArrayList<CircleYio> barbells;
    public RectangleYio topGoalArea, bottomGoalArea;
    private float gw;
    public ArrayList<CircleYio> goalCorners;
    private float gcSize;
    private float gh;
    public float br;
    public ArrayList<CircleYio> boardCorners;
    public FactorYio goalEffectFactor;
    public int goalIndex;
    public RectangleYio topDefenseArea, bottomDefenseArea;


    public SoccerBoard(SoccerManager soccerManager) {
        this.soccerManager = soccerManager;

        position = new RectangleYio();
        center = new PointYio();
        topBezelHeight = (float) GraphicsYio.convertToWidth(TOP_BEZEL_HEIGHT) * GraphicsYio.width;
        goalIndex = -1;
        goalEffectFactor = new FactorYio();

        initPosition();
        initDarkAreas();
        initCenterLine();
        initPlaygrounds();
        updateCircleRadius();
        initBarbells();
        initGoalAreas();
        initGoalCorners();
        initBoardCorners();
        initDefenseAreas();
    }


    private void initDefenseAreas() {
        topDefenseArea = new RectangleYio();
        bottomDefenseArea = new RectangleYio();

        bottomDefenseArea.set(
                bottomGoalArea.x - br,
                bottomGoalArea.y + bottomGoalArea.height,
                bottomGoalArea.width + 2 * br,
                2.5 * bottomGoalArea.height
        );

        topDefenseArea.set(
                topGoalArea.x - br,
                topGoalArea.y - 2.5 * topGoalArea.height,
                topGoalArea.width + 2 * br,
                2.5 * topGoalArea.height
        );
    }


    void move() {
        goalEffectFactor.move();
    }


    void onGoalDetected(int goalIndex) {
        this.goalIndex = goalIndex;

        goalEffectFactor.setValues(1, 0);
        goalEffectFactor.destroy(1, 0.6);
    }


    private void initBoardCorners() {
        boardCorners = new ArrayList<>();

        addBoardCorner(
                position.x + gcSize,
                position.y + position.height - gcSize,
                0
        );

        addBoardCorner(
                position.x + position.width - gcSize,
                position.y + position.height - gcSize,
                - Math.PI / 2
        );

        addBoardCorner(
                position.x + gcSize,
                position.y + gcSize,
                Math.PI / 2
        );

        addBoardCorner(
                position.x + position.width - gcSize,
                position.y + gcSize,
                Math.PI
        );
    }


    private void addBoardCorner(double x, double y, double a) {
        CircleYio circleYio = new CircleYio();
        circleYio.set(x, y, 2 * gcSize);
        circleYio.setAngle(a);
        boardCorners.add(circleYio);
    }


    private void initGoalCorners() {
        goalCorners = new ArrayList<>();
        gcSize = 2 * GraphicsYio.borderThickness;

        addGoalCorner(
                topGoalArea.x + gcSize,
                topGoalArea.y + topGoalArea.height - gcSize,
                0
        );

        addGoalCorner(
                topGoalArea.x + topGoalArea.width - gcSize,
                topGoalArea.y + topGoalArea.height - gcSize,
                - Math.PI / 2
        );

        addGoalCorner(
                bottomGoalArea.x + gcSize,
                bottomGoalArea.y + gcSize,
                Math.PI / 2
        );

        addGoalCorner(
                bottomGoalArea.x + bottomGoalArea.width - gcSize,
                bottomGoalArea.y + gcSize,
                Math.PI
        );
    }


    private void addGoalCorner(double x, double y, double a) {
        CircleYio circleYio = new CircleYio();
        circleYio.set(x, y, 2 * gcSize);
        circleYio.setAngle(a);
        goalCorners.add(circleYio);
    }


    public void initCollisionLines() {
        ObjectsLayer objectsLayer = soccerManager.gameController.objectsLayer;

        float posX = position.x;
        float posY = position.y;
        float posWidth = position.width;
        float posHeight = position.height;
        float leftGoalX = posX + posWidth / 2 - gw / 2;
        float rightGoalX = posX + posWidth / 2 + gw / 2;

        objectsLayer.addCollisionLine(posX, posY, posX, posY + posHeight);
        objectsLayer.addCollisionLine(posX + posWidth, posY, posX + posWidth, posY + posHeight);

        objectsLayer.addCollisionLine(posX, posY, leftGoalX, posY);
        objectsLayer.addCollisionLine(rightGoalX, posY, posX + posWidth, posY);
        objectsLayer.addCollisionLine(posX, posY + posHeight, leftGoalX, posY + posHeight);
        objectsLayer.addCollisionLine(rightGoalX, posY + posHeight, posX + posWidth, posY + posHeight);

        objectsLayer.addCollisionLine(leftGoalX, posY, leftGoalX, posY - gh);
        objectsLayer.addCollisionLine(rightGoalX, posY, rightGoalX, posY - gh);
        objectsLayer.addCollisionLine(leftGoalX, posY + posHeight, leftGoalX, posY + posHeight + gh);
        objectsLayer.addCollisionLine(rightGoalX, posY + posHeight, rightGoalX, posY + posHeight + gh);

        objectsLayer.addCollisionLine(leftGoalX, posY - gh, rightGoalX, posY - gh);
        objectsLayer.addCollisionLine(leftGoalX, posY + posHeight + gh, rightGoalX, posY + posHeight + gh);

        for (CollisionLine collisionLine : objectsLayer.collisionLines) {
            collisionLine.setCollisionId(0);
        }

        objectsLayer.addCollisionLine(leftGoalX, posY, rightGoalX, posY).setCollisionId(1); // only for usual balls
        objectsLayer.addCollisionLine(leftGoalX, posY + posHeight, rightGoalX, posY + posHeight).setCollisionId(1); // only for usual balls
    }


    private void initGoalAreas() {
        topGoalArea = new RectangleYio();
        bottomGoalArea = new RectangleYio();

        gh = 0.5f * dh;

        bottomGoalArea.set(position.x + position.width / 2 - gw / 2, position.y - gh, gw, gh);
        topGoalArea.set(position.x + position.width / 2 - gw / 2, position.y + position.height, gw, gh);
    }


    private void initBarbells() {
        barbells = new ArrayList<>();

        gw = 0.35f * position.width;
        br = 0.025f * position.width;

        barbells.add(new CircleYio(position.x + position.width / 2 - gw / 2, position.y, br));
        barbells.add(new CircleYio(position.x + position.width / 2 + gw / 2, position.y, br));
        barbells.add(new CircleYio(position.x + position.width / 2 - gw / 2, position.y + position.height, br));
        barbells.add(new CircleYio(position.x + position.width / 2 + gw / 2, position.y + position.height, br));
    }


    private void updateCircleRadius() {
        circleRadius = 0.3f * position.width;
    }


    private void initPlaygrounds() {
        topPlayground = new RectangleYio();
        bottomPlayground = new RectangleYio();

        float ph = 0.7f * dh;
        float pw = 0.35f * position.width;

        bottomPlayground.set(
                position.x + position.width / 2 - pw / 2,
                position.y,
                pw, ph
        );

        topPlayground.set(
                position.x + position.width / 2 - pw / 2,
                position.y + position.height - ph,
                pw, ph
        );
    }


    private void initCenterLine() {
        cLineLeft = new PointYio();
        cLineRight = new PointYio();

        cLineLeft.set(position.x, position.y + position.height / 2);
        cLineRight.set(position.x + position.width, position.y + position.height / 2);
    }


    private void initDarkAreas() {
        darkAreas = new ArrayList<>();

        dh = position.height / 12;
        float y = position.y;
        for (int i = 0; i < 6; i++) {
            RectangleYio area = new RectangleYio();

            area.set(
                    position.x, y,
                    position.width, dh
            );

            y += 2 * dh;

            darkAreas.add(area);
        }
    }


    private void initPosition() {
        float r = soccerManager.defaultBallRadius;

        float height = GraphicsYio.height - 2 * topBezelHeight - 2 * r;
        float width = GraphicsYio.width - 2 * r;
        float k = height / width;

        if (k > SIDE_RATIO) {
            height = width * SIDE_RATIO;
        } else {
            width = height / SIDE_RATIO;
        }

        center.x = GraphicsYio.width / 2;
        center.y = (GraphicsYio.height - topBezelHeight) / 2;

        position.set(
                center.x - width / 2,
                center.y - height / 2,
                width,
                height
        );
    }
}
