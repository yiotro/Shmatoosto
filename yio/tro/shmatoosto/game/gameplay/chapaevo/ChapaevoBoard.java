package yio.tro.shmatoosto.game.gameplay.chapaevo;

import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

import static yio.tro.shmatoosto.game.view.GameView.TOP_BEZEL_HEIGHT;

public class ChapaevoBoard {


    ChapaevoManager chapaevoManager;
    public RectangleYio position;
    public PointYio center;
    float topBezelHeight;


    public ChapaevoBoard(ChapaevoManager chapaevoManager) {
        this.chapaevoManager = chapaevoManager;

        position = new RectangleYio();
        center = new PointYio();
        topBezelHeight = (float) GraphicsYio.convertToWidth(TOP_BEZEL_HEIGHT) * GraphicsYio.width;

        initPosition();
    }


    private void initPosition() {
        float r = chapaevoManager.defaultBallRadius;
        float width = GraphicsYio.width - 2 * r;
        float h1 = 1.5f * width;
        float h2 = GraphicsYio.height - topBezelHeight - 2 * r;
        float height = Math.min(h1, h2);

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
