package mahjong.gui;

import java.awt.Color;
import java.awt.Dimension;
//import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;

import javax.swing.JComponent;

import mahjong.engine.Slant;
import mahjong.engine.Tile;

/**
 * TODO
 * 
 * 
 * 
 * @author aifargonos
 */
public class TileComponent extends JComponent {
	
	private static final long serialVersionUID = 1931505564776421063L;
	
	
	
	public static Polygon getOutline(Dimension size) {
		int depth = (int)Math.round(size.width/(Config.tileWidthToDepthRatio + 1));
		
		Polygon polygon = new Polygon();
		
		if(Config.slant == Slant.SE_TO_NW || Config.slant == Slant.NW_TO_SE) {
			/* 
			 * +---+
			 * |    \
			 * |     +
			 * +     |
			 *  \    |
			 *   +---+
			 */
			polygon.addPoint(0, 0);
			polygon.addPoint(size.width - 1 - depth, 0);
			polygon.addPoint(size.width - 1, depth);
			polygon.addPoint(size.width - 1, size.height - 1);
			polygon.addPoint(depth, size.height - 1);
			polygon.addPoint(0, size.height - 1 - depth);
			polygon.addPoint(0, 0);
		} else {
			/* 
			 *   +---+
			 *  /    |
			 * +     |
			 * |     +
			 * |    /
			 * +---+
			 */
			polygon.addPoint(size.width - 1, 0);
			polygon.addPoint(depth, 0);
			polygon.addPoint(0, depth);
			polygon.addPoint(0, size.height - 1);
			polygon.addPoint(size.width - 1 - depth, size.height - 1);
			polygon.addPoint(size.width - 1, size.height - 1 - depth);
			polygon.addPoint(size.width - 1, 0);
		}
		
		return polygon;
	}
	
	public static Polygon getRightSide(Dimension size) {
		int depth = (int)Math.round(size.width/(Config.tileWidthToDepthRatio + 1));
		
		Polygon polygon = new Polygon();
		
		if(Config.slant == Slant.SE_TO_NW) {
			/* 
			 *     +
			 *     |\
			 *     | +
			 *     + |
			 *      \|
			 *       +
			 */
			polygon.addPoint(size.width - 1 - depth, 0);
			polygon.addPoint(size.width - 1, depth);
			polygon.addPoint(size.width - 1, size.height - 1);
			polygon.addPoint(size.width - 1 - depth, size.height - 1 - depth);
			polygon.addPoint(size.width - 1 - depth, 0);
		} else if(Config.slant == Slant.SW_TO_NE) {
			/* 
			 *   +
			 *  /|
			 * + |
			 * | +
			 * |/
			 * +
			 */
			polygon.addPoint(depth + 1, 0);
			polygon.addPoint(0, depth);
			polygon.addPoint(0, size.height);
			polygon.addPoint(depth + 1, size.height - 1 - depth);
			polygon.addPoint(depth + 1, 0);
		} else if(Config.slant == Slant.NW_TO_SE) {
			/* 
			 * +
			 * |\
			 * | +
			 * + |
			 *  \|
			 *   +
			 */
			polygon.addPoint(0, 0);
			polygon.addPoint(depth + 1, depth);
			polygon.addPoint(depth + 1, size.height - 1);
			polygon.addPoint(0, size.height - 1 - depth);
			polygon.addPoint(0, 0);
		} else if(Config.slant == Slant.NE_TO_SW) {
			/* 
			 *       +
			 *      /|
			 *     + |
			 *     | +
			 *     |/
			 *     +
			 */
			polygon.addPoint(size.width - 1, 0);
			polygon.addPoint(size.width - 1 - depth, depth);
			polygon.addPoint(size.width - 1 - depth, size.height - 1);
			polygon.addPoint(size.width - 1, size.height - 1 - depth);
			polygon.addPoint(size.width - 1, 0);
		}
		
		return polygon;
	}
	
