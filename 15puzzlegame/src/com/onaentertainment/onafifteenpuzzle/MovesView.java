package com.onaentertainment.onafifteenpuzzle;

import com.onaentertainment.onafifteenpuzzle.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class MovesView extends View
{
	private int digitPositions[];
	private double paddings = 0.8;
	private int totalWidth;
	private int xCoor = 0;
	private int yCoor = 0;
	private Integer numberOfMoves = 0;
	private Bitmap movesBitmap;	
	private char[] moves;
	
	private Bitmap digitBitmaps_movesIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_moves);
	
	private Bitmap[] digitBitmaps = {
			BitmapFactory.decodeResource(getResources(), R.drawable.timer0),
			BitmapFactory.decodeResource(getResources(), R.drawable.timer1),
			BitmapFactory.decodeResource(getResources(), R.drawable.timer2),
			BitmapFactory.decodeResource(getResources(), R.drawable.timer3),
			BitmapFactory.decodeResource(getResources(), R.drawable.timer4),
			BitmapFactory.decodeResource(getResources(), R.drawable.timer5),
			BitmapFactory.decodeResource(getResources(), R.drawable.timer6),
			BitmapFactory.decodeResource(getResources(), R.drawable.timer7),
			BitmapFactory.decodeResource(getResources(), R.drawable.timer8),
			BitmapFactory.decodeResource(getResources(), R.drawable.timer9),
			};
	
	public MovesView(Context context, AttributeSet attrs) 
	{
        super(context, attrs);
        
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MovesView);
        int paddings_value = attributes.getInt(R.styleable.MovesView_moves_paddings, 8);
        this.paddings = (double) paddings_value / 10;
        
        digitPositions = new int[4];
        digitPositions[0] = digitBitmaps_movesIcon.getWidth();
        digitPositions[1] = digitBitmaps[0].getWidth() + digitPositions[0];
        digitPositions[2] = digitBitmaps[0].getWidth() + digitPositions[1];
        digitPositions[3] = digitBitmaps[0].getWidth() + digitPositions[2];
        
        totalWidth = digitBitmaps[0].getWidth() * 6;
	}
	
	private void scaleBitmaps(int h)
	{
		for(int i = 0; i < 10; i++)
		{
			digitBitmaps[i] = Bitmap.createScaledBitmap(digitBitmaps[i], digitBitmaps[i].getWidth()*h/digitBitmaps[i].getHeight(), h, true);
		}
		digitBitmaps_movesIcon = Bitmap.createScaledBitmap(digitBitmaps_movesIcon, digitBitmaps_movesIcon.getWidth()*h/digitBitmaps_movesIcon.getHeight(), h, true);
		
		//update positions
        digitPositions[0] = digitBitmaps_movesIcon.getWidth();
        digitPositions[1] = digitBitmaps[0].getWidth() + digitPositions[0];
        digitPositions[2] = digitBitmaps[0].getWidth() + digitPositions[1];
        digitPositions[3] = digitBitmaps[0].getWidth() + digitPositions[2];
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
		int newh = (int)(h*paddings);		
        totalWidth = w;
        
		if((digitBitmaps[0].getWidth() * newh / digitBitmaps[0].getHeight()) * 4 <= totalWidth)
			scaleBitmaps(newh);
		
		//update y offset
        yCoor = (h - digitBitmaps[0].getHeight()) / 2;
	}
	
	public void onDraw(Canvas canvas)
	{
		if(numberOfMoves < 10)
	        xCoor = (totalWidth - digitBitmaps_movesIcon.getWidth() - digitBitmaps[0].getWidth()) / 2;
		else if (numberOfMoves < 100)
	        xCoor = (totalWidth - digitBitmaps_movesIcon.getWidth() - digitBitmaps[0].getWidth() * 2) / 2;
		else
	        xCoor = (totalWidth - digitBitmaps_movesIcon.getWidth() - digitBitmaps[0].getWidth() * 3) / 2;
        
		//draw moves
		moves = numberOfMoves.toString().toCharArray();
		canvas.drawBitmap(digitBitmaps_movesIcon, xCoor, yCoor, null);
		
		for(int i = 0; i < moves.length; i++)
		{
			switch(moves[i])
			{
			case '0':
				movesBitmap = digitBitmaps[0];
				break;
			case '1':
				movesBitmap = digitBitmaps[1];
				break;
			case '2':
				movesBitmap = digitBitmaps[2];
				break;
			case '3':
				movesBitmap = digitBitmaps[3];
				break;
			case '4':
				movesBitmap = digitBitmaps[4];
				break;
			case '5':
				movesBitmap = digitBitmaps[5];
				break;
			case '6':
				movesBitmap = digitBitmaps[6];
				break;
			case '7':
				movesBitmap = digitBitmaps[7];
				break;
			case '8':
				movesBitmap = digitBitmaps[8];
				break;
			case '9':
				movesBitmap = digitBitmaps[9];
				break;
			default:
				movesBitmap = digitBitmaps[0];
				break;
			}
			canvas.drawBitmap(movesBitmap, xCoor + digitPositions[i], yCoor, null);
		}
	}
	
	public void setMoves(int moves)
	{
		numberOfMoves = moves;
		if(numberOfMoves == 0 || numberOfMoves == 10 || numberOfMoves == 100)
			this.requestLayout();
		this.invalidate();
	}
	
	public int getMoves()
	{
		return numberOfMoves;
	}
}
