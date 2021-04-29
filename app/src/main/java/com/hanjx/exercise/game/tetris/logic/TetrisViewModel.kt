package com.hanjx.exercise.game.tetris.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hanjx.exercise.game.tetris.logic.Block.Companion.randomBlock
import kotlinx.coroutines.*

class TetrisViewModel : ViewModel() {
    // TODO: 2021/5/4 线程不安全
    val screenDisplayState = mutableStateListOf<Boolean>().apply {
        for (i in 0 until COLUMN_COUNT * ROW_COUNT) {
            add(false)
        }
    }
    var currBlock by mutableStateOf(randomBlock())

    private var running = false
    private var downJob: Job? = null

    fun doAction(action: Action) {
        when (action) {
            Action.StartGame -> {
                if (!running) {
                    running = true
                    // TODO: 2021/5/4 线程不安全
                    downJob = GlobalScope.launch {
                        while (true) {
                            delay(500)
                            doAction(Action.MoveDown)
                        }
                    }
                }
            }
            Action.PauseGame -> {
                if (running) {
                    running = false
                    downJob?.cancel()
                }
            }
            Action.ResetGame -> {
                doAction(Action.PauseGame)
                screenDisplayState.indices.forEach {
                    screenDisplayState[it] = false
                }
                currBlock = randomBlock()
            }
            else -> blockAction(action)
        }
    }

    private fun blockAction(action: Action) {
        if (!running) {
            return
        }
        val oldIndexes = currBlock.currOffsets.offsets2Indexes()
        var changeBlock = false
        when {
            action == Action.MoveLeft && canMoveLeft() -> currBlock.leftTop.x--
            action == Action.MoveRight && canMoveRight() -> currBlock.leftTop.x++
            action == Action.Rotate && canRotate() ->
                currBlock.currState =
                    if (currBlock.currState == currBlock.offsets.size - 1) 0 else currBlock.currState + 1
            action == Action.MoveDown && canMoveDown() -> currBlock.leftTop.y++
            action == Action.Drop && canMoveDown() -> {
                while (canMoveDown()) {
                    currBlock.leftTop.y++
                }
                changeBlock = true
            }
            !canMoveDown() && (action == Action.MoveDown || action == Action.Drop) -> {
                changeBlock = true
            }
        }
        val newIndexes = currBlock.currOffsets.offsets2Indexes()
        oldIndexes.forEach {
            if (!newIndexes.contains(it)) {
                screenDisplayState[it] = false
            }
        }
        newIndexes.forEach {
            screenDisplayState[it] = true
        }
        if (changeBlock) {
            if (newIndexes.size != BLOCK_POINT_COUNT) {
                doAction(Action.ResetGame)
            } else {
                clearLine()
                currBlock = randomBlock()
            }
        }
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
        if (currBlock.offsets.size == 1) {
            return false
        }
        val currIndexes = currBlock.currOffsets.offsets2Indexes()
        val leftTop = currBlock.leftTop
        val nextState =
            if (currBlock.currState == currBlock.offsets.size - 1) 0 else currBlock.currState + 1
        currBlock.offsets[nextState].forEach {
            val index = it.x + leftTop.x + COLUMN_COUNT * (it.y + leftTop.y)
            if (!currIndexes.contains(index) && screenDisplayState[index]) {
                return false
            }
        }
        return true
    }

    companion object {
        operator fun List<Boolean>.get(x: Int, y: Int) = get(y * COLUMN_COUNT + x)
        operator fun MutableList<Boolean>.set(x: Int, y: Int, v: Boolean) =
            set(y * COLUMN_COUNT + x, v)
    }
}