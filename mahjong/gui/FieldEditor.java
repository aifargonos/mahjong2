package mahjong.gui;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import mahjong.dict_tile_content.DictTileContent;
import mahjong.engine.Coordinates;
import mahjong.engine.Field;
import mahjong.engine.Tile;
import mahjong.engine.TileContent;

/**
 * TODO
 * 
 * lepsie rozlysit tie xml formaty ...
 * v zapornych to blbo kresli kurzor !!!
 * statusbar s poziciou kurzora ...
 * blbo to prekresluje !!!
 * 
 * 
 * @author aifargonos
 */
public class FieldEditor extends JPanel implements MouseInputListener, MouseWheelListener, ActionListener, ComponentListener {
	
	private static final long serialVersionUID = -4666698720219808565L;
	
	
	
	public static final String AC_SAVE = "save";
	public static final String AC_CLEAR = "clear";
	public static final String AC_LOAD = "load";
	public static final String AC_CENTRE = "centre";
	public static final String AC_GENERATE = "generate";
	
	
	
	public static Point truncateToCoordinates(Point p, int z) {
		return FieldComponent.coordinatesToBounds(FieldComponent.pointToCoordinates(p, z)).getLocation();
	}
	
	
	
	private Point cursorPos;
	private int cursorZ;
	private Point dragDiff;
	private boolean keepCentred;
	
	private Field field;
	
	private JFrame frame;
	
	private FieldComponent fieldComponent;
	private JButton saveButton;
	private JButton clearButton;
	private JButton loadButton;
	private JToggleButton centreButton;
	private JButton generateButton;
	
	
	
	public FieldEditor() {
		super(new BorderLayout());
		this.cursorPos = null;
		this.cursorZ = 0;
		this.dragDiff = null;
		this.keepCentred = false;
		this.field = new Field();
		this.frame = new JFrame();
		this.fieldComponent = new FieldComponent(field);
		
		fieldComponent.setPreferredSize(Config.fieldComponentDefaultSize);
		fieldComponent.addMouseListener(this);
		fieldComponent.addMouseMotionListener(this);
		fieldComponent.addMouseWheelListener(this);
		fieldComponent.addComponentListener(this);
		
		
		add(fieldComponent);
		
		
		JToolBar toolbar = new JToolBar();
		
		saveButton = new JButton();
		saveButton.setActionCommand(AC_SAVE);
		saveButton.addActionListener(this);
		toolbar.add(saveButton);
		
		clearButton = new JButton();
		clearButton.setActionCommand(AC_CLEAR);
		clearButton.addActionListener(this);
		toolbar.add(clearButton);
		
		loadButton = new JButton();
		loadButton.setActionCommand(AC_LOAD);
		loadButton.addActionListener(this);
		toolbar.add(loadButton);
		
		centreButton = new JToggleButton();
		centreButton.setActionCommand(AC_CENTRE);
		centreButton.addActionListener(this);
		toolbar.add(centreButton);
		
		generateButton = new JButton();
		generateButton.setActionCommand(AC_GENERATE);
		generateButton.addActionListener(this);
		toolbar.add(generateButton);
		
		
		frame.add(this);
		frame.add(toolbar, BorderLayout.NORTH);
		
		
		texts();// TODO ... toto bude inac ...
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		
	}
	
	public void texts() {
		frame.setTitle(Config.texts.fieldEditorTitle);
		saveButton.setText(Config.texts.save);
		saveButton.setMnemonic(Config.texts.saveMnemonic);
		clearButton.setText(Config.texts.clear);
		clearButton.setMnemonic(Config.texts.clearMnemonic);
		loadButton.setText(Config.texts.load);
		loadButton.setMnemonic(Config.texts.loadMnemonic);
		centreButton.setText(Config.texts.centre);
		centreButton.setMnemonic(Config.texts.centreMnemonic);
		generateButton.setText(Config.texts.generate);
		generateButton.setMnemonic(Config.texts.generateMnemonic);
	}
	
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if(cursorPos == null) return;
		
		Color oldColor = g.getColor();
		Color color = Config.skin.fieldEditorCursorColor;
		
		Dimension size = Config.getTileSize();
		
