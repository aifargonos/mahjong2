package mahjong.engine;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


/**
 * TODO
 * 
 * zmenit random tak, aby to nerozhadzovalo az tak pravidelne ...
 * 
 * @author aifargonos
 */
public class Field {
	
	
	
	public static final String XML_ENCODING = "UTF-8";
	public static final String XML_VERSION = "1.0";
	public static final String XML_ROOT_ELEMENT = "field";
	
	
	
	private TreeMap<Coordinates, Tile> field;
	
	
	
	public Field() {
		this.field = new TreeMap<Coordinates, Tile>();
	}
	
	
	
	public boolean isEmpty() {
		return field.isEmpty();
	}
	
	public boolean isFree(Coordinates c) {
		c = new Coordinates(c);
		if(field.containsKey(c)) return false;
		c.x++;
		if(field.containsKey(c)) return false;
		c.y++;
		if(field.containsKey(c)) return false;
		c.x--;
		if(field.containsKey(c)) return false;
		c.y--;
		return true;
	}
	
	public boolean put(Tile tile) {
		Coordinates c = tile.getPosition();
		if(!isFree(c)) return false;
		
		field.put(c, tile);
		field.put(c.getTranslatedCopy(1, 0, 0), tile);
		field.put(c.getTranslatedCopy(1, 1, 0), tile);
		field.put(c.getTranslatedCopy(0, 1, 0), tile);
		
		return true;
	}
	
	public Tile get(Coordinates c) {
		return field.get(c);
	}
	
	public boolean remove(Tile tile) {
		Coordinates c = tile.getPosition();
		if(!field.containsKey(c)) return false;
		
		field.remove(c);
		c.x++;
		field.remove(c);
		c.y++;
		field.remove(c);
		c.x--;
		field.remove(c);
		
		return true;
	}
	
	public boolean remove(Coordinates c) {
		if(!field.containsKey(c)) return false;
		return remove(field.get(c));
	}
	
	public void clear() {
		field.clear();
	}
	
	public Tile[] getTiles() {
		return getTiles(Slant.SE_TO_NW);
	}
	
	public Tile[] getTiles(Slant comparator) {
		
		TreeSet<Tile> tiles = new TreeSet<Tile>(comparator);
		for(Tile tile : field.values()) {
			tiles.add(tile);
		}
		
		Tile[] ret = new Tile[tiles.size()];
		return tiles.toArray(ret);
	}
	
	
	
