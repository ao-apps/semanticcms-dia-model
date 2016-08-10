/*
 * semanticcms-dia-model - Java API for embedding Dia-based diagrams in web pages.
 * Copyright (C) 2013, 2014, 2015, 2016  AO Industries, Inc.
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

import java.io.File;

/**
 * Exports images from Dia and caches results for performance.
 *
 * TODO: Make Dia a new element type.
 * TODO: Find way to register a view dynamically on stock docs screens?
 */
final public class DiaExport {

	public static final String EXTENSION = "dia";
	public static final String DOT_EXTENSION = "." + EXTENSION;

	private final File tmpFile;
	private final int width;
	private final int height;

	// TODO: Make more restrictive once in same package that uses it
	public DiaExport(
		File tmpFile,
		int width,
		int height
	) {
		this.tmpFile = tmpFile;
		this.width = width;
		this.height = height;
	}

	public File getTmpFile() {
		return tmpFile;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
