package com.hanjx.exercise.game.tetris

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hanjx.exercise.game.tetris.logic.COLUMN_COUNT
import com.hanjx.exercise.game.tetris.logic.ROW_COUNT
import com.hanjx.exercise.game.tetris.logic.TetrisViewModel
import com.hanjx.exercise.game.tetris.logic.TetrisViewModel.Companion.get
import com.hanjx.exercise.game.tetris.logic.TetrisViewModel.Companion.forXY

@Composable
fun GameScreen(modifier: Modifier) {
    val viewModel = viewModel(TetrisViewModel::class.java)
    val displayState = remember { viewModel.screenDisplayState }

    Column(modifier.fillMaxSize()) {
        Box(
            modifier
                .background(Color(0xFFDDDDDD))
                .align(Alignment.CenterHorizontally)
                .size(240.dp, 480.dp)
                .drawBehind {
                    drawMatrixScreenBg(
                        20.dp,
                        Color(0x50000000),
                        Color(0xFF000000),
                        displayState
                    )
                })
        Button(
            modifier = modifier.align(Alignment.CenterHorizontally),
            onClick = { viewModel.rotate() }
        ) {
            Text("ROTATE")
        }
        Row(
            modifier.align(Alignment.CenterHorizontally)
        ) {
            Button({ viewModel.moveLeft() }) {
                Text("LEFT")
            }
            Button({ viewModel.drop() }) {
                Text("DROP")
            }
            Button({ viewModel.moveRight() }) {
                Text("RIGHT")
            }
        }
        Button(
            modifier = modifier.align(Alignment.CenterHorizontally),
            onClick = { viewModel.moveDown() }
        ) {
            Text("DOWN")
        }

        Row(
            Modifier
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Button(onClick = { viewModel.start() }) {
                Text("START")
            }
            Button(onClick = { viewModel.pause() }) {
                Text("PAUSE")
            }
        }

    }
}

fun DrawScope.drawMatrixScreenBg(
    matrixSize: Dp,
    matrixNormalColor: Color,
    matrixDisplayColor: Color,
    matrixDisplayStatus: MutableList<Boolean>
) {
    matrixDisplayStatus.forXY { x, y ->
        drawMatrix(
            matrixSize, x, y, if (matrixDisplayStatus[x, y]) {
                matrixDisplayColor
            } else {
                matrixNormalColor
            }
        )
    }
}

fun DrawScope.drawMatrix(
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
                drawMatrixScreenBg(
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