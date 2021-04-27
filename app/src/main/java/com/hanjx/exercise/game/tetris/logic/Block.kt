package com.hanjx.exercise.game.tetris.logic

import kotlin.random.Random

class Block(
    val offsets: Set<Offset>,
    val orientationCount: Int
) {
    var next: Block = this

    companion object {
        fun randomBlock(): Block {
            var node = blockNodeList[Random.nextInt(blockNodeList.size)]
            for (i in 0..Random.nextInt(4)) {
                node = node.next
            }

            return node
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

        private val blockNodeList = listOf(
            offsetsList2Node(O, Offset(5, -2)),
            offsetsList2Node(I, Offset(4, -4)),
            offsetsList2Node(Z, Offset(4, -2)),
            offsetsList2Node(S, Offset(4, -2)),
            offsetsList2Node(T, Offset(4, -2)),
            offsetsList2Node(L, Offset(5, -3)),
            offsetsList2Node(rL, Offset(5, -3))
        )

        private fun offsetsList2Node(
            offsetList: List<Set<Offset>>,
            initOffset: Offset
        ): Block {
            val nodeList = ArrayList<Block>()
            offsetList.forEach { offsets ->
                offsets.forEach { offset ->
                    offset.x += initOffset.x
                    offset.y += initOffset.y
                }
                nodeList.add(Block(offsets, offsetList.size))
            }
            nodeList.last().next = nodeList[0]
            for (i in 0 until offsetList.size - 1) {
                nodeList[i].next = nodeList[i + 1]
            }
            return nodeList[0]
        }
    }
}

class Offset(
    var x: Int,
    var y: Int
)