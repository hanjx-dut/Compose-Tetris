package com.hanjx.exercise.game.tetris.logic

import kotlin.random.Random

class Block(
    val offsets: List<List<Offset>>,
    var currState: Int,
) {
    val leftTop: Offset = Offset(4, -4)
    val summaryOffset: List<Offset> = offsets[currState]

    private val reusedCurrOffsets = listOf(Offset(), Offset(), Offset(), Offset())
    val currOffsets: List<Offset>
        get() = reusedCurrOffsets.apply {
            forEachIndexed { i, offset ->
                offset.x = offsets[currState][i].x + leftTop.x
                offset.y = offsets[currState][i].y + leftTop.y
            }
        }

    companion object {
        fun randomBlock(): Block {
            val offsetsList = blockEnumList[Random.nextInt(blockEnumList.size)]
            return Block(offsetsList, Random.nextInt(offsetsList.size)).apply {
                var maxY = Int.MIN_VALUE
                currOffsets.forEach {
                    if (it.y > maxY) {
                        maxY = it.y
                    }
                }
                for (i in 0 until -1 - maxY) {
                    leftTop.y++
                }
            }
        }

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

        private val blockEnumList = listOf(O, I, Z, S, T, L, rL)
    }
}

class Offset(
    var x: Int = 0,
    var y: Int = 0
)