	public static Polygon getBottomSide(Dimension size) {
		int depth = (int)Math.round(size.width/(Config.tileWidthToDepthRatio + 1));
		
		Polygon polygon = new Polygon();
		
		if(Config.slant == Slant.SE_TO_NW) {
			/* 
			 * 
			 * 
			 * 
			 * +---+
			 *  \   \
			 *   +---+
			 */
			polygon.addPoint(0, size.height - 1 - depth);
			polygon.addPoint(size.width - 1 - depth, size.height - 1 - depth);
			polygon.addPoint(size.width - 1, size.height - 1);
			polygon.addPoint(depth, size.height - 1);
			polygon.addPoint(0, size.height - 1 - depth);
		} else if(Config.slant == Slant.SW_TO_NE) {
			/* 
			 * 
			 * 
			 * 
			 *   +---+
			 *  /   /
			 * +---+
			 */
			polygon.addPoint(size.width - 1, size.height - 1 - depth);
			polygon.addPoint(depth, size.height - 1 - depth);
			polygon.addPoint(0, size.height - 1);
			polygon.addPoint(size.width - 1 - depth, size.height - 1);
			polygon.addPoint(size.width - 1, size.height - 1 - depth);
		} else if(Config.slant == Slant.NW_TO_SE) {
			/* 
			 * +---+
			 *  \   \
			 *   +---+
			 * 
			 * 
			 * 
			 */
			polygon.addPoint(0, 0);
			polygon.addPoint(size.width - 1 - depth, 0);
			polygon.addPoint(size.width - 1, depth + 1);
			polygon.addPoint(depth, depth + 1);
			polygon.addPoint(0, 0);
		} else if(Config.slant == Slant.NE_TO_SW) {
			/* 
			 *   +---+
			 *  /   /
			 * +---+
			 * 
			 * 
			 * 
			 */
			polygon.addPoint(size.width - 1, 0);
			polygon.addPoint(depth, 0);
			polygon.addPoint(0, depth + 1);
			polygon.addPoint(size.width - 1 - depth, depth + 1);
			polygon.addPoint(size.width - 1, 0);
		}
		
		return polygon;
	}
	
	
	
	private boolean highlighted;
	private boolean selected;
	
	private Tile tile;
	
	
	
	public TileComponent(Tile tile) {
		this.tile = tile;
		this.highlighted = false;
		this.selected = false;
		config();
	}
	
	public TileComponent(Tile tile, Rectangle bounds) {
		this(tile);
		setBounds(bounds);
	}
	
	public void config() {
		// TODO ???
	}
	
	
	
	@Override
	protected void paintBorder(Graphics g) {
		
		// rozmery
		Dimension s = getSize();
		
		// polygony
		Polygon rightSide = getRightSide(s);
		Polygon bottomSide = getBottomSide(s);
		Polygon outline = getOutline(s);
		
		// farbenie
		Color oldColor = g.getColor();
		Color color = getBackground();
		color = color.darker();
		g.setColor(color);
		g.fillPolygon(bottomSide);
		color = color.darker();
		g.setColor(color);
		g.fillPolygon(rightSide);
		
		// obrysy
		g.setColor(getForeground());
		g.drawPolygon(outline);
		
		g.setColor(oldColor);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		// rozmery
		Dimension size = getSize();
		int depth = (int)Math.round(size.width/(Config.tileWidthToDepthRatio + 1));
		size.width -= depth + 1;
		size.height -= depth + 1;
		Rectangle bounds = new Rectangle(size);
		if(Config.slant == Slant.SE_TO_NW) {
			bounds.x = 1;
			bounds.y = 1;
		} else if(Config.slant == Slant.SW_TO_NE) {
			bounds.x = depth;
			bounds.y = 1;
		} else if(Config.slant == Slant.NW_TO_SE) {
			bounds.x = depth;
			bounds.y = depth;
		} else if(Config.slant == Slant.NE_TO_SW) {
			bounds.x = 1;
			bounds.y = depth;
		}
		
		// farbenie
		Color oldColor = g.getColor();
		if(isSelected()) {
			g.setColor(Config.selectedColor);
		} else if(isHighlighted()) {
			g.setColor(Config.highlightedColor);
		} else {
			g.setColor(getBackground());
		}
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		
		g.setColor(oldColor);
		
		// content
//		Font oldFont = g.getFont();
//		g.setFont(new Font("AR PL UMing CN", Font.PLAIN, 12));// TODO ???
//		g.setFont(new Font("Serif.plain", Font.PLAIN, 12));// TODO magic const !!!
		if(tile.getContent() != null) {
			tile.getContent().draw(g, bounds);
		}
//		g.setFont(oldFont);
		
	}
	
	
	
	public boolean isHighlighted() {
		return highlighted;
	}
	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
		repaint();
	}
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
		repaint();
	}
	
	public Tile getTile() {
		return tile;
	}
	public void setTile(Tile tile) {
		this.tile = tile;
	}
	
	
	
}
