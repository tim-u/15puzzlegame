package com.onaentertainment.onafifteenpuzzle;

import com.onaentertainment.onafifteenpuzzle.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class LabelView extends View {
	
	private Bitmap background;
	private int width, height;
	private double imageHeight, imageWidth;
	private boolean alignCenter;
	private double textTopPadding = 0.2;
	private double textSize = 0.8;
	private double sidePadding = 0.02;

	public LabelView(Context context, AttributeSet attrs) 
	{
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.LabelView);
        
        // assign background image
        Drawable image = attributes.getDrawable(R.styleable.LabelView_background);  
        background = ((BitmapDrawable)image).getBitmap();
        
        alignCenter = attributes.getBoolean(R.styleable.LabelView_alignCenter, false);
        
    	width = height = 0;
    	imageHeight = imageWidth = 0.0;
	}
		
	/**
	 * Reposition the bitmap background
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
		width = w;
		height = h;
		imageHeight = height * textSize;
		imageWidth = imageHeight/background.getHeight() * background.getWidth();

		if(imageHeight > 0 && imageWidth > 0)
		{
			background = Bitmap.createScaledBitmap(background, (int) imageWidth, (int) imageHeight, true);
		}
	}
	
	/**
	 * Position and draw the bitmap background
	 */
	@Override
	protected void onDraw(Canvas canvas) 
	{
		if((width - imageWidth) > 0 && background != null)
		{
			double padding = alignCenter ? 0.5 : sidePadding;
			canvas.drawBitmap(background, (int)((width - imageWidth) * padding), (int)(height * textTopPadding), null);
		}
	}
}
