package yio.tro.shmatoosto.menu.elements.slider;

import com.badlogic.gdx.Gdx;
import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.menu.LanguagesManager;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;

import java.util.ArrayList;

public class SliderYio extends InterfaceElement<SliderYio> implements SliderListener{

    boolean isCurrentlyPressed, segmentsVisible, solidWidth, accentVisible;
    public int numberOfSegments;
    public float runnerValue, circleSize, segmentSize, textWidth;
    float viewMagnifier, circleDefaultSize, verticalTouchOffset;
    float circleSizeDelta, defaultHeight, nameTopOffset;

    public FactorYio sizeFactor;
    String valueString;
    ArrayList<SliderListener> listeners;
    private RectangleYio touchRectangle; // used for debug
    SliderBehavior behavior;
    private float animDistance;
    String name;
    PointYio namePosition;
    private double defaultParentLeftOffset;
    private float horizontalTouchOffset;


    public SliderYio(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);

        behavior = new SbDefault();
        sizeFactor = new FactorYio();
        listeners = new ArrayList<>();
        touchRectangle = new RectangleYio();
        namePosition = new PointYio();

        // defaults
        solidWidth = false;
        circleDefaultSize = 0.012f * Gdx.graphics.getHeight();
        circleSize = circleDefaultSize;
        verticalTouchOffset = 0.05f * Gdx.graphics.getHeight();
        circleSizeDelta = 0.005f * Gdx.graphics.getHeight();
        defaultHeight = 0.09f * GraphicsYio.height;
        nameTopOffset = 0.02f * GraphicsYio.height;
        horizontalTouchOffset = 0.05f * Gdx.graphics.getWidth();
        segmentsVisible = false;
        defaultParentLeftOffset = 0.05;
        accentVisible = false;
        setAnimation(AnimationYio.def);
        setName("[Slider]");
        setValues(0, 3);
    }


    @Override
    protected SliderYio getThis() {
        return this;
    }


    public SliderYio setAnimation(AnimationYio animType) {
        this.animType = animType;
        return getThis();
    }


    public boolean isCoorInsideSlider(float x, float y) {
        return x > viewPosition.x - horizontalTouchOffset &&
                x < viewPosition.x + viewPosition.width + horizontalTouchOffset &&
                y > viewPosition.y - verticalTouchOffset &&
                y < viewPosition.y + verticalTouchOffset;
    }


    @Override
    public boolean touchDown() {
        if (!touchable) return false;
        if (isCoorInsideSlider(currentTouch.x, currentTouch.y) && appearFactor.get() == 1) {
            sizeFactor.appear(3, 2);
            isCurrentlyPressed = true;
            setValueByX(currentTouch.x);
            return true;
        }
        return false;
    }


    @Override
    public boolean touchDrag() {
        if (isCurrentlyPressed) {
            setValueByX(currentTouch.x);
            return true;
        }
        return false;
    }


    @Override
    public boolean touchUp() {
        if (isCurrentlyPressed) {
            sizeFactor.destroy(1, 1);
            isCurrentlyPressed = false;
            updateValueString();
            return true;
        }
        return false;
    }


    @Override
    public RenderInterfaceElement getRenderSystem() {
        return MenuRenders.renderSlider;
    }


    void setValueByX(float x) {
        x -= viewPosition.x;
        runnerValue = x / position.width;
        if (runnerValue < 0) runnerValue = 0;
        if (runnerValue > 1) runnerValue = 1;
        behavior.onValueChanged(this);
        updateValueString();
    }


    void pullRunnerToCenterOfSegment() {
        double cx = getValueIndex() * segmentSize;
        double delta = cx - runnerValue;
        runnerValue += 0.2 * delta;
        limitRunnerValue();
    }


    private void limitRunnerValue() {
        if (runnerValue > 1) {
            runnerValue = 1;
        }
        if (runnerValue < 0) {
            runnerValue = 0;
        }
    }


    private void updateVerticalPos() {
        switch (animType) {
            case none:
            case def:
                viewPosition.y = position.y;
                break;
            case up:
                viewPosition.y = (1 - appearFactor.get()) * (1.1f * Gdx.graphics.getHeight() - position.y) + position.y;
                break;
            case down:
                viewPosition.y = appearFactor.get() * (position.y + 0.1f * Gdx.graphics.getHeight()) - 0.1f * Gdx.graphics.getHeight();
                break;
            case center:
                viewPosition.y = 0.5f * Gdx.graphics.getHeight() + (position.y - 0.5f * Gdx.graphics.getHeight()) * appearFactor.get();
                break;
            case down_short:
                animDownShort();
                break;
        }
    }


    @Override
    protected void animDownShort() {
        if (animDistance == -1) {
            animDistance = 0.3f * GraphicsYio.width;
        }

        viewPosition.y = position.y - (1 - appearFactor.get()) * animDistance;
    }


    @Override
    public void move() {
        updateViewPosition();

        if (sizeFactor.hasToMove()) sizeFactor.move();
        if (appearFactor.get() == 0) return;

        circleSize = circleDefaultSize + circleSizeDelta * sizeFactor.get();
        if (!isCurrentlyPressed) pullRunnerToCenterOfSegment();
    }


    @Override
    protected void onApplyParent() {
        super.onApplyParent();

        updateNamePosition();
    }


    private void updateNamePosition() {
        namePosition.x = viewPosition.x - 0.01f * GraphicsYio.width;
        namePosition.y = viewPosition.y + viewPosition.height - nameTopOffset;
    }


    @Override
    protected void updateViewPosition() {
        viewPosition.height = position.height;

        if (solidWidth) {
            viewPosition.width = position.width;
            viewPosition.x = position.x;
        } else {
            viewPosition.width = position.width * appearFactor.get();
            viewPosition.x = position.x + 0.5f * position.width - 0.5f * viewPosition.width;
        }

        updateVerticalPos();
    }


    @Override
    public SliderYio setParent(InterfaceElement parent) {
        SliderYio sliderYio = super.setParent(parent);

        alignLeft(defaultParentLeftOffset);
        setSize(parent.getPosition().width / GraphicsYio.width - 2 * defaultParentLeftOffset);

        return sliderYio;
    }


    @Override
    protected void onSizeChanged() {
        position.height = defaultHeight;
        viewPosition.height = position.height;
    }


    public float getRunnerValueViewX() {
        return viewPosition.x + runnerValue * viewPosition.width;
    }


    public float getRunnerValue() {
        return runnerValue;
    }


    @Override
    public void onAppear() {
        appearFactor.setValues(0, 0.001);
    }


    @Override
    public SliderYio clone(InterfaceElement src) {
        super.clone(src);

        if (src instanceof SliderYio) {
            setSegmentsVisible(((SliderYio) src).segmentsVisible);
        }

        return this;
    }


    @Override
    public void onDestroy() {
        appearFactor.setDy(-0.2);
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    public SliderYio setValues(double runnerValue, int numberOfValues) {
        setRunnerValue((float) runnerValue);
        setNumberOfSegments(numberOfValues - 1);
        animDistance = -1;
        updateValueString();

        return getThis();
    }


    public void setRunnerValue(float runnerValue) {
        this.runnerValue = runnerValue;
    }


    public void setValueIndex(int index) {
        setRunnerValue((float)index / numberOfSegments);
        updateValueString();
    }


    public int getValueIndex() {
        return (int) (runnerValue / segmentSize + 0.5);
    }


    public void setNumberOfSegments(int numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
        segmentSize = 1.0f / numberOfSegments;
        viewMagnifier = (numberOfSegments + 1f) / numberOfSegments;
    }


    public void addListener(SliderListener listener) {
        if (listeners.contains(listener)) return;
        listeners.add(listener);
    }


    void notifyListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onSliderChange(this);
        }
    }


    @Override
    public void onSliderChange(SliderYio sliderYio) {
        behavior.onAnotherSliderValueChanged(this, sliderYio);
        updateValueString();
    }


    public float getSegmentCircleSize() {
        return 0.4f * circleSize;
    }


    public float getSegmentLeftSidePos(int index) {
        return viewPosition.x + index * segmentSize * viewPosition.width;
    }


    public boolean isSegmentsVisible() {
        return segmentsVisible;
    }


    public SliderYio setSegmentsVisible(boolean segmentsVisible) {
        this.segmentsVisible = segmentsVisible;
        return this;
    }


    public RectangleYio getTouchRectangle() {
        touchRectangle.x = viewPosition.x - 0.05f * Gdx.graphics.getWidth();
        touchRectangle.y = viewPosition.y - verticalTouchOffset;
        touchRectangle.width = viewPosition.width + 0.1f * Gdx.graphics.getWidth();
        touchRectangle.height = 2 * verticalTouchOffset;;
        return touchRectangle;
    }


    public void updateValueString() {
        valueString = behavior.getValueString(menuControllerYio.languagesManager, this);
        textWidth = GraphicsYio.getTextWidth(Fonts.gameFont, valueString);
        notifyListeners();
    }


    public SliderYio setVerticalTouchOffset(float verticalTouchOffset) {
        this.verticalTouchOffset = verticalTouchOffset;
        return getThis();
    }


    public String getValueString() {
        return valueString;
    }


    public boolean isAccentVisible() {
        return accentVisible;
    }


    public void setAccentVisible(boolean accentVisible) {
        this.accentVisible = accentVisible;
    }


    public SliderYio setSolidWidth(boolean solidWidth) {
        this.solidWidth = solidWidth;
        return this;
    }


    public SliderYio addToList(ArrayList<SliderYio> list) {
        Yio.addToEndByIterator(list, this);
        return getThis();
    }


    public SliderYio setName(String key) {
        this.name = LanguagesManager.getInstance().getString(key);

        return this;
    }


    public String getName() {
        return name;
    }


    public PointYio getNamePosition() {
        return namePosition;
    }


    public int getMaxIndex() {
        return numberOfSegments + 1;
    }


    public SliderYio setBehavior(SliderBehavior behavior) {
        this.behavior = behavior;
        updateValueString();

        return this;
    }


    @Override
    public String toString() {
        return "[Slider: " +
                name +
                "]";
    }
}
