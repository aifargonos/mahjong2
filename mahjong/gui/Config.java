package mahjong.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;

import javax.swing.filechooser.FileNameExtensionFilter;

import mahjong.engine.Slant;

public class Config {
	
	
	
	public static Texts texts = new Texts();
	public static Skin skin = new Skin();
	
	
	
	public static Dimension fieldComponentDefaultSize = new Dimension(500, 400);
	
	
	
	public static Point zero = new Point(0, 0);
//	public static Point zero = new Point(100, 100);
	
	public static int tileWidth = 80;
	public static double tileWidthToHeightRatio = 0.75;
	public static double tileWidthToDepthRatio = 8;
	
	public static int getTileWidth() {
		return tileWidth;
	}
	public static int getTileHeight() {
		return (int)Math.round(tileWidth/tileWidthToHeightRatio);
	}
	public static int getTileDepth() {
		return (int)Math.round(tileWidth/tileWidthToDepthRatio);
	}
	public static Dimension getTileSize() {
		int depth = getTileDepth();
		return new Dimension(getTileWidth() + depth, getTileHeight() + depth);
	}
	
//	public static Slant slant = Slant.SE_TO_NW;
	public static Slant slant = Slant.SW_TO_NE;
//	public static Slant slant = Slant.NW_TO_SE;
//	public static Slant slant = Slant.NE_TO_SW;
	
	public static Color highlightedColor = Color.ORANGE;
	public static Color selectedColor = Color.GREEN;
	
	
	
	public static FileNameExtensionFilter xmlFileFilter = new FileNameExtensionFilter(texts.xmlFile, "xml", "XML");
	
	
	
	public static File layoutFile = new File("difficult-layout.xml");
//	public static File layoutFile = new File("my-layout.xml");
//	public static File layoutFile = new File("aab_skuska_layout.xml");
//	public static File contentsFile = new File("contents.xml");
	public static File contentsFile = new File("corrected_contents.xml");
//	public static File contentsFile = new File("simple_contents.xml");
	
	
	
}
