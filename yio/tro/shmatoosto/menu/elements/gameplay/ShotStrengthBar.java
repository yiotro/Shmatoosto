package yio.tro.shmatoosto.menu.elements.gameplay;

import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

public class ShotStrengthBar {


    BilliardAimUiElement billiardAimUiElement;
    public RectangleYio position, viewPosition;
    public RectangleYio redRectangle;
    public double currentPower;
    float touchOffset;
    public CircleYio viewStickCircle;
    boolean shootMode;


    public ShotStrengthBar(BilliardAimUiElement billiardAimUiElement) {
        this.billiardAimUiElement = billiardAimUiElement;

        position = new RectangleYio();
        viewPosition = new RectangleYio();
        redRectangle = new RectangleYio();
        currentPower = 0;
        touchOffset = 0.02f * GraphicsYio.width;
        viewStickCircle = new CircleYio();
        shootMode = false;
    }


    void move() {
        updateViewPosition();
        updateRedRectangle();
        updateViewStickCircle();
    }


    private void updateViewStickCircle() {
        viewStickCircle.radius = viewPosition.height / 2;
        viewStickCircle.center.x = viewPosition.x + viewPosition.width / 2;

        if (!shootMode) {
            float ty = (float) (viewPosition.y + viewPosition.height - currentPower * viewPosition.height - viewStickCircle.radius);
            viewStickCircle.center.y += 0.2 * (ty - viewStickCircle.center.y);
        } else {
            viewStickCircle.center.y += 0.08f * GraphicsYio.width;
            if (viewStickCircle.center.y > viewPosition.y + viewPosition.height - viewStickCircle.radius) {
                viewStickCircle.center.y = viewPosition.y + viewPosition.height - viewStickCircle.radius;
            }
        }
    }


    boolean isTouched(PointYio currentTouch) {
        return InterfaceElement.isTouchInsideRectangle(currentTouch.x, currentTouch.y, viewPosition, touchOffset, touchOffset);
    }


    void updateCurrentPower(PointYio currentTouch) {
        if (currentTouch.y > viewPosition.y + viewPosition.height) {
            setCurrentPower(0);
            return;
        }

        if (currentTouch.y < viewPosition.y) {
            setCurrentPower(1);
            return;
        }

        setCurrentPower(1 - (currentTouch.y - viewPosition.y) / viewPosition.height);
    }


    public void setCurrentPower(double currentPower) {
        if (this.currentPower == currentPower) return;

        this.currentPower = currentPower;
    }


    public void setShootMode(boolean shootMode) {
        this.shootMode = shootMode;
    }


    private void updateRedRectangle() {
        redRectangle.setBy(viewPosition);
        redRectangle.height = (float) (currentPower * viewPosition.height);
        redRectangle.y = viewPosition.y + viewPosition.height - redRectangle.height;
    }


    private void updateViewPosition() {
        viewPosition.setBy(position);
        viewPosition.x += 1.5f * (1 - getMovePosFactor()) * position.width;
    }


    private float getMovePosFactor() {
        if (billiardAimUiElement.getFactor().isInDestroyState()) return 1;

        return billiardAimUiElement.getFactor().get();
    }
}
