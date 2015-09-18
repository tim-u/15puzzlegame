package com.onaentertainment.onafifteenpuzzle;


import com.onaentertainment.onafifteenpuzzle.R;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BoardView extends View{

	private int[] textures_red = {R.drawable.cell_red_1, R.drawable.cell_red_2, R.drawable.cell_red_3,
						      R.drawable.cell_red_4, R.drawable.cell_red_5, R.drawable.cell_red_6,
						      R.drawable.cell_red_7, R.drawable.cell_red_8, R.drawable.cell_red_9,
						      R.drawable.cell_red_10,R.drawable.cell_red_11,R.drawable.cell_red_12,
						      R.drawable.cell_red_13,R.drawable.cell_red_14,R.drawable.cell_red_15,
						      R.drawable.cell_red_16,R.drawable.cell_red_17,R.drawable.cell_red_18,
						      R.drawable.cell_red_19,R.drawable.cell_red_20,R.drawable.cell_red_21,
						      R.drawable.cell_red_22,R.drawable.cell_red_23,R.drawable.cell_red_24 };

	private int[] textures_blue = {R.drawable.cell_blue_1, R.drawable.cell_blue_2, R.drawable.cell_blue_3,
						      R.drawable.cell_blue_4, R.drawable.cell_blue_5, R.drawable.cell_blue_6,
						      R.drawable.cell_blue_7, R.drawable.cell_blue_8, R.drawable.cell_blue_9,
						      R.drawable.cell_blue_10,R.drawable.cell_blue_11,R.drawable.cell_blue_12,
						      R.drawable.cell_blue_13,R.drawable.cell_blue_14,R.drawable.cell_blue_15,
						      R.drawable.cell_blue_16,R.drawable.cell_blue_17,R.drawable.cell_blue_18,
						      R.drawable.cell_blue_19,R.drawable.cell_blue_20,R.drawable.cell_blue_21,
						      R.drawable.cell_blue_22,R.drawable.cell_blue_23,R.drawable.cell_blue_24 };

	private int[] textures_purple = {R.drawable.cell_purple_1, R.drawable.cell_purple_2, R.drawable.cell_purple_3,
						      R.drawable.cell_purple_4, R.drawable.cell_purple_5, R.drawable.cell_purple_6,
						      R.drawable.cell_purple_7, R.drawable.cell_purple_8, R.drawable.cell_purple_9,
						      R.drawable.cell_purple_10,R.drawable.cell_purple_11,R.drawable.cell_purple_12,
						      R.drawable.cell_purple_13,R.drawable.cell_purple_14,R.drawable.cell_purple_15,
						      R.drawable.cell_purple_16,R.drawable.cell_purple_17,R.drawable.cell_purple_18,
						      R.drawable.cell_purple_19,R.drawable.cell_purple_20,R.drawable.cell_purple_21,
						      R.drawable.cell_purple_22,R.drawable.cell_purple_23,R.drawable.cell_purple_24 };

	private int[] textures_classic = {R.drawable.cell_classic_1, R.drawable.cell_classic_2, R.drawable.cell_classic_3,
						      R.drawable.cell_classic_4, R.drawable.cell_classic_5, R.drawable.cell_classic_6,
						      R.drawable.cell_classic_7, R.drawable.cell_classic_8, R.drawable.cell_classic_9,
						      R.drawable.cell_classic_10,R.drawable.cell_classic_11,R.drawable.cell_classic_12,
						      R.drawable.cell_classic_13,R.drawable.cell_classic_14,R.drawable.cell_classic_15,
						      R.drawable.cell_classic_16,R.drawable.cell_classic_17,R.drawable.cell_classic_18,
						      R.drawable.cell_classic_19,R.drawable.cell_classic_20,R.drawable.cell_classic_21,
						      R.drawable.cell_classic_22,R.drawable.cell_classic_23,R.drawable.cell_classic_24 };
	
	private int[] textures;
	
	private boolean cellMoved = false;
	public boolean m_gameWon = false;
	public boolean m_gameIsPaused = true;
	
	private Cell[][] cells;		 // arrays to store the cells,
	private int[] emptySlot;	 // the location of the empty slot
	private int[] boundingArea;  // and movable area
	
	private int halfCellSize;
	private int numberOfMoves = 0;
	private int dimensions = 4;
	private int length = 100;
	private Paint paint;
	private int touchPointToCellXOffset, touchPointToCellYOffset;
	
	private SoundPool sounds;
	private int sClick;
	private boolean soundIsOn;
	private boolean cellHasReachedBorder = true;
	private boolean cellHasTouchedOtherCell = true;
	
	public BoardView(Context context, AttributeSet attrs) {
		
        super(context, attrs);
		this.setClickable(true);
		
		paint = new Paint();
		paint.setDither(true);
		textures = textures_classic;
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);

		// sounds
		sounds = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
		sClick = sounds.load(context, R.raw.click, 1);
		soundIsOn = true;
		
		// initialise the board with slots
		cells = new Cell[dimensions][dimensions];
		m_gameWon = false;		
		numberOfMoves = 0;
	}

	/**
	 * Draws the box and the cells
	 */
	protected void onDraw(Canvas canvas) 
	{		
		if(isPaused() && !isWon())
		{
			return;
		}
		
		for (int i = 0; i < dimensions; i++) {
			for (int j = 0; j < dimensions; j++) 
			{
				if(cells[i][j] != null && !cells[i][j].isEmpty())
					cells[i][j].draw(canvas, paint);
			}
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		length = MeasureSpec.getSize(widthMeasureSpec);
		length = length / dimensions * dimensions;
		setMeasuredDimension(length, length);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if(isPaused())
		{
			return false;
		}
		
		for (int rowNumber = 0; rowNumber < dimensions; rowNumber ++){
			for (int columnNumber = 0; columnNumber < dimensions; columnNumber ++)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN && this.isClickable()) 
				{
					if(!cells[rowNumber][columnNumber].isEmpty())
					{
						cells[rowNumber][columnNumber].handleActionDown((int)event.getX(), (int)event.getY());
						if(cells[rowNumber][columnNumber].isTouched())
						{
							cellHasReachedBorder = true;
							cellHasTouchedOtherCell = true;
							boundingArea = getMovableArea(rowNumber, columnNumber);
							touchPointToCellYOffset = (int)event.getY() - cells[rowNumber][columnNumber].getY();
							touchPointToCellXOffset = (int)event.getX() - cells[rowNumber][columnNumber].getX();
						}
					}
				} 
		
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
	
					if (isCellTouched(rowNumber, columnNumber)) 
					{
						if(isCellMoveableLeft(rowNumber))
						{
							setCellX((int)event.getX(), rowNumber, columnNumber);
							cellMoved = true;
						}
						else if((isCellMoveableUp(columnNumber)))
						{
							setCellY((int)event.getY(), rowNumber, columnNumber);
							cellMoved = true;
						}
					}
				} 
				
				if (event.getAction() == MotionEvent.ACTION_UP) {
					// touch was released
					boolean cellIsMoveable = isCellMoveableLeft(rowNumber) || isCellMoveableUp(columnNumber);
					if (isCellTouched(rowNumber, columnNumber) && cellIsMoveable)
					{
						snapAllCells(rowNumber, columnNumber);
						if(cellMoved == true)
						{
//							clickSound.start();
//							clickSound.stop();
							cellMoved = false;	
							m_gameWon = isWinGame();
						}
						this.invalidate();
						return true;
					}
				}
			}
		}
		this.invalidate();
		return true;
	}
	
	public void changeColor(String color)
	{
		switch(color)
		{
			case "BLUE":
				textures = textures_blue;
				break;

			case "RED":
				textures = textures_red;
				break;

			case "PURPLE":
				textures = textures_purple;
				break;

			case "CLASSIC":
			default:
				textures = textures_classic;
				break;
		}

		Bitmap bitmap;
		for (int i = 0; i < dimensions; i++) {
			for (int j = 0; j < dimensions; j++) 
			{
				if (cells[i][j] != null && !cells[i][j].isEmpty())
				{
					bitmap = BitmapFactory.decodeResource(getResources(), textures[cells[i][j].getId() - 1]);
					bitmap = Bitmap.createScaledBitmap(bitmap, halfCellSize*2, halfCellSize*2, true);

					cells[i][j].setBitmap(bitmap);
				}
			}
		}
		
		this.invalidate();
	}
	
	/**
	 * Disables clicks (opposite of gameIsPaused state)
	 * @param gameIsPaused - true if paused
	 */
	public void pausedGame(boolean gameIsPaused)
	{
		this.setClickable(!gameIsPaused);
		this.m_gameIsPaused = gameIsPaused;
		this.invalidate();
	}
	
	/**
	 * Determines if the game is paused
	 * @return true if paused, false otherwise
	 */
	public boolean isPaused()
	{
		return this.m_gameIsPaused;
	}
	
	/**
	 * Determines if the game is won
	 * @return true if won, false otherwise
	 */
	public boolean isWon()
	{
		return m_gameWon;
	}

	/**
	 * Sets a new dimension value and recalculates the positions and sizes of the cells
	 * @param dimension - the new dimension value
	 */
	public void setDimension(int dimension)
	{
		startNewGame(dimension);
	}
	
	public int getDimension()
	{
		return dimensions;
	}
	
	/**
	 * Returns the number of moves
	 * @return - the number of moves
	 */
	public int getMoves()
	{
		return numberOfMoves;
	}
	
	/**
	 * Starts a new game
	 */
	public void startNewGame(int dimension)
	{	
		this.requestLayout();
		
		if(cells != null)
			destroyCells();
		
		this.dimensions = dimension;
		
		// initialise the board with slots
		emptySlot = new int[]{dimensions-1,dimensions-1};
		cells = new Cell[dimensions][dimensions];
		boundingArea = new int[]{};
		halfCellSize = (int) Math.round(length / (dimensions * 2.0));
		
		m_gameWon = false;
		
		createCells();
		assignBitmapsToCells();
		shuffleCells();
		
		numberOfMoves = 0;
		pausedGame(false);
	}

	/**
	 * Checks if the game is won
	 * @return true if game is won, false otherwise
	 */
	public boolean isWinGame() 
	{
		for (int i = 0; i < dimensions; i++) {
			for (int j = 0; j < dimensions; j++) 
			{
				if (!cells[i][j].isEmpty() && cells[i][j].getId() != (i * dimensions + j + 1))
					return false;
			}
		}
		return true;
	}
	
	public void turnSoundsOff(boolean mute)
	{
		soundIsOn = (mute) ? false : true;
	}
	
	private void playSound(int sound, boolean condition)
	{
		if(condition && soundIsOn)
		{
			sounds.play(sound, 1.0f, 1.0f, 0, 0, 1.0f);
		}
	}
	
	/**
	 * Checks if the cell is touched or not
	 * @param i - the row of the cell
	 * @param j - the column of the cell
	 * @return true if cell is touched, false otherwise
	 */
	private boolean isCellTouched(int i, int j)
	{
		return cells[i][j].isTouched();
	}
	
	/**
	 * Method checks if the cell is on the same row as empty slot
	 * @param i - the row of the cell
	 * @return true if the cell is movable left or right
	 */
	private boolean isCellMoveableLeft(int i)
	{
		return (emptySlot[0] == i) ? true : false;
	}
	
	/**
	 * Method checks if the cell is on the same column as empty slot
	 * @param j - the column of the cell
	 * @return true if the cell is movable up or down
	 */
	private boolean isCellMoveableUp(int j)
	{
		return (emptySlot[1] == j) ? true : false;
	}
	
	/**
	 * The method sets the X position of the cell moving the cells in the way if necessary
	 * @param x - the new position of the cell
	 * @param i - the row index of the cell
	 * @param j - the column index of the cell
	 */
	private void setCellX(int x, int i, int j)
	{
		int positionWithOffset = x - touchPointToCellXOffset;
		int cellSize = halfCellSize*2;
		
		// drag other cells along
		if(positionWithOffset > cells[i][j].getX()   // the touched cell moves to the right
				&& emptySlot[1] - j > 1 // the empty slot is on the right
				&& positionWithOffset + halfCellSize > cells[i][j+1].getX() - halfCellSize) // cells are overlapping
		{
			for(int a = 1; a < emptySlot[1] - j; a++)
			{
				if (positionWithOffset >= boundingArea[2])// hits the end
				{
					cells[i][j+a].setX(boundingArea[2] + cellSize*a);
					playSound(sClick, !cellHasReachedBorder);
					cellHasReachedBorder = true;
				}
				else
				{
					cells[i][j+a].setX(positionWithOffset + cellSize*a);
					playSound(sClick, !cellHasTouchedOtherCell);
					cellHasTouchedOtherCell = true;
					cellHasReachedBorder = false;
				}
			}
		}	
		else if (positionWithOffset < cells[i][j].getX() 
				&& j - emptySlot[1] > 1
				&& positionWithOffset - halfCellSize < cells[i][j-1].getX() + halfCellSize)
		{
			for(int a = 1; a < j - emptySlot[1]; a++)
			{
				if (positionWithOffset <= boundingArea[0])// hits the end
				{
					cells[i][j-a].setX(boundingArea[0] - cellSize*a);
					playSound(sClick, !cellHasReachedBorder);
					cellHasReachedBorder = true;
				}
				else
				{
					cells[i][j-a].setX(positionWithOffset - cellSize*a);
					playSound(sClick, !cellHasTouchedOtherCell);
					cellHasTouchedOtherCell = true;
					cellHasReachedBorder = false;
				}
			}
		}
		else
		{
			cellHasTouchedOtherCell = false;
		}

		// now move the cell
		if (positionWithOffset >= boundingArea[2])// hits the end
		{
			cells[i][j].setX(boundingArea[2]);
			playSound(sClick, !cellHasReachedBorder);
			cellHasReachedBorder = true;
			cellHasTouchedOtherCell = true;
		}
		else if (positionWithOffset <= boundingArea[0])// hits the end
		{
			cells[i][j].setX(boundingArea[0]);
			playSound(sClick, !cellHasReachedBorder);
			cellHasReachedBorder = true;
			cellHasTouchedOtherCell = true;
		}
		else
		{
			cells[i][j].setX(positionWithOffset);
			cellHasReachedBorder = false;
		}
	}
	
	/**
	 * The method sets the Y position of the cell moving the cells in the way if necessary
	 * @param y - the new position of the cell
	 * @param i - the row index of the cell
	 * @param j - the column index of the cell
	 */
	private void setCellY(int y, int i, int j)
	{		
		int positionWithOffset = y - touchPointToCellYOffset;
		int cellSize = halfCellSize*2;
		
		if(positionWithOffset > cells[i][j].getY() 
				&& emptySlot[0] - i > 1
				&& positionWithOffset + halfCellSize > cells[i+1][j].getY() - halfCellSize)
		{
			for(int a = 1; a < emptySlot[0] - i; a++)
			{
				if (positionWithOffset >= boundingArea[3])// hits the end
				{
					cells[i+a][j].setY(boundingArea[3] + cellSize*a);
					playSound(sClick, !cellHasReachedBorder);
					cellHasReachedBorder = true;
				}
				else
				{
					cells[i+a][j].setY(positionWithOffset + cellSize*a);
					playSound(sClick, !cellHasTouchedOtherCell);
					cellHasTouchedOtherCell = true;
					cellHasReachedBorder = false;
				}
			}
		}	
		else if (positionWithOffset < cells[i][j].getY() 
				&& i - emptySlot[0] > 1 
				&& positionWithOffset - halfCellSize < cells[i-1][j].getY() + halfCellSize)
		{
			for(int a = 1; a < i - emptySlot[0]; a++)
			{
				if (positionWithOffset <= boundingArea[1]) // hits the end
				{
					cells[i-a][j].setY(boundingArea[1] - cellSize*a);
					playSound(sClick, !cellHasReachedBorder);
					cellHasReachedBorder = true;
				}
				else
				{
					cells[i-a][j].setY(positionWithOffset - cellSize*a);
					playSound(sClick, !cellHasTouchedOtherCell);
					cellHasTouchedOtherCell = true;
					cellHasReachedBorder = false;
				}
			}
		}
		else
		{
			cellHasTouchedOtherCell = false;
		}
		
		// now move the cell
		if (positionWithOffset >= boundingArea[3])
		{
			cells[i][j].setY(boundingArea[3]);
			playSound(sClick, !cellHasReachedBorder);
			cellHasReachedBorder = true;
			cellHasTouchedOtherCell = true;
		}
		else if (positionWithOffset <= boundingArea[1])
		{
			cells[i][j].setY(boundingArea[1]);
			playSound(sClick, !cellHasReachedBorder);
			cellHasReachedBorder = true;
			cellHasTouchedOtherCell = true;
		}
		else
		{
			cells[i][j].setY(positionWithOffset);
			cellHasReachedBorder = false;
		}
	}
	
	private boolean isSnappingNeeded()
	{
		for(int i = 0; i < dimensions; i++)
		{
			for(int j = 0; j < dimensions; j++)
			{
				if(cells[i][j].getX() != getCoordBasedOnPosition(j) || cells[i][j].getY() != getCoordBasedOnPosition(i))
				{
					return true;
				}	
			}
		}
		
		return false;
	}
	
	private void snapAllCells(int i, int j)
	{
		// move the cells that are "in the way"
		if(j == emptySlot[1] && Math.abs(i - emptySlot[0]) > 1)
		{
			switch(i - emptySlot[0])
			{
				case 4:
					moveCellToNearestSlot(i-3, j);
				case 3:
					moveCellToNearestSlot(i-2, j);
				case 2:
					moveCellToNearestSlot(i-1, j);
					break;
				case -4:
					moveCellToNearestSlot(i+3, j);
				case -3:
					moveCellToNearestSlot(i+2, j);
				case -2:
					moveCellToNearestSlot(i+1, j);
					break;
				default:
					break;
			}
		}
		else if(i == emptySlot[0] && Math.abs(j - emptySlot[1]) > 1)
		{
			switch(j - emptySlot[1])
			{
				case 4:
					moveCellToNearestSlot(i, j-3);
				case 3:
					moveCellToNearestSlot(i, j-2);
				case 2:
					moveCellToNearestSlot(i, j-1);
					break;
				case -4:
					moveCellToNearestSlot(i, j+3);
				case -3:
					moveCellToNearestSlot(i, j+2);
				case -2:
					moveCellToNearestSlot(i, j+1);
					break;
				default:
					break;
			}	
		}
		moveCellToNearestSlot(i, j);
		playSound(sClick, !cellHasReachedBorder || isSnappingNeeded());
	}
	
	/**
	 * Moves the cell to the nearest empty slot
	 * @param i - the row index of the cell
	 * @param j - the column index of the cell
	 */
	private void moveCellToNearestSlot(int i, int j)
	{
		if(Math.abs(cells[i][j].getX() - getCoordBasedOnPosition(j)) < Math.abs(cells[i][j].getX() - getCoordBasedOnPosition(emptySlot[1])) || 
		   Math.abs(cells[i][j].getY() - getCoordBasedOnPosition(i)) < Math.abs(cells[i][j].getY() - getCoordBasedOnPosition(emptySlot[0])))
		{
			cells[i][j].setX(getCoordBasedOnPosition(j));
			cells[i][j].setY(getCoordBasedOnPosition(i));
			return;
		}
		
		moveCellToEmptySlot(i, j);
	}
	
	/**
	 * Moves the cell to the empty slot
	 * @param i - the row index of the cell
	 * @param j - the column index of the cell
	 */
	private void moveCellToEmptySlot(int i, int j)
	{
		// switch the cells
		Cell temp = cells[i][j];
		cells[i][j] = cells[emptySlot[0]][emptySlot[1]];
		cells[i][j].setX(getCoordBasedOnPosition(j));
		cells[i][j].setY(getCoordBasedOnPosition(i));
		cells[i][j].setTouched(false);
		
		cells[emptySlot[0]][emptySlot[1]] = temp;
		cells[emptySlot[0]][emptySlot[1]].setX(getCoordBasedOnPosition(emptySlot[1]));
		cells[emptySlot[0]][emptySlot[1]].setY(getCoordBasedOnPosition(emptySlot[0]));
		cells[emptySlot[0]][emptySlot[1]].setTouched(false);
		
		// update the empty slot position
		emptySlot[0] = i;
		emptySlot[1] = j;
		
		if(numberOfMoves < 1000)
			numberOfMoves++;
	}
	
	/**
	 * Get the coordinate based on cell's position
	 */
	private int getCoordBasedOnPosition(int a)
	{
		return a * (2 * halfCellSize) + halfCellSize;
	}
	
	/**
	 * Calculates the movable area for a cell
	 * @param i - the row index of the cell
	 * @param j - the column index of the cell
	 * @return the array with (in that order): x min, y min, x max, y max 
	 */
	private int[] getMovableArea(int i, int j)
	{
		if(j - emptySlot[1] > 0) // cell is on the right
			return new int[]{cells[i][j-1].getX(), cells[i][j-1].getY(), cells[i][j].getX(), cells[i][j].getY()};
		else if(j - emptySlot[1] < 0) // cell is on the left
			return new int[]{cells[i][j].getX(), cells[i][j].getY(), cells[i][j+1].getX(), cells[i][j+1].getY()};
		else if(i - emptySlot[0] > 0) // cell is at the bottom
			return new int[]{cells[i-1][j].getX(), cells[i-1][j].getY(), cells[i][j].getX(), cells[i][j].getY()};
		else if(i - emptySlot[0] < 0) // cell is on top
			return new int[]{cells[i][j].getX(), cells[i][j].getY(), cells[i+1][j].getX(), cells[i+1][j].getY()};
		else 
			return null;
	}
	
	private void destroyCells()
	{
		emptySlot = null;
		boundingArea = null;
		
		if(cells[0][0] != null)
		{
			for (int i = 0; i < dimensions; i++) 
			{
				for (int j = 0; j < dimensions; j++) 
				{
					cells[i][j] = null;
				}
			}
		}
	}
	
	private void createCells()
	{
		for (int i = 0; i < dimensions; i ++) 
		{
			for (int j = 0; j < dimensions; j++) 
			{
				// set empty cell
				boolean isEmptyCell = (i == dimensions-1 && j == dimensions-1) ? true : false;

				cells[i][j] = new Cell(getCoordBasedOnPosition(j), getCoordBasedOnPosition(i), isEmptyCell);
			}
		}
	}
	
	/**
	 * Assigns bitmaps to cells
	 */
	private void assignBitmapsToCells()
	{
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		for (int a = 0; a < dimensions * dimensions - 1; a++) 
		{
			ids.add(a + 1);
		}

		for (int i = 0; i < dimensions; i++) {
			for (int j = 0; j < dimensions; j++) 
			{
				int index = (i * dimensions) + j;
				if (index != dimensions * dimensions - 1)
				{
					Bitmap bitmap = BitmapFactory.decodeResource(getResources(), textures[ids.get(index) - 1]);
					bitmap = Bitmap.createScaledBitmap(bitmap, halfCellSize*2, halfCellSize*2, true);
					
					if (!cells[i][j].isEmpty())
					{
						cells[i][j].setBitmap(bitmap);
					}
					
					cells[i][j].setId(ids.get(index));
				}
			}
		}
	}

	private void shuffleCells()
	{
		
		for (int i = 0; i < 500; i ++) 
		{
			boolean moved = false;
			
			while(!moved)
			{
				int randomDir = (int) (Math.random() * 4);
				
				switch(randomDir)
				{
					case(0): // up
					{
						if(emptySlot[1] != 0)
						{
							moveCellToEmptySlot(emptySlot[0], emptySlot[1]-1);
							moved = true;
						}
						break;
					}
					case(1): // down
					{
						if(emptySlot[1] != dimensions-1)
						{
							moveCellToEmptySlot(emptySlot[0], emptySlot[1]+1);
							moved = true;
						}
						break;
					}
					case(2): // left
					{
						if(emptySlot[0] != 0)
						{
							moveCellToEmptySlot(emptySlot[0]-1, emptySlot[1]);
							moved = true;
						}
						break;
					}
					case(3): // right
					{
						if(emptySlot[0] != dimensions-1)
						{
							moveCellToEmptySlot(emptySlot[0]+1, emptySlot[1]);
							moved = true;
						}
						break;
					}
					default:
						break;
				}
				
			}
		}
	}
}
