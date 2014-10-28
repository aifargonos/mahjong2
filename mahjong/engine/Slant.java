package mahjong.engine;

import java.util.Comparator;

public interface Slant extends Comparator<Tile> {
	
	
	
	/**
	 * <code>
	 * +---+<br/>
	 * |&nbsp;&nbsp;&nbsp;|\<br/>
	 * |&nbsp;&nbsp;&nbsp;|&nbsp;+<br/>
	 * +---+&nbsp;|<br/>
	 * &nbsp;\&nbsp;&nbsp;&nbsp;\|<br/>
	 * &nbsp;&nbsp;+---+<br/>
	 * </code>
	 * <br/>
	 * 
	 * +---+
	 * |   |\
	 * |   | +
	 * +---+ |
	 *  \   \|
	 *   +---+
	 */
	public static final Slant SE_TO_NW = new Slant() {
		public int compare(Tile t1, Tile t2) {
			return t1.compareTo(t2);
		}
	};
	
	/**
	 * <code>
	 * &nbsp;&nbsp;+---+<br/>
	 * &nbsp;/|&nbsp;&nbsp;&nbsp;|<br/>
	 * +&nbsp;|&nbsp;&nbsp;&nbsp;|<br/>
	 * |&nbsp;+---+<br/>
	 * |/&nbsp;&nbsp;&nbsp;/<br/>
	 * +---+<br/>
	 * </code>
	 * <br/>
	 * 
	 *   +---+
	 *  /|   |
	 * + |   |
	 * | +---+
	 * |/   /
	 * +---+
	 */
	public static final Slant SW_TO_NE = new Slant() {
		public int compare(Tile t1, Tile t2) {
			Coordinates c1 = t1.getPosition();
			Coordinates c2 = t2.getPosition();
			if(c1.z < c2.z) return -1;
			if(c1.z > c2.z) return 1;
			if(c1.y - c1.x < c2.y - c2.x) return -1;
			if(c1.y - c1.x > c2.y - c2.x) return 1;
			if(c1.x < c2.x) return -1;
			if(c1.x > c2.x) return 1;
			return 0;
		}
	};
	
	/**
	 * <code>
	 * &nbsp;&nbsp;+---+<br/>
	 * &nbsp;/&nbsp;&nbsp;&nbsp;/|<br/>
	 * +---+&nbsp;|<br/>
	 * |&nbsp;&nbsp;&nbsp;|&nbsp;+<br/>
	 * |&nbsp;&nbsp;&nbsp;|/<br/>
	 * +---+<br/>
	 * </code>
	 * <br/>
	 * 
	 *   +---+
	 *  /   /|
	 * +---+ |
	 * |   | +
	 * |   |/
	 * +---+
	 */
	public static final Slant NE_TO_SW = new Slant() {
		public int compare(Tile t1, Tile t2) {
			Coordinates c1 = t1.getPosition();
			Coordinates c2 = t2.getPosition();
			if(c1.z < c2.z) return -1;
			if(c1.z > c2.z) return 1;
			if(c1.x - c1.y < c2.x - c2.y) return -1;
			if(c1.x - c1.y > c2.x - c2.y) return 1;
			if(c1.x < c2.x) return -1;
			if(c1.x > c2.x) return 1;
			return 0;
		}
	};
	
	/**
	 * <code>
	 * +---+<br/>
	 * |\&nbsp;&nbsp;&nbsp;\<br/>
	 * |&nbsp;+---+<br/>
	 * +&nbsp;|&nbsp;&nbsp;&nbsp;|<br/>
	 * &nbsp;\|&nbsp;&nbsp;&nbsp;|<br/>
	 * &nbsp;&nbsp;+---+<br/>
	 * </code>
	 * <br/>
	 * 
	 * +---+
	 * |\   \
	 * | +---+
	 * + |   |
	 *  \|   |
	 *   +---+
	 */
	public static final Slant NW_TO_SE = new Slant() {
		public int compare(Tile t1, Tile t2) {
			Coordinates c1 = t1.getPosition();
			Coordinates c2 = t2.getPosition();
			if(c1.z < c2.z) return -1;
			if(c1.z > c2.z) return 1;
			if(- c1.x - c1.y < - c2.x - c2.y) return -1;
			if(- c1.x - c1.y > - c2.x - c2.y) return 1;
			if(c1.x < c2.x) return -1;
			if(c1.x > c2.x) return 1;
			return 0;
		}
	};
	
	
	
}
