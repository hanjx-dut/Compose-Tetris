package com.hanjx.exercise.game.tetris

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hanjx.exercise.game.tetris.logic.*

@Composable
fun Tetris(modifier: Modifier = Modifier) {
    val viewModel = viewModel(TetrisViewModel::class.java)
    Column(modifier.fillMaxSize()) {
        Row(
            Modifier
                .padding(top = 15.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            GameScreen(
                modifier = Modifier,
                displayState = viewModel.pointState
            )
            StatusScreen(
                modifier = Modifier.padding(start = 10.dp),
                maxScore = viewModel.recordScore,
                level = viewModel.level,
                currScore = viewModel.currScore,
                currBlock = viewModel.currBlock,
                nextBlock = viewModel.nextBlock,
            )
        }

        FuncButtons(
            modifier = modifier
                .padding(top = 5.dp)
                .align(Alignment.CenterHorizontally),
            startClick = { viewModel.doAction(Action.StartGame) },
            pauseClick = { viewModel.doAction(Action.PauseGame) },
            resetClick = { viewModel.doAction(Action.ResetGame) }
        )

        ControlButtons(
            modifier = modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally),
            rotateClick = { viewModel.doAction(Action.Rotate) },
            leftClick = { viewModel.doAction(Action.MoveLeft) },
            rightClick = { viewModel.doAction(Action.MoveRight) },
            downClick = { viewModel.doAction(Action.MoveDown) },
            dropClick = { viewModel.doAction(Action.Drop) }
        )
    }
}

@Preview
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    displayState: List<Boolean> = List(ROW_COUNT * COLUMN_COUNT) { it > 100 }
) {
    val pointSize = 20.dp
    Box(
        modifier
            .background(Color(0xFFDDDDDD))
            .size(pointSize * COLUMN_COUNT, pointSize * ROW_COUNT)
            .drawBehind {
                drawScreen(
                    COLUMN_COUNT,
                    pointSize,
                    Color(0x50000000),
                    Color(0xFF000000),
                    displayState
                )
            })
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun StatusScreen(
    modifier: Modifier = Modifier,
    maxScore: Int = 99999,
    currScore: Int = 1234,
    level: Int = 0,
    currBlock: Block = Block(),
    nextBlock: Block = Block(),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "RECORD\n$maxScore",
            style = TextStyle(textAlign = TextAlign.Center)
        )
        Text(
            text = "SCORE\n$currScore",
            style = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier.padding(top = 20.dp)
        )
        BlockSummary(Modifier.padding(top = 30.dp), "CURRENT", currBlock)
        BlockSummary(Modifier, "NEXT", nextBlock)
        Text(
            text = "LEVEL\n${level}",
            style = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}

@Composable
fun BlockSummary(
    modifier: Modifier = Modifier,
    title: String,
    block: Block
) {
    Column(modifier) {
        Text(
            text = title,
            style = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier.padding(top = 20.dp)
        )
        Box(
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally)
                .size(
                    width = (block.summaryOffset.bottom.size * 10).dp,
                    height = 40.dp
                )
                .drawBehind {
                    block.summaryOffset.forEach {
                        drawScreenPoint(10.dp, it.x, it.y, Color.Black)
                    }
                }
        )
    }
}

@Preview
@Composable
fun FuncButtons(
    modifier: Modifier = Modifier,
    startClick: () -> Unit = {},
    pauseClick: () -> Unit = {},
    resetClick: () -> Unit = {}
) {
    Row(modifier = modifier) {
        FunctionText(Modifier, "START", startClick)
        FunctionText(Modifier, "PAUSE", pauseClick)
        FunctionText(Modifier, "RESET", resetClick)
    }
}

@Composable
fun FunctionText(
    modifier: Modifier = Modifier,
    text: String,
    click: () -> Unit
) {
    Text(
        text = text,
        color = Color(0xFF3C7CFC),
        fontSize = 20.sp,
        modifier = modifier
            .padding(horizontal = 20.dp)
            .clickable(onClick = click)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ControlButtons(
    modifier: Modifier = Modifier,
    rotateClick: () -> Unit = {},
    leftClick: () -> Unit = {},
    rightClick: () -> Unit = {},
    downClick: () -> Unit = {},
    dropClick: () -> Unit = {}
) {
    Box(
        modifier.size(180.dp)
    ) {
        ControlButton(Modifier.align(Alignment.TopCenter), R.drawable.app_rotate, rotateClick)
        ControlButton(Modifier.align(Alignment.CenterStart), R.drawable.app_arrow_left, leftClick)
        ControlButton(Modifier.align(Alignment.CenterEnd), R.drawable.app_arrow_right, rightClick)
        ControlButton(Modifier.align(Alignment.BottomCenter), R.drawable.app_arrow_down, downClick)
        ControlButton(Modifier.align(Alignment.Center), R.drawable.app_drop, dropClick)
    }
}

@Composable
fun ControlButton(
    modifier: Modifier = Modifier,
    @DrawableRes resId: Int,
    click: () -> Unit
) {
    Image(
        painter = painterResource(resId),
        contentDescription = "",
        modifier = modifier
            .size(60.dp)
            .clickable(onClick = click)
    )
}

fun DrawScope.drawScreen(
    columnCount: Int,
    matrixSize: Dp,
    normalColor: Color,
    displayColor: Color,
    displayStatus: List<Boolean>
) {
    displayStatus.forEachIndexed { i, state ->
        drawScreenPoint(
            matrixSize, i % columnCount, i / columnCount, if (state) {
                displayColor
            } else {
                normalColor
            }
        )
    }
}

fun DrawScope.drawScreenPoint(
    size: Dp,
    x: Int,
    y: Int,
    color: Color,
) {
    drawRect(
        color = color,
        topLeft = Offset((x + 0.1f) * size.toPx(), (y + 0.1f) * size.toPx()),
        size = Size(size.toPx() * 0.8f, size.toPx() * 0.8f),
        style = Stroke(size.toPx() * 0.1f)
    )

    drawRect(
        color = color,
        topLeft = Offset((x + 0.25f) * size.toPx(), (y + 0.25f) * size.toPx()),
        size = Size(size.toPx() * 0.5f, size.toPx() * 0.5f),
        style = Fill
    )
}