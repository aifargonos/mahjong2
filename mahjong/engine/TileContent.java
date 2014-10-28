package mahjong.engine;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

abstract public class TileContent {
	
	
	
	public static final String XML_ENCODING = "UTF-8";
	public static final String XML_VERSION = "1.0";
	public static final String XML_ROOT_ELEMENT = "dictTileContents";
	
	
	
	public static void save(Class<? extends TileContent> descendant, OutputStream xmlOut, Collection<TileContent> tileContents) throws XMLStreamException, JAXBException {
		
		XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(xmlOut, XML_ENCODING);
        
        writer.writeStartDocument(XML_ENCODING, XML_VERSION);
        writer.writeCharacters("\n");
        writer.writeStartElement(XML_ROOT_ELEMENT);
        writer.writeCharacters("\n");
        
		JAXBContext context = JAXBContext.newInstance(descendant);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", true);// nefacha ...
		marshaller.setProperty("jaxb.fragment", true);
		
		for(TileContent tc : tileContents) {
			if(tc.getClass().equals(descendant)) {
				marshaller.marshal(descendant.cast(tc), writer);
	            writer.writeCharacters("\n");
			}
		}
		
		writer.writeEndElement();
        writer.writeCharacters("\n");
		writer.writeEndDocument();
		writer.close();
		
	}
	
	public static void load(Class<? extends TileContent> descendant, InputStream xmlIn, Collection<TileContent> tileContents) throws XMLStreamException, JAXBException {
		
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(xmlIn, XML_ENCODING);
		
		JAXBContext context = JAXBContext.newInstance(descendant);
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
						if(o.getClass().equals(descendant)) {
							tileContents.add(descendant.cast(o));
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
	
	abstract public boolean isAssociatedWith(TileContent tc);
	
	abstract public void draw(Graphics g, Rectangle rect);
	
}
