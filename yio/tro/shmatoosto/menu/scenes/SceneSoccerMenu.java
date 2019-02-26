package yio.tro.shmatoosto.menu.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.shmatoosto.game.loading.LoadingParameters;
import yio.tro.shmatoosto.game.loading.LoadingType;
import yio.tro.shmatoosto.menu.LanguagesManager;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.BackgroundYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.elements.CheckButtonYio;
import yio.tro.shmatoosto.menu.elements.slider.SliderBehavior;
import yio.tro.shmatoosto.menu.elements.slider.SliderYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;

public class SceneSoccerMenu extends SceneYio {


    public ButtonYio startButton;
    private ButtonYio label;
    private CheckButtonYio chkTwoPlayers;
    private SliderYio ballsSlider;
    private SliderYio difficultySlider;
    private int goalsValues[];
    private SliderYio goalsLimitSlider;


    @Override
    protected void initialize() {
        setBackground(3);

        goalsValues = new int[]{-1, 1, 2, 3, 4, 5, 6, 8, 10};

        spawnBackButton(new Reaction() {
            @Override
            protected void reaction() {
                saveValues();
                Scenes.chooseGameMode.create();
            }
        });

        createStartButton();

        label = uiFactory.getButton()
                .setPosition(0.05, 0.15, 0.9, 0.6)
                .applyText(" ")
                .setTouchable(false)
                .setAnimation(AnimationYio.center);

        createSliders();
        createChecks();

        loadValues();
    }


    private void createSliders() {
        ballsSlider = uiFactory.getSlider()
                .setParent(label)
                .setSize(0.8)
                .alignTop(0.03)
                .setAnimation(AnimationYio.down)
                .alignLeft(0.05)
                .setName("balls")
                .setValues(0, 4)
                .setSegmentsVisible(true)
                .setBehavior(new SliderBehavior() {
                    @Override
                    public String getValueString(LanguagesManager languagesManager, SliderYio sliderYio) {
                        return "" + convertBallsSliderIndexIntoValue(sliderYio.getValueIndex());
                    }
                });

        difficultySlider = uiFactory.getSlider()
                .clone(previousElement)
                .alignBottom(previousElement, 0.05)
                .setName("difficulty")
                .setAnimation(AnimationYio.down)
                .setValues(0.25, 3)
                .setBehavior(new SliderBehavior() {
                    @Override
                    public String getValueString(LanguagesManager languagesManager, SliderYio sliderYio) {
                        return convertSliderIndexIntoDifficultyString(languagesManager, sliderYio);
                    }
                })
                .setVerticalTouchOffset(0.1f * GraphicsYio.width);

        goalsLimitSlider = uiFactory.getSlider()
                .clone(previousElement)
                .alignBottom(previousElement, 0.05)
                .setName("goals_limit")
                .setAnimation(AnimationYio.down)
                .setValues(0, goalsValues.length)
                .setBehavior(new SliderBehavior() {
                    @Override
                    public String getValueString(LanguagesManager languagesManager, SliderYio sliderYio) {
                        return convertSliderIndexIntoGoalsString(sliderYio.getValueIndex());
                    }
                })
                .setVerticalTouchOffset(0.1f * GraphicsYio.width);
    }


    private String convertSliderIndexIntoGoalsString(int sliderIndex) {
        int value = goalsValues[sliderIndex];

        if (value == -1) {
            return LanguagesManager.getInstance().getString("no");
        }

        return "" + value;
    }


    private String convertSliderIndexIntoDifficultyString(LanguagesManager languagesManager, SliderYio sliderYio) {
        switch (sliderYio.getValueIndex()) {
            default:
            case 0:
                return languagesManager.getString("easy");
            case 1:
                return languagesManager.getString("normal");
            case 2:
                return languagesManager.getString("hard");
            case 3:
                return languagesManager.getString("impossible");
        }
    }


    private int convertBallsSliderIndexIntoValue(int index) {
        switch (index) {
            default:
            case 0:
                return 6;
            case 1:
                return 8;
            case 2:
                return 10;
            case 3:
                return 12;
        }
    }


    private void createChecks() {
        chkTwoPlayers = uiFactory.getCheckButton()
                .setParent(label)
                .setHeight(0.2 / 3)
                .setName("two_player_mode")
                .alignBottom(previousElement, 0.06);

//        chkObstacles = uiFactory.getCheckButton()
//                .clone(previousElement)
//                .setName("obstacles")
//                .alignBottom(previousElement, 0.01);
    }


    private void loadValues() {
        Preferences prefs = getPreferences();

        ballsSlider.setValueIndex(prefs.getInteger("balls", 3));
        difficultySlider.setValueIndex(prefs.getInteger("difficulty", 0));
        chkTwoPlayers.setChecked(prefs.getBoolean("2_player", false));
        goalsLimitSlider.setValueIndex(prefs.getInteger("goals_limit", 0));
    }


    private void saveValues() {
        Preferences prefs = getPreferences();

        prefs.putInteger("balls", ballsSlider.getValueIndex());
        prefs.putInteger("difficulty", difficultySlider.getValueIndex());
        prefs.putBoolean("2_player", chkTwoPlayers.isChecked());
        prefs.putInteger("goals_limit", goalsLimitSlider.getValueIndex());

        prefs.flush();
    }


    private Preferences getPreferences() {
        return Gdx.app.getPreferences("shmatoosto.soccer_parameters");
    }


    private void createStartButton() {
        startButton = uiFactory.getButton()
                .setPosition(0.55, 0.9, 0.4, 0.07)
                .applyText("start", BackgroundYio.green)
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        saveValues();
                        LoadingParameters loadingParameters = new LoadingParameters();

                        loadingParameters.addParameter("balls", convertBallsSliderIndexIntoValue(ballsSlider.getValueIndex()));
                        loadingParameters.addParameter("difficulty", difficultySlider.getValueIndex());
                        loadingParameters.addParameter("2_player", chkTwoPlayers.isChecked());
                        loadingParameters.addParameter("ai_only", false);
                        loadingParameters.addParameter("goals_limit", goalsValues[goalsLimitSlider.getValueIndex()]);

                        yioGdxGame.loadingManager.startInstantly(LoadingType.soccer, loadingParameters);
                    }
                })
                .setAnimation(AnimationYio.up);
    }
}
