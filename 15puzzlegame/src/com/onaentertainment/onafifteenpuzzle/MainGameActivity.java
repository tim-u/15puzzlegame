package com.onaentertainment.onafifteenpuzzle;

import com.onaentertainment.onafifteenpuzzle.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainGameActivity extends Activity {

	private static final String PURPLE = "PURPLE";
	private static final String RED = "RED";
	private static final String BLUE = "BLUE";
	private static final String CLASSIC = "CLASSIC";
	Timer timer, movesCount;
	private int dimensions = 4;
	private boolean paused = false;
	private boolean initialRun = true;
	
	private MovesView timerMovesView;
	private BoardView boardView;
	private File highscore_file, userpref_file;
	private String highscores = "highscores";
	private String userpref = "userpref";
	private Integer[] highScores = new Integer[]{0,0,0,0,0,0};
	private String currentColor = CLASSIC;
	private boolean soundOn = true;
	private boolean musicOn = true;

	private MediaPlayer backgroundMusic;
	private SoundPool sounds_winGame;
	private int winGameSound;
	
	private InterstitialAd interstitial;
	private AdRequest adRequest;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        getWindow().setFormat(PixelFormat.RGBA_8888); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_main_game);
        
        initializeTimers();
	    
	    // create a new file to store the high scores
	    highscore_file = new File(getFilesDir(), highscores);
	    if(highscore_file.exists())
	    {
			readScore();
		}

	    userpref_file = new File(getFilesDir(), userpref);
	    if(userpref_file.exists())
	    {
			readUserPreferences();
		}
	    
	    
		if(currentColor.equals(RED))
			switchColorToRed(null);
		else if(currentColor.equals(PURPLE))
			switchColorToPurple(null);
		else if(currentColor.equals(BLUE))
			switchColorToBlue(null);
		else if(currentColor.equals(CLASSIC))
			switchColorToClassic(null);
	    
		TimerView timer = (TimerView) findViewById(R.id.simpleTimer);
		timer.setPause(true); 
		boardView = (BoardView) findViewById(R.id.board);
		boardView.pausedGame(true);

		backgroundMusic = MediaPlayer.create(this, R.raw.background);
		backgroundMusic.setLooping(true);
		backgroundMusic.start();
		if(!musicOn)
		{
			backgroundMusic.pause();
		}
	    boardView.turnSoundsOff(!soundOn);
		
		sounds_winGame = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
		winGameSound = sounds_winGame.load(this, R.raw.win, 1);
		
		// Create the interstitial.
	    interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId("ca-app-pub-9365170775618157/3682015226");
	    
	    interstitial.setAdListener(new AdListener() 
	    {
            @Override
            public void onAdClosed() 
            {
        	    interstitial.loadAd(adRequest);
            }
        });

	    // Create ad request.
	    adRequest = new AdRequest.Builder().addTestDevice("016dea1e4bd5b650").build();

	    // Begin loading your interstitial.
	    interstitial.loadAd(adRequest);
	}
	  
	public void displayInterstitial() 
	{
	    if (interstitial.isLoaded()) 
	    {
	        interstitial.show();
	    }
	}
    
    @Override
    protected void onPause() 
    {
    	super.onPause();
    	if(!paused)
    	{
			TimerView timer = (TimerView) findViewById(R.id.simpleTimer);
			timer.setPause(true);
    	}
    	backgroundMusic.pause();
    }
    
    @Override
    protected void onResume() 
    {
    	super.onResume();
    	if(!paused && !initialRun)
    	{
			TimerView timer = (TimerView) findViewById(R.id.simpleTimer);
			timer.setPause(false);
    	}
    	toggleMusic();
    }
	
    public void switchColorToBlue(View view)
    {
    	if(currentColor != BLUE)
    	{
	    	View timerView = findViewById(R.id.timerBackground);
	    	timerView.setBackgroundResource(R.drawable.timer_background_blue);
	    	timerView.setPadding(0, 0, 0, 0);
	    	
	    	View menuButton = (View) findViewById(R.id.menuButton);
	    	
	    	if(paused)
	    	{
	    		menuButton.setBackgroundResource(R.drawable.button_play_blue);
	    	}
	    	else
	    	{
	    		menuButton.setBackgroundResource(R.drawable.button_pause_blue);
	    	}
	    	
	    	BoardView board = (BoardView) findViewById(R.id.board);
	    	board.changeColor(BLUE);
	    	
	    	findViewById(R.id.button_three_by_three).setBackgroundResource(R.drawable.option3x3_blue);
	    	findViewById(R.id.button_four_by_four).setBackgroundResource(R.drawable.option4x4_blue);
	    	findViewById(R.id.button_five_by_five).setBackgroundResource(R.drawable.option5x5_blue);
	    	
	    	View button_soundOnOff = findViewById(R.id.button_soundOnOff);
	    	if(soundOn)
	    		button_soundOnOff.setBackgroundResource(R.drawable.button_on_blue);
	    	else
	        	button_soundOnOff.setBackgroundResource(R.drawable.button_off_blue);
	
	    	View button_musicOnOff = findViewById(R.id.button_musicOnOff);
	    	if(musicOn)
	    		button_musicOnOff.setBackgroundResource(R.drawable.button_on_blue);
	    	else
	    		button_musicOnOff.setBackgroundResource(R.drawable.button_off_blue);
	
	    	findViewById(R.id.button_color_red).setBackgroundResource(R.drawable.button_color_inactive_red);
	    	findViewById(R.id.button_color_purple).setBackgroundResource(R.drawable.button_color_inactive_purple);
	    	findViewById(R.id.button_color_classic).setBackgroundResource(R.drawable.button_color_inactive_classic);
	    	findViewById(R.id.button_color_blue).setBackgroundResource(R.drawable.button_color_active_blue);
	    	
	    	findViewById(R.id.winscreen_restart).setBackgroundResource(R.drawable.new_game_blue);

	    	currentColor = BLUE;
	    	saveUserPreferences();
    	}
    }
    
    public void switchColorToRed(View view)
    {
    	if(currentColor != RED)
    	{
	    	View timerView = findViewById(R.id.timerBackground);
	    	timerView.setBackgroundResource(R.drawable.timer_background_red);
	    	timerView.setPadding(0, 0, 0, 0);
	    	
	    	View menuButton = (View) findViewById(R.id.menuButton);

	    	if(paused)
	    	{
	    		menuButton.setBackgroundResource(R.drawable.button_play_red);
	    	}
	    	else
	    	{
	    		menuButton.setBackgroundResource(R.drawable.button_pause_red);
	    	}
	    	
	    	BoardView board = (BoardView) findViewById(R.id.board);
	    	board.changeColor(RED);
	    	
	    	findViewById(R.id.button_three_by_three).setBackgroundResource(R.drawable.option3x3_red);
	    	findViewById(R.id.button_four_by_four).setBackgroundResource(R.drawable.option4x4_red);
	    	findViewById(R.id.button_five_by_five).setBackgroundResource(R.drawable.option5x5_red);	    	
	    	
	    	View button_soundOnOff = findViewById(R.id.button_soundOnOff);
	    	if(soundOn)
	    		button_soundOnOff.setBackgroundResource(R.drawable.button_on_red);
	    	else
	        	button_soundOnOff.setBackgroundResource(R.drawable.button_off_red);
	
	    	View button_musicOnOff = findViewById(R.id.button_musicOnOff);
	    	if(musicOn)
	    		button_musicOnOff.setBackgroundResource(R.drawable.button_on_red);
	    	else
	    		button_musicOnOff.setBackgroundResource(R.drawable.button_off_red);

	    	findViewById(R.id.button_color_blue).setBackgroundResource(R.drawable.button_color_inactive_blue);
	    	findViewById(R.id.button_color_purple).setBackgroundResource(R.drawable.button_color_inactive_purple);
	    	findViewById(R.id.button_color_classic).setBackgroundResource(R.drawable.button_color_inactive_classic);
	    	findViewById(R.id.button_color_red).setBackgroundResource(R.drawable.button_color_active_red);
	    	
	    	findViewById(R.id.winscreen_restart).setBackgroundResource(R.drawable.new_game_red);

	    	currentColor = RED;
	    	saveUserPreferences();
    	}
    }
    
    public void switchColorToPurple(View view)
    {
    	if(currentColor != PURPLE)
    	{
	    	View timerView = findViewById(R.id.timerBackground);
	    	timerView.setBackgroundResource(R.drawable.timer_background_purple);
	    	timerView.setPadding(0, 0, 0, 0);
	    	
	    	View menuButton = (View) findViewById(R.id.menuButton);

	    	if(paused)
	    	{
	    		menuButton.setBackgroundResource(R.drawable.button_play_purple);
	    	}
	    	else
	    	{
	    		menuButton.setBackgroundResource(R.drawable.button_pause_purple);
	    	}
	    	
	    	BoardView board = (BoardView) findViewById(R.id.board);
	    	board.changeColor(PURPLE);
	    	
	    	findViewById(R.id.button_three_by_three).setBackgroundResource(R.drawable.option3x3_purple);
	    	findViewById(R.id.button_four_by_four).setBackgroundResource(R.drawable.option4x4_purple);
	    	findViewById(R.id.button_five_by_five).setBackgroundResource(R.drawable.option5x5_purple);    	
	    	
	    	View button_soundOnOff = findViewById(R.id.button_soundOnOff);
	    	if(soundOn)
	    		button_soundOnOff.setBackgroundResource(R.drawable.button_on_purple);
	    	else
	        	button_soundOnOff.setBackgroundResource(R.drawable.button_off_purple);
	
	    	View button_musicOnOff = findViewById(R.id.button_musicOnOff);
	    	if(musicOn)
	    		button_musicOnOff.setBackgroundResource(R.drawable.button_on_purple);
	    	else
	    		button_musicOnOff.setBackgroundResource(R.drawable.button_off_purple);
	    	
	    	findViewById(R.id.button_color_red).setBackgroundResource(R.drawable.button_color_inactive_red);
	    	findViewById(R.id.button_color_blue).setBackgroundResource(R.drawable.button_color_inactive_blue);
	    	findViewById(R.id.button_color_classic).setBackgroundResource(R.drawable.button_color_inactive_classic);
	    	findViewById(R.id.button_color_purple).setBackgroundResource(R.drawable.button_color_active_purple);
	    	
	    	findViewById(R.id.winscreen_restart).setBackgroundResource(R.drawable.new_game_purple);

	    	currentColor = PURPLE;
	    	saveUserPreferences();
    	}
    }
    
    public void switchColorToClassic(View view)
    {
    	if(currentColor != CLASSIC)
    	{
	    	View timerView = findViewById(R.id.timerBackground);
	    	timerView.setBackgroundResource(R.drawable.timer_background_classic);
	    	timerView.setPadding(0, 0, 0, 0);
	    	
	    	View menuButton = (View) findViewById(R.id.menuButton);

	    	if(paused)
	    	{
	    		menuButton.setBackgroundResource(R.drawable.button_play_classic);
	    	}
	    	else
	    	{
	    		menuButton.setBackgroundResource(R.drawable.button_pause_classic);
	    	}
	    	
	    	BoardView board = (BoardView) findViewById(R.id.board);
	    	board.changeColor(CLASSIC);
	    	
	    	findViewById(R.id.button_three_by_three).setBackgroundResource(R.drawable.option3x3_classic);
	    	findViewById(R.id.button_four_by_four).setBackgroundResource(R.drawable.option4x4_classic);
	    	findViewById(R.id.button_five_by_five).setBackgroundResource(R.drawable.option5x5_classic);    	
	    	
	    	View button_soundOnOff = findViewById(R.id.button_soundOnOff);
	    	if(soundOn)
	    		button_soundOnOff.setBackgroundResource(R.drawable.button_on_classic);
	    	else
	        	button_soundOnOff.setBackgroundResource(R.drawable.button_off_classic);
	
	    	View button_musicOnOff = findViewById(R.id.button_musicOnOff);
	    	if(musicOn)
	    		button_musicOnOff.setBackgroundResource(R.drawable.button_on_classic);
	    	else
	    		button_musicOnOff.setBackgroundResource(R.drawable.button_off_classic);
	    	
	    	findViewById(R.id.button_color_red).setBackgroundResource(R.drawable.button_color_inactive_red);
	    	findViewById(R.id.button_color_blue).setBackgroundResource(R.drawable.button_color_inactive_blue);
	    	findViewById(R.id.button_color_purple).setBackgroundResource(R.drawable.button_color_inactive_purple);
	    	findViewById(R.id.button_color_classic).setBackgroundResource(R.drawable.button_color_active_classic);
	    	
	    	findViewById(R.id.winscreen_restart).setBackgroundResource(R.drawable.new_game_classic);

	    	currentColor = CLASSIC;
	    	saveUserPreferences();
    	}	
    }
    
    /**
     * Set new dimension value and start a new game
     * @param view
     */
	public void switchDimensions(View view)
	{
		if(view.getId() == R.id.button_three_by_three)
			dimensions = 3;
		else if(view.getId() == R.id.button_four_by_four)
			dimensions = 4;
		else
			dimensions = 5;

		findViewById(R.id.soundOption).setVisibility(View.INVISIBLE);
		findViewById(R.id.startGameOption).setVisibility(View.INVISIBLE);
		findViewById(R.id.colorOption).setVisibility(View.INVISIBLE);
		findViewById(R.id.menuClose).setVisibility(View.INVISIBLE);

		startNewGame();
	}
	
	public void startNewGame(View view)
	{
		displayInterstitial();
		view.setVisibility(View.INVISIBLE);
		startNewGame();
	}
	
	/**
	 * Start a new game and unpause if necessary
	 */
	public void startNewGame()
	{
		if(initialRun)
		{
			findViewById(R.id.highscore_tip).setVisibility(View.INVISIBLE);
			findViewById(R.id.menuButton).setVisibility(View.VISIBLE);
		}
		
		paused = false;
		
		boardView = (BoardView) findViewById(R.id.board);
		
		if(boardView.isWon())
		{
			View winView = findViewById(R.id.winscreen);
			winView.setVisibility(View.INVISIBLE);
		}
		
		boardView.startNewGame(dimensions);
		boardView.invalidate();
		
		TimerView timer = (TimerView) findViewById(R.id.simpleTimer);
		timer.resetTime();
		timer.setPause(false);

		switchMenuButton();
		initialRun = false;
	}
	
	private void playWinGameSound()
	{
		if(soundOn)
		{
			sounds_winGame.play(winGameSound, 1.0f, 1.0f, 0, 0, 1.0f);
		}
	}
	
	public void toggleMusic()
	{
		if(musicOn)
			backgroundMusic.start();
		else
			backgroundMusic.pause();
	}
	
	public void soundOnOff(View view)
	{
		boardView = (BoardView) findViewById(R.id.board);
		if(soundOn)
		{
			soundOn = false;
			boardView.turnSoundsOff(true);
			if(currentColor == RED)
				view.setBackgroundResource(R.drawable.button_off_red);
			else if(currentColor == PURPLE)
				view.setBackgroundResource(R.drawable.button_off_purple);
			else if(currentColor == BLUE)
				view.setBackgroundResource(R.drawable.button_off_blue);
			else if(currentColor == CLASSIC)
				view.setBackgroundResource(R.drawable.button_off_classic);
		}
		else
		{
			soundOn = true;
			boardView.turnSoundsOff(false);
			if(currentColor == RED)
				view.setBackgroundResource(R.drawable.button_on_red);
			else if(currentColor == PURPLE)
				view.setBackgroundResource(R.drawable.button_on_purple);
			else if(currentColor == BLUE)
				view.setBackgroundResource(R.drawable.button_on_blue);
			else if(currentColor == CLASSIC)
				view.setBackgroundResource(R.drawable.button_on_classic);
		}

		saveUserPreferences();
	}
	
	public void musicOnOff(View view)
	{
		if(musicOn)
		{
			if(currentColor == RED)
				view.setBackgroundResource(R.drawable.button_off_red);
			else if(currentColor == PURPLE)
				view.setBackgroundResource(R.drawable.button_off_purple);
			else if(currentColor == BLUE)
				view.setBackgroundResource(R.drawable.button_off_blue);
			else if(currentColor == CLASSIC)
				view.setBackgroundResource(R.drawable.button_off_classic);
		}
		else
		{
			if(currentColor == RED)
				view.setBackgroundResource(R.drawable.button_on_red);
			else if(currentColor == PURPLE)
				view.setBackgroundResource(R.drawable.button_on_purple);
			else if(currentColor == BLUE)
				view.setBackgroundResource(R.drawable.button_on_blue);
			else if(currentColor == CLASSIC)
				view.setBackgroundResource(R.drawable.button_on_classic);
		}

		musicOn = !musicOn;
		toggleMusic();
		saveUserPreferences();
	}
	
	/**
	 * Displays or hides the high score menu
	 * @param view
	 */
	public void showBestScores(View view)
	{
		if(initialRun)
		{
			findViewById(R.id.highscore_tip).setVisibility(View.INVISIBLE);
		}
		
		View bsView = findViewById(R.id.best_scores);
		
		if(bsView.getVisibility() == View.INVISIBLE && !paused)
		{
			// show 4x4 high scores by default
			View tab_4x4 = findViewById(R.id.highscore_4x4);
			TimerView timer = (TimerView) findViewById(R.id.simpleTimer);
			TimerView best_time = (TimerView) findViewById(R.id.bestTime);
			MovesView best_moves = (MovesView) findViewById(R.id.bestMoves);
	    	
	    	
			if(currentColor == RED)
			{
				tab_4x4.setBackgroundResource(R.drawable.highscore_4x4_active_red);
				best_time.setBackgroundResource(R.drawable.score_background_red);
				best_moves.setBackgroundResource(R.drawable.score_background_red);
			}
			else if(currentColor == BLUE)
			{
				tab_4x4.setBackgroundResource(R.drawable.highscore_4x4_active_blue);
				best_time.setBackgroundResource(R.drawable.score_background_blue);
				best_moves.setBackgroundResource(R.drawable.score_background_blue);
			}
			else if(currentColor == PURPLE)
			{
				tab_4x4.setBackgroundResource(R.drawable.highscore_4x4_active_purple);
				best_time.setBackgroundResource(R.drawable.score_background_purple);
				best_moves.setBackgroundResource(R.drawable.score_background_purple);
			}
			else if(currentColor == CLASSIC)
			{
				tab_4x4.setBackgroundResource(R.drawable.highscore_4x4_active_classic);
				best_time.setBackgroundResource(R.drawable.score_background_classic);
				best_moves.setBackgroundResource(R.drawable.score_background_classic);
			}
			
			// set the latest values
			best_time.setTime(highScores[2]);
			best_moves.setMoves(highScores[3]);
			bsView.setVisibility(View.VISIBLE);
			bsView.setClickable(true);
			timer.setPause(true);
		}
		else if(bsView.getVisibility() == View.VISIBLE && paused)
			hideBestScores(view);
	}
	
	/**
	 * Hides the menu and resumes the game
	 * @param view
	 */
	public void hideBestScores(View view)
	{
		// reset tabs
		View tab_3x3 = findViewById(R.id.highscore_3x3);
		View tab_5x5 = findViewById(R.id.highscore_5x5);
		tab_3x3.setBackgroundResource(R.drawable.highscore_3x3_inactive);
		tab_5x5.setBackgroundResource(R.drawable.highscore_5x5_inactive);

		// hide the menu and resume the timer
		View bsView = findViewById(R.id.best_scores);
		TimerView timer = (TimerView) findViewById(R.id.simpleTimer);
		bsView.setVisibility(View.INVISIBLE);
		bsView.setClickable(false);
		if(!initialRun)
		{
			timer.setPause(false);
		}
	}
	
	/**
	 * Switches between the tabs to show different high scores based on difficulty
	 * @param view
	 */
	public void switchHighscores(View view)
	{
		TimerView best_time = (TimerView) findViewById(R.id.bestTime);
		MovesView best_moves = (MovesView) findViewById(R.id.bestMoves);
		
		// show the scores according to the tab
		if(view.getId() == R.id.highscore_3x3)
		{
			best_time.setTime(highScores[0]);
			best_moves.setMoves(highScores[1]);
			View tab_4x4 = findViewById(R.id.highscore_4x4);
			View tab_5x5 = findViewById(R.id.highscore_5x5);
			tab_4x4.setBackgroundResource(R.drawable.highscore_4x4_inactive);
			tab_5x5.setBackgroundResource(R.drawable.highscore_5x5_inactive);
			if(currentColor == RED)
				view.setBackgroundResource(R.drawable.highscore_3x3_active_red);
			if(currentColor == BLUE)
				view.setBackgroundResource(R.drawable.highscore_3x3_active_blue);
			if(currentColor == PURPLE)
				view.setBackgroundResource(R.drawable.highscore_3x3_active_purple);
			if(currentColor == CLASSIC)
				view.setBackgroundResource(R.drawable.highscore_3x3_active_classic);
		}
		if(view.getId() == R.id.highscore_4x4)
		{
			best_time.setTime(highScores[2]);
			best_moves.setMoves(highScores[3]);
			View tab_3x3 = findViewById(R.id.highscore_3x3);
			View tab_5x5 = findViewById(R.id.highscore_5x5);
			tab_3x3.setBackgroundResource(R.drawable.highscore_3x3_inactive);
			tab_5x5.setBackgroundResource(R.drawable.highscore_5x5_inactive);
			if(currentColor == RED)
				view.setBackgroundResource(R.drawable.highscore_4x4_active_red);
			if(currentColor == BLUE)
				view.setBackgroundResource(R.drawable.highscore_4x4_active_blue);
			if(currentColor == PURPLE)
				view.setBackgroundResource(R.drawable.highscore_4x4_active_purple);
			if(currentColor == CLASSIC)
				view.setBackgroundResource(R.drawable.highscore_4x4_active_classic);
		}
		if(view.getId() == R.id.highscore_5x5)
		{
			best_time.setTime(highScores[4]);
			best_moves.setMoves(highScores[5]);
			View tab_3x3 = findViewById(R.id.highscore_3x3);
			View tab_4x4 = findViewById(R.id.highscore_4x4);
			tab_3x3.setBackgroundResource(R.drawable.highscore_3x3_inactive);
			tab_4x4.setBackgroundResource(R.drawable.highscore_4x4_inactive);
			if(currentColor == RED)
				view.setBackgroundResource(R.drawable.highscore_5x5_active_red);
			if(currentColor == BLUE)
				view.setBackgroundResource(R.drawable.highscore_5x5_active_blue);
			if(currentColor == PURPLE)
				view.setBackgroundResource(R.drawable.highscore_5x5_active_purple);
			if(currentColor == CLASSIC)
				view.setBackgroundResource(R.drawable.highscore_5x5_active_classic);
		}
	}
	
	public void showHideMenu(View view)
	{	
		if(!boardView.isWon())
		{
			View soundOptionView = findViewById(R.id.soundOption);
			View startGameOptionView = findViewById(R.id.startGameOption);
			View colorOptionView = findViewById(R.id.colorOption);
			View menuCloseView = findViewById(R.id.menuClose);
	
			pauseGame();
			switchMenuButton();
			
			if(paused)
			{
				soundOptionView.setVisibility(View.VISIBLE);
				startGameOptionView.setVisibility(View.VISIBLE);
				colorOptionView.setVisibility(View.VISIBLE);
				menuCloseView.setVisibility(View.VISIBLE);
			}
			else
			{
				soundOptionView.setVisibility(View.INVISIBLE);
				startGameOptionView.setVisibility(View.INVISIBLE);
				colorOptionView.setVisibility(View.INVISIBLE);
				menuCloseView.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	/**
	 * Pauses and resumes the game
	 * @param view
	 */
	public void pauseGame()
	{
		if(initialRun)
		{
			paused = !paused;
			return;
		}
		
		boardView = (BoardView) findViewById(R.id.board);
		TimerView timer = (TimerView) findViewById(R.id.simpleTimer);
			
		boardView.pausedGame(!paused);
		timer.setPause(!paused);
		paused = !paused;
	}
	
	public void switchMenuButton()
	{
		View menuButtonView = (View) findViewById(R.id.menuButton);
		
		if(paused)
		{
			if(currentColor == BLUE)
			{
				menuButtonView.setBackgroundResource(R.drawable.button_play_blue);
			}
			else if(currentColor == RED)
			{
				menuButtonView.setBackgroundResource(R.drawable.button_play_red);
			}
			else if(currentColor == PURPLE)
			{
				menuButtonView.setBackgroundResource(R.drawable.button_play_purple);
			}
			else if(currentColor == CLASSIC)
			{
				menuButtonView.setBackgroundResource(R.drawable.button_play_classic);
			}
		}
		else
		{
			if(currentColor == BLUE)
			{
				menuButtonView.setBackgroundResource(R.drawable.button_pause_blue);
			}
			else if(currentColor == RED)
			{
				menuButtonView.setBackgroundResource(R.drawable.button_pause_red);
			}
			else if(currentColor == PURPLE)
			{
				menuButtonView.setBackgroundResource(R.drawable.button_pause_purple);
			}
			else if(currentColor == CLASSIC)
			{
				menuButtonView.setBackgroundResource(R.drawable.button_pause_classic);
			}
		}
		
	}
	
	/**
	 * Initialises timers for moves count and time
	 */
	private void initializeTimers()
	{
		// a task to update the timer
		timer = new Timer();
	    timer.schedule(new TimerTask()
	    {
	        @Override
	        public void run() {
	        	updateTime();
	        }
	
	    }, 0, 1000);
	    
	    // a task to update the moves and determine a win
	    movesCount = new Timer();
	    movesCount.schedule(new TimerTask()
	    {
	        @Override
	        public void run() {
	        	updateMoves();
	        }
	
	    }, 0, 100);
	}
	
	/**
	 * Updates the time for the timer
	 */
	public void updateTime()
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() 
			{
	        	TimerView timerView = (TimerView) findViewById(R.id.simpleTimer);
	        	timerView.incrementTime();
	        	timerView.invalidate();
			}
		});
	}
	
	/**
	 * Updates the moves count 
	 */
	public void updateMoves()
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() 
			{
				timerMovesView = (MovesView) findViewById(R.id.moves);
				boardView = (BoardView) findViewById(R.id.board);
	        	timerMovesView.setMoves(boardView.getMoves());
	        	
	        	// conditions to win
	        	if(boardView.isWon() && !paused)
	        	{
	        		gameIsWon();
	        	}
			}
		});
	}
    
	/**
	 * Called after winning, this method updates the high scores and display the winning screen
	 */
    public void gameIsWon()
    {
    	pauseGame();
    	
    	playWinGameSound();

		View winView = findViewById(R.id.winscreen);
		TimerView timerView = (TimerView) findViewById(R.id.simpleTimer);
		MovesView movesView = (MovesView) findViewById(R.id.moves);
		View winscreen_restart = findViewById(R.id.winscreen_restart);

    	// old scores
    	int currentHighScore = (dimensions - 3) * 2;
    	
    	boolean newRecordIsSet = false;

		// new scores
    	int newTimeInSeconds = timerView.getTimeInSeconds();
    	int newMoves = movesView.getMoves();
    	
    	if(newTimeInSeconds < highScores[currentHighScore] || highScores[currentHighScore] == 0)
    	{
    		highScores[currentHighScore] = newTimeInSeconds;
    		newRecordIsSet = true;
    	}
    	if(newMoves < highScores[currentHighScore+1] || highScores[currentHighScore+1] == 0)
    	{
    		highScores[currentHighScore+1] = newMoves;
    		newRecordIsSet = true;
    	}

    	saveScore();
		
		if(currentColor == RED)
			winscreen_restart.setBackgroundResource(R.drawable.new_game_red);//le.createFromPath(R.drawable.new_game_red);
		else if(currentColor == BLUE)
			winscreen_restart.setBackgroundResource(R.drawable.new_game_blue);
		else if(currentColor == PURPLE)
			winscreen_restart.setBackgroundResource(R.drawable.new_game_purple);
		else
			winscreen_restart.setBackgroundResource(R.drawable.new_game_classic);
		winView.setVisibility(View.VISIBLE);
		findViewById(R.id.winscreen_restart).setVisibility(View.VISIBLE);
		findViewById(R.id.new_record).setVisibility(newRecordIsSet? View.VISIBLE : View.INVISIBLE);
    }
    
    /**
     * Saves the high score values into a file
     */
    private void saveScore()
    {    	
    	try 
    	{
    		highscore_file.delete();
    		FileOutputStream outputStream = openFileOutput(highscores, MODE_PRIVATE);
			outputStream.write(highScores[0].toString().getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write(highScores[1].toString().getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write(highScores[2].toString().getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write(highScores[3].toString().getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write(highScores[4].toString().getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write(highScores[5].toString().getBytes());
			outputStream.write("\n".getBytes());
			outputStream.close();
		} 
    	catch (IOException e) 
		{
			e.printStackTrace();
		}
    }
    
    /**
     * Reads the high score values from the file
     */
    private void readScore()
    {
    	try 
    	{
    		FileInputStream inputStream = openFileInput(highscores);
		    
		    if (inputStream != null) 
		    {
		      // open buffer for reading
			  BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput(highscores)));
		      
			  String line;
			  int i = 0;
			  
			  while((line = inputReader.readLine()) != null)
			  {
				  highScores[i++] = Integer.parseInt(line);
			  }
		    }
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
	    }
    }
    
    /**
     * Saves the user preferences into a file
     */
    private void saveUserPreferences()
    {    	
    	try 
    	{
    		userpref_file.delete();
    		FileOutputStream outputStream = openFileOutput(userpref, MODE_PRIVATE);
    		
			outputStream.write(currentColor.getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write(Boolean.toString(soundOn).getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write(Boolean.toString(musicOn).getBytes());
			outputStream.write("\n".getBytes());
			outputStream.close();
		} 
    	catch (IOException e) 
		{
			e.printStackTrace();
		}
    }
    
    /**
     * Reads the user preferences from the file
     */
    private void readUserPreferences()
    {
    	try 
    	{
    		FileInputStream inputStream = openFileInput(userpref);
		    
		    if (inputStream != null) 
		    {
				// open buffer for reading
				BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput(userpref)));
				  
				String line;
				int i = 0;
				String userPrefs[] = new String[3];
				  
				while((line = inputReader.readLine()) != null)
				{
				    userPrefs[i++] = line;
				}
				
				currentColor = userPrefs[0];
				soundOn = Boolean.parseBoolean(userPrefs[1]);
				musicOn = Boolean.parseBoolean(userPrefs[2]);
		    }
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
	    }
    }
}
