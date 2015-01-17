import java.awt.Color;

public class Piece	{
	int x, y, size;
	boolean red;
	Piece(boolean red, int size, int x, int y)	{
		this.x = x;
		this.y = y;
		this.red = red;
		this.size = size;
	}
	Piece(int x, int y)	{
		this.x = x;
		this.y = y;
	}
	Piece()	{
		x = -1;
		y = -1;
		red = true;
		size = 0;
	}
	Piece(Piece p)	{
		x = p.getX();
		y = p.getY();
		red = p.getRed();
		size = p.getSize();
	}
	public int getSize()	{
		return size;
	}
	public void setX(int x)	{
		this.x = x;
	}
	public void setY(int y)	{
		this.y = y;
	}
	public void moveTo(Piece p)	{
		x = p.getX();
		y = p.getY();
		//red = p.getRed();
	}
	public void moveTo(Piece p, boolean setRed)	{
		x = p.getX();
		y = p.getY();
		if (setRed)	red = p.getRed();
	}
	public void setRed(boolean red)	{
		this.red = red;
	}
	public int getX()	{
		return x;
	}
	public int getY()	{
		return y;
	}
	public Color getColor()	{
		return red ? Color.red:Color.green;
	}
	public boolean getRed()	{
		return red;
	}
	public boolean hasValues(boolean red, int x, int y)	{
		return this.red==red && this.x==x && this.y==y;
	}
	public boolean hasValues(Piece p)	{
		return red==p.getRed() && p.getX()==x && p.getY()==y;
	}
	public boolean isAt(int x, int y)	{
		return this.x==x && this.y==y;
	}
	public boolean isAt(Piece p)	{
		return x==p.getX() && y==p.getY();
	}
	public String toString()	{
		return "Color: " + (red ? "Red":"Green") + "\tSize: " + size + "\t\tX: " + x + "\tY: " + y;
	}
}