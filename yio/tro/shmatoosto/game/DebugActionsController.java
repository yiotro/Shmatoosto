package yio.tro.shmatoosto.game;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.game_objects.ObjectsLayer;
import yio.tro.shmatoosto.game.gameplay.*;
import yio.tro.shmatoosto.game.gameplay.billiard.BilliardAiPreparationManager;
import yio.tro.shmatoosto.game.gameplay.billiard.BilliardManager;
import yio.tro.shmatoosto.game.gameplay.chapaevo.ChapaevoManager;
import yio.tro.shmatoosto.game.tests.TestAiScore;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.elements.gameplay.BilliardAimUiElement;
import yio.tro.shmatoosto.menu.elements.gameplay.ChapaevoAimUiElement;
import yio.tro.shmatoosto.menu.scenes.Scenes;

import java.util.ArrayList;

public class DebugActionsController {

    GameController gameController;
    private final YioGdxGame yioGdxGame;
    private final MenuControllerYio menuControllerYio;


    public DebugActionsController(GameController gameController) {
        this.gameController = gameController;
        yioGdxGame = gameController.yioGdxGame;
        menuControllerYio = yioGdxGame.menuControllerYio;
    }


    public void debugActions() {
        updateReferences();
        //
        doShowCurrentAimAngle();
    }


    private void doShowIfProblemWasDetected() {
        System.out.println("DebugFlags.problemDetected = " + DebugFlags.problemDetected);
    }


    private void doCheckChapaevoScores() {
        System.out.println();
        System.out.println("DebugActionsController.doCheckChapaevoScores");

        ChapaevoManager chapaevoManager = (ChapaevoManager) gameController.gameplayManager;
        System.out.println("chapaevoManager.firstScore = " + chapaevoManager.firstScore);
        System.out.println("chapaevoManager.secondScore = " + chapaevoManager.secondScore);
    }


    private void doCheckSimulationInChapaevoMode() {
        ChapaevoAimUiElement chapaevoAimUiElement = Scenes.chapaevoAimUI.chapaevoAimUiElement;

        ChapaevoManager chapaevoManager = (ChapaevoManager) gameController.gameplayManager;
        SimulationManager simulationManager = getSimulationManager();

        BColorYio entityColor = chapaevoManager.getEntityColor(gameController.getCurrentPlayerEntity());
        simulationManager.setCurrentColor(entityColor);
        Ball selectedBall = chapaevoAimUiElement.selectedBall;
        SimResults simResults = simulationManager.perform(selectedBall, chapaevoAimUiElement.aimAngle, chapaevoAimUiElement.aimPower * selectedBall.maxSpeed);
        chapaevoAimUiElement.fixSelectedBallReferenceAfterSimulation();

        System.out.println("simResults = " + simResults);

        simResults.dispose();
    }


    private void doShowCurrentAimAngle() {
        BilliardAimUiElement billiardAimUiElement = Scenes.billiardAimUI.billiardAimUiElement;
        System.out.println("billiardAimUI.aimAngle = " + Yio.roundUp(billiardAimUiElement.aimAngle, 4));
    }


    private void doFindSomeNiceShots() {
        AbstractGameplayManager gameplayManager = gameController.gameplayManager;
        if (!(gameplayManager instanceof BilliardManager)) return;

        BilliardManager billiardManager = (BilliardManager) gameplayManager;
        BilliardAiPreparationManager aiPreparationManager = billiardManager.aiPreparationManager;

        aiPreparationManager.updateShotItemsByDoubleShots(true);
        ArrayList<AiShotItem> shotItems = aiPreparationManager.shotItems;
        if (shotItems.size() == 0) {
            System.out.println("Wasn't been able to find any double shots");
            return;
        }

        System.out.println("Double shot found");
        AiShotItem aiShotItem = shotItems.get(YioGdxGame.random.nextInt(shotItems.size()));
        BilliardAimUiElement billiardAimUiElement = Scenes.billiardAimUI.billiardAimUiElement;
        billiardAimUiElement.aimAngle = aiShotItem.angle;
    }


    private void doFindSomeDoubleShots() {
        AbstractGameplayManager gameplayManager = gameController.gameplayManager;
        if (!(gameplayManager instanceof BilliardManager)) return;

        BilliardManager billiardManager = (BilliardManager) gameplayManager;
        BilliardAiPreparationManager aiPreparationManager = billiardManager.aiPreparationManager;

        aiPreparationManager.updateShotItemsByDoubleShots(false);
        ArrayList<AiShotItem> shotItems = aiPreparationManager.shotItems;
        if (shotItems.size() == 0) {
            System.out.println("Wasn't been able to find any double shots");
            return;
        }

        System.out.println("Double shot found");
        AiShotItem aiShotItem = shotItems.get(YioGdxGame.random.nextInt(shotItems.size()));
        BilliardAimUiElement billiardAimUiElement = Scenes.billiardAimUI.billiardAimUiElement;
        billiardAimUiElement.aimAngle = aiShotItem.angle;
    }


