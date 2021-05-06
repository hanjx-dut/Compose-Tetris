package com.hanjx.exercise.game.tetris

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hanjx.exercise.game.tetris.logic.*

@Composable
fun GameScreen(modifier: Modifier) {
    val viewModel = viewModel(TetrisViewModel::class.java)

    Column(modifier.fillMaxSize()) {
        Row(
            Modifier
                .padding(top = 15.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Screen(
                modifier = Modifier,
                displayState = remember { viewModel.screenDisplayState }
            )
            StatusScreen(
                modifier = Modifier.padding(start = 10.dp),
                maxScore = viewModel.recordScore,
                currScore = viewModel.currScore,
                currBlock = viewModel.currBlock,
                nextBlock = viewModel.nextBlock,
                blockPointSize = 10.dp,
                blockColor = Color(0xFF000000)
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

@Composable
fun Screen(
    modifier: Modifier,
    displayState: List<Boolean>
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

@Composable
fun StatusScreen(
    modifier: Modifier,
    maxScore: Int,
    currScore: Int,
    currBlock: Block,
    nextBlock: Block,
    blockPointSize: Dp,
    blockColor: Color
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "RECORD\n$maxScore",
            color = Color(0xFF3C464F),
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "SCORE\n$currScore",
            style = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(
            text = "CURRENT",
            style = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier.padding(top = 20.dp)
        )
        Box(
            modifier = Modifier
                .padding(top = 20.dp)
                .size(
                    width = blockPointSize.times(currBlock.summaryOffset.bottom.size),
                    height = blockPointSize.times(4)
                )
                .drawBehind {
                    currBlock.summaryOffset.forEach {
                        drawScreenPoint(blockPointSize, it.x, it.y, blockColor)
                    }
                }
        )
        Text(
            text = "NEXT",
            style = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier.padding(top = 20.dp)
        )
        Box(
            modifier = Modifier
                .padding(top = 20.dp)
                .size(
                    width = blockPointSize.times(nextBlock.summaryOffset.bottom.size),
                    height = blockPointSize.times(4)
                )
                .drawBehind {
                    nextBlock.summaryOffset.forEach {
                        drawScreenPoint(blockPointSize, it.x, it.y, blockColor)
                    }
                }
        )
    }
}

@Composable
fun ControlButtons(
    modifier: Modifier,
    rotateClick: () -> Unit,
    leftClick: () -> Unit,
    rightClick: () -> Unit,
    downClick: () -> Unit,
    dropClick: () -> Unit
) {
    Box(modifier.size(180.dp)) {
        Image(
            painter = painterResource(R.drawable.app_rotate),
            contentDescription = "rotate",
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .align(Alignment.TopCenter)
                .clickable(onClick = rotateClick)
        )
        Image(
            painter = painterResource(R.drawable.app_arrow_left),
            contentDescription = "left",
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .align(Alignment.CenterStart)
                .clickable(onClick = leftClick)
        )
        Image(
            painter = painterResource(R.drawable.app_arrow_right),
            contentDescription = "right",
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .align(Alignment.CenterEnd)
                .clickable(onClick = rightClick)
        )
        Image(
            painter = painterResource(R.drawable.app_arrow_down),
            contentDescription = "down",
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .align(Alignment.BottomCenter)
                .clickable(onClick = downClick)
        )
        Image(
            painter = painterResource(R.drawable.app_drop),
            contentDescription = "drop",
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .align(Alignment.Center)
                .clickable(onClick = dropClick)
        )
    }
}

@Composable
fun FuncButtons(
    modifier: Modifier,
    startClick: () -> Unit,
    pauseClick: () -> Unit,
    resetClick: () -> Unit
) {
    Row(modifier = modifier) {
        Text(
            text = "START",
            color = Color(0xFF3C7CFC),
            fontSize = 20.sp,
            modifier = modifier
                .padding(horizontal = 20.dp)
                .clickable(onClick = startClick)
        )
        Text(
            text = "PAUSE",
            color = Color(0xFF3C7CFC),
            fontSize = 20.sp,
            modifier = modifier
                .padding(horizontal = 20.dp)
                .clickable(onClick = pauseClick)
        )
        Text(
            text = "RESET",
            color = Color(0xFF3C7CFC),
            fontSize = 20.sp,
            modifier = modifier
                .padding(horizontal = 20.dp)
                .clickable(onClick = resetClick)
        )
    }
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

@Preview
@Composable
fun ScreenPreview() {
    Box(
        Modifier
            .background(Color(0xFFDDDDDD))
            .size(120.dp, 240.dp)
            .drawBehind {
                drawScreen(
                    COLUMN_COUNT,
                    10.dp,
                    Color(0x70000000),
                    Color(0xFF000000),
                    MutableList(ROW_COUNT * COLUMN_COUNT) {
                        it % COLUMN_COUNT == it / COLUMN_COUNT
                    }
                )
            }
    )
}

@Preview
@Composable
fun StatusScreenPreview() {
    StatusScreen(
        modifier = Modifier,
        maxScore = 1000000,
        currScore = 500,
        currBlock = Block.randomBlock(),
        nextBlock = Block.randomBlock(),
        blockPointSize = 10.dp,
        blockColor = Color(0xFF000000)
    )
}

@Preview
@Composable
fun FuncBarPreview() {
    FuncButtons(Modifier, {}, {}, {})
}

@Preview
@Composable
fun ControlBarPreview() {
    ControlButtons(Modifier.background(Color.White), {}, {}, {}, {}, {})
}