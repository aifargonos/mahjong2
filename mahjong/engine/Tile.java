package mahjong.engine;

/**
 * TODO
 * 
 * ak chcem spravit inteligentne Tile, musi ti vediet povedat .:
 * 	obsah
 * 	poziciu
 * 		si bude checkovat s field a pri zmene sa bude rovno premiestnovat
 * 			musi mat referenciu na field, do ktoreho patri
 * 	susedov
 * 	ci nieco blokuje
 * 	kolkymi je blokovana
 * 		musi mat mnozinu blokujucich, ktora sa musi menit s
 * 			poziciou
 * 			obsahom
 * 
 * @author aifargonos
 */
public class Tile implements Comparable<Tile> {
	
	
	
	private Coordinates position;
	private TileContent content;
	
	
	
	public Tile() {
		this(new Coordinates());
	}
	
	public Tile(Coordinates position) {
		this.position = new Coordinates(position);
		this.content = null;
	}
	
	
	
	public TileContent getContent() {
		return content;
	}
	public void setContent(TileContent content) {
		this.content = content;
	}
	
	public Coordinates getPosition() {
		return new Coordinates(position);
	}
	public Coordinates getPosition(Coordinates c) {
		c.set(position);
		return c;
	}
	public void setPosition(Coordinates position) {
		this.position = new Coordinates(position);
	}
	
	
	
	public int compareTo(Tile tile) {
		return position.compareTo(tile.getPosition());
	}
	
	
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + position;
	}
	
	
	
}
