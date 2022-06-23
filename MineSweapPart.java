import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.MINUTES;
import java.util.concurrent.ThreadLocalRandom;

public class MineSweapPart extends JFrame {
	private static final LocalTime STARTTIME = LocalTime.now();
	private static final long serialVersionUID = 1L;
	private static final int WINDOW_HEIGHT = 760;
	private static final int WINDOW_WIDTH = 760;
	private static final int TOTAL_MINES = 16;


	private static int guessedMinesLeft = TOTAL_MINES;
	private static int actualMinesLeft = TOTAL_MINES;

	private static final String INITIAL_CELL_TEXT = "";
	private static final String UNEXPOSED_FLAGGED_CELL_TEXT = "@";
	private static final String EXPOSED_MINE_TEXT = "M";

	// visual indication of an exposed MyJButton
	private static final Color EXPOSED_CELL_BACKGROUND_COLOR = Color.lightGray;
	// colors used when displaying the getStateStr() String
	private static final Color EXPOSED_CELL_FOREGROUND_COLOR_MAP[] =
			{Color.lightGray, Color.blue, Color.green, Color.cyan, Color.yellow, Color.orange,
			Color.pink, Color.magenta, Color.red, Color.red};


	// holds the "number of mines in perimeter" value for each MyJButton
	private static final int MINEGRID_ROWS = 16;
	private static final int MINEGRID_COLS = 16;
	private int[][] mineGrid = new int[MINEGRID_ROWS][MINEGRID_COLS];

	private static final int NO_MINES_IN_PERIMETER_MINEGRID_VALUE = 0;
	private static final int ALL_MINES_IN_PERIMETER_MINEGRID_VALUE = 8;
	private static final int IS_A_MINE_IN_MINEGRID_VALUE = 9;

	private boolean running = true;

	public MineSweapPart() {
		this.setTitle("MineSweap " + MineSweapPart.guessedMinesLeft + " Mines left");
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setResizable(false);
		this.setLayout(new GridLayout(MINEGRID_ROWS, MINEGRID_COLS, 0, 0));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// set the grid of MyJbuttons
		this.createContents();

		// place MINES number of mines in sGrid and adjust all of the "mines in perimeter" values
		this.setMines();

		this.setVisible(true);
	}

	public void createContents() {
		for (int mgr = 0; mgr < MINEGRID_ROWS; ++mgr) {
			for (int mgc = 0; mgc < MINEGRID_COLS; ++mgc) {
				// set sGrid[mgr][mgc] entry to 0 - no mines in it's perimeter
				this.mineGrid[mgr][mgc] = NO_MINES_IN_PERIMETER_MINEGRID_VALUE;

				// create a MyJButton that will be at location (mgr, mgc) in the GridLayout
				MyJButton but = new MyJButton(INITIAL_CELL_TEXT, mgr, mgc);

				// register the event handler with this MyJbutton
				but.addActionListener(new MyListener());

				// add the MyJButton to the GridLayout collection
				this.add(but);
			}
		}
	}


	// begin nested private class
	private class MyListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (running) {
				// used to determine if ctrl or alt key was pressed at the time of mouse action
				int mod = event.getModifiers();
				MyJButton mjb = (MyJButton) event.getSource();

				// is the MyJbutton that the mouse action occurred in flagged
				boolean flagged = mjb.getText().equals(MineSweapPart.UNEXPOSED_FLAGGED_CELL_TEXT);

				// is the MyJbutton that the mouse action occurred in already exposed
				boolean exposed = mjb.getBackground().equals(EXPOSED_CELL_BACKGROUND_COLOR);

				// flag a cell : ctrl + left click
				if (!flagged && !exposed && (mod & ActionEvent.CTRL_MASK) != 0) {
					if(MineSweapPart.guessedMinesLeft == 0 ) {
                        return;
                    }

					mjb.setText(MineSweapPart.UNEXPOSED_FLAGGED_CELL_TEXT);
					--MineSweapPart.guessedMinesLeft;
					// if the MyJbutton that the mouse action occurred in is a mine
					if (mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_MINEGRID_VALUE) {
						// what else do you need to adjust?
						// could the game be over?
						MineSweapPart.actualMinesLeft--;

						if (actualMinesLeft == 0) {
							JOptionPane.showMessageDialog(null, "Victory! \n Time: " +
								MineSweapPart.STARTTIME.until(LocalTime.now(), MINUTES) +" Min");
                        System.exit(0);
						}
					}
					setTitle("MineSweap " + MineSweapPart.guessedMinesLeft + " Mines left");
				}

