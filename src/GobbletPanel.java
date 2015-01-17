import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Font;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GobbletPanel extends JPanel	{
	final int SIDEMARGIN = 250;
	final int SQUARESIZE = 250;
	Graphics g;

	Piece[] Pieces;
	Piece[][] board;
	Piece[][] PieceSave;

	Piece toMove;
	Piece inito;

	boolean redturn;

	JPanel main;
	JButton cancelbutton, undobutton, newGameButton;

	Piece lastMousePiece = null;

	int turnNum;

	GobbletPanel()	{
		setLayout(new BorderLayout());
		main = new GamePanel();
		add(main, BorderLayout.NORTH);
		add(new BottomPanel(), BorderLayout.SOUTH);
	}
	void saveBoard(int index)	{
		for (int i = 0; i < Pieces.length; i++)
			PieceSave[index][i] = new Piece(Pieces[i]);
		if (undobutton != null)	undobutton.setEnabled(true);
		if (newGameButton != null) newGameButton.setEnabled(true);
	}
	void loadBoard(int index)	{
		for (int i = 0; i < Pieces.length; i++)
			Pieces[i] = new Piece(PieceSave[index][i]);
		if (index == 0) undobutton.setEnabled(false);
	}
	private class GamePanel extends JPanel implements MouseListener, MouseMotionListener	{
		GamePanel()	{
			setPreferredSize(new Dimension(1500, 1000));
			turnNum = 0;
			createPieces();
			redturn = true;
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		void createPieces()	{
			board = new Piece[4][4];
			for (int i = 0; i < board.length; i++)
				for (int a = 0; a < board[i].length; a++)
					board[i][a] = null;
	
			Pieces = new Piece[24];
			PieceSave = new Piece[500][24];
			for (int i = 0; i < 12; i++)
				Pieces[i] = new Piece(true, i%4+1, -1, i%3);
			for (int i = 12; i < 24; i++)
				Pieces[i] = new Piece(false, i%4+1, -1, i%3);
			saveBoard(turnNum);
			turnNum++;
		}
		public void paintComponent(Graphics a)	{
			super.paintComponent(a);
			g = a;
			drawBoard();
		}
		void drawBoard()	{
			if (gameOver)
				for (int i = 0; i < victoryStones.length; i++)
					drawPiece(victoryStones[i].getX()*SQUARESIZE+SIDEMARGIN+SQUARESIZE/2, victoryStones[i].getY()*SQUARESIZE+SQUARESIZE/2, SQUARESIZE, Color.yellow);
			g.setColor(Color.black);
			for (int i = 0; i <= 5; i++)	{
				g.drawLine(SIDEMARGIN, i * SQUARESIZE, getWidth()-SIDEMARGIN, i * SQUARESIZE);
				g.drawLine(SIDEMARGIN + i * SQUARESIZE, 0, SIDEMARGIN + i * SQUARESIZE, getHeight());
			}
			for (int i = 0; i < board.length; i++)
				for (int a = 0; a < board[i].length; a++)
					if (board[i][a] != null)
						drawPiece(i*SQUARESIZE+SIDEMARGIN+SQUARESIZE/2, a*SQUARESIZE+SQUARESIZE/2, board[i][a].getSize()*SQUARESIZE/5, board[i][a].getColor());
	
			for (int i = 0; i < 3; i++)
				drawPiece(SIDEMARGIN/2, i * SQUARESIZE+SQUARESIZE/2 + getHeight()/8, getSize(true, -1, i)*SQUARESIZE/5, Color.red);
	
			for (int i = 0; i < 3; i++)
				drawPiece(getWidth()-SIDEMARGIN/2, i * SQUARESIZE+SQUARESIZE/2 + getHeight()/8, getSize(false, -1, i)*SQUARESIZE/5, Color.green);
	
			if (toMove != null)	drawPiece(toMove.getX()*SQUARESIZE+SIDEMARGIN+SQUARESIZE/2, toMove.getY()*SQUARESIZE+SQUARESIZE/2, toMove.getSize()*SQUARESIZE/5, toMove.getColor());
		}
		int getSize(boolean red, int x, int y)	{
			int maxsize = 0;
			for (int i = 0; i < Pieces.length; i++)
				if (Pieces[i].hasValues(red, x, y))
					if (Pieces[i].getSize() > maxsize)
						maxsize = Pieces[i].getSize();
			return maxsize;
		}
		Piece getVisiblePieceAt(boolean red, int x, int y)	{
			int maxsize = 0;
			int index = -1;
			for (int i = 0; i < Pieces.length; i++)
				if (Pieces[i].hasValues(red, x, y))
					if (Pieces[i].getSize() > maxsize)	{
						maxsize = Pieces[i].getSize();
						index = i;
					}
			return index != -1 ? Pieces[index]:null;
		}
		Piece getVisiblePieceAt(Piece p, boolean checkRed)	{
			return checkRed ? getVisiblePieceAt(p.getRed(), p.getX(), p.getY()):getVisiblePieceAt(p.getX(), p.getY());
		}
		void drawPiece(int x, int y, int size, Color c)	{
			g.setColor(c);
			g.fillRect(x-size/2, y-size/2, size, size);
		}
		Piece getCoord(int x, int y)	{
			Piece temp = new Piece();
			if (x < SIDEMARGIN)	{
				int index = 0;
				temp.setRed(true);
				for (; y > getHeight()/8+index*SQUARESIZE+SQUARESIZE; index++);
				temp.setX(-1);
				temp.setY(index);
			}
			else if (x > getWidth()-SIDEMARGIN)	{
				int index = 0;
				temp.setRed(false);
				for (; y > getHeight()/8+index*SQUARESIZE+SQUARESIZE; index++);
				temp.setX(-1);
				temp.setY(index);
			}
			else {
				temp.setX((x - SIDEMARGIN)/SQUARESIZE);
				temp.setY(y/SQUARESIZE);
			}
			return temp;
		}
		Piece getPieceCoord(int x, int y)	{
			if (x < SIDEMARGIN)	{
				int index = 0;
				for (; y > getHeight()/8+index*SQUARESIZE+SQUARESIZE; index++);
				return getVisiblePieceAt(true, -1, index);
			}
			else if (x > getWidth()-SIDEMARGIN)	{
				int index = 0;
				for (; y > getHeight()/8+index*SQUARESIZE+SQUARESIZE; index++);
				return getVisiblePieceAt(false, -1, index);
			}
			else return board[(x - SIDEMARGIN)/SQUARESIZE][y/SQUARESIZE];
		}
		boolean canMoveTo(Piece toMove, Piece moveTo)	{
			if (moveTo.getX() < 0) return false;
			if (moveTo.isAt(inito))	return false;
			if (inito.getX() < 0 && !threePieces(moveTo)) return false;
			Piece temp = board[moveTo.getX()][moveTo.getY()];
			if (temp == null) return true;
			return toMove.getSize() > temp.getSize();
		}
		boolean threePieces(Piece moveTo)	{	// implements the rule that you can't eat a piece when starting from outside the board unless...
			int X = moveTo.getX(), Y = moveTo.getY();
			boolean Red = inito.getRed();
			int countPieces = 0;
			if (board[X][Y] == null) return true;
			if (board[X][Y].getRed() == Red) return false;
			for (int x = 0; x < board.length; x++)
				if (board[x][Y] == null);
				else if (board[x][Y].getRed() != Red)
					countPieces++;
			if (countPieces >= 3) return true;
			countPieces = 0;
			for (int y = 0; y < board[0].length; y++)
				if (board[X][y] == null);
				else if (board[X][y].getRed() != Red)
					countPieces++;
			if (countPieces >= 3) return true;
			countPieces = 0;
			int min = X < Y ? X:Y;
			for (int x = X-min, y = Y-min; x < board.length && y < board.length; x++, y++)
				if (board[x][y] == null);
				else if (board[x][y].getRed() != Red)
					countPieces++;
			if (countPieces >= 3) return true;
			countPieces = 0;
			min = (3-X) < Y ? (3-X):Y;
			if (X + Y > 1 && X + Y < 5)
			for (int x = X+min, y = Y-min; x > -1 && y < board.length; x--, y++)
				if (board[x][y] == null);
				else if (board[x][y].getRed() != Red)
					countPieces++;
			if (countPieces >= 3) return true;
			return false;
		}
		public void mouseExited(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseReleased(MouseEvent e){}
		public void mousePressed(MouseEvent e)	{
			if (gameOver)	return;
			int x = e.getX();
			int y = e.getY();
			if (toMove == null)
				if (getPieceCoord(x,y) == null)
					return;
				else if (getPieceCoord(x,y).getRed() == redturn)	{
					toMove = getPieceCoord(x, y);
					inito = new Piece(toMove);
					cancelbutton.setEnabled(true);
				}
				else;
			//else if (canMoveTo(toMove, getCoord(x, y))) {
			else if (getCoord(e.getX(), e.getY()).getX() != -1){
				toMove = null;
				inito = null;
				lastMousePiece = null;
				saveBoard(turnNum);
				turnNum++;
				refreshBoard();
				cancelbutton.setEnabled(false);
				redturn = !redturn;
				cancelbutton.setBackground(redturn ? Color.red:Color.green);
				repaint();
			}
		}
		public void mouseMoved(MouseEvent e)	{
			if (gameOver) return;
			if (toMove != null)
				if (lastMousePiece == null || !lastMousePiece.isAt(getCoord(e.getX(), e.getY()))) {
					lastMousePiece = getCoord(e.getX(), e.getY());
					if (canMoveTo(toMove, lastMousePiece)) {
						toMove.moveTo(lastMousePiece);
						if (getVisiblePieceAt(inito, false) != null && getVisiblePieceAt(inito, false).getRed() != toMove.getRed() && inito.getX() != -1)
							cancelbutton.setEnabled(false);
						refreshBoard();
						if (gameOver(true)) endGame();
						repaint();
					}				
				}
		}
		boolean gameOver = false;
		boolean victoryColor;
		int victoryDirection, victoryX, victoryY;
		Piece[] victoryStones;
		boolean gameOver(boolean Red)	{
			victoryColor = !Red;
			int countPieces = 0;
			victoryDirection = 1;
			for (int y = 0; y < board.length; y++)	{
				victoryY = y;
				for (int x = 0; x < board.length; x++)
					if (board[x][y] == null);
					else if (board[x][y].getRed() != Red)
						countPieces++;
				if (countPieces >= 4)	return true;
				countPieces = 0;
			}
			victoryDirection = 2;
			for (int x = 0; x < board.length; x++)	{
				victoryX = x;
				for (int y = 0; y < board[0].length; y++)
					if (board[x][y] == null);
					else if (board[x][y].getRed() != Red)
						countPieces++;
				if (countPieces >= 4) return true;
				countPieces = 0;
			}
			victoryDirection = 3;
			for (int x = 0, y = 0; x < board.length && y < board.length; x++, y++)
				if (board[x][y] == null);
				else if (board[x][y].getRed() != Red)
					countPieces++;
			if (countPieces >= 4) return true;
			countPieces = 0;
			victoryDirection = 4;
			for (int x = 3, y = 0; x > -1 && y < board.length; x--, y++)
				if (board[x][y] == null);
				else if (board[x][y].getRed() != Red)
					countPieces++;
			if (countPieces >= 4) return true;
			return Red ? gameOver(false):false;
		}
		void endGame()	{
			cancelbutton.setEnabled(false);
			victoryStones = new Piece[4];
			for (int i = 0; i < victoryStones.length; i++)
				switch (victoryDirection)	{
					case 1: victoryStones[i] = new Piece(i, victoryY);	break;
					case 2: victoryStones[i] = new Piece(victoryX, i);	break;
					case 3: victoryStones[i] = new Piece(i, i);			break;
					case 4: victoryStones[i] = new Piece(i, victoryStones.length-1-i);		break;
				}
			gameOver = true;
			repaint();
		}
		public void mouseDragged(MouseEvent e)	{}
		public void mouseClicked(MouseEvent e){}
	}
	void refreshBoard()	{	// force-refreshes board
		for (int i = 0; i < board.length; i++)
			for (int a = 0; a < board[i].length; a++)
				board[i][a] = getVisiblePieceAt(i, a);
	}
	Piece getVisiblePieceAt(int x, int y)	{
		int maxsize = 0;
		int index = -1;
		for (int i = 0; i < Pieces.length; i++)
			if (Pieces[i].isAt(x, y))
				if (Pieces[i].getSize() > maxsize)	{
					maxsize = Pieces[i].getSize();
					index = i;
				}
		return index != -1 ? Pieces[index]:null;
	}
	private class BottomPanel extends JPanel	{
		BottomPanel()	{
			setLayout(null);
			setPreferredSize(new Dimension(1500, 100));
			cancelbutton = new CancelButton();
			undobutton = new UndoButton();
			newGameButton = new NewGameButton();
			add(cancelbutton);
			add(undobutton);
			add(newGameButton);
		}
		private class CancelButton extends JButton implements ActionListener	{
			CancelButton()	{
				setBounds(700, 50, 100, 40);
				setBackground(Color.red);
				setText("Cancel");
				setFont(new Font("Arial", Font.BOLD, 20));
				setForeground(Color.black);
				setEnabled(false);
				addActionListener(this);
			}
			public void actionPerformed(ActionEvent e)	{
				toMove.moveTo(inito, true);
				toMove = null;
				inito = null;
				refreshBoard();
				main.repaint();
				setEnabled(false);
			}
		}
		private class UndoButton extends JButton implements ActionListener	{
			UndoButton()	{
				setBounds(580, 50, 100, 40);
				setBackground(Color.magenta);
				setText("Undo");
				setFont(new Font("Arial", Font.BOLD, 20));
				setForeground(Color.black);
				setEnabled(false);
				addActionListener(this);
			}
			public void actionPerformed(ActionEvent e)	{
				toMove = null;
				inito = null;
				lastMousePiece = null;
				turnNum-=2;
				loadBoard(turnNum);
				turnNum++;
				redturn = !redturn;
				refreshBoard();
				main.repaint();
				cancelbutton.setBackground(redturn ? Color.red:Color.green);
			}
		}
		private class NewGameButton extends JButton implements ActionListener	{
			NewGameButton()	{
				setBounds(820, 50, 150, 40);
				setBackground(Color.yellow);
				setText("New Game");
				setFont(new Font("Arial", Font.BOLD, 20));
				setForeground(Color.black);
				setEnabled(false);
				addActionListener(this);
			}
			public void actionPerformed(ActionEvent e)	{
				toMove = null;
				inito = null;
				lastMousePiece = null;
				turnNum = 0;
				loadBoard(turnNum);
				turnNum++;
				redturn = true;
				cancelbutton.setBackground(Color.red);
				setEnabled(false);
				refreshBoard();
				main.repaint();
			}
		}
	}
}