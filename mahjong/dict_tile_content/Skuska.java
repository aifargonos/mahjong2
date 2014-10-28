package mahjong.dict_tile_content;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import mahjong.engine.TileContent;


public class Skuska {
	
	
	
	public static void main(String[] args) throws IOException, XMLStreamException, JAXBException {
		
		System.out.println("Anoj!!!");
		
		
		Vector<TileContent> tileContents = new Vector<TileContent>();
		TreeMap<String, DictTileContent> dtcs = new TreeMap<String, DictTileContent>();
		HashSet<String> done = new HashSet<String>();
		
		
		InputStream xmlIn = new FileInputStream("corrected_contents.xml");
		TileContent.load(DictTileContent.class, xmlIn, tileContents);
		xmlIn.close();
		
		
		for(TileContent content : tileContents) {
			DictTileContent dtc = (DictTileContent)content;
			dtcs.put(dtc.getText(), dtc);
		}
		
		
		PrintStream out = new PrintStream("out1.dot");
		
//		for(String key = dtcs.lastKey(); key != null; key = dtcs.floorKey(key)) {
		for(String key = dtcs.lastKey(); key != null; key = dtcs.lowerKey(key)) {
			DictTileContent dtc = dtcs.get(key);
			
			if(done.contains(dtc.getText())) continue;
			
			System.out.println(dtc.getText());
			
			TreeSet<String> assocs = new TreeSet<String>(dtc.associatedWithList);
			
			for(String assoc = assocs.last(); assoc != null; assoc = assocs.lower(assoc)) {
//			for(String assoc : dtc.associatedWithList) {
				if(done.contains(assoc)) continue;
				out.println("\"" + dtc.getText() + "\" -- \"" + assoc + "\";");
				System.out.println("\"" + dtc.getText() + "\" -- \"" + assoc + "\";");
			}
			
			done.add(dtc.getText());
			
		}
		
		out.close();
		
		
		System.out.println(".. caw");
		
	}
	
	
	
}
