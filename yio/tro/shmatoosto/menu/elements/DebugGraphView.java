package yio.tro.shmatoosto.menu.elements;

import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;

import java.util.ArrayList;

public class DebugGraphView extends InterfaceElement<DebugGraphView> {

    ArrayList<PointYio> sources, views;
    ArrayList<Double> results;
    String name;
    PointYio namePosition;
    private float nameOffset;
    boolean nameActive;
    private float textWidth;


    public DebugGraphView(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);

        sources = new ArrayList<>();
        views = new ArrayList<>();
        results = new ArrayList<>();

        name = null;
        namePosition = new PointYio();
        nameOffset = 0.02f * GraphicsYio.width;
        nameActive = false;
    }


    private void processResults() {
        int size = results.size();
        double xDelta = 1d / (size - 1);

        double max = detectMaxResult();
        double min = detectMinResult();

        double x = 0;
        double y;

        sources.clear();
        views.clear();
        for (Double result : results) {
            y = (result - min) / (max - min);

            PointYio pointYio = new PointYio();
            pointYio.set(x, y);
            sources.add(pointYio);
            views.add(new PointYio());

            x += xDelta;
        }
    }


    public ArrayList<PointYio> getViews() {
        return views;
    }


    private double detectMaxResult() {
        double max = 0;
        boolean detected = false;

        for (Double result : results) {
            if (!detected || result > max) {
                max = result;
                detected = true;
            }
        }

        return max;
    }


    private double detectMinResult() {
        double min = 0;
        boolean detected = false;

        for (Double result : results) {
            if (!detected || result < min) {
                min = result;
                detected = true;
            }
        }

        return min;
    }


    public DebugGraphView draw(int type, double speed) {
        FactorYio factorYio = new FactorYio();

        factorYio.appear(type, speed);
        factorYio.setValues(0, 0.01);

//        factorYio.destroy(type, speed);
//        factorYio.setValues(1, -0.01);

        clearResults();
        while (factorYio.hasToMove()) {
            addResult(factorYio.get());
            factorYio.move();
        }

        processResults();

        return this;
    }


    public DebugGraphView clearResults() {
        results.clear();
        return this;
    }


    public DebugGraphView addResult(double result) {
        Yio.addToEndByIterator(results, result);
        return this;
    }


    @Override
    protected DebugGraphView getThis() {
        return this;
    }


    @Override
    public void move() {
        updateViewPosition();

        if (factorMoved && hasName()) {
            updateNameActive();
        }
    }


    private void updateNameActive() {
        textWidth = GraphicsYio.getTextWidth(Fonts.gameFont, name);
        nameActive = (textWidth + nameOffset < viewPosition.width);
    }


    public boolean isNameActive() {
        return nameActive;
    }


    private void updateNamePosition() {
        namePosition.x = viewPosition.x + nameOffset;
        namePosition.y = viewPosition.y + viewPosition.height - nameOffset;
    }


    @Override
    protected void onApplyParent() {
        super.onApplyParent();

        for (int i = 0; i < views.size(); i++) {
            PointYio view = views.get(i);
            PointYio src = sources.get(i);

            view.x = viewPosition.x + src.x * viewPosition.width;
            view.y = viewPosition.y + src.y * viewPosition.height;
        }

        updateNamePosition();
    }


    @Override
    public void onDestroy() {

    }


    @Override
    public void onAppear() {

    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    public DebugGraphView setName(String name) {
        this.name = name;
        return this;
    }


    public String getName() {
        return name;
    }


    public PointYio getNamePosition() {
        return namePosition;
    }


    public boolean hasName() {
        return name != null;
    }


    @Override
    public boolean touchDown() {
        return false;
    }


    @Override
    public boolean touchDrag() {
        return false;
    }


    @Override
    public boolean touchUp() {
        return false;
    }


    @Override
    public RenderInterfaceElement getRenderSystem() {
        return MenuRenders.renderDebugGraphView;
    }
}
