package yio.tro.shmatoosto.menu.scenes.gameplay;

import yio.tro.shmatoosto.game.GameResults;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.player_entities.AbstractPlayerEntity;
import yio.tro.shmatoosto.menu.LanguagesManager;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.scenes.SceneYio;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class SceneAfterGame extends SceneYio {


    private ButtonYio label;


    @Override
    protected void initialize() {
        setBackground(1);

        double lw = 0.9;
        label = uiFactory.getButton()
                .setSize(lw, 0.2)
                .centerHorizontal()
                .centerVertical()
                .setTouchable(false)
                .setAnimation(AnimationYio.down);

        double bw = 0.35;
        double offset = (lw - 2 * bw) / 3;
        uiFactory.getButton()
                .setParent(label)
                .setSize(bw, 0.045)
                .alignRight(offset)
                .alignBottom(0.01)
                .applyText("ok")
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        Scenes.chooseGameMode.create();
                    }
                });

        uiFactory.getButton()
                .clone(previousElement)
                .alignLeft(offset)
                .alignBottom(0.01)
                .applyText("restart")
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        yioGdxGame.loadingManager.startInstantly(GameRules.initialLoadingType, GameRules.initialParameters);
                    }
                });
    }


    public void updateLabel(GameResults gameResults) {
        label.clearText();
        label.applyText(getWinnerString(gameResults), 3);
        label.updateTextDeltas();
    }


    private String getWinnerString(GameResults gameResults) {
        AbstractPlayerEntity winnerEntity = getWinnerEntity(gameResults);
        if (winnerEntity == null) return LanguagesManager.getInstance().getString("a_draw");

        String prefix;
        if (winnerEntity.isHuman()) {
            prefix = "player_";
        } else {
            prefix = "ai_";
        }

        if (isHumanVsAiGame(gameResults)) {
            return LanguagesManager.getInstance().getString(prefix + "won");
        }

        return LanguagesManager.getInstance().getString(prefix + castWinnerIndexIntoSuffix(gameResults) + "_won");
    }


    private String castWinnerIndexIntoSuffix(GameResults gameResults) {
        switch (gameResults.winnerIndex) {
            default: return "error";
            case 1: return "one";
            case 2: return "two";
        }
    }


    private AbstractPlayerEntity getWinnerEntity(GameResults gameResults) {
        if (gameResults.winnerIndex == 1) {
            return gameResults.entityOne;
        }

        if (gameResults.winnerIndex == 2) {
            return gameResults.entityTwo;
        }

        return null;
    }


    private boolean isHumanVsAiGame(GameResults gameResults) {
        if (gameResults.entityOne.isHuman() && !gameResults.entityTwo.isHuman()) return true;
        if (!gameResults.entityOne.isHuman() && gameResults.entityTwo.isHuman()) return true;

        return false;
    }
}
