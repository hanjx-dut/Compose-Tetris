package com.hanjx.exercise.game.tetris.logic

import kotlin.random.Random

class Block(
    val offsetsList: List<Set<Offset>>,
) {
    var currState: Int = 0
    val currOffsets: Set<Offset>
        get() = offsetsList[currState]
    val nextOffsets: Set<Offset>
        get() = offsetsList[if (currState == offsetsList.size - 1) 0 else currState + 1]

    fun rotate() {
        currState = if (currState == offsetsList.size - 1) 0 else currState + 1
    }

    fun changeAll(action: (Offset) -> Unit) {
        offsetsList.forEach {
            it.forEach(action)
        }
    }

    companion object {
        fun randomBlock(): Block {
            val pair = blockEnumList[Random.nextInt(blockEnumList.size)]
            return offsetsList2Node(pair.first, pair.second).apply {
                currState = Random.nextInt(offsetsList.size)
            }
        }

        private val O = listOf(
            setOf(Offset(0, 0), Offset(0, 1), Offset(1, 0), Offset(1, 1))
        )
        private val I = listOf(
            setOf(Offset(0, 0), Offset(1, 0), Offset(2, 0), Offset(3, 0)),
            setOf(Offset(1, 0), Offset(1, 1), Offset(1, 2), Offset(1, 3))
        )
        private val Z = listOf(
            setOf(Offset(0, 0), Offset(1, 0), Offset(1, 1), Offset(2, 1)),
            setOf(Offset(1, 0), Offset(1, 1), Offset(0, 1), Offset(0, 2))
        )
        private val S = listOf(
            setOf(Offset(2, 0), Offset(1, 0), Offset(1, 1), Offset(0, 1)),
            setOf(Offset(0, 0), Offset(0, 1), Offset(1, 1), Offset(1, 2))
        )
        private val T = listOf(
            setOf(Offset(0, 0), Offset(1, 0), Offset(2, 0), Offset(1, 1)),
            setOf(Offset(1, 0), Offset(1, 1), Offset(0, 1), Offset(1, 2)),
            setOf(Offset(1, 0), Offset(0, 1), Offset(1, 1), Offset(2, 1)),
            setOf(Offset(1, 0), Offset(1, 1), Offset(1, 2), Offset(2, 1))
        )
        private val L = listOf(
            setOf(Offset(0, 0), Offset(0, 1), Offset(0, 2), Offset(1, 2)),
            setOf(Offset(0, 0), Offset(1, 0), Offset(2, 0), Offset(0, 1)),
            setOf(Offset(0, 0), Offset(1, 0), Offset(1, 1), Offset(1, 2)),
            setOf(Offset(2, 0), Offset(2, 1), Offset(1, 1), Offset(0, 1))
        )
        private val rL = listOf(
            setOf(Offset(1, 0), Offset(1, 1), Offset(1, 2), Offset(0, 2)),
            setOf(Offset(0, 0), Offset(0, 1), Offset(1, 1), Offset(2, 1)),
            setOf(Offset(0, 0), Offset(1, 0), Offset(0, 1), Offset(0, 2)),
            setOf(Offset(0, 0), Offset(1, 0), Offset(2, 0), Offset(2, 1))
        )

        private val blockEnumList = listOf(
            Pair(O, Offset(5, -2)),
            Pair(I, Offset(4, -4)),
            Pair(Z, Offset(4, -2)),
            Pair(S, Offset(4, -2)),
            Pair(T, Offset(4, -2)),
            Pair(L, Offset(5, -3)),
            Pair(rL, Offset(5, -3))
        )

        private fun offsetsList2Node(
            offsetList: List<Set<Offset>>,
            initOffset: Offset
        ): Block {
            val copyOffsetsList = mutableListOf<Set<Offset>>()
            offsetList.forEach { offsets ->
                val copyOffsets = mutableSetOf<Offset>()
                offsets.forEach { offset ->
                    copyOffsets.add(Offset(offset.x + initOffset.x, offset.y + initOffset.y))
                }
                copyOffsetsList.add(copyOffsets)
            }
            return Block(copyOffsetsList)
        }
    }
}

class Offset(
    var x: Int,
    var y: Int
)