	public void save(OutputStream xmlOut) throws XMLStreamException, JAXBException {
        
		XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(xmlOut, XML_ENCODING);
        
        writer.writeStartDocument(XML_ENCODING, XML_VERSION);
        writer.writeCharacters("\n");
        writer.writeStartElement(XML_ROOT_ELEMENT);
        writer.writeCharacters("\n");
        
		JAXBContext context = JAXBContext.newInstance(Coordinates.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", true);// nefacha ...
		marshaller.setProperty("jaxb.fragment", true);
		
		for(Tile tile : field.values()) {
			marshaller.marshal(tile.getPosition(), writer);
            writer.writeCharacters("\n");
		}
		
		writer.writeEndElement();
        writer.writeCharacters("\n");
		writer.writeEndDocument();
		writer.close();
		
	}
	
	public void load(InputStream xmlIn) throws XMLStreamException, JAXBException {
		
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(xmlIn, XML_ENCODING);
		
		JAXBContext context = JAXBContext.newInstance(Coordinates.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		boolean inRoot = false;
		while(reader.hasNext()) {
			
			if(reader.isStartElement()) {
				if(reader.getLocalName().equals(XML_ROOT_ELEMENT)) {
					if(inRoot) {
						throw new XMLStreamException("root element in root element", reader.getLocation());
					} else {
						inRoot = true;
					}
				} else {
					if(inRoot) {
						
						Object o = unmarshaller.unmarshal(reader);
						if(o instanceof Coordinates) {
							put(new Tile((Coordinates)o));
						}
						
					} else {
						throw new XMLStreamException("something out of root element", reader.getLocation());
					}
				}
			} else if(reader.isEndElement()) {
				if(reader.getLocalName().equals(XML_ROOT_ELEMENT)) {
					if(inRoot) {
						inRoot = false;
					} else {
						throw new XMLStreamException("end of root element out of root element", reader.getLocation());
					}
				}
			}
			
			reader.next();
		}
		
		reader.close();
		
	}
	
	
	
	private Tile getNeighbour(Coordinates c, Coordinates d) {
		return field.get(c.translate(d));
	}
	
	private Tile getNeighbour(Tile t, Coordinates d) {
		return getNeighbour(t.getPosition(), d);
	}
	
	
	
	/**
	 * POZOR !!! iba vertikalne blokovanie !!! neoveruje to horizontalne blokovanie !!!
	 * TODO !!! aj tam je tile, co uz ma content, tak ho to neblokuje !!!
	 * 
	 * @param tile
	 * @return
	 */
	private boolean blocksVertically(Tile tile) {
		/* 
		 * ak je pod kockou nieco bez obsahu
		 */
		Tile neighbour = getNeighbour(tile, Coordinates.UNE);
		if(neighbour != null && neighbour.getContent() == null) return true;
		neighbour = getNeighbour(tile, Coordinates.UNW);
		if(neighbour != null && neighbour.getContent() == null) return true;
		neighbour = getNeighbour(tile, Coordinates.USE);
		if(neighbour != null && neighbour.getContent() == null) return true;
		neighbour = getNeighbour(tile, Coordinates.USW);
		if(neighbour != null && neighbour.getContent() == null) return true;
		return false;
	}
	
	private boolean isContentToLeft(Set<Tile> tiles) {
		Set<Tile> newTiles = new HashSet<Tile>();
		Set<Tile> tmp = null;
		for(Tile tile : tiles) {
			Tile neighbour = getNeighbour(tile, Coordinates.WN);
			if(neighbour != null) newTiles.add(neighbour);
			neighbour = getNeighbour(tile, Coordinates.WS);
			if(neighbour != null) newTiles.add(neighbour);
		}
		tiles.clear();
		while(!newTiles.isEmpty()) {
			for(Tile tile : newTiles) {
				if(tile.getContent() != null) return true;
				Tile neighbour = getNeighbour(tile, Coordinates.WN);
				if(neighbour != null) tiles.add(neighbour);
				neighbour = getNeighbour(tile, Coordinates.WS);
				if(neighbour != null) tiles.add(neighbour);
			}
			newTiles.clear();
			tmp = newTiles;
			newTiles = tiles;
			tiles = tmp;
		}
		return false;
	}
	private boolean blocksLeft(Tile tile) {
		/*
		 * ak je od laveho suseda, ktory nema obsah, vlavo nieco s obsahom
		 */
		Set<Tile> tiles = new HashSet<Tile>();
		Tile neighbour = getNeighbour(tile, Coordinates.WN);
		if(neighbour != null && neighbour.getContent() == null) tiles.add(neighbour);
		neighbour = getNeighbour(tile, Coordinates.WS);
		if(neighbour != null && neighbour.getContent() == null) tiles.add(neighbour);
		return isContentToLeft(tiles);
	}
	
	private boolean isContentToRight(Set<Tile> tiles) {
		Set<Tile> newTiles = new HashSet<Tile>();
		Set<Tile> tmp = null;
		for(Tile tile : tiles) {
			Tile neighbour = getNeighbour(tile, Coordinates.EN);
			if(neighbour != null) newTiles.add(neighbour);
			neighbour = getNeighbour(tile, Coordinates.ES);
			if(neighbour != null) newTiles.add(neighbour);
		}
		tiles.clear();
		while(!newTiles.isEmpty()) {
			for(Tile tile : newTiles) {
				if(tile.getContent() != null) return true;
				Tile neighbour = getNeighbour(tile, Coordinates.EN);
				if(neighbour != null) tiles.add(neighbour);
				neighbour = getNeighbour(tile, Coordinates.ES);
				if(neighbour != null) tiles.add(neighbour);
			}
			newTiles.clear();
			tmp = newTiles;
			newTiles = tiles;
			tiles = tmp;
		}
		return false;
	}
	private boolean blocksRight(Tile tile) {
		/*
		 * ak je od praveho suseda, ktory nema obsah, vpravo nieco s obsahom
		 */
		Set<Tile> tiles = new HashSet<Tile>();
		Tile neighbour = getNeighbour(tile, Coordinates.EN);
		if(neighbour != null && neighbour.getContent() == null) tiles.add(neighbour);
		neighbour = getNeighbour(tile, Coordinates.ES);
		if(neighbour != null && neighbour.getContent() == null) tiles.add(neighbour);
		return isContentToRight(tiles);
	}
	
	
	
	/**
	 * TODO nie vzdy treba ist aj vertikalne, ale keby som to spravil iba horizontalne,
	 *      malo by to vacsiu reziu
	 * 
	 * @param tile
	 * @param blockedBySet
	 */
	private void addToBlockedBySetToLeft(Tile tile, Set<Tile> blockedBySet) {
		Tile neighbour = getNeighbour(tile, Coordinates.WN);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
//			addToBlockedBySetToLeft(neighbour, blockedBySet);
			addToBlockedBySet(neighbour, blockedBySet);
		}
		neighbour = getNeighbour(tile, Coordinates.WS);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
//			addToBlockedBySetToLeft(neighbour, blockedBySet);
			addToBlockedBySet(neighbour, blockedBySet);
		}
	}
	/**
	 * TODO nie vzdy treba ist aj vertikalne, ale keby som to spravil iba horizontalne,
	 *      malo by to vacsiu reziu
	 * 
	 * @param tile
	 * @param blockedBySet
	 */
	private void addToBlockedBySetToRight(Tile tile, Set<Tile> blockedBySet) {
		Tile neighbour = getNeighbour(tile, Coordinates.EN);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
//			addToBlockedBySetToRight(neighbour, blockedBySet);
			addToBlockedBySet(neighbour, blockedBySet);
		}
		neighbour = getNeighbour(tile, Coordinates.ES);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
//			addToBlockedBySetToRight(neighbour, blockedBySet);
			addToBlockedBySet(neighbour, blockedBySet);
		}
	}
	private void addToBlockedBySet(Tile tile, Set<Tile> blockedBySet) {
		// horizontally
		HashSet<Tile> tiles = new HashSet<Tile>();
		tiles.add(tile);
		if(isContentToLeft(tiles)) {
			addToBlockedBySetToRight(tile, blockedBySet);
		}
		tiles.clear();
		tiles.add(tile);
		if(isContentToRight(tiles)) {
			addToBlockedBySetToLeft(tile, blockedBySet);
		}
		// vertically
		Tile neighbour = getNeighbour(tile, Coordinates.ANE);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
			addToBlockedBySet(neighbour, blockedBySet);
		}
		neighbour = getNeighbour(tile, Coordinates.ANW);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
			addToBlockedBySet(neighbour, blockedBySet);
		}
		neighbour = getNeighbour(tile, Coordinates.ASE);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
			addToBlockedBySet(neighbour, blockedBySet);
		}
		neighbour = getNeighbour(tile, Coordinates.ASW);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
			addToBlockedBySet(neighbour, blockedBySet);
		}
	}
	private int getBlockedByCount(Tile tile) {
		HashSet<Tile> blockedBySet = new HashSet<Tile>();
		addToBlockedBySet(tile, blockedBySet);
		return blockedBySet.size();
	}
	
	
	
	private Tile getRandomTile(Set<Tile> tiles) {
		
		HashMap<Tile, Integer> blockedByResults = new HashMap<Tile, Integer>(tiles.size());
		for(Tile tile : tiles) {
			blockedByResults.put(tile, getBlockedByCount(tile));
		}
		
		Vector<Tile> weightedTiles = new Vector<Tile>();
		for(Tile tile : blockedByResults.keySet()) {
			for(int i = 0; i < blockedByResults.get(tile) + 1; i++) {
				weightedTiles.add(tile);
			}
		}
		
		return weightedTiles.get((int)(Math.random() * weightedTiles.size()));
	}
	
	
	
	public void generate(List<TileContent> tileContents) {
		
		
		// random associated pairs
		
		int oldEnd = tileContents.size();
		
		while(oldEnd > 0) {
			
			// first
			TileContent first = tileContents.remove((int)(Math.random() * oldEnd));
			oldEnd--;
			if(oldEnd == 0) break;
			
			// second
			int start = (int)(Math.random() * oldEnd);
			TileContent second = null;
			int i = start;
			do {
				if(tileContents.get(i).isAssociatedWith(first)) {
					second = tileContents.remove(i);
					oldEnd--;
					break;
				}
				i++;
				if(i >= oldEnd) {
					i = 0;
				}
			} while(i != start);
			
			// pair
			if(second != null) {
				tileContents.add(first);
				tileContents.add(second);
			}
			
		}
		
		
		/* 
		 * a teraz vlastne generovani .:
		 * budem zaradom obsadzovat tie tiles-y, co nic neblokuju a nastavovat blokovanie ... 
		 * 
		 * spravim zoznam tiles-ov, ktore nic neblokuju
		 * loop az kym nevyprazdnim tileContents
		 * spomedzi tych, co su v zozname a su najviac blokovane, vyberem nahodny.
		 * ten dostane prvy content z associated paru a vyberem ho zo zoznamu.
		 * zo zoznamu vyberem take tiles-y, ktore kvoli tomuto priradeniu nieco zacali blokovat.
		 * spomedzi tych, co su v zozname a su najviac blokovane, vyberem nahodny.
		 * ten dostane druhy content z associated paru a vyberem ho zo zoznamu.
		 * zo zoznamu vyberem take tiles-y, ktore kvoli tomuto priradeniu nieco zacali blokovat.
		 *   do zoznamu pridam take tiles-y, ktore vdaka obom priradeniam uz nic neblokuju.
		 */
		
		// clear
		
		for(Tile tile : field.values()) {
			tile.setContent(null);
		}
		
		// nonBlockingTiles
		
		HashSet<Tile> nonBlockingTiles = new HashSet<Tile>(field.values());
		for(Iterator<Tile> it = nonBlockingTiles.iterator(); it.hasNext();) {
			Tile tile = it.next();
			if(tile.getContent() != null || blocksVertically(tile) || blocksLeft(tile) || blocksRight(tile)) {
				it.remove();
				continue;
			}
		}
		
		// generate
		
//		HashMap<Tile, Integer> blockedByResults = new HashMap<Tile, Integer>(nonBlockingTiles.size());
//		Vector<Tile> maxTiles = new Vector<Tile>();
		HashSet<Tile> tiles = new HashSet<Tile>();
		
		Iterator<TileContent> tileContentsIterator = tileContents.iterator();
		while(tileContentsIterator.hasNext() && !nonBlockingTiles.isEmpty()) {
			
//			System.out.println("nonBlockingTiles: " + nonBlockingTiles);// TODO DEBUG
//			if(nonBlockingTiles.isEmpty()) {
//				System.err.println("!!! pruser .: nonBlockingTiles.isEmpty()");
//				return;// TODO dat sem exception
//			}
//			
			// counting BlockedByCount
			
//			blockedByResults.clear();
//			for(Tile tile : nonBlockingTiles) {
//				blockedByResults.put(tile, getBlockedByCount(tile));
//			}
//			System.out.println("blockedByResults: " + blockedByResults);// TODO DEBUG
//			Integer[] results = new Integer[blockedByResults.size()];// TODO DEBUG
//			results = blockedByResults.values().toArray(results);// TODO DEBUG
//			Arrays.sort(results);// TODO DEBUG
//			System.out.println("results: " + Arrays.toString(results));// TODO DEBUG
			
			// finding most blocked tiles
			
//			int max = 0;
//			for(int i : blockedByResults.values()) {
//				if(i > max) max = i;
//			}
//			System.out.println("max: " + max);// TODO DEBUG
//			maxTiles.clear();
//			for(Tile tile : blockedByResults.keySet()) {
//				if(blockedByResults.get(tile) == max) maxTiles.add(tile);
//			}
//			System.out.println("maxTiles: " + maxTiles);// TODO DEBUG
			
			// random
			
//			Tile first = maxTiles.get((int)(Math.random() * maxTiles.size()));
			Tile first = getRandomTile(nonBlockingTiles);
//			System.out.println("first: " + first);// TODO DEBUG
			
			// setting first content
			
			first.setContent(tileContentsIterator.next());
			if(!tileContentsIterator.hasNext()) {
				System.err.println("!!! pruser .: v tileContents nebol parny pocet .: chyba niekde vyzsie v algoitme");
			}
			
			// blocking tiles
			
			nonBlockingTiles.remove(first);
			
			tiles.clear();
			Tile neighbour = getNeighbour(first, Coordinates.WN);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToLeft(neighbour, tiles);// TODO ide to aj horizontalne
			neighbour = getNeighbour(first, Coordinates.WS);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToLeft(neighbour, tiles);// TODO ide to aj horizontalne
			neighbour = getNeighbour(first, Coordinates.EN);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToRight(neighbour, tiles);// TODO ide to aj horizontalne
			neighbour = getNeighbour(first, Coordinates.ES);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToRight(neighbour, tiles);// TODO ide to aj horizontalne
			nonBlockingTiles.removeAll(tiles);
//			System.out.println("nonBlockingTiles: " + nonBlockingTiles);// TODO DEBUG
			
			
			// counting BlockedByCount
			
//			blockedByResults.clear();
//			for(Tile tile : nonBlockingTiles) {
//				blockedByResults.put(tile, getBlockedByCount(tile));
//			}
//			System.out.println("blockedByResults: " + blockedByResults);// TODO DEBUG
//			results = new Integer[blockedByResults.size()];// TODO DEBUG
//			results = blockedByResults.values().toArray(results);// TODO DEBUG
//			Arrays.sort(results);// TODO DEBUG
//			System.out.println("results: " + Arrays.toString(results));// TODO DEBUG
			
			// finding most blocked tiles
			
//			max = 0;
//			for(int i : blockedByResults.values()) {
//				if(i > max) max = i;
//			}
//			System.out.println("max: " + max);// TODO DEBUG
//			maxTiles.clear();
//			for(Tile tile : blockedByResults.keySet()) {
//				if(blockedByResults.get(tile) == max) maxTiles.add(tile);
//			}
//			System.out.println("maxTiles: " + maxTiles);// TODO DEBUG
			
			// random
			
//			Tile second = maxTiles.get((int)(Math.random() * maxTiles.size()));
			Tile second = getRandomTile(nonBlockingTiles);
//			System.out.println("second: " + second);// TODO DEBUG
			
			// setting second content
			
			second.setContent(tileContentsIterator.next());
			
			// blocking tiles
			
			nonBlockingTiles.remove(second);
			
			tiles.clear();
			neighbour = getNeighbour(second, Coordinates.WN);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToLeft(neighbour, tiles);// TODO ide to aj horizontalne TODO staci " && neighbour.getContent() == null" iba tu ??
			neighbour = getNeighbour(second, Coordinates.WS);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToLeft(neighbour, tiles);// TODO ide to aj horizontalne TODO staci " && neighbour.getContent() == null" iba tu ??
			neighbour = getNeighbour(second, Coordinates.EN);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToRight(neighbour, tiles);// TODO ide to aj horizontalne TODO staci " && neighbour.getContent() == null" iba tu ??
			neighbour = getNeighbour(second, Coordinates.ES);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToRight(neighbour, tiles);// TODO ide to aj horizontalne TODO staci " && neighbour.getContent() == null" iba tu ??
			nonBlockingTiles.removeAll(tiles);
//			System.out.println("nonBlockingTiles: " + nonBlockingTiles);// TODO DEBUG
			
			/* 
			 * do mnoziny dam tie tiles-y, u ktorych sa mohlo zmenit blokovanie
			 * teda: pravych, lavych, hornych susedov, co nie su v nonBlockingTiles a nemaju content
			 * toto blokovanie overim, a podla toho ich pridam do nonBlockingTiles
			 */
			
			// unblocking tiles
			
			tiles.clear();
			neighbour = getNeighbour(first, Coordinates.ANE);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.ANW);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.ASE);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.ASW);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.WN);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.WS);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.EN);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.ES);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.ANE);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.ANW);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.ASE);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.ASW);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.WN);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.WS);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.EN);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.ES);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingTiles.contains(neighbour)) tiles.add(neighbour);
			
