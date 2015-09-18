package com.onaentertainment.onafifteenpuzzle;

import com.onaentertainment.onafifteenpuzzle.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class TimerView extends View
{
	private boolean paused = false;
	private double paddings = 0.8;
	private int minutes, seconds = 0;
	private String timeValue = "00:00";
	private int totalWidth;
	private int digitPositions[];
	private char timerChar[];
	private int xCoor = 0;
	private int yCoor = 0;
	private Bitmap timerDigitBitmap;
	
	private Bitmap digitBitmaps_timerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_timer);
	
	private Bitmap[] digitBitmaps = 
			{BitmapFactory.decodeResource(getResources(), R.drawable.timer0),
			 BitmapFactory.decodeResource(getResources(), R.drawable.timer1),
			 BitmapFactory.decodeResource(getResources(), R.drawable.timer2),
			 BitmapFactory.decodeResource(getResources(), R.drawable.timer3),
			 BitmapFactory.decodeResource(getResources(), R.drawable.timer4),
			 BitmapFactory.decodeResource(getResources(), R.drawable.timer5),
			 BitmapFactory.decodeResource(getResources(), R.drawable.timer6),
			 BitmapFactory.decodeResource(getResources(), R.drawable.timer7),
			 BitmapFactory.decodeResource(getResources(), R.drawable.timer8),
			 BitmapFactory.decodeResource(getResources(), R.drawable.timer9),
			 BitmapFactory.decodeResource(getResources(), R.drawable.timer_colon)};
	
	public TimerView(Context context, AttributeSet attrs) 
	{
        super(context, attrs);
        
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TimerView);
        int paddings_value = attributes.getInt(R.styleable.TimerView_timer_paddings, 8);
        this.paddings = (double) paddings_value / 10;

        //set the position for each digit
        digitPositions = new int[8];
        digitPositions[0] = digitBitmaps_timerIcon.getWidth();
        digitPositions[1] = digitBitmaps[0].getWidth() + digitPositions[0];
        digitPositions[2] = digitBitmaps[0].getWidth() + digitPositions[1];
        digitPositions[3] = digitBitmaps[10].getWidth() + digitPositions[2]; // when colon - special distance
        digitPositions[4] = digitBitmaps[0].getWidth() + digitPositions[3];
        digitPositions[5] = digitBitmaps[0].getWidth() + digitPositions[4];
        
        totalWidth = digitBitmaps[0].getWidth() * 6;
	}
	
	private void scaleBitmaps(int h)
	{
		for(int i = 0; i < 11; i++)
		{
			digitBitmaps[i] = Bitmap.createScaledBitmap(digitBitmaps[i], digitBitmaps[i].getWidth()*h/digitBitmaps[i].getHeight(), h, true);
		}
		digitBitmaps_timerIcon = Bitmap.createScaledBitmap(digitBitmaps_timerIcon, digitBitmaps_timerIcon.getWidth()*h/digitBitmaps_timerIcon.getHeight(), h, true);
		
		//update positions
        digitPositions[0] = digitBitmaps_timerIcon.getWidth();
        digitPositions[1] = digitBitmaps[0].getWidth() + digitPositions[0];
        digitPositions[2] = digitBitmaps[0].getWidth() + digitPositions[1];
        digitPositions[3] = digitBitmaps[10].getWidth() + digitPositions[2]; // when colon - special distance
        digitPositions[4] = digitBitmaps[0].getWidth() + digitPositions[3];
        digitPositions[5] = digitBitmaps[0].getWidth() + digitPositions[4];
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
		int newh = (int)(h*paddings);
		
		if((digitBitmaps[0].getWidth() * newh / digitBitmaps[0].getHeight()) * 6 <= totalWidth)
			scaleBitmaps(newh);
		
		xCoor = (w - (digitBitmaps[0].getWidth() * 4 + digitBitmaps[10].getWidth() + digitBitmaps_timerIcon.getWidth())) / 2;
		yCoor = (h - digitBitmaps[0].getHeight()) / 2;
	}
	
	public void onDraw(Canvas canvas)
	{
		timerChar = timeValue.toCharArray();
		canvas.drawBitmap(digitBitmaps_timerIcon, xCoor, yCoor, null);
		
		for(int i = 0; i < timerChar.length; i++)
		{
			switch(timerChar[i])
			{
			case '0':
				timerDigitBitmap = digitBitmaps[0];
				break;
			case '1':
				timerDigitBitmap = digitBitmaps[1];
				break;
			case '2':
				timerDigitBitmap = digitBitmaps[2];
				break;
			case '3':
				timerDigitBitmap = digitBitmaps[3];
				break;
			case '4':
				timerDigitBitmap = digitBitmaps[4];
				break;
			case '5':
				timerDigitBitmap = digitBitmaps[5];
				break;
			case '6':
				timerDigitBitmap = digitBitmaps[6];
				break;
			case '7':
				timerDigitBitmap = digitBitmaps[7];
				break;
			case '8':
				timerDigitBitmap = digitBitmaps[8];
				break;
			case '9':
				timerDigitBitmap = digitBitmaps[9];
				break;
			case ':':
				timerDigitBitmap = digitBitmaps[10];
				break;
			default:
				timerDigitBitmap = digitBitmaps[0];
				break;
			}
			canvas.drawBitmap(timerDigitBitmap, xCoor + digitPositions[i], yCoor, null);
		}
	}
	
	/**
	 * Pauses the timer
	 * @param state
	 */
	public void setPause(boolean state)
	{
		paused = state;
	}
	
	/**
	 * Resets the timer to 0
	 */
	public void resetTime()
	{
		minutes = 0;
		seconds = 0;
	}
	
	/**
	 * Sets the time to specific value
	 * @param timeValue - the time value
	 */
	public void setTime(int seconds)
	{
		this.minutes = (int)(seconds / 60);
		this.seconds = seconds - minutes * 60;
		intToStringTime();
		this.invalidate();
	}
	
	public int getTimeInSeconds()
	{
		return minutes * 60 + seconds;
	}
	
	/**
	 *  Increments the timer by 1 second
	 */
	public void incrementTime()
	{
		// stop incrementing if reached limit
		if(minutes == 59 && seconds == 59)
			return;
		
		if(!paused)
		{
			seconds++;
			if(seconds == 60)
			{
				minutes++;
				seconds = 0;
			}
		}		
		intToStringTime();
	}
	
	private void intToStringTime()
	{
		timeValue = ""; // reset the timeValue
		
		// convert the ints to string	
		if(minutes < 10)
			timeValue = "0"; // additional 0 for minutes < 10
		
		timeValue = timeValue + minutes + ":";
		
		if(seconds < 10)
			timeValue = timeValue + "0";
		
		timeValue = timeValue + seconds;
	}
}
