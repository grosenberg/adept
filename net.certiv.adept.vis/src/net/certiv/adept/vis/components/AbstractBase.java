package net.certiv.adept.vis.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import net.certiv.adept.util.Log;

public abstract class AbstractBase {

	protected static final FileFilter pngFilter = new FileNameExtensionFilter("PNG Files", "png");
	protected static final String ext = ".png";
	protected static final String Eol = System.lineSeparator();

	protected static final Dimension minDim = new Dimension(100, 100);

	protected static final Comparator<Number> NumComp = new Comparator<Number>() {

		@Override
		public int compare(Number o1, Number o2) {
			if (o1.doubleValue() < o2.doubleValue()) return -1;
			if (o1.doubleValue() > o2.doubleValue()) return 1;
			return 0;
		}
	};

	protected static final String KEY_WIDTH = "frame_width";
	protected static final String KEY_HEIGHT = "frame_height";
	protected static final String KEY_X = "frame_x";
	protected static final String KEY_Y = "frame_y";

	protected Action openAction;
	protected Action pngAction;

	protected JFrame frame;
	protected Container content;
	protected Preferences prefs;
	protected String qual;

	public AbstractBase(String title, String icon) {
		frame = new JFrame(title);
		qual = this.getClass().getName() + ".";
		ImageIcon imgicon = new ImageIcon(this.getClass().getClassLoader().getResource(icon));
		frame.setIconImage(imgicon.getImage());

		content = frame.getContentPane();
		content.setLayout(new BorderLayout(0, 0));
		prefs = Preferences.userRoot().node(nodeName(this.getClass()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				Rectangle b = frame.getBounds();
				prefs.putInt(qual + KEY_X, b.x);
				prefs.putInt(qual + KEY_Y, b.y);
				prefs.putInt(qual + KEY_WIDTH, b.width);
				prefs.putInt(qual + KEY_HEIGHT, b.height);
				saveWindowClosingPrefs(prefs);
			}
		});
	}

	protected JPanel createPanel(String title) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		if (title != null) {
			panel.setBorder(createTitledBorder(title));
		}
		return panel;
	}

	protected JSplitPane createSplitPane(double weight) {
		JSplitPane pane = new JSplitPane();
		pane.setResizeWeight(weight);
		return pane;
	}

	protected JScrollPane createScrollPane(JComponent comp) {
		JScrollPane scrollPane = new JScrollPane(comp);
		scrollPane.setMinimumSize(minDim);
		return scrollPane;
	}

	protected JTable createTable() {
		JTable table = new JTable();
		table.setBorder(new LineBorder(new Color(0, 0, 0)));
		table.setFillsViewportHeight(true);
		return table;
	}

	protected Border createTitledBorder(String title) {
		return new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), title, TitledBorder.LEADING,
				TitledBorder.TOP, null, null);
	}

	private String nodeName(Class<? extends AbstractBase> c) {
		if (c.isArray()) throw new IllegalArgumentException("Arrays have no associated preferences node.");
		String className = c.getName();
		if (className.lastIndexOf('.') < 0) return "/<unnamed>";
		return "/" + className.replace('.', '/');
	}

	protected void saveWindowClosingPrefs(Preferences prefs) {}

	protected void handleFileOpen(File file) {}

	/** Base file name for png name to save proposals */
	protected String getBaseName() {
		return "";
	}

	protected void setLocation() {
		int x = prefs.getInt(qual + KEY_X, 100);
		int y = prefs.getInt(qual + KEY_Y, 100);
		int width = prefs.getInt(qual + KEY_WIDTH, 600);
		int height = prefs.getInt(qual + KEY_HEIGHT, 400);
		frame.setBounds(x, y, width, height);
	}

	protected Action getOpenAction() {
		if (openAction == null) {
			openAction = new OpenAction();
		}
		return openAction;
	}

	protected Action getPngAction(JComponent comp) {
		if (pngAction == null) {
			pngAction = new PngAction(comp);
		} else {
			((PngAction) pngAction).setViewer(comp);
		}
		return pngAction;
	}

	class OpenAction extends AbstractAction {

		public OpenAction() {
			super("Open", new ImageIcon("resources/open.png"));
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			JFileChooser chooser = new JFileChooser();
			Path path = Paths.get("../net.certiv.adept.test/test.snippets");
			chooser.setCurrentDirectory(path.toFile());

			if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;
			File file = chooser.getSelectedFile();
			if (file == null || !file.isFile()) return;

			handleFileOpen(file);
		}
	}

	class PngAction extends AbstractAction {

		private JComponent comp;

		public PngAction(JComponent comp) {
			super("Save png", new ImageIcon("resources/png.png"));
			this.comp = comp;
		}

		public void setViewer(JComponent comp) {
			this.comp = comp;
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			JFileChooser chooser = new JFileChooser();
			Path path = Paths.get("doc");
			if (!path.toFile().exists()) {
				try {
					Files.createDirectory(path);
				} catch (IOException e) {}
			}

			chooser.setSelectedFile(proposeName(path));
			chooser.setCurrentDirectory(path.toFile());
			chooser.addChoosableFileFilter(pngFilter);
			chooser.setFileFilter(pngFilter);

			if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) return;
			File file = chooser.getSelectedFile();

			generatePng(comp, file);
		}
	}

	private File proposeName(Path path) {
		String name = getBaseName();
		int cnt = 1;
		File png = new File(path.toString(), name + ext);
		while (png.exists()) {
			png = new File(path.toString(), String.format("%s%03d%s", name, cnt, ext));
			cnt++;
		}
		return png;
	}

	private void generatePng(JComponent comp, File png) {
		BufferedImage bi = new BufferedImage(comp.getSize().width, comp.getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		comp.paint(g);
		g.dispose();

		try {
			ImageIO.write(bi, "png", png);
		} catch (IOException e) {
			Log.error(this, "Write failed to " + png.getPath());
		}

		try {
			Desktop.getDesktop().open(png);
		} catch (Exception ex) {
			Log.error(this, "Failed to open " + png.getPath());
		}
	}

	protected class GraphMouseAdaptor<V> implements GraphMouseListener<V> {

		public GraphMouseAdaptor() {}

		public void graphClicked(V v, MouseEvent e) {}

		public void graphPressed(V v, MouseEvent e) {}

		public void graphReleased(V v, MouseEvent e) {}
	}
}