    private void doFindAngleWhereQuickSimulationIsWrong() {
        SimulationManager simulationManager = getSimulationManager();
        Ball cue;
        BilliardAimUiElement billiardAimUiElement = Scenes.billiardAimUI.billiardAimUiElement;

        double sa = Yio.getRandomAngle();
        ArrayList<AiShotItem> shotItems = new ArrayList<>();
        for (double a = sa; a < sa + 2 * Math.PI; a += 0.01) {
            cue = getObjectsLayer().findCue();
            SimResults quickResults = simulationManager.perform(cue, a, cue.maxSpeed);
            cue = getObjectsLayer().findCue();
            SimResults normalResults = simulationManager.perform(cue, a, cue.maxSpeed);

            quickResults.dispose();
            normalResults.dispose();

            if (!quickResults.equals(normalResults)) {
                AiShotItem aiShotItem = new AiShotItem();
                aiShotItem.setAngle(a);
                aiShotItem.setScoreDelta(normalResults.scoreDelta);
                aiShotItem.setFunScore(quickResults.scoreDelta);
                shotItems.add(aiShotItem);
            }
        }

        if (shotItems.size() == 0) {
            System.out.println("Wasn't been able to find angle with wrong quick results");
            return;
        }

        AiShotItem worstItem = shotItems.get(0);
        int worstD = 0;
        for (AiShotItem shotItem : shotItems) {
            int d = (int) Math.abs(shotItem.scoreDelta - shotItem.funScore); // fun score is actually quick results
            if (d > worstD) {
                worstD = d;
                worstItem = shotItem;
            }
        }

        billiardAimUiElement.aimAngle = worstItem.angle;
        System.out.println();
        System.out.println("DebugActionsController.doFindAngleWhereQuickSimulationIsWrong");
        System.out.println("quick result: " + worstItem.scoreDelta);
        System.out.println("normal result: " + ((int) worstItem.funScore));
    }


    private SimulationManager getSimulationManager() {
        return getObjectsLayer().simulationManager;
    }


    private void doShowMaxCurrentBallSpeed() {
        double maxSpeed = 0;

        for (Ball ball : getObjectsLayer().balls) {
            if (ball.getSpeedValue() > maxSpeed) {
                maxSpeed = ball.getSpeedValue();
            }
        }

        maxSpeed /= getObjectsLayer().findCue().collisionRadius;
        maxSpeed = Yio.roundUp(maxSpeed, 2);

        System.out.println("maxSpeed = " + maxSpeed);
    }


    private ObjectsLayer getObjectsLayer() {
        return gameController.objectsLayer;
    }


    public void doSlightlyAdjustAimByAi() {
        doUpdateAiPreparation();

        AbstractGameplayManager gameplayManager = gameController.gameplayManager;
        if (!(gameplayManager instanceof BilliardManager)) return;

        BilliardManager billiardManager = (BilliardManager) gameplayManager;
        BilliardAiPreparationManager aiPreparationManager = billiardManager.aiPreparationManager;

        ArrayList<AiShotItem> shotItems = aiPreparationManager.shotItems;
        if (shotItems.size() == 0) return;

        AiShotItem aiShotItem = shotItems.get(shotItems.size() - 1);

        BilliardAimUiElement billiardAimUiElement = Scenes.billiardAimUI.billiardAimUiElement;
        billiardAimUiElement.aimAngle = aiShotItem.angle;
    }


    private void doPerformAiOnlyMatch() {
        (new TestAiScore(gameController)).perform();
    }


    private void doFindBestAimAngle() {
        System.out.println();
        System.out.println("DebugActionsController.doPickBestAimAngle");

        double bestAngle = -1;
        int bestScore = 0;
        int currentScore;

        SimulationManager simulationManager = getSimulationManager();
        for (double a = 0; a < 2 * Math.PI; a += 0.005) {
            Ball cue = getObjectsLayer().findCue();
            SimResults results = simulationManager.perform(cue, a, cue.maxSpeed);
            currentScore = results.scoreDelta;
            results.dispose();

            if (bestAngle == -1 || currentScore > bestScore) {
                bestAngle = a;
                bestScore = currentScore;
            }
        }

        System.out.println("bestScore = " + bestScore);
        BilliardAimUiElement billiardAimUiElement = Scenes.billiardAimUI.billiardAimUiElement;
        billiardAimUiElement.aimAngle = bestAngle;
    }


    private void doShowSimulationResults() {
        SimulationManager simulationManager = getSimulationManager();
        Ball cue = getObjectsLayer().findCue();
        BilliardAimUiElement billiardAimUiElement = Scenes.billiardAimUI.billiardAimUiElement;
        double aimAngle = billiardAimUiElement.aimAngle;

        SimResults quickResults = simulationManager.perform(cue, aimAngle, cue.maxSpeed);
        System.out.println();
        System.out.println("quickResults.scoreDelta = " + quickResults.scoreDelta);
        quickResults.dispose();

        cue = getObjectsLayer().findCue();
        SimResults normalResults = simulationManager.perform(cue, aimAngle, cue.maxSpeed);
        System.out.println("normalResults.scoreDelta = " + normalResults.scoreDelta);
        normalResults.dispose();
    }


    private void doTestSnapshots() {
        SnapshotManager snapshotManager = gameController.snapshotManager;
        Snapshot lastSnapshot = snapshotManager.getLastSnapshot();

        if (lastSnapshot != null) {
            snapshotManager.recreateLastSnapshot();
        } else {
            snapshotManager.takeSnapshot();
        }
    }


    private void doUpdateAiPreparation() {
        AbstractGameplayManager gameplayManager = gameController.gameplayManager;
        if (!(gameplayManager instanceof BilliardManager)) return;

        BilliardManager billiardManager = (BilliardManager) gameplayManager;
        BilliardAiPreparationManager aiPreparationManager = billiardManager.aiPreparationManager;

        aiPreparationManager.setInspectedBallByAim();
        aiPreparationManager.updateInspectionList();
        aiPreparationManager.updateShotItemsByInspectionList();
    }


    private void doShowVisibleInterfaceElements() {
        menuControllerYio.showVisibleElementsInConsole();
    }


    private void doShowBilliardAimUI() {
        Scenes.billiardAimUI.create();
    }


    private void updateReferences() {
        // no refs currently
    }


}
