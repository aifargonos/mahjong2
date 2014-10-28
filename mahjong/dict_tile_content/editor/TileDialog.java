package mahjong.dict_tile_content.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mahjong.dict_tile_content.DictTileContent;

public class TileDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -8229567597734010909L;
	
	private static final String[] options = {"vertical", "diagonal", "horizontal"};
	
	
	
	public static final int PADDING = 10;
	
	public static final String CHOICE_OK = "choice ok";
	public static final String CHOICE_CANCEL = "choice cancel";
	
	
	
	private String choice;
	
	private DictTileContent dtc;
	
	private JTextField text;
	private JComboBox direction;
	
	
	
	public TileDialog(Window owner) {
		super(owner);
		
		dtc = null;
		
		// construct
		text = new JTextField();// TODO treba size ??
		direction = new JComboBox(options);
		
		settings();
		addChildren();
		pack();
		
		setLocationRelativeTo(owner);
	}
	
	public TileDialog(Window owner, DictTileContent dtc) {
		super(owner);
		
		this.dtc = dtc;
		
		// construct
		text = new JTextField();// TODO treba size ??
		direction = new JComboBox(options);
		
		settings();
		initDefaultValues();
		addChildren();
		pack();
		
		setLocationRelativeTo(owner);
	}
	
	private void settings() {
		
		setTitle(Config.texts.newTile);
		setModal(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);
		enableInputMethods(false);// TODO ??
		direction.enableInputMethods(false);// TODO ??
//		text.enableInputMethods(true);// TODO ??
		text.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {text.selectAll();}
			public void focusLost(FocusEvent e) {text.select(0, 0);}
		});
		
	}
	
	private void initDefaultValues() {
		text.setText(dtc.getText());
		direction.setSelectedItem(dirToOption(dtc.getDir()));
	}
	
	private void addChildren() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		
		panel.add(Box.createVerticalStrut(PADDING));
		
		
		// input
		
		JPanel input = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel loginLabel = new JLabel(Config.texts.text + ":");
		c.gridx = 0;
		c.gridy = 0;
		c.insets.set(0, 0, PADDING/2, PADDING/2);
		c.anchor = GridBagConstraints.EAST;
		input.add(loginLabel, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets.set(0, PADDING/2, PADDING/2, 0);
		c.anchor = GridBagConstraints.WEST;
		input.add(text, c);
		
		JLabel directionLabel = new JLabel(Config.texts.direction + ":");
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets.set(PADDING/2, 0, 0, PADDING/2);
		c.anchor = GridBagConstraints.EAST;
		input.add(directionLabel, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets.set(PADDING/2, PADDING/2, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		input.add(direction, c);
		
		panel.add(input);
		
		panel.add(Box.createVerticalStrut(PADDING));
		
		
		// buttons
		
		Box buttons = Box.createHorizontalBox();
		
		JButton okButton = new JButton(Config.texts.ok);
		okButton.setMnemonic(Config.texts.okMnemonic);
		okButton.setActionCommand(CHOICE_OK);
		okButton.addActionListener(this);
		buttons.add(okButton);
		
		buttons.add(Box.createHorizontalStrut(PADDING));
		
		JButton cancelButton = new JButton(Config.texts.cancel);
		cancelButton.setMnemonic(Config.texts.cancelMnemonic);
		cancelButton.setActionCommand(CHOICE_CANCEL);
		cancelButton.addActionListener(this);
		buttons.add(cancelButton);
		
		buttons.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(buttons);
		
		
		panel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
		setContentPane(panel);
		getRootPane().setDefaultButton(okButton);
		
	}
	
	private String dirToOption(int dir) {
		switch(dir) {
		case DictTileContent.DIR_HORISONTAL:
			return options[2];
		case DictTileContent.DIR_VERTICAL:
			return options[0];
		case DictTileContent.DIR_DIAGONAL:
			return options[1];
		default:
			return null;
		}
	}
	
	private int optionToDir(Object option) {
		if(option.equals(options[2])) {
			return DictTileContent.DIR_HORISONTAL;
		} else if(option.equals(options[0])) {
			return DictTileContent.DIR_VERTICAL;
		} else if(option.equals(options[1])) {
			return DictTileContent.DIR_DIAGONAL;
		} else {
			return DictTileContent.DIR_HORISONTAL;
		}
	}
	
	
	
	public static final DictTileContent showNewDialog(Window owner) {
		TileDialog dialog = new TileDialog(owner);
		dialog.setVisible(true);
		return dialog.dtc;
	}
	
	public static final DictTileContent showEditDialog(Window owner, DictTileContent ktc) {
		TileDialog dialog = new TileDialog(owner, ktc);
		dialog.setVisible(true);
		return dialog.dtc;
	}
	
	
	
	public String getChoice() {
		return choice;
	}
	
	public String getText() {
		return text.getText();
	}
	
	public int getDirection() {
		return optionToDir(direction.getSelectedItem());
	}
	
	
	
	public void actionPerformed(ActionEvent ae) {
		
		choice = ae.getActionCommand();
		
		if(choice.equals(CHOICE_OK)) {
			if(dtc == null) {
				dtc = new DictTileContent(getText(), getDirection());
			} else {
				dtc.setText(getText());
				dtc.setDir(getDirection());
			}
		}
		
		setVisible(false);
		
	}
	
	
	
}
