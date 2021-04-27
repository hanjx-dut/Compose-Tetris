package com.hanjx.exercise.game.tetris.logic

const val COLUMN_COUNT = 12
const val ROW_COUNT = 24
const val ROW_CENTER = 5

val Set<Offset>.left: Set<Offset>
    get() {
        val left = mutableMapOf<Int, Offset>()
        forEach {
            val curr = left[it.y]
            if (curr == null || it.x < curr.x) {
                left[it.y] = it
            }
        }
        return left.values.toSet()
    }

val Set<Offset>.right: Set<Offset>
    get() {
        val right = mutableMapOf<Int, Offset>()
        forEach {
            val curr = right[it.y]
            if (curr == null || it.x > curr.x) {
                right[it.y] = it
            }
        }
        return right.values.toSet()
    }

val Set<Offset>.bottom: Set<Offset>
    get() {
        val bottom = mutableMapOf<Int, Offset>()
        forEach {
            val curr = bottom[it.x]
            if (curr == null || it.y > curr.y) {
                bottom[it.x] = it
            }
        }
        return bottom.values.toSet()
    }
