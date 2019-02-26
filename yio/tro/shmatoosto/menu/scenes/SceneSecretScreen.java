package yio.tro.shmatoosto.menu.scenes;

import yio.tro.shmatoosto.game.Difficulty;
import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.game.loading.LoadingParameters;
import yio.tro.shmatoosto.game.loading.LoadingType;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.ButtonYio;

public class SceneSecretScreen extends SceneYio{


    private ButtonYio mainLabel;


    @Override
    protected void initialize() {
        setBackground(2);

        spawnBackButton(new Reaction() {
            @Override
            protected void reaction() {
                Scenes.mainMenu.create();
            }
        });

        mainLabel = uiFactory.getButton()
                .setSize(0.8, 0.2)
                .centerHorizontal()
                .alignTop(0.15)
                .setTouchable(false)
                .applyText(convertStringToArray("Hi! #This is a secret screen."));

        uiFactory.getButton()
                .setSize(0.8, 0.07)
                .centerHorizontal()
                .alignBottom(mainLabel, 0.1)
                .setTouchOffset(0.02)
                .applyText("Debug tests")
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        Scenes.debugTests.create();
                    }
                });

        uiFactory.getButton()
                .clone(previousElement)
                .alignBottom(previousElement, 0.03)
                .centerHorizontal()
                .applyText("Show fps")
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        switchShowFps();
                    }
                });
    }


    private void switchShowFps() {
        if (DebugFlags.showFps) {
            DebugFlags.showFps = false;
            Scenes.notification.show("Show fps disabled");
        } else {
            DebugFlags.showFps = true;
            Scenes.notification.show("Show fps enabled");
        }
    }
}
