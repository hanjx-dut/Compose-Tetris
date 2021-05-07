package com.hanjx.exercise.game.tetris.logic

import kotlin.random.Random

class Block {
    val offsets: List<List<Offset>> = blockOffsetEnum[Random.nextInt(blockOffsetEnum.size)]
    var state: Int = Random.nextInt(offsets.size)
    val leftTop: Offset =
        Offset((COLUMN_COUNT - offsets[state].bottom.size) / 2, -offsets[state].left.size)

    val summaryOffset = List(BLOCK_POINT_COUNT) { Offset() }.apply {
        val dx = offsets[state].minOf { it.x }
        val dy = offsets[state].minOf { it.y }
        offsets[state].forEachIndexed { i, offset ->
            this[i].x = offset.x - dx
            this[i].y = offset.y - dy
        }
    }

    val currOffsets: List<Offset>
        get() = reusedCurrOffsets.apply {
            forEachIndexed { i, offset ->
                offset.x = offsets[state][i].x + leftTop.x
                offset.y = offsets[state][i].y + leftTop.y
            }
        }

    companion object {
        private val reusedCurrOffsets = List(4) { Offset() }
        private val O = listOf(
            listOf(Offset(0, 0), Offset(0, 1), Offset(1, 0), Offset(1, 1))
        )
        private val I = listOf(
            listOf(Offset(0, 0), Offset(1, 0), Offset(2, 0), Offset(3, 0)),
            listOf(Offset(1, 0), Offset(1, 1), Offset(1, 2), Offset(1, 3))
        )
        private val Z = listOf(
            listOf(Offset(0, 0), Offset(1, 0), Offset(1, 1), Offset(2, 1)),
            listOf(Offset(1, 0), Offset(1, 1), Offset(0, 1), Offset(0, 2))
        )
        private val S = listOf(
            listOf(Offset(2, 0), Offset(1, 0), Offset(1, 1), Offset(0, 1)),
            listOf(Offset(0, 0), Offset(0, 1), Offset(1, 1), Offset(1, 2))
        )
        private val T = listOf(
            listOf(Offset(0, 0), Offset(1, 0), Offset(2, 0), Offset(1, 1)),
            listOf(Offset(1, 0), Offset(1, 1), Offset(0, 1), Offset(1, 2)),
            listOf(Offset(1, 0), Offset(0, 1), Offset(1, 1), Offset(2, 1)),
            listOf(Offset(1, 0), Offset(1, 1), Offset(1, 2), Offset(2, 1))
        )
        private val L = listOf(
            listOf(Offset(0, 0), Offset(0, 1), Offset(0, 2), Offset(1, 2)),
            listOf(Offset(0, 0), Offset(1, 0), Offset(2, 0), Offset(0, 1)),
            listOf(Offset(0, 0), Offset(1, 0), Offset(1, 1), Offset(1, 2)),
            listOf(Offset(2, 0), Offset(2, 1), Offset(1, 1), Offset(0, 1))
        )
        private val rL = listOf(
            listOf(Offset(1, 0), Offset(1, 1), Offset(1, 2), Offset(0, 2)),
            listOf(Offset(0, 0), Offset(0, 1), Offset(1, 1), Offset(2, 1)),
            listOf(Offset(0, 0), Offset(1, 0), Offset(0, 1), Offset(0, 2)),
            listOf(Offset(0, 0), Offset(1, 0), Offset(2, 0), Offset(2, 1))
        )

        private val blockOffsetEnum = listOf(O, I, Z, S, T, L, rL)
    }
}

class Offset(
    var x: Int = 0,
    var y: Int = 0
)

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