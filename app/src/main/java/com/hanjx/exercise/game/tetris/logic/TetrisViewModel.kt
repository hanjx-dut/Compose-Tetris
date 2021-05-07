package com.hanjx.exercise.game.tetris.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.math.max

class TetrisViewModel : ViewModel() {
    val pointState = mutableStateListOf<Boolean>().apply {
        for (i in 0 until COLUMN_COUNT * ROW_COUNT) add(false)
    }
    var currBlock by mutableStateOf(Block())
    var nextBlock by mutableStateOf(Block())
    var recordScore by mutableStateOf(0)
    var currScore by mutableStateOf(0)

    private var running = false
    private var downJob: Job? = null

    fun doAction(action: Action) {
        viewModelScope.launch(Dispatchers.Main) {
            when (action) {
                Action.StartGame -> {
                    if (!running) {
                        running = true
                        downJob = launch(Dispatchers.Default) {
                            while (true) {
                                delay(500)
                                launch(Dispatchers.Main) {
                                    doAction(Action.MoveDown)
                                }
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
                    running = false
                    downJob?.cancel()
                    pointState.indices.forEach {
                        pointState[it] = false
                    }
                    currBlock = Block()
                    nextBlock = Block()
                }
                else -> blockAction(action)
            }
        }
    }

    private fun blockAction(action: Action) {
        if (!running) return
        val currOffsets = currBlock.currOffsets
        val oldIndexes = currOffsets.indexes
        var changeBlock = false
        when (action) {
            Action.MoveLeft -> {
                val canMoveLeft: (Offset) -> Boolean =
                    { it.x > 0 && (it.y < 0 || !pointState[it.x - 1, it.y]) }
                if (currOffsets.left.checkAll(canMoveLeft))
                    currBlock.leftTop.x--
            }
            Action.MoveRight -> {
                val canMoveRight: (Offset) -> Boolean =
                    { it.x < COLUMN_COUNT - 1 && (it.y < 0 || !pointState[it.x + 1, it.y]) }
                if (currOffsets.right.checkAll(canMoveRight))
                    currBlock.leftTop.x++
            }
            Action.Rotate -> {
                val nextState =
                    if (currBlock.state == currBlock.offsets.size - 1) 0 else currBlock.state + 1
                val canRotate: (Offset) -> Boolean = {
                    val index = it.index + currBlock.leftTop.index
                    index < 0 || oldIndexes.contains(index) || !pointState[index]
                }
                if (currBlock.offsets.size > 1 && currBlock.offsets[nextState].checkAll(canRotate))
                    currBlock.state = nextState
            }
            Action.MoveDown -> {
                val canMoveDown: (Offset) -> Boolean =
                    { it.y < -1 || it.y < ROW_COUNT - 1 && !pointState[it.x, it.y + 1] }
                if (currOffsets.bottom.checkAll(canMoveDown)) {
                    currBlock.leftTop.y++
                } else {
                    changeBlock = true
                }
            }
            Action.Drop -> {
                val canMoveDown: (Offset) -> Boolean =
                    { it.y < -1 || it.y < ROW_COUNT - 1 && !pointState[it.x, it.y + 1] }
                while (currBlock.currOffsets.bottom.checkAll(canMoveDown))
                    currBlock.leftTop.y++
                changeBlock = true
            }
            else -> {
            }
        }
        val newIndexes = currBlock.currOffsets.indexes
        oldIndexes.forEach {
            if (!newIndexes.contains(it)) {
                pointState[it] = false
            }
        }
        newIndexes.forEach {
            pointState[it] = true
        }
        if (changeBlock) {
            if (newIndexes.size != BLOCK_POINT_COUNT) {
                recordScore = max(recordScore, currScore)
                currScore = 0
                doAction(Action.ResetGame)
            } else {
                clearLine()
                currBlock = nextBlock
                nextBlock = Block()
            }
        }
    }

    private fun clearLine() {
        val newDisplayed = mutableSetOf<Int>()
        var newLineCount = ROW_COUNT - 1
        var clearedLine = 0
        for (y in ROW_COUNT - 1 downTo 0) {
            val linePointCount = pointState
                .subList(y * COLUMN_COUNT, (y + 1) * COLUMN_COUNT)
                .count { it }
            if (linePointCount == 0) {
                continue
            }
            if (linePointCount == COLUMN_COUNT) {
                clearedLine++
                continue
            }
            for (x in 0 until COLUMN_COUNT) {
                if (pointState[x, y])
                    newDisplayed.add(newLineCount * COLUMN_COUNT + x)
            }
            newLineCount--
        }
        if (clearedLine > 0) {
            // 1 line = 100, 2 line = 250, 3 line = 400, 4 line = 600
            currScore += 100 * clearedLine + (50 * (clearedLine - 1))
            pointState.indices.forEach {
                pointState[it] = newDisplayed.contains(it)
            }
        }
    }

    companion object {
        operator fun List<Boolean>.get(x: Int, y: Int) = get(y * COLUMN_COUNT + x)

        fun <T> Iterable<T>.checkAll(checker: (T) -> Boolean): Boolean {
            forEach { if (!checker.invoke(it)) return false }
            return true
        }

        val Offset.index: Int
            get() = x + y * COLUMN_COUNT

        val Iterable<Offset>.indexes: MutableSet<Int>
            get() = mutableSetOf<Int>().also { result ->
                forEach {
                    if (it.x in 0 until COLUMN_COUNT && it.y in 0 until ROW_COUNT) {
                        result.add(it.index)
                    }
                }
            }
    }
}