package yio.tro.shmatoosto.menu.scenes;

import yio.tro.shmatoosto.game.tests.*;
import yio.tro.shmatoosto.menu.LanguagesManager;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.elements.CircleButtonYio;
import yio.tro.shmatoosto.menu.elements.slider.SliderBehavior;
import yio.tro.shmatoosto.menu.elements.slider.SliderYio;

public class SceneDebugTests extends SceneYio{


    private CircleButtonYio backButton;
    private ButtonYio label;
    public int[] sliderValues;
    private SliderYio slider;


    @Override
    protected void initialize() {
        setBackground(2);

        initSliderValues();

        backButton = spawnBackButton(new Reaction() {
            @Override
            protected void reaction() {
                Scenes.mainMenu.create();
            }
        });

        label = uiFactory.getButton()
                .setSize(0.8, 0.15)
                .centerHorizontal()
                .alignBottom(backButton, 0.1)
                .applyText(" ")
                .setTouchable(false);

        slider = uiFactory.getSlider()
                .setSize(0.7, 0)
                .setParent(label)
                .centerHorizontal()
                .alignTop(0.02)
                .setName("Quantity")
                .setValues(0.7, sliderValues.length)
                .setBehavior(new SliderBehavior() {
                    @Override
                    public String getValueString(LanguagesManager languagesManager, SliderYio sliderYio) {
                        return "" + sliderValues[sliderYio.getValueIndex()];
                    }
                });

        uiFactory.getButton()
                .setSize(0.8, 0.07)
                .centerHorizontal()
                .alignBottom(label, 0.1)
                .applyText("Billiard AI")
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        TestManyAiScores testManyAiScores = new TestManyAiScores(gameController);
                        testManyAiScores.setQuantity(getQuantity());
                        testManyAiScores.perform();
                    }
                });

        addTestButton("Launch AI only game", new Reaction() {
            @Override
            protected void reaction() {
                Scenes.aiOnlyGameMenu.create();
            }
        });

        addTestButton("Precision", new Reaction() {
            @Override
            protected void reaction() {
                TestMeasurePrecision testMeasurePrecision = new TestMeasurePrecision(gameController);
                testMeasurePrecision.setQuantity(getQuantity());
                testMeasurePrecision.perform();
            }
        });

        addTestButton("Chapaevo AI", new Reaction() {
            @Override
            protected void reaction() {
                TestChapaevoAiManyGames testChapaevoAiManyGames = new TestChapaevoAiManyGames(gameController);
                testChapaevoAiManyGames.setQuantity(getQuantity());
                testChapaevoAiManyGames.perform();
            }
        });
    }


    private void addTestButton(String name, Reaction reaction) {
        uiFactory.getButton()
                .clone(previousElement)
                .centerHorizontal()
                .alignBottom(previousElement, 0.02)
                .applyText(name)
                .setReaction(reaction);
    }


    private int getQuantity() {
        return sliderValues[slider.getValueIndex()];
    }


    private void initSliderValues() {
        sliderValues = new int[]{1, 2, 3, 5, 10, 15, 20, 25, 35, 40, 50, 60, 75, 100, 250, 500};
    }


}
