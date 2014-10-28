package mahjong.dict_tile_content.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import mahjong.dict_tile_content.DictTileContent;
import mahjong.engine.TileContent;
import mahjong.gui.TileContentComponent;

/**
 * TODO .:
 * 
 * ked pri editovani zmenis text, treba si dat pozor na asociacie !!!
 * 	bud ich vsade prepisat, alebo vsetky zrusit ... asi prepisat
 * 
 * spravit automaticke rozmiestnovanie.
 * 
 * spravit to hierarchicky
 * 	editovanie casti grafu
 * 
 * zoom
 * 
 * 
 * @author aifargonos
 */
public class DictTileContentEditor extends JPanel implements ActionListener, MouseInputListener {
	
	private static final long serialVersionUID = 1594291262729916230L;
	
	
	
	public static final String AC_NEW_TILE = "new_tile";
	public static final String AC_EDIT = "edit";
	public static final String AC_DELETE = "delete";
	public static final String AC_SAVE = "save";
	public static final String AC_CLOSE = "close";
	public static final String AC_LOAD = "load";
	
	public static final String XML_ENCODING = "UTF-8";
	public static final String XML_VERSION = "1.0";
	public static final String XML_ROOT_ELEMENT = "dictTileContents";
	
	
	
	private class TCCPopup extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = 6031773666794929050L;
		
		private TileContentComponent tcc;
		
		public TCCPopup(TileContentComponent tcc) {
			
			this.tcc = tcc;
			tcc.setComponentPopupMenu(this);
			
			JMenuItem item = new JMenuItem(Config.texts.edit);
			item.setMnemonic(Config.texts.editMnemonic);
			item.setActionCommand(AC_EDIT);
			item.addActionListener(this);
			add(item);
			
			item = new JMenuItem(Config.texts.delete);
			item.setMnemonic(Config.texts.deleteMnemonic);
			item.setActionCommand(AC_DELETE);
			item.addActionListener(this);
			add(item);
			
		}
		
