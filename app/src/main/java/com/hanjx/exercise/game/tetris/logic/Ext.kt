package com.hanjx.exercise.game.tetris.logic

const val BLOCK_POINT_COUNT = 4
const val COLUMN_COUNT = 12
const val ROW_COUNT = 24

val List<Offset>.left: List<Offset>
    get() {
        val left = mutableMapOf<Int, Offset>()
        forEach {
            val curr = left[it.y]
            if (curr == null || it.x < curr.x) {
                left[it.y] = it
            }
        }
        return left.values.toList()
    }

val List<Offset>.right: List<Offset>
    get() {
        val right = mutableMapOf<Int, Offset>()
        forEach {
            val curr = right[it.y]
            if (curr == null || it.x > curr.x) {
                right[it.y] = it
            }
        }
        return right.values.toList()
    }

val List<Offset>.bottom: List<Offset>
    get() {
        val bottom = mutableMapOf<Int, Offset>()
        forEach {
            val curr = bottom[it.x]
            if (curr == null || it.y > curr.y) {
                bottom[it.x] = it
            }
        }
        return bottom.values.toList()
    }

fun Offset.inScreen(
    row: Int = ROW_COUNT,
    column: Int = COLUMN_COUNT
): Boolean {
    return x in 0 until column && y in 0 until row
}

fun List<Offset>.offsets2Indexes(
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