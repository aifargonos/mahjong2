package mahjong.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import mahjong.engine.Coordinates;
import mahjong.engine.Field;
import mahjong.engine.Slant;
import mahjong.engine.Tile;

/**
 * TODO
 * 	centre() rozdelit na mensie funkcie ...
 * 		findMost() ...
 * 	... a potom si pamatat most a normalne layOut-ovat iba podla toho
 * 
 * @author aifargonos
 */
public class FieldComponent extends JComponent {
	
	private static final long serialVersionUID = 6893031700926970033L;
	
	
	
	public static Rectangle coordinatesToBounds(Coordinates c, Rectangle ret) {
		
		Point zero = Config.zero;
		int width = Config.getTileWidth();
		int height = Config.getTileHeight();
		int depth = Config.getTileDepth();
		
		int xDepthCorrection = (Config.slant == Slant.SE_TO_NW || Config.slant == Slant.NE_TO_SW) ? -(c.z + 1) : c.z;
		int yDepthCorrection = (Config.slant == Slant.SE_TO_NW || Config.slant == Slant.SW_TO_NE) ? -(c.z + 1) : c.z;
		
		ret.x = c.x*width/2 + zero.x + xDepthCorrection * depth;
		ret.y = c.y*height/2 + zero.y + yDepthCorrection * depth;
		ret.width = width + depth;
		ret.height = height + depth;
		
		return ret;
	}
	public static Rectangle coordinatesToBounds(Coordinates c) {
		return coordinatesToBounds(c, new Rectangle());
	}
	
	public static Coordinates pointToCoordinates(Point p, int z) {
		Coordinates ret = new Coordinates(0, 0, z);
		
		Point zero = Config.zero;
		int width = Config.getTileWidth();
		int height = Config.getTileHeight();
		int depth = Config.getTileDepth();
		
		int xDepthCorrection = (Config.slant == Slant.SE_TO_NW || Config.slant == Slant.NE_TO_SW) ? -(z + 1) : z;
		int yDepthCorrection = (Config.slant == Slant.SE_TO_NW || Config.slant == Slant.SW_TO_NE) ? -(z + 1) : z;
		
		ret.x = (p.x - zero.x - xDepthCorrection * depth) * 2/width;
		ret.y = (p.y - zero.y - yDepthCorrection * depth) * 2/height;
		
		return ret;
	}
	
	
	
	private MouseListener mlForTiles;
	
	private Field field;
	
	
	
	public FieldComponent(Field field) {
		this(field, null);
	}
	
	public FieldComponent(Field field, MouseListener mlForTiles) {
		this.mlForTiles = mlForTiles;
		this.field = field;
		
		addComponents();
		
	}
	
	
	
	private void addComponents() {
		
		Tile[] tiles = field.getTiles(Config.slant);
		
		for(int i = tiles.length - 1; i >= 0 ; i--) {
			TileComponent tc = new TileComponent(tiles[i], coordinatesToBounds(tiles[i].getPosition()));
			if(mlForTiles != null) {
				tc.addMouseListener(mlForTiles);
			}
			add(tc);
		}
		
	}
	
	
	
	public void refresh() {
		removeAll();
		addComponents();
	}
	
	
	
	/**
	 * TODO
	 * 	este to robi kraviny ...
	 * 	padding
	 * 	premenovat na fit(ToComponent)
	 */
	public void centre() {
		
		if(field.isEmpty()) return;
		
		// most ... tiles
		
		Component[] components = getComponents();
		
		Coordinates c = new Coordinates();
		Rectangle bounds = new Rectangle();
		
		Coordinates mostLeftCoordinates = null;
		Coordinates mostTopCoordinates = null;
		Coordinates mostRightCoordinates = null;
		Coordinates mostBottomCoordinates = null;
		int mostLeft = 0;
		int mostTop = 0;
		int mostRight = 0;
		int mostBottom = 0;
		for(Component component : components) {
			if(component instanceof TileComponent) {
				mostLeftCoordinates = ((TileComponent)component).getTile().getPosition();
				mostTopCoordinates = ((TileComponent)component).getTile().getPosition();
				mostRightCoordinates = ((TileComponent)component).getTile().getPosition();
				mostBottomCoordinates = ((TileComponent)component).getTile().getPosition();
				component.getBounds(bounds);
				mostLeft = bounds.x;
				mostTop = bounds.y;
				mostRight = bounds.x + bounds.width;
				mostBottom = bounds.y + bounds.height;
				break;
			}
		}
		
		for(Component component : components) {
			if(component instanceof TileComponent) {
				component.getBounds(bounds);
				((TileComponent)component).getTile().getPosition(c);
				if(bounds.x < mostLeft) {
					mostLeft = bounds.x;
					mostLeftCoordinates.set(c);
				}
				if(bounds.y < mostTop) {
					mostTop = bounds.y;
					mostTopCoordinates.set(c);
				}
				if(bounds.x + bounds.width > mostRight) {
					mostRight = bounds.x + bounds.width;
					mostRightCoordinates.set(c);
				}
				if(bounds.y + bounds.height > mostBottom) {
					mostBottom = bounds.y + bounds.height;
					mostBottomCoordinates.set(c);
				}
			}
		}
		
		// new size
		
		Dimension componentSize = getSize();
		Dimension tilesSize = new Dimension(mostRight - mostLeft, mostBottom - mostTop);
		double ratio = Math.min(componentSize.width / (double)tilesSize.width, componentSize.height / (double)tilesSize.height);
		
		Config.tileWidth *= ratio;
		
		// most ... once again
		
		mostLeft = coordinatesToBounds(mostLeftCoordinates, bounds).x;
		mostTop = coordinatesToBounds(mostTopCoordinates, bounds).y;
		mostRight = coordinatesToBounds(mostRightCoordinates, bounds).x;
		mostRight += bounds.width;
		mostBottom = coordinatesToBounds(mostBottomCoordinates, bounds).y;
		mostBottom += bounds.height;
		
		tilesSize = new Dimension(mostRight - mostLeft, mostBottom - mostTop);
		
		// new zero
		
		Config.zero.x = (int)Math.round(Config.zero.x - mostLeft + (componentSize.width - tilesSize.width)/2.0);
		Config.zero.y = (int)Math.round(Config.zero.y - mostTop + (componentSize.height - tilesSize.height)/2.0);
		
		// finito
		
		revalidate();
		
	}
	
	
	
	@Override
	public void doLayout() {
		super.doLayout();
		
		for(Component component : getComponents()) {
			if(component instanceof TileComponent) {
				TileComponent tc = (TileComponent)component;
				tc.setBounds(coordinatesToBounds(tc.getTile().getPosition()));
			}
		}
		
	}
	
	
	
}
