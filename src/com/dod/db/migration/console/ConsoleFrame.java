package com.dod.db.migration.console;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class ConsoleFrame extends JFrame {
	private static ConsoleFrame consoleFrame = null;
	private JPanel consolePanel = null;
	private JPanel jContentPane = null;
	private JScrollPane jScrollPane1 = null;
	private JTextPane consoleTextPane = null;
	private JButton clearButton = null;
	private JCheckBox scrollCheckBox = null;
	private StyleContext sc;
	private DefaultStyledDocument doc;
	private Style redStyle;
	private Style blueStyle;
	private PrintStream outPrintStream;
	private PrintStream errPrintStream;
	
	private PrintStream orjOutPrintStream;
	private PrintStream orjErrPrintStream;
	
	private static final int HATA_STR = -1;
	private static final int MESAJ_STR = 1;
	protected static final int NORMAL_STR = 0;
ImageIcon icon = null;
	public ConsoleFrame() {
		super();
		initialize();
	}

	private void initialize() {
		this.setSize(400,300);
		this.setContentPane(getJContentPane());
		this.setTitle("Konsol");
        this.setIconImage(icon.getImage());
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}

	private JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getJScrollPane1(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getConsolePanel(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setName("jScrollPane1");
			jScrollPane1.setViewportView(getConsoleTextPane());
		}
		return jScrollPane1;
	}
	
	private class ConsolePrintStream extends PrintStream {
	    int renkKodu;
	    ConsolePrintStream(OutputStream out, int renkKodu){
	        super(out);
	        this.renkKodu = renkKodu;
	    }
	    public void print(String x) {
	        konsolaYaz(x, renkKodu, false);
	    }

		public void println(String x) {
	        konsolaYaz(x, renkKodu, true);
	    }
		
	    public void print(Object obj) {
	        konsolaYaz(String.valueOf(obj), renkKodu, false);
	    }
		
		public void write(byte[] buf, int off, int len) {
	        konsolaYaz(new String(buf,off, len), renkKodu, false);
		}

		public void write(int b) {
	        konsolaYaz(String.valueOf((char) b), renkKodu, false);
		}
	}
	
	private PrintStream getOutPrintStream() {
		if(outPrintStream == null){
		    outPrintStream = new ConsolePrintStream(System.err, NORMAL_STR);
		}
		return outPrintStream;
    }

	private PrintStream getErrPrintStream() {
		if(errPrintStream == null){
		    errPrintStream = new ConsolePrintStream(System.out, HATA_STR);
		}
		return errPrintStream;
    }
	
	
	private JTextPane getConsoleTextPane() {
		if (consoleTextPane == null) {
			sc = new StyleContext();
			doc = new DefaultStyledDocument(sc);
		    redStyle = sc.addStyle(null, null);
		    StyleConstants.setForeground(redStyle, Color.RED);
		    blueStyle = sc.addStyle(null, null);
		    StyleConstants.setForeground(blueStyle, Color.BLUE);

			consoleTextPane = new JTextPane(doc);
			consoleTextPane.setEditable(true);
			consoleTextPane.setAutoscrolls(true);
			
			orjOutPrintStream = System.out;
			orjErrPrintStream = System.err;
			System.setOut(getOutPrintStream());
			System.setErr(getErrPrintStream());
		}
		return consoleTextPane;
	}

	private JPanel getConsolePanel() {
		if (consolePanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.fill = GridBagConstraints.NONE;
			gridBagConstraints2.weighty = 0.0;
			gridBagConstraints2.weightx = 0.0;
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.insets = new Insets(5, 5, 5, 5);
			consolePanel = new JPanel();
			consolePanel.setLayout(new GridBagLayout());
			consolePanel.add(getClearButton(), gridBagConstraints2);
			consolePanel.add(getScrollCheckBox(), gridBagConstraints1);
		}
		return consolePanel;
	}
	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton();
			clearButton.setText("Temizle");
			clearButton.setName("clearButton");
			clearButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					consoleTextPane.setText(null);
				}
			});
		}
		return clearButton;
	}

	private void konsolaYaz(String str, int renkKodu, boolean satirSonu){
		Style s = null;
		if(renkKodu == HATA_STR){
			s = redStyle;
		}
		else if(renkKodu == MESAJ_STR){
			s = blueStyle;
		}
		
		try {
			if(satirSonu) {
				doc.insertString(doc.getLength(), str + "\n" , s);
			}
			else {
				doc.insertString(doc.getLength(), str, s);
			}
			if(scrollCheckBox.isSelected()){
				consoleTextPane.setCaretPosition(doc.getLength());
			}
			_show();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void _show(){
	    if(!isVisible()){
			setVisible(true);		    
		}
	}

	
	private JCheckBox getScrollCheckBox() {
		if (scrollCheckBox == null) {
			scrollCheckBox = new JCheckBox();
			scrollCheckBox.setText("Takip et");
			scrollCheckBox.setSelected(true);
		}
		return scrollCheckBox;
	}

	public static ConsoleFrame getConsoleFrame(){
		if(consoleFrame == null) {
			consoleFrame = new ConsoleFrame();
			consoleFrame.setLocationRelativeTo(null);
			consoleFrame.setTitle("Konsol");
		}
		return consoleFrame;
	}
	
	public static ConsoleFrame showConsoleFrame(){
		getConsoleFrame();
	    if(!consoleFrame.isVisible()){
			consoleFrame.setVisible(true);		    
		}
	    return consoleFrame;
	}
	
	public static void closeConsoleFrame(){
		if(consoleFrame != null) {
			consoleFrame.dispose();
			System.setErr(consoleFrame.orjErrPrintStream);
			System.setOut(consoleFrame.orjOutPrintStream);
			consoleFrame = null;
		}
	}

	public static void mesajYaz(String str){
	    showConsoleFrame().konsolaYaz(str, MESAJ_STR, true);
	}

	public static void hataYaz(String str){
	    showConsoleFrame().konsolaYaz(str, HATA_STR, true);
	}

	public static void hataYaz(String str, boolean satirSonu){
	    showConsoleFrame().konsolaYaz(str, HATA_STR, satirSonu);
	}
	
	public static void normalYaz(String str){
	    showConsoleFrame().konsolaYaz(str, NORMAL_STR, false);
	}
	
	public static void normalYaz(String str, boolean satirSonu){
	    showConsoleFrame().konsolaYaz(str, NORMAL_STR, satirSonu);
	}
	
	public static PrintStream outPrintStream(){
	    return getConsoleFrame().getOutPrintStream();
	}
	
	public static PrintStream errPrintStream(){
	    return getConsoleFrame().getErrPrintStream();
	}

	public static void main(String[] args) {
	    showConsoleFrame();
    }
}
