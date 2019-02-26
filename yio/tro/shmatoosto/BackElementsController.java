package yio.tro.shmatoosto;

import yio.tro.shmatoosto.stuff.AtlasLoader;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RepeatYio;

import java.util.ArrayList;

public class BackElementsController {

    private final YioGdxGame yioGdxGame;
    int currentBckElementIndex;
    ArrayList<BackElement> backElements;
    RepeatYio<BackElementsController> repeatResurrectElements;
    RepeatYio<BackElementsController> repeatInternalMovement;
    AtlasLoader atlasLoader;


    public BackElementsController(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;

        initAtlas();
        initBackElements();
    }


    private void initAtlas() {
        atlasLoader = new AtlasLoader("menu/back_elements/atlas_texture.png", "menu/back_elements/atlas_structure.txt", false);
    }


    void initBackElements() {
        currentBckElementIndex = 0;

        backElements = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            BackElement element = new BackElement(this);
            backElements.add(element);
        };

        initRepeats();

        for (int i = 0; i < 100; i++) {
            internalMovement();
        }
    }


    private void initRepeats() {
        repeatResurrectElements = new RepeatYio<BackElementsController>(this, 10) {
            @Override
            public void performAction() {
                parent.resurrectRandomBackElement();
            }
        };

        repeatInternalMovement = new RepeatYio<BackElementsController>(this, 1) {
            @Override
            public void performAction() {
                parent.internalMovement();
            }
        };
    }


    void move() {
        if (!yioGdxGame.isGamePaused()) return;

        repeatInternalMovement.move();
    }


    private void internalMovement() {
        for (BackElement backElement : backElements) {
            backElement.move();
        }

        repeatResurrectElements.move();
    }


    void resurrectRandomBackElement() {
        int x = YioGdxGame.random.nextInt((int) (GraphicsYio.width / BackElement.size) + 1);
        int y = YioGdxGame.random.nextInt((int) (GraphicsYio.height / BackElement.size) + 1);
        getNextBackElement().resurrect(x, y);
    }


    BackElement getNextBackElement() {
        BackElement element = backElements.get(currentBckElementIndex);
        currentBckElementIndex++;
        if (currentBckElementIndex >= backElements.size()) {
            currentBckElementIndex = 0;
        }
        return element;
    }
}