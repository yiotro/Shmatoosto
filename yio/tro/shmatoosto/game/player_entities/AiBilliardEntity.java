package yio.tro.shmatoosto.game.player_entities;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.Difficulty;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.gameplay.*;
import yio.tro.shmatoosto.game.gameplay.billiard.BilliardAiPreparationManager;
import yio.tro.shmatoosto.game.gameplay.billiard.BilliardManager;
import yio.tro.shmatoosto.menu.scenes.Scenes;

import java.util.ArrayList;

public class AiBilliardEntity extends AbstractPlayerEntity {

    ArrayList<AiShotItem> shotItems;
    private BilliardAiPreparationManager aiPreparationManager;
    ArrayList<AiListener> listeners;
    private long startTime;
    private SimulationManager simulationManager;
    private Ball cue;
    Ball tempBall;


    public AiBilliardEntity(GameController gameController) {
        super(gameController);
        shotItems = new ArrayList<>();
        listeners = new ArrayList<>();
        tempBall = new Ball(gameController.objectsLayer);
    }


    @Override
    public void onApplyAimingMode() {
        startTime = System.currentTimeMillis();
        prepareReferences();
        if (cue == null) return;

        if (checkForImpossibleDifficulty()) return;

        updateShotItemsByUsualStuff();
        filterScrewedShotItems();
        findSomeInterestingShots();

        if (tryToFindGoodEnoughSolution()) return;

        onGoodEnoughShotItemNodFound();
    }


    private boolean checkForImpossibleDifficulty() {
        if (GameRules.difficulty != Difficulty.IMPOSSIBLE) return false;

        BilliardManager billiardManager = (BilliardManager) gameController.gameplayManager;
        if (billiardManager.isTooEarlyToStartCountingScores()) return false;

        double bestAngle = -1;
        int bestScore = 0;
        int currentScore;
        double startAngle = Yio.getRandomAngle();

        for (double a = startAngle; a < startAngle + 2 * Math.PI; a += 0.15) {
            Ball cue = gameController.objectsLayer.findCue();
            SimResults results = simulationManager.perform(cue, a, cue.maxSpeed);
            currentScore = results.scoreDelta;
            results.dispose();

            if (bestAngle == -1 || currentScore > bestScore) {
                bestAngle = a;
                bestScore = currentScore;
            }

            if (bestScore >= 3) break;
        }

        AiShotItem next = aiPreparationManager.poolShotItems.getNext();
        next.setScoreDelta(bestScore);
        next.setAngle(bestAngle);
        next.setPower(cue.maxSpeed);
        applyShotItem(next);
        aiPreparationManager.poolShotItems.add(next);

        return true;
    }


    private void findSomeInterestingShots() {
        aiPreparationManager.updateShotItemsByDoubleShots(true);
        if (aiPreparationManager.shotItems.size() == 0) return;

        shotItems.addAll(0, aiPreparationManager.shotItems);
    }


    private void filterScrewedShotItems() {
        // this filters shots with first collision not with target ball

        tempBall.setRadius(getCue().position.radius);
        tempBall.updateCollisionRadius();

        // this should leave at least 1 shot item
        for (int i = shotItems.size() - 1; i >= 1; i--) {
            AiShotItem aiShotItem = shotItems.get(i);
            if (!isShotItemScrewed(aiShotItem)) continue;

            shotItems.remove(i);
        }
    }


    private boolean isShotItemScrewed(AiShotItem aiShotItem) {
        tempBall.position.center.setBy(getCue().position.center);
        Ball targetBall = aiShotItem.targetBall;

        float distance = tempBall.position.center.distanceTo(targetBall.position.center);
        double a = aiShotItem.angle;
        float step = tempBall.collisionRadius;
        int n = (int) (distance / step) - 1;
        if (n > 50) return false; // step can be zero sometimes

        for (int i = 0; i < n; i++) {
            tempBall.position.center.relocateRadial(step, a);
            gameController.objectsLayer.updateNearbyObjects(tempBall);
            for (Ball nearbyBall : gameController.objectsLayer.nearbyBalls) {
                if (nearbyBall.isCue()) continue;
                if (nearbyBall == targetBall) continue;
                if (!tempBall.isInCollisionWith(nearbyBall)) continue;

                return true;
            }
        }

        return false;
    }


