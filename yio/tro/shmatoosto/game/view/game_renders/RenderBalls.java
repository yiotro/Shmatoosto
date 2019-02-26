package yio.tro.shmatoosto.game.view.game_renders;

import yio.tro.shmatoosto.game.GameMode;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.gameplay.FallBallAnimation;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.Storage3xTexture;

public class RenderBalls extends GameRender {


    private Storage3xTexture ballYellow;
    private Storage3xTexture ballPurple;
    private Storage3xTexture ballRed;
    private Storage3xTexture ballBlue;
    private Storage3xTexture shadow;
    private Storage3xTexture ballCyan;
    private Storage3xTexture ballBlack;
    private Storage3xTexture ballWhite;
    private Storage3xTexture ballGreen;
    PointYio tempPoint;
    private Storage3xTexture selectorChapaevo;
    private Storage3xTexture selectorSoccer;
    private Storage3xTexture soccerBall;


    public RenderBalls() {
        tempPoint = new PointYio();
    }


    @Override
    protected void loadTextures() {
        ballYellow = load3xTexture("ball_yellow");
        ballPurple = load3xTexture("ball_purple");
        ballRed = load3xTexture("ball_red");
        ballBlue = load3xTexture("ball_blue");
        ballCyan = load3xTexture("ball_cyan");
        shadow = load3xTexture("ball_shadow");
        ballBlack = load3xTexture("ball_black");
        ballWhite = load3xTexture("ball_white");
        ballGreen = load3xTexture("ball_green");
        selectorChapaevo = load3xTexture("ball_selection");
        selectorSoccer = load3xTexture("selector_soccer");
        soccerBall = load3xTexture("soccer_ball");
    }


    private Storage3xTexture getBallTexture(Ball ball) {
        switch (ball.bColorYio) {
            default:
            case yellow: return ballYellow;
            case blue: return ballBlue;
            case red: return ballRed;
            case purple: return ballPurple;
            case cyan: return ballCyan;
            case white: return ballWhite;
            case green: return ballGreen;
            case soccer: return soccerBall;
        }
    }


    public void renderFallBallAnimations() {
        for (FallBallAnimation fallBallAnimation : gameController.objectsLayer.fallBallAnimations) {
            Ball ball = fallBallAnimation.ball;
            GraphicsYio.setBatchAlpha(batchMovable, fallBallAnimation.alphaFactor.get());

            GraphicsYio.drawByCircle(batchMovable, shadow.getTextureRegion(), ball.position);
            GraphicsYio.drawByCircle(batchMovable, getBallTexture(ball).getTextureRegion(), ball.position);
        }

        GraphicsYio.setBatchAlpha(batchMovable, 1);
    }


    @Override
    public void render() {
        renderShadows();
        renderSelections();
        renderMainParts();
    }


    private void renderMainParts() {
        for (Ball ball : gameController.objectsLayer.balls) {
            if (ball.hidden) continue;

            GraphicsYio.drawByCircle(batchMovable, getBallTexture(ball).getTextureRegion(), ball.position);
        }
    }


    private void renderShadows() {
        for (Ball ball : gameController.objectsLayer.balls) {
            if (ball.hidden) continue;

            GraphicsYio.drawByCircle(batchMovable, shadow.getTextureRegion(), ball.position);
        }
    }


    private void renderSelections() {
        for (Ball ball : gameController.objectsLayer.balls) {
            if (ball.hidden) continue;
            if (!ball.isSelected()) continue;

            GraphicsYio.setBatchAlpha(batchMovable, ball.selectionFactor.get());

            GraphicsYio.drawFromCenterRotated(
                    batchMovable,
                    getSelectionTexture().getTextureRegion(),
                    ball.position.center.x,
                    ball.position.center.y,
                    1.3f * ball.selectionFactor.get() * ball.position.radius,
                    ball.selectionAngle
            );
        }

        GraphicsYio.setBatchAlpha(batchMovable, 1);
    }


    private Storage3xTexture getSelectionTexture() {
        if (gameController.gameMode == GameMode.soccer) {
            return selectorSoccer;
        }

        return selectorChapaevo;
    }


    public void renderDebug() {
        for (Ball ball : gameController.objectsLayer.balls) {
            if (ball.movementAngle == 0) continue;

            tempPoint.setBy(ball.position.center);
            tempPoint.relocateRadial(0.05f * GraphicsYio.width, ball.movementAngle);

            GraphicsYio.drawLine(batchMovable, gameView.blackPixel, ball.position.center, tempPoint, GraphicsYio.borderThickness);
        }
    }


    @Override
    protected void disposeTextures() {

    }
}
