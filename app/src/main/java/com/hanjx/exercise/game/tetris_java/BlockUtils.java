package com.hanjx.exercise.game.tetris_java;

import com.hanjx.exercise.game.tetris.logic.Offset;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BlockUtils {
    public static Collection<Offset> getOffsetsLeft(Collection<Offset> offsets) {
        Map<Integer, Offset> leftMap = new HashMap<>();
        for (Offset offset : offsets) {
            Offset curr = leftMap.get(offset.getY());
            if (curr == null || offset.getX() < curr.getX()) {
                leftMap.put(offset.getY(), offset);
            }
        }
        return leftMap.values();
    }

    public static Collection<Offset> getOffsetsRight(Collection<Offset> offsets) {
        Map<Integer, Offset> leftMap = new HashMap<>();
        for (Offset offset : offsets) {
            Offset curr = leftMap.get(offset.getY());
            if (curr == null || offset.getX() > curr.getX()) {
                leftMap.put(offset.getY(), offset);
            }
        }
        return leftMap.values();
    }

    public static Collection<Offset> getOffsetsBottom(Collection<Offset> offsets) {
        Map<Integer, Offset> leftMap = new HashMap<>();
        for (Offset offset : offsets) {
            Offset curr = leftMap.get(offset.getX());
            if (curr == null || offset.getY() > curr.getY()) {
                leftMap.put(offset.getX(), offset);
            }
        }
        return leftMap.values();
    }
}