    private boolean tryToFindGoodEnoughSolution() {
        for (AiShotItem shotItem : shotItems) {
            SimResults results;
            results = simulationManager.perform(cue, shotItem.angle, shotItem.power);
            cue = getCue(); // update reference after snapshot recreated
            shotItem.setScoreDelta(results.scoreDelta);
            shotItem.setCollisionBeforeFirstScore(results.collisionBeforeFirstScore);
            results.dispose();

            if (isShotItemGoodEnough(shotItem)) {
                applyShotItem(shotItem);
                return true;
            }
        }

        return false;
    }


    private void onGoodEnoughShotItemNodFound() {
        AiShotItem bestShotItem = findBestShotItem();
        if (bestShotItem == null) return;

        applyShotItem(bestShotItem);
    }


    private Ball getCue() {
        return gameController.objectsLayer.findCue();
    }


    private void applyShotItem(AiShotItem aiShotItem) {
        checkToScrewUp(aiShotItem);
        notifyListeners(aiShotItem);

        Scenes.billiardAimUI.create();
        Scenes.billiardAimUI.applyTargetShot(aiShotItem.power / getCue().maxSpeed, aiShotItem.angle);
    }


    private void checkToScrewUp(AiShotItem aiShotItem) {
        if (YioGdxGame.random.nextDouble() >= getScrewUpChance()) return;

        aiShotItem.angle += (2 * YioGdxGame.random.nextDouble() - 1) * 0.1;
    }


    private double getScrewUpChance() {
        switch (GameRules.difficulty) {
            default:
            case Difficulty.HARD:
                return 0;
            case Difficulty.NORMAL:
                return 0.35;
            case Difficulty.EASY:
                return 0.65;
        }
    }


    private boolean isShotItemGoodEnough(AiShotItem shotItem) {
        BilliardManager billiardManager = (BilliardManager) gameController.gameplayManager;
        if (billiardManager.isTooEarlyToStartCountingScores()) {
            return true;
        }

        return shotItem.scoreDelta >= 1;
    }


    private void notifyListeners(AiShotItem aiShotItem) {
        if (!DebugFlags.testingModeEnabled) return;

        for (AiListener listener : listeners) {
            listener.onScorePredicted(aiShotItem.scoreDelta);
            listener.onTimeTaken((int) (System.currentTimeMillis() - startTime));
        }
    }


    private AiShotItem findBestShotItem() {
        BilliardManager billiardManager = (BilliardManager) gameController.gameplayManager;
        if (billiardManager.isTooEarlyToStartCountingScores()) {
            return findShotItemForFirstShot();
        }

        AiShotItem bestItem = null;
        int bestScore = 0;

        for (AiShotItem shotItem : shotItems) {
            if (bestItem == null || shotItem.scoreDelta > bestScore) {
                bestItem = shotItem;
                bestScore = shotItem.scoreDelta;
            }
        }

        return bestItem;
    }


    public double findMediumScore() {
        double sum = 0;

        for (AiShotItem shotItem : shotItems) {
            sum += shotItem.scoreDelta;
        }

        return sum / shotItems.size();
    }


    private AiShotItem findShotItemForFirstShot() {
        for (AiShotItem shotItem : shotItems) {
            if (shotItem.scoreDelta == 0) return shotItem;
        }

        int size = shotItems.size();
        if (size == 0) return null;

        return shotItems.get(YioGdxGame.random.nextInt(size));
    }


    private void prepareReferences() {
        AbstractGameplayManager gameplayManager = gameController.gameplayManager;
        BilliardManager billiardManager = (BilliardManager) gameplayManager;
        aiPreparationManager = billiardManager.aiPreparationManager;
        simulationManager = gameController.objectsLayer.simulationManager;
        cue = getCue();
    }


    private void updateShotItemsByUsualStuff() {
        shotItems.clear();

        for (Ball ball : gameController.objectsLayer.balls) {
            if (ball.isCue()) continue;

            aiPreparationManager.setInspectedBall(ball);
            aiPreparationManager.updateInspectionList();
            aiPreparationManager.updateShotItemsByInspectionList();
            shotItems.addAll(aiPreparationManager.shotItems);
        }
    }


    public void addListener(AiListener aiListener) {
        listeners.add(aiListener);
    }


    public void removeListener(AiListener aiListener) {
        listeners.remove(aiListener);
    }


    @Override
    public boolean isHuman() {
        return false;
    }
}
