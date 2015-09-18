package com.onaentertainment.onafifteenpuzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Basic Cell class
 *
 */
public class Cell {

	private Bitmap texture;
	private int x;
	private int y;
	private int id;
	private boolean touched;
	private boolean isEmpty;
	
	public Cell(int x, int y, boolean isEmpty)
	{
		this.x = x;
		this.y = y;
		this.isEmpty = isEmpty;
		
		Options options = new BitmapFactory.Options();
	    options.inScaled = false;
	}
	
	public void setBitmap(Bitmap bitmap){
		texture = bitmap;
	}

	public Bitmap getBitmap() {
		return texture;
	}
	
	public void updateBitmap(int width, int height){
		
		texture = Bitmap.createScaledBitmap(texture, width, height, true);
	}
	
	public int getX(){	
		return x;	
	}
	
	public void setX(int x){
		this.x = x;	
	}
	
	public int getY(){
		return y;	
	}
	
	public void setY(int y){
		this.y = y;	
	}
	
	public boolean isEmpty(){
		return isEmpty;	
	}
	
	public void setIsEmpty(boolean isEmpty){
		this.isEmpty = isEmpty;
	}
	
	public int getId(){
		return id;	
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getHeight(){
		return texture.getHeight();	
	}
	
	public int getWidth(){
		return texture.getWidth();	
	}
	
	public boolean isTouched() {
		return touched;
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
	}
	
	public void draw(Canvas canvas, Paint paint) 
	{
		if(texture != null)
			canvas.drawBitmap(texture, x - (texture.getWidth() / 2), y - (texture.getHeight() / 2), paint);
	}
	
	public void handleActionDown(int eventX, int eventY) 
	{
		// width is same as height
		int halfCellSize = texture.getWidth() / 2; 
		
		if (eventX >= (x - halfCellSize) && (eventX <= (x + halfCellSize))) 
		{
			if (eventY >= (y - halfCellSize) && (eventY <= (y + halfCellSize))) 
			{
				setTouched(true); // cell is touched
				return;
			}
		} 

		setTouched(false);
	}
}
