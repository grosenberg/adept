/*******************************************************************************
 * Copyright (c) 2017, 2018 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.adept.model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

import net.certiv.adept.Tool;
import net.certiv.adept.format.TextEdit;
import net.certiv.adept.lang.Record;
import net.certiv.adept.tool.ErrorDesc;

public class Document {

	private DocModel model;
	private Record data;

	private String pathname;
	private int docId;
	private int tabWidth;
	private String content;

	private String modified;
	private List<TextEdit> edits = Collections.emptyList();

	public Document(String pathname, int tabWidth, String content) {
		this.pathname = pathname;
		this.docId = pathname.hashCode();
		this.tabWidth = tabWidth;
		this.content = content;
	}

	public DocModel getModel() {
		return model;
	}

	public void setModel(DocModel model) {
		this.model = model;
	}

	public Record getRecord() {
		return data;
	}

	public void setBuilder(Record data) {
		this.data = data;
	}

	public String getPathname() {
		return pathname;
	}

	public String getFilename() {
		try {
			return Paths.get(pathname).getFileName().toString();
		} catch (Exception e) {
			return pathname;
		}
	}

	/** Returns the id of this document */
	public int getDocId() {
		return docId;
	}

	public int getTabWidth() {
		return tabWidth;
	}

	public String getContent() {
		return content;
	}

	public List<TextEdit> getEdits() {
		return edits;
	}

	public void setEdits(List<TextEdit> list) {
		this.edits = list;
	}

	public TextEdit getEditLeft(int tokenIndex) {
		for (TextEdit edit : edits) {
			if (edit.endIndex() == tokenIndex) return edit;
		}
		return null;
	}

	public TextEdit getEditRight(int tokenIndex) {
		for (TextEdit edit : edits) {
			if (edit.begIndex() == tokenIndex) return edit;
		}
		return null;
	}

	public boolean isModified() {
		return modified != null && !modified.isEmpty();
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public boolean saveModified(Tool tool, boolean overwrite) {
		Path path = Paths.get(pathname);
		if (!overwrite) {
			Path backup = Paths.get(pathname + ".bak");
			try {
				Files.copy(path, backup, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				tool.toolError(this, ErrorDesc.CANNOT_WRITE_FILE, e, backup.toString());
				return false;
			}
		}

		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write(modified);
		} catch (IOException e) {
			tool.toolError(this, ErrorDesc.CANNOT_WRITE_FILE, e, path.toString());
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return String.format("Document %s", pathname);
	}
}
