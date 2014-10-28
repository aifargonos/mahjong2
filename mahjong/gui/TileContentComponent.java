package mahjong.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;

import mahjong.engine.TileContent;

public class TileContentComponent extends JComponent {
	
	private static final long serialVersionUID = 8306956243393251331L;
	
	
	
	private TileContent tileContent;
	
	
	
	public TileContentComponent(TileContent tc, Dimension size) {
		this.tileContent = tc;
		setSize(size);
	}
	
	
	
	@Override
	protected void paintBorder(Graphics g) {
		
		Dimension size = getSize();
		
		g.drawRect(0, 0, size.width - 1, size.height - 1);
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		Dimension size = getSize();
		
		Color oldColor = g.getColor();
		g.setColor(getBackground());
		g.fillRect(0, 0, size.width, size.height);
		g.setColor(oldColor);
		
		tileContent.draw(g, new Rectangle(1, 1, size.width - 2, size.height - 2));
		
	}
	
	
	
	@Override
	public Dimension getPreferredSize() {
		return super.getSize();
	}
	
	@Override
	public Dimension getMaximumSize() {
		return super.getSize();
	}
	
	@Override
	public Dimension getMinimumSize() {
		return super.getSize();
	}
	
	
	
	public TileContent getTileContent() {
		return tileContent;
	}
	
	public void setTileContent(TileContent tileContent) {
		this.tileContent = tileContent;
	}
	
	
	
	@Override
	public String toString() {
		return super.toString() + " of " + tileContent;
	}
	
	
	
}