		public void actionPerformed(ActionEvent ae) {
			
			if(ae.getActionCommand().equals(AC_EDIT)) {
				if(tcc.getTileContent() instanceof DictTileContent) {
					TileDialog.showEditDialog(frame, (DictTileContent)tcc.getTileContent());
					tcc.repaint();
				}
			} else if(ae.getActionCommand().equals(AC_DELETE)) {
				removeTCC(tcc);
			}
			
		}
		
	}
	
	
	
	private JFrame frame;
	
	private Component moving = null;
	private TileContentComponent selected = null;
	
	
	
	public DictTileContentEditor() {
		super(null);
		
		// settings
		Dimension size = Config.editorSize;
		setSize(size);
		setPreferredSize(size);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		// popup
		JPopupMenu popup = new JPopupMenu();
		
		JMenuItem item = new JMenuItem(Config.texts.newTile);
		item.setMnemonic(Config.texts.newTileMnemonic);
		item.setActionCommand(AC_NEW_TILE);
		item.addActionListener(this);
		popup.add(item);
		
		setComponentPopupMenu(popup);
		
		// ...
		
		// frame
		frame = new JFrame(Config.texts.frameTitle);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// toolbar
		JToolBar toolbar = new JToolBar();
		
		JButton button = new JButton(Config.texts.newTile);
		button.setMnemonic(Config.texts.newTileMnemonic);
		button.setActionCommand(AC_NEW_TILE);
		button.addActionListener(this);
		toolbar.add(button);
		
		button = new JButton(Config.texts.save);
		button.setMnemonic(Config.texts.saveMnemonic);
		button.setActionCommand(AC_SAVE);
		button.addActionListener(this);
		toolbar.add(button);
		
		button = new JButton(Config.texts.close);
		button.setMnemonic(Config.texts.closeMnemonic);
		button.setActionCommand(AC_CLOSE);
		button.addActionListener(this);
		toolbar.add(button);
		
		button = new JButton(Config.texts.load);
		button.setMnemonic(Config.texts.loadMnemonic);
		button.setActionCommand(AC_LOAD);
		button.addActionListener(this);
		toolbar.add(button);
		
		frame.add(toolbar, BorderLayout.NORTH);
		
		// show
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		
	}
	
	
	
	private void startMoving(Component component) {
		
		if(moving != null) {
			stopMoving();
		}
		
		moving = component;
		setComponentZOrder(moving, 0);
		
		Point loc = getMousePosition(true);
		if(loc != null) {
			Dimension size = moving.getSize();
			moving.setLocation(loc.x - size.width/2, loc.y - size.height/2);
			repaint();
		}
		
	}
	
	private void stopMoving() {
		moving = null;
	}
	
	private void removeTCC(TileContentComponent tcc) {
		remove(tcc);
		for(Component component : getComponents()) {
			if(component instanceof TileContentComponent) {
				TileContentComponent t = (TileContentComponent)component;
				if(tcc.getTileContent() instanceof DictTileContent && t.getTileContent() instanceof DictTileContent) {
					((DictTileContent)tcc.getTileContent()).disassociateWith((DictTileContent)t.getTileContent());
				}
			}
		}
		repaint();
	}
	
	private void save(File file) {
		
		try {
			
			OutputStream xmlOut = new FileOutputStream(file);
			
			Vector<TileContent> tileContents = new Vector<TileContent>();
			for(Component component : getComponents()) {
				if(component instanceof TileContentComponent && ((TileContentComponent)component).getTileContent() instanceof DictTileContent) {
					tileContents.add((DictTileContent)((TileContentComponent)component).getTileContent());
				}
			}
			
			TileContent.save(DictTileContent.class, xmlOut, tileContents);
			
			xmlOut.close();
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void load(File file) {
		
		try {
			
			InputStream xmlIn = new FileInputStream(file);
			
			Vector<TileContent> tileContents = new Vector<TileContent>();
			TileContent.load(DictTileContent.class, xmlIn, tileContents);
			for(TileContent dtc : tileContents) {
				if(dtc instanceof DictTileContent) {
					TileContentComponent tcc = new TileContentComponent((DictTileContent)dtc, Config.defaultTileSize);
					tcc.addMouseListener(this);
					tcc.addMouseMotionListener(this);
					new TCCPopup(tcc);
					add(tcc);
				}
			}
			
			// TODO rozmiestnit
			
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
		
		repaint();
	}
	
	
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new DictTileContentEditor();
			}
		});
		
	}
	
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Component[] components = getComponents();
		for(int i = 0; i < components.length-1; i++) {
			for(int j = i+1; j < components.length; j++) {
				if(components[i] instanceof TileContentComponent && components[j] instanceof TileContentComponent) {
					if(((TileContentComponent)components[i]).getTileContent().isAssociatedWith(((TileContentComponent)components[j]).getTileContent())) {
						Rectangle boundsI = components[i].getBounds();
						Rectangle boundsJ = components[j].getBounds();
						g.drawLine(boundsI.x + boundsI.width/2, boundsI.y + boundsI.height/2, boundsJ.x + boundsJ.width/2, boundsJ.y + boundsJ.height/2);
					}
				}
			}
		}
		
	}
	
	public void actionPerformed(ActionEvent ae) {
		
		if(ae.getActionCommand().equals(AC_NEW_TILE)) {
			
			DictTileContent dtc = TileDialog.showNewDialog(frame);
			if(dtc != null) {
				TileContentComponent tcc = new TileContentComponent(dtc, Config.defaultTileSize);
				tcc.addMouseListener(this);
				tcc.addMouseMotionListener(this);
				new TCCPopup(tcc);
				add(tcc);
				startMoving(tcc);
				
				repaint();
			}
			
		} else if(ae.getActionCommand().equals(AC_SAVE)) {
			
			JFileChooser fileChooser = new JFileChooser(new File("."));
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML file", "xml", "XML"));
			
			if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				save(fileChooser.getSelectedFile());
			}
			
		} else if(ae.getActionCommand().equals(AC_CLOSE)) {
			removeAll();
			repaint();
		} else if(ae.getActionCommand().equals(AC_LOAD)) {
			
			JFileChooser fileChooser = new JFileChooser(new File("."));
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML file", "xml", "XML"));
			
			if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				load(fileChooser.getSelectedFile());
			}
			
		}
		
	}
	
	public void mouseClicked(MouseEvent me) {
		
		if(me.getClickCount() == 2) {
			if (me.getSource() instanceof TileContentComponent) {
				TileContentComponent tcc = (TileContentComponent) me.getSource();
				if(tcc.getTileContent() instanceof DictTileContent) {
					TileDialog.showEditDialog(frame, (DictTileContent)tcc.getTileContent());
					tcc.repaint();
				}
			}
		}
		
		if(moving != null) {
			stopMoving();
		} else {
			for(Component component : getComponents()) {
				if(component == me.getSource()) {
					startMoving(component);
					break;
				}
			}
		}
		
	}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mousePressed(MouseEvent me) {
		
		if(selected == null && me.getSource() instanceof TileContentComponent) {
			selected = (TileContentComponent)me.getSource();
		}
		
	}
	public void mouseReleased(MouseEvent me) {
		Point loc = getMousePosition(true);
		if(loc == null) {
			return;
		}
		Component source = getComponentAt(loc);
		
		if(selected != null) {
			if(selected != source && source instanceof TileContentComponent) {
				if(selected.getTileContent() instanceof DictTileContent && ((TileContentComponent)source).getTileContent() instanceof DictTileContent) {
					DictTileContent selDTC = (DictTileContent)selected.getTileContent();
					DictTileContent souDTC = (DictTileContent)((TileContentComponent)source).getTileContent();
					if(selDTC.isAssociatedWith(souDTC)) {
						selDTC.disassociateWith(souDTC);
					} else {
						selDTC.associateWith(souDTC);
					}
					repaint();
				}
			}
			selected = null;
		}
		
	}
	
	public void mouseDragged(MouseEvent me) {}
	public void mouseMoved(MouseEvent me) {
		
		if(moving != null) {
			Point loc = getMousePosition(true);
			if(loc != null) {
				Dimension size = moving.getSize();
				moving.setLocation(loc.x - size.width/2, loc.y - size.height/2);
				repaint();
			}
		}
		
	}
	
	
	
}
