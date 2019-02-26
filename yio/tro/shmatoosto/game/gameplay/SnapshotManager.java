package yio.tro.shmatoosto.game.gameplay;

import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;

public class SnapshotManager {


    GameController gameController;
    public ArrayList<Snapshot> snapshots;
    ObjectPoolYio<Snapshot> poolSnapshots;


    public SnapshotManager(GameController gameController) {
        this.gameController = gameController;
        snapshots = new ArrayList<>();
        initPools();
    }


    public void clear() {
        for (Snapshot snapshot : snapshots) {
            poolSnapshots.add(snapshot);
        }

        snapshots.clear();
    }


    public Snapshot getLastSnapshot() {
        if (snapshots.size() == 0) return null;

        return snapshots.get(snapshots.size() - 1);
    }


    public void recreateLastSnapshot() {
        Snapshot lastSnapshot = getLastSnapshot();
        if (lastSnapshot == null) return;

        lastSnapshot.recreate();
    }


    public Snapshot takeSnapshot() {
        Snapshot next = poolSnapshots.getNext();
        next.syncWithCurrentBoardState();
        snapshots.add(next);
        return next;
    }


    public void removeSnapshot(Snapshot snapshot) {
        if (!snapshots.contains(snapshot)) return;

        poolSnapshots.addWithCheck(snapshot);
        snapshots.remove(snapshot);
    }


    private void initPools() {
        poolSnapshots = new ObjectPoolYio<Snapshot>() {
            @Override
            public Snapshot makeNewObject() {
                return new Snapshot(gameController);
            }
        };
    }
}