		if(g instanceof Graphics2D) {
			Graphics2D g2D = (Graphics2D)g;
			g2D.setStroke(new BasicStroke(Config.skin.fieldEditorCursorThickness));
		}
		
		Polygon outline = TileComponent.getOutline(size);
		outline.translate(cursorPos.x, cursorPos.y);
		g.setColor(color);
		g.drawPolygon(outline);
		
		g.setColor(oldColor);
	}
	
	
	
	private void save(File file) {
		
		try {
			
			OutputStream xmlOut = new FileOutputStream(file);
			field.save(xmlOut);
			xmlOut.close();
			
		} catch(JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void load(File file) {
		
		field.clear();
		
		try {
			
			InputStream xmlIn = new FileInputStream(file);
			field.load(xmlIn);
			xmlIn.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fieldComponent.refresh();
		fieldComponent.centre();
	}
	
	private void generate(File file) {
		
		try {
			
			Vector<TileContent> tileContents = new Vector<TileContent>();
			
			InputStream xmlIn = new FileInputStream(file);
			TileContent.load(DictTileContent.class, xmlIn, tileContents);// TODO DictTileContent TODO
			xmlIn.close();
			
			field.generate(tileContents);
			
			// TODO refreshe ...
			repaint();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public void mouseClicked(MouseEvent me) {}
	public void mouseEntered(MouseEvent me) {
		if(dragDiff != null) return;
		cursorPos = truncateToCoordinates(me.getPoint(), cursorZ);
		Dimension size = Config.getTileSize();
		int d = Config.skin.fieldEditorCursorThickness/2;
		Rectangle clip = new Rectangle(cursorPos.x - d, cursorPos.y - d, size.width + 2*d, size.height + 2*d);
		repaint(clip);
	}
	public void mouseExited(MouseEvent me) {
		if(cursorPos == null) return;
		Dimension size = Config.getTileSize();
		int d = Config.skin.fieldEditorCursorThickness/2;
		Rectangle clip = new Rectangle(cursorPos.x - d, cursorPos.y - d, size.width + 2*d, size.height + 2*d);
		repaint(clip);
		cursorPos = null;
	}
	public void mousePressed(MouseEvent me) {
		
		if(me.getButton() == MouseEvent.BUTTON1) {
			
			field.put(new Tile(FieldComponent.pointToCoordinates(me.getPoint(), cursorZ)));
			
			fieldComponent.refresh();
			
			Dimension size = Config.getTileSize();
			int d = Config.skin.fieldEditorCursorThickness/2;
			Rectangle clip = new Rectangle(cursorPos.x - d, cursorPos.y - d, size.width + 2*d, size.height + 2*d);
			repaint(clip);
			
		} else if(me.getButton() == MouseEvent.BUTTON2) {
			dragDiff = new Point(me.getPoint().x - Config.zero.x, me.getPoint().y - Config.zero.y);
			
			Dimension size = Config.getTileSize();
			int d = Config.skin.fieldEditorCursorThickness/2;
			Rectangle clip = new Rectangle(cursorPos.x - d, cursorPos.y - d, size.width + 2*d, size.height + 2*d);
			repaint(clip);
			cursorPos = null;
			
		} else if(me.getButton() == MouseEvent.BUTTON3) {
			
			Coordinates c = FieldComponent.pointToCoordinates(me.getPoint(), cursorZ);
			Tile tile = field.get(c);
			if(tile != null && tile.getPosition().equals(c)) {
				
				field.remove(tile);
				
				fieldComponent.refresh();
				
				Dimension size = Config.getTileSize();
				int d = Config.skin.fieldEditorCursorThickness/2;
				Rectangle clip = new Rectangle(cursorPos.x - d, cursorPos.y - d, size.width + 2*d, size.height + 2*d);
				repaint(clip);
			}
			
		}
		
	}
	public void mouseReleased(MouseEvent me) {
		if(me.getButton() == MouseEvent.BUTTON2) {
			dragDiff = null;
			
			cursorPos = truncateToCoordinates(me.getPoint(), cursorZ);
			Dimension size = Config.getTileSize();
			int d = Config.skin.fieldEditorCursorThickness/2;
			Rectangle clip = new Rectangle(cursorPos.x - d, cursorPos.y - d, size.width + 2*d, size.height + 2*d);
			repaint(clip);
			
		}
	}
	public void mouseDragged(MouseEvent me) {
		if(dragDiff != null) {
			
			Config.zero.x = me.getPoint().x - dragDiff.x;
			Config.zero.y = me.getPoint().y - dragDiff.y;
			
			fieldComponent.revalidate();
		}
	}
	public void mouseMoved(MouseEvent me) {
		Point oldPos = cursorPos;
		cursorPos = truncateToCoordinates(me.getPoint(), cursorZ);
		if(!oldPos.equals(cursorPos)) {
			Dimension size = Config.getTileSize();
			int d = Config.skin.fieldEditorCursorThickness/2;
			Rectangle clip = new Rectangle(cursorPos.x - d, cursorPos.y - d, size.width + 2*d, size.height + 2*d);
			Rectangle clipOld = new Rectangle(oldPos.x - d, oldPos.y - d, size.width + 2*d, size.height + 2*d);
			repaint(clip.union(clipOld));
		}
	}
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		
		if(mwe.isControlDown()) {
			
			Dimension size = Config.getTileSize();
			int d = Config.skin.fieldEditorCursorThickness/2;
			Rectangle oldClip = new Rectangle(cursorPos.x - d, cursorPos.y - d, size.width + 2*d, size.height + 2*d);
			
			Config.tileWidth += mwe.getWheelRotation() * mwe.getScrollAmount();
			
			fieldComponent.revalidate();
			
			size = Config.getTileSize();
			Rectangle clip = new Rectangle(cursorPos.x - d, cursorPos.y - d, size.width + 2*d, size.height + 2*d);
			repaint(clip.union(oldClip));
			
		} else {
		
			Point oldPos = cursorPos;
			int oldZ = cursorZ;
			int scroll = mwe.getWheelRotation();
			if(scroll < 0) {
				cursorZ--;
			} else if(scroll > 0) {
				cursorZ++;
			}
			if(oldZ != cursorZ) {
				cursorPos = truncateToCoordinates(mwe.getPoint(), cursorZ);
				Dimension size = Config.getTileSize();
				int d = Config.skin.fieldEditorCursorThickness/2;
				Rectangle clip = new Rectangle(cursorPos.x - d, cursorPos.y - d, size.width + 2*d, size.height + 2*d);
				Rectangle clipOld = new Rectangle(oldPos.x - d, oldPos.y - d, size.width + 2*d, size.height + 2*d);
				repaint(clip.union(clipOld));
			}
			
		}
		
	}
	
	public void actionPerformed(ActionEvent ae) {
		
		if(ae.getActionCommand().equals(AC_SAVE)) {
			
			JFileChooser fileChooser = new JFileChooser(new File("."));
			fileChooser.addChoosableFileFilter(Config.xmlFileFilter);
			
			if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				save(fileChooser.getSelectedFile());
			}
			
		} else if(ae.getActionCommand().equals(AC_CLEAR)) {
			
			field.clear();
			fieldComponent.removeAll();
			repaint();
			
		} else if(ae.getActionCommand().equals(AC_LOAD)) {
			
			JFileChooser fileChooser = new JFileChooser(new File("."));
			fileChooser.addChoosableFileFilter(Config.xmlFileFilter);
			
			if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				load(fileChooser.getSelectedFile());
			}
			
		} else if(ae.getActionCommand().equals(AC_CENTRE)) {
			
			if(keepCentred) {
				keepCentred = false;
			} else {
				keepCentred = true;
				fieldComponent.centre();
			}
			
		} else if(ae.getActionCommand().equals(AC_GENERATE)) {
			
			JFileChooser fileChooser = new JFileChooser(new File("."));
			fileChooser.addChoosableFileFilter(Config.xmlFileFilter);
			
			if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				generate(fileChooser.getSelectedFile());
			}
			
		}
		
	}
	
	public void componentHidden(ComponentEvent ce) {}
	public void componentMoved(ComponentEvent ce) {}
	public void componentResized(ComponentEvent ce) {
		if(keepCentred) fieldComponent.centre();
	}
	public void componentShown(ComponentEvent ce) {}
	
	
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new FieldEditor();
			}
		});
		
	}
	
	
	
}
