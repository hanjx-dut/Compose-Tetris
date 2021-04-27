package com.hanjx.exercise.game.tetris.logic

const val COLUMN_COUNT = 12
const val ROW_COUNT = 24

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

fun Offset.inScreen(
    row: Int = ROW_COUNT,
    column: Int = COLUMN_COUNT
): Boolean {
    return x in 0 until column && y in 0 until row
}

fun Set<Offset>.offsets2Indexes(
    row: Int = ROW_COUNT,
    column: Int = COLUMN_COUNT
): MutableSet<Int> {
    return mutableSetOf<Int>().also { result ->
        forEach {
            if (it.inScreen(row, column)) {
                result.add(it.x + it.y * column)
            }
        }
    }
}