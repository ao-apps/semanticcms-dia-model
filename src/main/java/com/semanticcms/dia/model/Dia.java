/*
 * semanticcms-dia-model - Java API for embedding Dia-based diagrams in web pages.
 * Copyright (C) 2013, 2014, 2015, 2016, 2017, 2020  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of semanticcms-dia-model.
 *
 * semanticcms-dia-model is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * semanticcms-dia-model is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with semanticcms-dia-model.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.semanticcms.dia.model;

import static com.aoindustries.lang.Strings.nullIfEmpty;
import com.semanticcms.core.model.Element;

public class Dia extends Element {

	public static final String EXTENSION = "dia";
	public static final String DOT_EXTENSION = "." + EXTENSION;

	private volatile String label;
	private volatile String book;
	private volatile String path;
	private volatile int width;
	private volatile int height;

	/**
	 * If not set, defaults to the last path segment of path, with any ".dia" extension stripped.
	 */
	@Override
	public String getLabel() {
		String l = label;
		if(l != null) return l;
		String p = path;
		if(p != null) {
			String filename = p.substring(p.lastIndexOf('/') + 1);
			if(filename.endsWith(DOT_EXTENSION)) filename = filename.substring(0, filename.length() - DOT_EXTENSION.length());
			if(filename.isEmpty()) throw new IllegalArgumentException("Invalid filename for diagram: " + p);
			return filename;
		}
		throw new IllegalStateException("Cannot get label, neither label nor path set");
	}

	public void setLabel(String label) {
		checkNotFrozen();
		this.label = nullIfEmpty(label);
	}

	public String getBook() {
		return book;
	}

	public void setBook(String book) {
		checkNotFrozen();
		this.book = nullIfEmpty(book);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		checkNotFrozen();
		this.path = nullIfEmpty(path);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		checkNotFrozen();
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		checkNotFrozen();
		this.height = height;
	}

	@Override
	protected String getDefaultIdPrefix() {
		return "dia";
	}
}
