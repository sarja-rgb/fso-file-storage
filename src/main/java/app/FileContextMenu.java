package app;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 * FileContextMenu adds right-click popup functionality to the JTable. It
 * provides options to upload, delete, and refresh files.
 */
public class FileContextMenu {
	private JTable fileTable;
	private FileManager fileManager;

	public FileContextMenu(JTable fileTable, FileManager fileManager) {
		this.fileTable = fileTable;
		this.fileManager = fileManager;
	}

	public void init() {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem uploadItem = new JMenuItem("Upload File to Selected Folder");
		JMenuItem deleteItem = new JMenuItem("Delete Selected");
		JMenuItem refreshItem = new JMenuItem("Refresh");

		uploadItem.addActionListener(e -> fileManager.uploadFileToSelectedFolder());
		deleteItem.addActionListener(e -> fileManager.deleteSelectedFile(fileManager.getSelectedFilePath()));
		refreshItem.addActionListener(e -> fileManager.listFiles());

		popupMenu.add(uploadItem);
		popupMenu.add(deleteItem);
		popupMenu.addSeparator();
		popupMenu.add(refreshItem);

		fileTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					int row = fileTable.rowAtPoint(e.getPoint());
					if (row != -1) {
						fileTable.setRowSelectionInterval(row, row);
					}
					popupMenu.show(fileTable, e.getX(), e.getY());
				}
			}
		});
	}
}
