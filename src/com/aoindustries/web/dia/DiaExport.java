/*
 * ao-web-dia - Java API for embedding Dia-based diagrams in web pages.
 * Copyright (C) 2013, 2014, 2015, 2016  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-web-dia.
 *
 * ao-web-dia is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-web-dia is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-web-dia.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.web.dia;

import com.aoindustries.awt.image.ImageSizeCache;
import com.aoindustries.io.FileUtils;
import com.aoindustries.lang.ProcessResult;
import com.aoindustries.web.page.PageRef;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

/**
 * Exports images from Dia and caches results for performance.
 */
final public class DiaExport {

	private static final String LINUX_DIA_PATH = "/usr/bin/dia";

	private static final String WINDOWS_DIA_PATH = "C:\\Program Files (x86)\\Dia\\bin\\dia.exe";

	private static final String WINDOWS_DIAW_PATH = "C:\\Program Files (x86)\\Dia\\bin\\diaw.exe";

	public static final String DIA_EXTENSION = ".dia";

	private static final String TEMP_SUBDIR = DiaExport.class.getName();

	public static boolean isWindows() {
		String osName = System.getProperty("os.name");
		return osName!=null && osName.toLowerCase(Locale.ROOT).contains("windows");
	}

	private static String getDiaExportPath() {
		if(isWindows()) {
			return WINDOWS_DIA_PATH;
		} else {
			return LINUX_DIA_PATH;
		}
	}

	public static String getDiaOpenPath() {
		if(isWindows()) {
			return WINDOWS_DIAW_PATH;
		} else {
			return LINUX_DIA_PATH;
		}
	}

	public static DiaExport exportDiagram(
		PageRef pageRef,
		Integer width,
		Integer height,
		File tmpDir
	) throws FileNotFoundException, IOException {
		File diaFile = pageRef.getResourceFile(true, true);

		String diaPath = pageRef.getPath();
		// Strip extension if matches expected value
		if(diaPath.toLowerCase(Locale.ROOT).endsWith(DIA_EXTENSION)) {
			diaPath = diaPath.substring(0, diaPath.length() - DIA_EXTENSION.length());
		}
		// Generate the temp filename
		File tmpFile = new File(
			tmpDir,
			TEMP_SUBDIR
				+ pageRef.getBookPrefix().replace('/', File.separatorChar)
				+ diaPath.replace('/', File.separatorChar)
				+ "-"
				+ (width==null ? "_" : width.toString())
				+ "x"
				+ (height==null ? "_" : height.toString())
				+ ".png"
		);
		// Make temp directory if needed (and all parents)
		tmpDir = tmpFile.getParentFile();
		if(!tmpDir.exists()) FileUtils.mkdirs(tmpDir);
		// Re-export when missing or timestamps indicate needs recreated
		if(!tmpFile.exists() || diaFile.lastModified() >= tmpFile.lastModified()) {
			// Determine size for scaling
			final String sizeParam;
			if(width==null) {
				if(height==null) {
					sizeParam = null;
				} else {
					sizeParam = "x" + height;
				}
			} else {
				if(height==null) {
					sizeParam = width + "x";
				} else {
					sizeParam = width + "x" + height;
				}
			}
			// Build the command
			final String diaExePath = getDiaExportPath();
			final String[] command;
			if(sizeParam == null) {
				command = new String[] {
					diaExePath,
					"--export=" + tmpFile.getCanonicalPath(),
					"--filter=png",
					"--log-to-stderr",
					diaFile.getCanonicalPath()
				};
			} else {
				command = new String[] {
					diaExePath,
					"--export=" + tmpFile.getCanonicalPath(),
					"--filter=png",
					"--size=" + sizeParam,
					"--log-to-stderr",
					diaFile.getCanonicalPath()
				};
			}
			// Export using dia
			ProcessResult result = ProcessResult.exec(command);
			int exitVal = result.getExitVal();
			if(exitVal != 0) throw new IOException(diaExePath + ": non-zero exit value: " + exitVal);
			if(!isWindows()) {
				// Dia does not set non-zero exit value, instead, it writes both errors and normal output to stderr
				// (Dia version 0.97.2, compiled 23:51:04 Apr 13 2012)
				String normalOutput = diaFile.getCanonicalPath() + " --> " + tmpFile.getCanonicalPath();
				// Read the standard error, if any one line matches the expected line, then it is OK
				// other lines include stuff like: Xlib:  extension "RANDR" missing on display ":0".
				boolean foundNormalOutput = false;
				String stderr = result.getStderr();
				try (BufferedReader errIn = new BufferedReader(new StringReader(stderr))) {
					String line;
					while((line = errIn.readLine())!=null) {
						if(line.equals(normalOutput)) {
							foundNormalOutput = true;
							break;
						}
					}
				}
				if(!foundNormalOutput) {
					throw new IOException(diaExePath + ": " + stderr);
				}
			}
		}
		// Get actual dimensions
		Dimension pngSize = ImageSizeCache.getImageSize(tmpFile);
		
		return new DiaExport(
			tmpFile,
			pngSize.width,
			pngSize.height
		);
	}

	private final File tmpFile;
	private final int width;
	private final int height;

	private DiaExport(
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
