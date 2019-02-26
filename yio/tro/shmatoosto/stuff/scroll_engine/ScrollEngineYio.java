package yio.tro.shmatoosto.stuff.scroll_engine;

import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;

public class ScrollEngineYio {


    private SegmentYio limits;
    private SegmentYio slider;
    private double speed, maxSpeed;
    private double friction;
    private double softLimitOffset;
    private double cutSpeed, cutOffset;
    private double correction;
    private boolean canSoftCorrect, blockMovement;
    private boolean targetEnabled;
    private double target, tStart;
    private FactorYio targetFactor;


    public ScrollEngineYio() {
        limits = new SegmentYio();
        slider = new SegmentYio();

        resetToBottom();
        friction = 0;
        softLimitOffset = 0;
        cutSpeed = 1;
        cutOffset = 1;
        canSoftCorrect = true;
        blockMovement = false;
        maxSpeed = 0.15 * GraphicsYio.width;

        targetEnabled = false;
        target = -1;
        tStart = -1;
        targetFactor = new FactorYio();
    }


    public void resetToBottom() {
        resetSpeed();
        correction = limits.a - slider.a;
        if (correction == 0) return;

        relocate(correction);
    }


    public void resetToTop() {
        resetSpeed();
        correction = limits.b - slider.b;
        if (correction == 0) return;

        relocate(correction);
    }


    public void giveImpulse(double impulse) {
        if (impulse > 0 && isOverTop()) return;
        if (impulse < 0 && isBelowBottom()) return;

        speed += impulse;
    }


    private void relocate(double delta) {
        if (blockMovement) return;

        slider.a += delta;
        slider.b += delta;
    }


    public void move() {
        if (blockMovement) return;

        moveTarget();

        if (speed == 0) {
            softCorrection();
            return;
        }

        limitSpeed();
        relocate(speed);
        updateSpeed();
        hardCorrection();
    }


    private void moveTarget() {
        if (!targetEnabled) return;

        if (targetFactor.move()) {
            setSpeed(0);
            double currentTarget = tStart + targetFactor.get() * (target - tStart);
            relocate(currentTarget - slider.a);
        } else {
            targetEnabled = false;
        }
    }


    public void setTarget(double target) {
        this.target = target;

        if (this.target > limits.b - slider.getLength()) {
            this.target = limits.b - slider.getLength();
        }

        targetEnabled = true;
        targetFactor.setValues(0, 0);
        targetFactor.appear(6, 1.2);
        tStart = slider.a;
    }


    public void cancelTarget() {
        targetEnabled = false;
    }


    private void limitSpeed() {
        if (speed > maxSpeed) {
            speed = maxSpeed;
        }

        if (speed < -maxSpeed) {
            speed = -maxSpeed;
        }
    }


    private void updateSpeed() {
        speed *= (1 - friction);

        if (Math.abs(speed) < cutSpeed) {
            resetSpeed();
        }
    }


    public void hardCorrection() {
        // bottom
        correction = (limits.a - softLimitOffset) - slider.a;
        if (correction > 0) {
            relocate(correction);
            resetSpeed();
            return;
        }

        // top
        correction = (limits.b + softLimitOffset) - slider.b;
        if (correction < 0) {
            relocate(correction);
            resetSpeed();
        }
    }


    private boolean softCorrection() {
        if (!canSoftCorrect || speed != 0) return false;

        // bottom
        correction = limits.a - slider.a;
        if (correction > 0) {
            if (correction < cutOffset) {
                resetToBottom();
            } else {
                relocate(0.1 * correction);
            }
            return true;
        }

        // top
        correction = limits.b - slider.b;
        if (correction < 0) {
            if (correction > -cutOffset) {
                resetToTop();
            } else {
                relocate(0.1 * correction);
            }
            return true;
        }

        return false;
    }


    private void resetSpeed() {
        speed = 0;
    }


    public SegmentYio getSlider() {
        return slider;
    }


    public void setFriction(double friction) {
        this.friction = friction;
    }


    public void setLimits(double a, double b) {
        limits.set(a, b);

        updateBlockMovement();
    }


    private void updateBlockMovement() {
        blockMovement = (slider.getLength() > limits.getLength());
    }


    public SegmentYio getLimits() {
        return limits;
    }


    public void setSoftLimitOffset(double softLimitOffset) {
        this.softLimitOffset = softLimitOffset;
    }


    public void setCutSpeed(double cutSpeed) {
        this.cutSpeed = cutSpeed;
    }


    public void updateCanSoftCorrect(boolean canSoftCorrect) {
        this.canSoftCorrect = canSoftCorrect;
    }


    public boolean isBelowBottom() {
        return slider.a < limits.a;
    }


    public boolean isOverTop() {
        return slider.b > limits.b;
    }


    public void setSpeed(double speed) {
        this.speed = speed;
    }


    public void setSlider(double a, double b) {
        slider.set(a, b);

        updateBlockMovement();
    }


    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }


    @Override
    public String toString() {
        return "[ScrollEngine" +
                " limit" + limits +
                ", slider" + slider +
                "]";
    }
}