				// unflag a cell : alt + left click
				else if (flagged && !exposed && (mod & ActionEvent.ALT_MASK) != 0) {
					mjb.setText(INITIAL_CELL_TEXT);
					++MineSweapPart.guessedMinesLeft;

					// if the MyJbutton that the mouse action occurred in is a mine
					if (mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_MINEGRID_VALUE) {
						// what else do you need to adjust?
						// could the game be over?
						++MineSweapPart.actualMinesLeft;
					}
					setTitle("MineSweap " + MineSweapPart.guessedMinesLeft + " Mines left");
				}
				// expose a cell : left click
				else if (!flagged && !exposed) {
					exposeCell(mjb);
				}
			}
		}

		public void exposeCell(MyJButton mjb) {
			if (!running) {
				return;
			}
			// expose this MyJButton
			mjb.setBackground(EXPOSED_CELL_BACKGROUND_COLOR);
			mjb.setForeground(EXPOSED_CELL_FOREGROUND_COLOR_MAP[mineGrid[mjb.ROW][mjb.COL]]);
			mjb.setText(getGridValueStr(mjb.ROW, mjb.COL));

			// if the MyJButton that was just exposed is a mine
			if (mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_MINEGRID_VALUE) {
				// what else do you need to adjust?
				// could the game be over?
				JOptionPane.showMessageDialog(null, "Game Over! \n Mines Found: " + 
					(MineSweapPart.TOTAL_MINES - MineSweapPart.actualMinesLeft));
                System.exit(0);
			}

			// if the MyJButton that was just exposed has no mines in its perimeter
			if (mineGrid[mjb.ROW][mjb.COL] == NO_MINES_IN_PERIMETER_MINEGRID_VALUE) {
				// lots of work here - must expose all MyJButtons in its perimeter
				// and so on
				// and so on
				// .
				// .
				// .
				// Hint: MyJButton mjbn = (MyJButton)mjb.getParent().getComponent(indn);
				// where indn is a linearized version of a row, col index pair
				
				if (mjb.ROW > 0) {
					MyJButton mjbTop = (MyJButton)mjb.getParent().getComponent(MINEGRID_COLS * (mjb.ROW - 1) + mjb.COL);
					boolean exposed = mjbTop.getBackground().equals(EXPOSED_CELL_BACKGROUND_COLOR);
					if (!exposed) {
						mjbTop.setBackground(EXPOSED_CELL_BACKGROUND_COLOR);
						mjbTop.setForeground(EXPOSED_CELL_FOREGROUND_COLOR_MAP[mineGrid[mjb.ROW][mjb.COL]]);
						mjbTop.setText(getGridValueStr(mjb.ROW, mjb.COL));
						exposeCell(mjbTop);
					}
				}

				if (mjb.ROW < MINEGRID_ROWS - 1) {
					MyJButton mjbBottom = (MyJButton)mjb.getParent().getComponent(MINEGRID_COLS * (mjb.ROW + 1) + mjb.COL);
					boolean exposed = mjbBottom.getBackground().equals(EXPOSED_CELL_BACKGROUND_COLOR);
					if (!exposed) {
						mjbBottom.setBackground(EXPOSED_CELL_BACKGROUND_COLOR);
						mjbBottom.setForeground(EXPOSED_CELL_FOREGROUND_COLOR_MAP[mineGrid[mjb.ROW][mjb.COL]]);
						mjbBottom.setText(getGridValueStr(mjb.ROW, mjb.COL));
						exposeCell(mjbBottom);
					}
				}				
				
				if (mjb.COL < MINEGRID_COLS - 1) {
					MyJButton mjbRight = (MyJButton)mjb.getParent().getComponent(MINEGRID_COLS * mjb.ROW + (mjb.COL + 1));
					boolean exposed = mjbRight.getBackground().equals(EXPOSED_CELL_BACKGROUND_COLOR);
					if (!exposed) {
						mjbRight.setBackground(EXPOSED_CELL_BACKGROUND_COLOR);
						mjbRight.setForeground(EXPOSED_CELL_FOREGROUND_COLOR_MAP[mineGrid[mjb.ROW][mjb.COL]]);
						mjbRight.setText(getGridValueStr(mjb.ROW, mjb.COL));
						exposeCell(mjbRight);
					}
				}

				if (mjb.COL > 0) {
					MyJButton mjbLeft = (MyJButton)mjb.getParent().getComponent(MINEGRID_COLS * mjb.ROW + (mjb.COL - 1));
					boolean exposed = mjbLeft.getBackground().equals(EXPOSED_CELL_BACKGROUND_COLOR);
					if (!exposed) {
						mjbLeft.setBackground(EXPOSED_CELL_BACKGROUND_COLOR);
						mjbLeft.setForeground(EXPOSED_CELL_FOREGROUND_COLOR_MAP[mineGrid[mjb.ROW][mjb.COL]]);
						mjbLeft.setText(getGridValueStr(mjb.ROW, mjb.COL));
						exposeCell(mjbLeft);
					}
				}

				return;
			}
		}
	}
	// end nested private class


	public static void main(String[] args) {
		new MineSweapPart();
	}


	// ************************************************************************************************

	// place MINES number of mines in sGrid and adjust all of the "mines in perimeter" values
	private void setMines() {
		// your code here ...
		for (int i = 0; i < TOTAL_MINES; i++) {
			int randRow = ThreadLocalRandom.current().nextInt(0, MINEGRID_ROWS);
  			int randColumn = ThreadLocalRandom.current().nextInt(0, MINEGRID_COLS);

			if (mineGrid[randRow][randColumn] != IS_A_MINE_IN_MINEGRID_VALUE) {
				mineGrid[randRow][randColumn] = IS_A_MINE_IN_MINEGRID_VALUE;

				if (mineGrid.length >= randRow + 1) {
					if (0 <= randRow - 1) {
						if (mineGrid[randRow - 1][randColumn] != IS_A_MINE_IN_MINEGRID_VALUE) {
							mineGrid[randRow - 1][randColumn]++;
						}
	
						if (mineGrid[randRow - 1].length > randColumn + 1 &&
							mineGrid[randRow - 1][randColumn + 1] != IS_A_MINE_IN_MINEGRID_VALUE) {
							mineGrid[randRow - 1][randColumn + 1]++;
						}
	
						if (0 <= randColumn - 1 && mineGrid[randRow - 1][randColumn -1] != IS_A_MINE_IN_MINEGRID_VALUE) {
							mineGrid[randRow - 1][randColumn - 1]++;
						}
					}
	
					if (mineGrid[randRow].length > randColumn + 1 &&
						mineGrid[randRow][randColumn + 1] != IS_A_MINE_IN_MINEGRID_VALUE) {
						mineGrid[randRow][randColumn + 1]++;
					}
	
					if (0 <= randColumn - 1 && mineGrid[randRow][randColumn - 1] != IS_A_MINE_IN_MINEGRID_VALUE) {
						mineGrid[randRow][randColumn - 1]++;
					}

					if (mineGrid.length > randRow + 1) {
						if (mineGrid[randRow + 1][randColumn] != IS_A_MINE_IN_MINEGRID_VALUE) {
							mineGrid[randRow + 1][randColumn]++;
						}
		
						if (mineGrid[randRow + 1].length > randColumn + 1 && 
							mineGrid[randRow + 1][randColumn + 1] != IS_A_MINE_IN_MINEGRID_VALUE) {
							mineGrid[randRow + 1][randColumn + 1]++;
						}
		
						if (0 <= randColumn - 1 && mineGrid[randRow + 1][randColumn - 1] != IS_A_MINE_IN_MINEGRID_VALUE) {
							mineGrid[randRow + 1][randColumn - 1]++;
						}
					}
				}
			} else {
				i--;
			}
		}
	}

	private String getGridValueStr(int row, int col) {
		// no mines in this MyJbutton's perimeter
		if (this.mineGrid[row][col] == NO_MINES_IN_PERIMETER_MINEGRID_VALUE)
			return INITIAL_CELL_TEXT;

		// 1 to 8 mines in this MyJButton's perimeter
		else if (this.mineGrid[row][col] > NO_MINES_IN_PERIMETER_MINEGRID_VALUE
				&& this.mineGrid[row][col] <= ALL_MINES_IN_PERIMETER_MINEGRID_VALUE)
			return "" + this.mineGrid[row][col];

		// this MyJButton in a mine
		else // this.mineGrid[row][col] = IS_A_MINE_IN_GRID_VALUE
			return MineSweapPart.EXPOSED_MINE_TEXT;
	}
}