//			System.out.print("unblocking: ");// TODO DEBUG
			for(Tile tile : tiles) {
				if(!blocksVertically(tile) && !blocksLeft(tile) && !blocksRight(tile)) {
//					System.out.print(tile + " ");// TODO DEBUG
					nonBlockingTiles.add(tile);
				}
			}
//			System.out.println();// TODO DEBUG
			
//			System.out.println("ZMACKNI ENTR !!!");// TODO DEBUG
//			try {
//				System.in.read();// TODO DEBUG
//			} catch (IOException e) {e.printStackTrace();}
			
		}
		
		// check .:
		for(Tile tile : field.values()) {
			if(tile.getContent() == null) {
				System.err.println("!!! pruser .: tile.getContent() == null");
				return;// TODO dat sem exception
			}
		}
		
	}
	
	
	
	public boolean isBlocked(Tile tile) {
		if(getNeighbour(tile, Coordinates.ANE) != null) return true;
		if(getNeighbour(tile, Coordinates.ANW) != null) return true;
		if(getNeighbour(tile, Coordinates.ASE) != null) return true;
		if(getNeighbour(tile, Coordinates.ASW) != null) return true;
		if((getNeighbour(tile, Coordinates.WN) != null || getNeighbour(tile, Coordinates.WS) != null)&&
				(getNeighbour(tile, Coordinates.EN) != null || getNeighbour(tile, Coordinates.ES) != null)) {
			return true;
		}
		return false;
	}
	
	
	
}
