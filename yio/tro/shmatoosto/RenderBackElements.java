package yio.tro.shmatoosto;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import yio.tro.shmatoosto.stuff.GraphicsYio;

public class RenderBackElements {

    private final YioGdxGame yioGdxGame;
    private final SpriteBatch batch;
    private Color c;


    public RenderBackElements(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        batch = yioGdxGame.batch;
    }


    void renderBackElements() {
        batch.begin();
        c = batch.getColor();

        for (BackElement backElement : yioGdxGame.backElementsController.backElements) {
            renderBackElement(backElement);
        }

        batch.setColor(c.r, c.g, c.b, 1);
        batch.end();
    }


    private void renderBackElement(BackElement backElement) {
        if (!backElement.visible) return;

        updateAlpha(backElement);

        GraphicsYio.drawFromCenterRotated(batch, backElement.texture, backElement.position.x, backElement.position.y, backElement.viewRadius, backElement.viewAngle);

        if (backElement.position.x > GraphicsYio.width - backElement.viewRadius) {
            GraphicsYio.drawFromCenterRotated(batch, backElement.texture, backElement.position.x - GraphicsYio.width, backElement.position.y, backElement.viewRadius, backElement.viewAngle);
        }

        if (backElement.position.y > GraphicsYio.height - backElement.viewRadius) {
            GraphicsYio.drawFromCenterRotated(batch, backElement.texture, backElement.position.x, backElement.position.y - GraphicsYio.height, backElement.viewRadius, backElement.viewAngle);
        }

        if (backElement.position.x < backElement.viewRadius) {
            GraphicsYio.drawFromCenterRotated(batch, backElement.texture, backElement.position.x + GraphicsYio.width, backElement.position.y, backElement.viewRadius, backElement.viewAngle);
        }

        if (backElement.position.y < backElement.viewRadius) {
            GraphicsYio.drawFromCenterRotated(batch, backElement.texture, backElement.position.x, backElement.position.y + GraphicsYio.height, backElement.viewRadius, backElement.viewAngle);
        }
    }


    private void updateAlpha(BackElement backElement) {
        if (yioGdxGame.gameView.appearFactor.get() == 0) {
            batch.setColor(c.r, c.g, c.b, 0.5f * backElement.alphaFactor.get());
        } else {
            batch.setColor(c.r, c.g, c.b, 0.5f * Math.min(backElement.alphaFactor.get(), Math.max(0, 1 - 5 * yioGdxGame.gameView.appearFactor.get())));
        }
    }
}