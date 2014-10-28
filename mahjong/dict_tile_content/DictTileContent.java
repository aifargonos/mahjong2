package mahjong.dict_tile_content;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import mahjong.engine.TileContent;

@XmlRootElement
public class DictTileContent extends TileContent {
	
	
	
	public static final int DIR_HORISONTAL = 0;
	public static final int DIR_DIAGONAL = 1;
	public static final int DIR_VERTICAL = 2;
	
	
	
	private String text;
	private int dir;
	/**
	 * List of texts of KanjiTileContent-s that go with this.
	 */
	@XmlElement(name="association")
//	private HashSet<String> associatedWithList;
	HashSet<String> associatedWithList;
	
	
	
	public DictTileContent() {
		this("", DIR_HORISONTAL);
	}
	
	public DictTileContent(String text, int dir) {
		this.text = text;
		this.dir = dir;
		associatedWithList = new HashSet<String>();
	}
	
	
	
	@Override
	public boolean isAssociatedWith(TileContent tc) {
		if(!(tc instanceof DictTileContent)) {
			return false;
		}
		
		DictTileContent dtc = (DictTileContent)tc;
		
		return dtc.text.equals(text) || associatedWithList.contains(dtc.text);
	}
	
	@Override
	public void draw(Graphics g, Rectangle area) {
		
		Graphics2D g2 = (Graphics2D)g;
		Object oldTAA = g2.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g.setFont(g.getFont().deriveFont(100f));
		
		Font f = g.getFont();
		FontMetrics fm = g.getFontMetrics();
		
		switch(dir) {
			case DIR_HORISONTAL: {
				
				float areaWPH = area.width / (float)area.height;
				float textWPH = fm.stringWidth(text) / (float)fm.getHeight();
				
				if(areaWPH > textWPH) {
					// area sirsi ako text
					
					float areaHPTextH = area.height / (float)fm.getHeight();
					g.setFont(f.deriveFont(f.getSize2D() * areaHPTextH));
					
				} else {
					// area uzsi ako text
					
					float areaWPTextW = area.width / (float)fm.stringWidth(text);
					g.setFont(f.deriveFont(f.getSize2D() * areaWPTextW));
					
				}
				
				fm = g.getFontMetrics();
				g.drawString(text, area.x + (area.width - fm.stringWidth(text)) / 2, area.y + area.height/2 + fm.getAscent()/3);
				
			} break;
			case DIR_VERTICAL: {
				
				Dimension textDim = new Dimension(0, fm.getHeight()*text.length());
				for(char ch : text.toCharArray()) {
					if(fm.charWidth(ch) > textDim.width) {
						textDim.width = fm.charWidth(ch);
					}
				}
				
				float areaWPH = area.width / (float)area.height;
				float textWPH = textDim.width / (float)textDim.height;
				
				if(areaWPH > textWPH) {
					// area sirsi ako text
					
					float areaHPTextH = area.height / (float)textDim.height;
					g.setFont(f.deriveFont(f.getSize2D() * areaHPTextH));
					
				} else {
					// area uzsi ako text
					
					float areaWPTextW = area.width / (float)textDim.width;
					g.setFont(f.deriveFont(f.getSize2D() * areaWPTextW));
					
				}
				
				fm = g.getFontMetrics();
				textDim = new Dimension(0, fm.getHeight()*text.length());
				for(char ch : text.toCharArray()) {
					if(fm.charWidth(ch) > textDim.width) {
						textDim.width = fm.charWidth(ch);
					}
				}
				
				int x = area.x + (area.width + textDim.width) / 2;
				int y = area.y + (area.height - textDim.height) / 2 + fm.getAscent();
				for(char ch : text.toCharArray()) {
					g.drawString(String.valueOf(ch), x - fm.charWidth(ch), y);
					y += fm.getHeight();
				}
				
			} break;
			case DIR_DIAGONAL: {
				
				AffineTransform oldAT = g2.getTransform();
				
				float areaWPH = area.width / (float)area.height;
				double textWPH = fm.stringWidth(text) / (double)fm.getHeight();
				
				if(areaWPH < textWPH && 1/areaWPH > textWPH) {
					// kreslim vertikalne
					
					float areaWPTextH = area.width / (float)fm.getHeight();
					g.setFont(f.deriveFont(f.getSize2D() * areaWPTextH));
					
					g2.rotate(Math.PI/2);
					
					fm = g.getFontMetrics();
					g.drawString(text, area.y + (area.height - fm.stringWidth(text)) / 2, -area.x - fm.getDescent());
					
				} else if(areaWPH > textWPH && 1/areaWPH < textWPH) {
					// kreslim horizontalne
					
					dir = DIR_HORISONTAL;
					draw(g, area);
					dir = DIR_DIAGONAL;
					
				} else {
					// kreslim diagonalne
					
					double theta = Math.atan((area.width - area.height*textWPH) / (area.height - area.width*textWPH));
					
					float textSizeRatio = (float)(area.width / (Math.sin(theta) + textWPH*Math.cos(theta)) / fm.getHeight());
					g.setFont(f.deriveFont(f.getSize2D()*textSizeRatio));
					
					fm = g.getFontMetrics();
					
					g2.translate(area.x, area.y - fm.getHeight() + Math.cos(theta)*fm.getHeight());
					g2.rotate(theta, 0, fm.getHeight());
					
//					g2.drawRect(0, 0, fm.stringWidth(text), fm.getHeight());// TODO DEBUG
					
					g2.drawString(text, 0, (float)fm.getAscent());
					
				}
				
				g2.setTransform(oldAT);
				
			} break;
		}
		
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, oldTAA);		
		
	}
	
	
	
	public void associateWith(DictTileContent dtc) {
		associatedWithList.add(dtc.text);
		dtc.associatedWithList.add(text);
	}
	
	public void disassociateWith(DictTileContent dtc) {
		associatedWithList.remove(dtc.text);
		dtc.associatedWithList.remove(text);
	}
	
	@XmlAttribute
	public int getDir() {
		return dir;
	}
	
	public void setDir(int dir) {
		this.dir = dir;
	}
	
	@XmlAttribute
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[text=" + text + ",dir=" + dir + ",associatedWithList=" + associatedWithList + "]";
	}
	
	
	
}
