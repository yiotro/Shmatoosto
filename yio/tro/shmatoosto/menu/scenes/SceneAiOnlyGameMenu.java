package yio.tro.shmatoosto.menu.scenes;

import yio.tro.shmatoosto.game.Difficulty;
import yio.tro.shmatoosto.game.loading.LoadingParameters;
import yio.tro.shmatoosto.game.loading.LoadingType;
import yio.tro.shmatoosto.menu.LanguagesManager;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.elements.slider.SliderBehavior;
import yio.tro.shmatoosto.menu.elements.slider.SliderYio;

public class SceneAiOnlyGameMenu extends SceneYio{


    private ButtonYio label;
    int bqValues[];
    private SliderYio ballsQuantitySlider;
    private SliderYio gameTypeSlider;


    @Override
    protected void initialize() {
        setBackground(3);

        bqValues = new int[]{6, 12, 18, 24, 30};

        spawnBackButton(new Reaction() {
            @Override
            protected void reaction() {
                Scenes.debugTests.create();
            }
        });

        double h = 0.4;
        label = uiFactory.getButton()
                .setSize(0.9, h)
                .centerHorizontal()
                .alignTop(0.1 + (0.9 - h) / 2)
                .setTouchable(false);

        ballsQuantitySlider = uiFactory.getSlider()
                .setParent(label)
                .setSize(0.7, 0)
                .centerHorizontal()
                .setSegmentsVisible(true)
                .alignTop(0.02)
                .setName("Balls quantity")
                .setValues(0.75, bqValues.length)
                .setBehavior(new SliderBehavior() {
                    @Override
                    public String getValueString(LanguagesManager languagesManager, SliderYio sliderYio) {
                        return "" + bqValues[sliderYio.getValueIndex()];
                    }
                });

        gameTypeSlider = uiFactory.getSlider()
                .clone(previousElement)
                .centerHorizontal()
                .alignBottom(previousElement, 0.05)
                .setName("Game type")
                .setValues(0, 3)
                .setBehavior(new SliderBehavior() {
                    @Override
                    public String getValueString(LanguagesManager languagesManager, SliderYio sliderYio) {
                        switch (sliderYio.getValueIndex()) {
                            default:
                                return "error";
                            case 0:
                                return "Billiard";
                            case 1:
                                return "Chapaevo";
                            case 2:
                                return "Soccer";
                        }
                    }
                });

        uiFactory.getButton()
                .setSize(0.4, 0.05)
                .setParent(label)
                .centerHorizontal()
                .alignBottom(0.01)
                .applyText("Launch")
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        launchAiOnlyGame();
                    }
                });
    }


    private LoadingType getLoadingType() {
        switch (gameTypeSlider.getValueIndex()) {
            default:
            case 0:
                return LoadingType.billiard;
            case 1:
                return LoadingType.chapaevo;
            case 2:
                return LoadingType.soccer;
        }
    }


    private void launchAiOnlyGame() {
        LoadingParameters loadingParameters = new LoadingParameters();

        loadingParameters.addParameter("balls", bqValues[ballsQuantitySlider.getValueIndex()]);
        loadingParameters.addParameter("difficulty", Difficulty.HARD);
        loadingParameters.addParameter("2_player", false);
        loadingParameters.addParameter("ai_only", true);
        loadingParameters.addParameter("infinite_game", false);
        loadingParameters.addParameter("obstacles", false);
        loadingParameters.addParameter("goals_limit", 5);

        yioGdxGame.loadingManager.startInstantly(getLoadingType(), loadingParameters);
    }
}
