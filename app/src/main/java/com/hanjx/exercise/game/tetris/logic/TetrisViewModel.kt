package com.hanjx.exercise.game.tetris.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hanjx.exercise.game.tetris.logic.Block.Companion.randomBlock
import kotlinx.coroutines.*

class TetrisViewModel : ViewModel() {
    val screenDisplayState = mutableStateListOf<Boolean>().apply {
        for (i in 0 until COLUMN_COUNT * ROW_COUNT) {
            add(false)
        }
    }
    var currBlock by mutableStateOf(randomBlock())

    private var running = false
    private var downJob: Job? = null

    fun gameOver() {
        reset()
    }

    fun reset() {
        pause()
        screenDisplayState.indices.forEach {
            screenDisplayState[it] = false
        }
        currBlock = randomBlock()
    }

    fun start() {
        if (running) return
        running = true
        downJob = GlobalScope.launch {
            while (true) {
                delay(500)
                moveDown()
            }
        }
    }

    fun pause() {
        running = false
        downJob?.cancel()
    }

    fun drop() {
        if (!running) return
        if (canMoveDown()) {
            changeBlock {
                while (canMoveDown()) {
                    currBlock.changeAll { offset ->
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
        if (!running) return
        if (canMoveDown()) {
            changeBlock {
                currBlock.changeAll { offset ->
                    offset.y++
                }
            }
        } else {
            nextBlock()
        }
    }

    fun moveLeft() {
        if (!running) return
        if (canMoveLeft()) {
            changeBlock {
                currBlock.changeAll { offset ->
                    offset.x--
                }
            }
        }
    }

    fun moveRight() {
        if (!running) return
        if (canMoveRight()) {
            changeBlock {
                currBlock.changeAll { offset ->
                    offset.x++
                }
            }
        }
    }

    fun rotate() {
        if (!running) return
        if (canRotate()) {
            changeBlock {
                currBlock.rotate()
            }
        }
    }

    private fun nextBlock() {
        currBlock.currOffsets.forEach {
            if (!it.inScreen()) {
                gameOver()
                return
            }
        }
        clearLine()
        currBlock = randomBlock()
    }

    private fun clearLine() {
        val newDisplayed = mutableSetOf<Int>()
        var newLineY = ROW_COUNT - 1
        for (fakeY in 0 until ROW_COUNT) {
            val realY = ROW_COUNT - fakeY - 1
            var linePointCount = 0
            for (x in 0 until COLUMN_COUNT) {
                if (screenDisplayState[x, realY]) {
                    linePointCount++
                }
            }
            if (linePointCount == 0 || linePointCount == COLUMN_COUNT) {
                continue
            }
            for (x in 0 until COLUMN_COUNT) {
                if (screenDisplayState[x, realY]) {
                    newDisplayed.add(newLineY * COLUMN_COUNT + x)
                }
            }
            newLineY--
        }

        for (x in 0 until COLUMN_COUNT) {
            for (y in 0 until ROW_COUNT) {
                screenDisplayState[x, y] = newDisplayed.contains(x + y * COLUMN_COUNT)
            }
        }
    }

    private fun changeBlock(action: () -> Unit) {
        val old = currBlock.currOffsets.offsets2Indexes()
        action.invoke()
        val new = currBlock.currOffsets.offsets2Indexes()
        old.forEach {
            if (!new.contains(it)) {
                screenDisplayState[it] = false
            }
        }
        new.forEach {
            screenDisplayState[it] = true
        }
    }

    private fun canMoveDown(): Boolean {
        currBlock.currOffsets.bottom.forEach {
            if (it.y >= -1 && (it.y >= ROW_COUNT - 1 || screenDisplayState[it.x, it.y + 1])) {
                return false
            }
        }
        return true
    }

    private fun canMoveLeft(): Boolean {
        currBlock.currOffsets.left.forEach {
            if (it.x <= 0 || it.y >= 0 && screenDisplayState[it.x - 1, it.y]) {
                return false
            }
        }
        return true
    }

    private fun canMoveRight(): Boolean {
        currBlock.currOffsets.right.forEach {
            if (it.x >= COLUMN_COUNT - 1 || it.y >= 0 && screenDisplayState[it.x + 1, it.y]) {
                return false
            }
        }
        return true
    }

    private fun canRotate(): Boolean {
        if (currBlock.nextOffsets == currBlock.currOffsets) {
            return false
        }
        currBlock.nextOffsets.forEach {
            if (!it.inScreen()) {
                return false
            }
        }
        val curr = currBlock.currOffsets.offsets2Indexes()
        val next = currBlock.nextOffsets.offsets2Indexes()
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
    }
}