package com.hanjx.exercise.game.tetris_java;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.hanjx.exercise.game.tetris.R;
import com.hanjx.exercise.game.tetris.logic.Action;
import com.hanjx.exercise.game.tetris_java.view.BlockPreview;
import com.hanjx.exercise.game.tetris_java.view.GameScreen;

public class TetrisActivity extends AppCompatActivity {
    private GameScreen gameScreen;
    private TextView recordText;
    private TextView scoreText;
    private BlockPreview currBlock;
    private BlockPreview nextBlock;
    private TextView levelText;
    private View start;
    private View pause;
    private View reset;
    private View drop;
    private View rotate;
    private View moveLeft;
    private View moveRight;
    private View moveDown;

    private TetrisViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tetris_home);

        gameScreen = findViewById(R.id.game_screen);
        recordText = findViewById(R.id.record_text);
        scoreText = findViewById(R.id.score_text);
        currBlock = findViewById(R.id.curr_block);
        nextBlock = findViewById(R.id.next_block);
        levelText = findViewById(R.id.level);
        start = findViewById(R.id.start_game);
        pause = findViewById(R.id.pause_game);
        reset = findViewById(R.id.reset_game);
        drop = findViewById(R.id.drop);
        rotate = findViewById(R.id.rotate);
        moveLeft = findViewById(R.id.move_left);
        moveRight = findViewById(R.id.move_right);
        moveDown = findViewById(R.id.move_down);

        viewModel = new ViewModelProvider(this).get(TetrisViewModel.class);

        viewModel.getPointStateLiveData().observe(this, gameScreen::setDisplayStatus);
        viewModel.getRecordLiveData().observe(this, record -> recordText.setText(String.format("RECORD\n%s", record)));
        viewModel.getScoreLiveDta().observe(this, score -> scoreText.setText(String.format("SCORD\n%s", score)));
        viewModel.getCurrBlockLiveData().observe(this, currBlock::setBlock);
        viewModel.getNextBlockLivaDta().observe(this, nextBlock::setBlock);
        viewModel.getLevelLiveData().observe(this, level -> levelText.setText(String.format("LEVEL\n%s", level)));

        start.setOnClickListener(v -> viewModel.doAction(Action.StartGame));
        pause.setOnClickListener(v -> viewModel.doAction(Action.PauseGame));
        reset.setOnClickListener(v -> viewModel.doAction(Action.ResetGame));
        drop.setOnClickListener(v -> viewModel.doAction(Action.Drop));
        rotate.setOnClickListener(v -> viewModel.doAction(Action.Rotate));
        moveLeft.setOnClickListener(v -> viewModel.doAction(Action.MoveLeft));
        moveRight.setOnClickListener(v -> viewModel.doAction(Action.MoveRight));
        moveDown.setOnClickListener(v -> viewModel.doAction(Action.MoveDown));
    }
}
