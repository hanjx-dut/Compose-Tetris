package com.hanjx.exercise.game.tetris_java;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hanjx.exercise.game.tetris.logic.Action;
import com.hanjx.exercise.game.tetris.logic.Block;
import com.hanjx.exercise.game.tetris.logic.Offset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TetrisViewModel extends ViewModel {
    private int rowCount = Const.ROW_COUNT;
    private int columnCount = Const.COLUMN_COUNT;
    private final MutableLiveData<List<Boolean>> pointStateLiveData = new MutableLiveData<>(new ArrayList<Boolean>() {{
        for (int i = 0; i < Const.COLUMN_COUNT * Const.ROW_COUNT; i++) {
            add(false);
        }
    }});
    private final MutableLiveData<Block> currBlockLiveData = new MutableLiveData<>(new Block());
    private final MutableLiveData<Block> nextBlockLivaDta = new MutableLiveData<>(new Block());
    private final MutableLiveData<Integer> scoreLiveDta = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> recordLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> levelLiveData = new MutableLiveData<>(1);
    private boolean running = false;
    private int gameTime = 0;
    private Handler loopHandler = new Handler();
    private Runnable downLooper = new Runnable() {
        @Override
        public void run() {
            int delay = 650 - (levelLiveData.getValue() * 50);
            gameTime += delay;
            levelLiveData.postValue(Math.min(gameTime / (30 * 1000) + 1, 15));
            doAction(Action.MoveDown);
            loopHandler.postDelayed(this, delay);
        }
    };

    public void doAction(Action action) {
        List<Boolean> currState = pointStateLiveData.getValue();
        Block currBlock = currBlockLiveData.getValue();
        Set<Integer> oldIndexes = offsets2Index(currBlock.getCurrOffsets());
        Offset leftTop = currBlock.getLeftTop();
        boolean changeBlock = false;
        switch (action) {
            case StartGame:
                if (!running) {
                    running = true;
                    loopHandler.postDelayed(downLooper, 650);
                }
                return;
            case PauseGame:
                if (running) {
                    running = false;
                    loopHandler.removeCallbacks(downLooper);
                }
                return;
            case ResetGame:
                running = false;
                loopHandler.removeCallbacks(downLooper);
                for (int i = 0; i < currState.size(); i++) {
                    currState.set(i, false);
                }
                pointStateLiveData.postValue(currState);
                recordLiveData.postValue(Math.max(recordLiveData.getValue(), scoreLiveDta.getValue()));
                currBlockLiveData.postValue(new Block());
                nextBlockLivaDta.postValue(new Block());
                scoreLiveDta.postValue(0);
                levelLiveData.postValue(1);
                return;
            case MoveLeft:
                for (Offset offset : BlockUtils.getOffsetsLeft(currBlock.getCurrOffsets())) {
                    int x = offset.getX(), y = offset.getY();
                    if (x <= 0 || (y >= 0 && currState.get(index(x - 1, y)))) {
                        return;
                    }
                }
                leftTop.setX(leftTop.getX() - 1);
                break;
            case MoveRight:
                for (Offset offset : BlockUtils.getOffsetsRight(currBlock.getCurrOffsets())) {
                    int x = offset.getX(), y = offset.getY();
                    if (x >= columnCount - 1 || (y >= 0 && currState.get(index(x + 1, y)))) {
                        return;
                    }
                }
                leftTop.setX(leftTop.getX() + 1);
                break;
            case Rotate:
                int nextState = currBlock.getState() == currBlock.getOffsets().size() - 1 ?
                        0 : currBlock.getState() + 1;
                List<Offset> next = currBlock.getOffsets().get(nextState);
                for (Offset offset : next) {
                    int x = offset.getX() + leftTop.getX();
                    int y = offset.getY() + leftTop.getY();
                    if (x < 0 || x >= columnCount || y >= rowCount) {
                        return;
                    }
                    int index = index(x, y);
                    if (index >= 0 && !oldIndexes.contains(index) && currState.get(index)) {
                        return;
                    }
                }
                currBlock.setState(nextState);
                break;
            case MoveDown:
                if (canMoveDown()) {
                    leftTop.setY(leftTop.getY() + 1);
                } else {
                    changeBlock = true;
                }
                break;
            case Drop:
                while (canMoveDown()) {
                    leftTop.setY(leftTop.getY() + 1);
                }
                changeBlock = true;
                break;
        }
        Set<Integer> newIndexes = offsets2Index(currBlock.getCurrOffsets());
        for (Integer oldIndex : oldIndexes) {
            if (!newIndexes.contains(oldIndex)) {
                currState.set(oldIndex, false);
            }
        }
        for (Integer newIndex : newIndexes) {
            currState.set(newIndex, true);
        }
        if (changeBlock) {
            if (newIndexes.size() != Const.BLOCK_POINT_COUNT) {
                doAction(Action.ResetGame);
                return;
            } else {
                clearLine();
                currBlockLiveData.postValue(nextBlockLivaDta.getValue());
                nextBlockLivaDta.postValue(new Block());
            }
        }
        pointStateLiveData.postValue(currState);
    }

    private void clearLine() {
        List<Boolean> currState = pointStateLiveData.getValue();
        Set<Integer> newDisplayed = new HashSet<>();
        int newLineIndex = rowCount - 1;
        int clearedLine = 0;
        for (int y = rowCount - 1; y >= 0; y--) {
            int linePointCount = 0;
            for (int x = 0; x < columnCount; x++) {
                if (currState.get(index(x, y))) {
                    linePointCount++;
                }
            }
            if (linePointCount == 0) {
                continue;
            }
            if (linePointCount == columnCount) {
                clearedLine++;
                continue;
            }
            for (int x = 0; x < columnCount; x++) {
                if (currState.get(index(x, y))) {
                    newDisplayed.add(newLineIndex * columnCount + x);
                }
            }
            newLineIndex--;
        }
        if (clearedLine > 0) {
            int currScore = scoreLiveDta.getValue();
            // 1 line = 100, 2 line = 250, 3 line = 400, 4 line = 600
            scoreLiveDta.postValue(currScore + 100 * clearedLine + (50 * (clearedLine)));
            for (int i = 0; i < currState.size(); i++) {
                currState.set(i, newDisplayed.contains(i));
            }
        }
    }

    private boolean canMoveDown() {
        for (Offset offset : BlockUtils.getOffsetsBottom(currBlockLiveData.getValue().getCurrOffsets())) {
            int x = offset.getX(), y = offset.getY();
            if (y >= rowCount - 1 ||
                    (y >= -1 && pointStateLiveData.getValue().get(index(x, y + 1)))) {
                return false;
            }
        }
        return true;
    }

    public MutableLiveData<List<Boolean>> getPointStateLiveData() {
        return pointStateLiveData;
    }

    public MutableLiveData<Block> getCurrBlockLiveData() {
        return currBlockLiveData;
    }

    public MutableLiveData<Block> getNextBlockLivaDta() {
        return nextBlockLivaDta;
    }

    public MutableLiveData<Integer> getScoreLiveDta() {
        return scoreLiveDta;
    }

    public MutableLiveData<Integer> getRecordLiveData() {
        return recordLiveData;
    }

    public MutableLiveData<Integer> getLevelLiveData() {
        return levelLiveData;
    }

    public int index(int x, int y) {
        return x + y * columnCount;
    }

    public Set<Integer> offsets2Index(Collection<Offset> offsets) {
        Set<Integer> result = new HashSet<>();
        for (Offset offset : offsets) {
            int index = index(offset.getX(), offset.getY());
            if (index >= 0) {
                result.add(index);
            }
        }
        return result;
    }
}
