package mahjong.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import mahjong.dict_tile_content.DictTileContent;
import mahjong.engine.Field;
import mahjong.engine.Tile;
import mahjong.engine.TileContent;

/**
 * TODO
 * 
 * @author aifargonos
 */
public class Game implements ComponentListener, MouseListener, ActionListener {
	
	
	
	private static class TilePair {
		
		public Tile first;
		public Tile second;
		
		public TilePair(Tile first, Tile second) {
			this.first = first;
			this.second = second;
		}
		
		@Override
		public String toString() {
			return "(" + first + ", " + second + ")";
		}
	}
	
	
	
	public static final String AC_NEW_GAME = "newGame";
	public static final String AC_UNDO = "undo";
	public static final String AC_REDO = "redo";
	
	
	
	private Stack<TilePair> undoList;
	private Stack<TilePair> redoList;
	
	private Field field;
	
	private FieldComponent fieldComponent;
	private JButton newGameButton;
	private JButton undoButton;
	private JButton redoButton;
	
	private TileComponent selected;
	
	
	
	public Game(Container contentPane) {
		this.undoList = new Stack<TilePair>();
		this.redoList = new Stack<TilePair>();
		this.field = new Field();
		this.fieldComponent = new FieldComponent(field, this);
		this.selected = null;
		
		fieldComponent.setPreferredSize(Config.fieldComponentDefaultSize);
		fieldComponent.addComponentListener(this);
		
		
		contentPane.add(fieldComponent);
		
		
		JToolBar toolbar = new JToolBar();
		
		newGameButton = new JButton();
		newGameButton.setActionCommand(AC_NEW_GAME);
		newGameButton.addActionListener(this);
		toolbar.add(newGameButton);
		
		undoButton = new JButton();
		undoButton.setActionCommand(AC_UNDO);
		undoButton.addActionListener(this);
		toolbar.add(undoButton);
		
		redoButton = new JButton();
		redoButton.setActionCommand(AC_REDO);
		redoButton.addActionListener(this);
		toolbar.add(redoButton);
		
		contentPane.add(toolbar, BorderLayout.NORTH);
		
		
		texts();
		
		
//		newGame();
	}
	
	public void texts() {
		newGameButton.setText(Config.texts.newGame);
		newGameButton.setMnemonic(Config.texts.newGameMnemonic);
		undoButton.setText(Config.texts.undo);
		undoButton.setMnemonic(Config.texts.undoMnemonic);
		redoButton.setText(Config.texts.redo);
		redoButton.setMnemonic(Config.texts.redoMnemonic);
	}
	
	
	
	private void enableUndoRedo() {
		undoButton.setEnabled(!undoList.isEmpty());
		redoButton.setEnabled(!redoList.isEmpty());
	}
	
	
	
	public void newGame() {
		newGame(Config.layoutFile, Config.contentsFile);
	}
	public void newGame(File layout, File contents) {
		
		// field
		
		field.clear();
		Vector<TileContent> tileContents = new Vector<TileContent>();
		
		try {
			
			InputStream xmlIn = new FileInputStream(layout);
			field.load(xmlIn);
			xmlIn.close();
			
			xmlIn = new FileInputStream(contents);
			TileContent.load(DictTileContent.class, xmlIn, tileContents);// TODO DictTileContent TODO
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
		
		field.generate(tileContents);
		
		// fieldComponent
		
		fieldComponent.refresh();
		fieldComponent.centre();
//		fieldComponent.repaint();
		
		// undo / redo
		
		undoList.clear();
		redoList.clear();
		enableUndoRedo();
		
	}
	
	private void dodo(Tile tile1, Tile tile2) {
		
		field.remove(tile1);
		field.remove(tile2);
		
		undoList.push(new TilePair(tile1, tile2));
		redoList.clear();
		
		fieldComponent.refresh();
		fieldComponent.repaint();
		
		enableUndoRedo();
	}
	
	private void undo() {
		
		if(undoList.isEmpty()) return;
		
		if(selected != null) {
			selected.setSelected(false);
			selected = null;
		}
		
		TilePair pair = undoList.pop();
		
		field.put(pair.first);
		field.put(pair.second);
		
		redoList.push(pair);
		
		fieldComponent.refresh();
		fieldComponent.repaint();
		
		enableUndoRedo();
	}
	
	private void redo() {
		
		if(redoList.isEmpty()) return;
		
		if(selected != null) {
			selected.setSelected(false);
			selected = null;
		}
		
		TilePair pair = redoList.pop();
		
		field.remove(pair.first);
		field.remove(pair.second);
		
		undoList.push(pair);
		
		fieldComponent.refresh();
		fieldComponent.repaint();
		
		enableUndoRedo();
	}
	
	
	
	public void mouseClicked(MouseEvent me) {}
	public void mouseEntered(MouseEvent me) {
		Object o = me.getSource();
		if(o instanceof TileComponent) {
			TileComponent tc = (TileComponent)o;
			if(!field.isBlocked(tc.getTile())) tc.setHighlighted(true);
		}
	}
	public void mouseExited(MouseEvent me) {
		Object o = me.getSource();
		if(o instanceof TileComponent) {
			TileComponent tc = (TileComponent)o;
			if(!field.isBlocked(tc.getTile())) tc.setHighlighted(false);
		}
	}
	public void mousePressed(MouseEvent me) {
		
		Object o = me.getSource();
		if(o instanceof TileComponent) {
			TileComponent tc = (TileComponent)o;
			
			if(field.isBlocked(tc.getTile())) return;
			
			if(selected == tc) {
				selected = null;
				tc.setSelected(false);
			} else {
				
				if(selected != null) {
					
					if(selected.getTile().getContent().isAssociatedWith(tc.getTile().getContent())) {
						selected.setSelected(false);
						
						dodo(selected.getTile(), tc.getTile());
						
						selected = null;
					} else {
						selected.setSelected(false);
						selected = tc;
						tc.setSelected(true);
					}
					
				} else {
					selected = tc;
					tc.setSelected(true);
				}
				
			}
			
		}
		
	}
	public void mouseReleased(MouseEvent me) {}
	
	public void componentHidden(ComponentEvent ce) {}
	public void componentMoved(ComponentEvent ce) {}
	public void componentResized(ComponentEvent ce) {
		fieldComponent.centre();
	}
	public void componentShown(ComponentEvent ce) {}
	
	public void actionPerformed(ActionEvent ae) {
		
		if(ae.getActionCommand().equals(AC_NEW_GAME)) {
			newGame();
			fieldComponent.repaint();
		} else if(ae.getActionCommand().equals(AC_UNDO)) {
			undo();
		} else if(ae.getActionCommand().equals(AC_REDO)) {
			redo();
		}
		
	}
	
	
	
}
