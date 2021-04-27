package com.hanjx.exercise.game.tetris.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hanjx.exercise.game.tetris.logic.Block.Companion.randomBlock

class TetrisViewModel : ViewModel() {
    val screenDisplayState = MutableList(COLUMN_COUNT * ROW_COUNT) { false }
    var currBlock by mutableStateOf(randomBlock())

    fun goBottom() {
        if (canMoveDown()) {
            changeBlock {
                while (canMoveDown()) {
                    currBlock.forAllOffset { offset ->
                        offset.y++
                    }
                }
            }
            nextBlock()
        } else {
            nextBlock()
        }
    }

    fun moveDown() {
        if (canMoveDown()) {
            changeBlock {
                currBlock.forAllOffset { offset ->
                    offset.y++
                }
            }
        } else {
            nextBlock()
        }
    }

    private fun nextBlock() {
        currBlock = randomBlock()
    }

    fun moveLeft() {
        if (canMoveLeft()) {
            changeBlock {
                currBlock.forAllOffset { offset ->
                    offset.x--
                }
            }
        }
    }

    fun moveRight() {
        if (canMoveRight()) {
            changeBlock {
                currBlock.forAllOffset { offset ->
                    offset.x++
                }
            }
        }
    }

    fun rotate() {
        if (canRotate()) {
            changeBlock {
                currBlock = currBlock.next
            }
        }
    }

    private fun changeBlock(action: () -> Unit) {
        val old = currBlock.offsets2Indexes()
        action.invoke()
        val new = currBlock.offsets2Indexes()
        new.forEach {
            if (!old.remove(it)) screenDisplayState[it] = true
        }
        old.forEach {
            screenDisplayState[it] = false
        }
    }

    private fun canMoveDown(): Boolean {
        currBlock.offsets.bottom.forEach {
            if (it.y >= -1 && (it.y >= ROW_COUNT - 1 || screenDisplayState[it.x, it.y + 1])) {
                return false
            }
        }
        return true
    }

    private fun canMoveLeft(): Boolean {
        currBlock.offsets.left.forEach {
            if (it.x <= 0 || it.y >= 0 && screenDisplayState[it.x - 1, it.y]) {
                return false
            }
        }
        return true
    }

    private fun canMoveRight(): Boolean {
        currBlock.offsets.right.forEach {
            if (it.x >= COLUMN_COUNT - 1 || it.y >= 0 && screenDisplayState[it.x + 1, it.y]) {
                return false
            }
        }
        return true
    }

    private fun canRotate(): Boolean {
        if (currBlock.next == currBlock) {
            return false
        }
        currBlock.next.offsets.left.forEach {
            if (it.x < 0) {
                return false
            }
        }
        currBlock.next.offsets.right.forEach {
            if (it.x > COLUMN_COUNT - 1) {
                return false
            }
        }
        currBlock.next.offsets.bottom.forEach {
            if (it.y > ROW_COUNT - 1) {
                return false
            }
        }
        val curr = currBlock.offsets2Indexes()
        val next = currBlock.next.offsets2Indexes()
        next.forEach {
            if (!curr.contains(it) && screenDisplayState[it]) {
                return false
            }
        }
        return true
    }

    companion object {
        operator fun MutableList<Boolean>.get(x: Int, y: Int) = get(y * COLUMN_COUNT + x)
        operator fun MutableList<Boolean>.set(x: Int, y: Int, v: Boolean) =
            set(y * COLUMN_COUNT + x, v)

        fun MutableList<Boolean>.forXY(
            column: Int = COLUMN_COUNT,
            action: (Int, Int) -> Unit
        ) {
            indices.forEach {
                action.invoke(it % column, it / column)
            }
        }

        fun Block.offsets2Indexes(
            column: Int = COLUMN_COUNT
        ): MutableSet<Int> {
            return mutableSetOf<Int>().apply {
                offsets.forEach {
                    if (it.x in 0 until COLUMN_COUNT && it.y in 0 until ROW_COUNT) {
                        add(it.x + it.y * column)
                    }
                }
            }
        }

        inline fun Block.forAllOffset(action: (Offset) -> Unit) {
            var curr = this
            val count = orientationCount
            for (i in 0 until count) {
                curr.offsets.forEach(action)
                curr = curr.next
            }
        }
    }
}