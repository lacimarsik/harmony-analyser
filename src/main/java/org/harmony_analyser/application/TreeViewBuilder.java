package org.harmony_analyser.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

/**
 * Build custom TreeView to select files and folders
 */

public class TreeViewBuilder {
	public static Image folderCollapseImage = new Image(ClassLoader.getSystemResourceAsStream("folder.png"));
	public static Image fileImage = new Image(ClassLoader.getSystemResourceAsStream("file.png"));
	public static Image computerImage = new Image(ClassLoader.getSystemResourceAsStream("computer.png"));

	public static TreeView buildFileSystemBrowser() {
		TreeItem<File> root = createNode(new File("/"));
		root.setGraphic(new ImageView(computerImage));
		return new TreeView<File>(root);
	}

	// This method creates a TreeItem to represent the given File. It does this
	// by overriding the TreeItem.getChildren() and TreeItem.isLeaf() methods
	// anonymously, but this could be better abstracted by creating a
	// 'FileTreeItem' subclass of TreeItem. However, this is left as an exercise
	// for the reader.
	public static TreeItem<File> createNode(final File f) {
		return new TreeItem<File>(f) {
			// We cache whether the File is a leaf or not. A File is a leaf if
			// it is not a directory and does not have any files contained within
			// it. We cache this as isLeaf() is called often, and doing the
			// actual check on File is expensive.
			private boolean isLeaf;

			// We do the children and leaf testing only once, and then set these
			// booleans to false so that we do not check again during this
			// run. A more complete implementation may need to handle more
			// dynamic file system situations (such as where a folder has files
			// added after the TreeView is shown). Again, this is left as an
			// exercise for the reader.
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override public ObservableList<TreeItem<File>> getChildren() {
				if (isFirstTimeChildren) {
					isFirstTimeChildren = false;

					// First getChildren() call, so we actually go off and
					// determine the children of the File contained in this TreeItem.
					super.getChildren().setAll(buildChildren(this));
				}
				return super.getChildren();
			}

			@Override public boolean isLeaf() {
				if (isFirstTimeLeaf) {
					isFirstTimeLeaf = false;
					File f = getValue();
					isLeaf = f.isFile();
				}

				return isLeaf;
			}

			private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
				File f = TreeItem.getValue();
				if (f != null && f.isDirectory()) {
					File[] files = f.listFiles();
					if (files != null) {
						ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();

						for (File childFile : files) {
							TreeItem item = createNode(childFile);
							if (childFile.isDirectory()){
								item.setGraphic(new ImageView(folderCollapseImage));
							} else {
								item.setGraphic(new ImageView(fileImage));
							}
							children.add(item);
						}

						return children;
					}
				}

				return FXCollections.emptyObservableList();
			}
		};
	}
}
