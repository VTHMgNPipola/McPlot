/*
 * McPlot - a reliable, powerful, lightweight and free graphing calculator
 * Copyright (C) 2021  VTHMgNPipola
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vthmgnpipola.mcplot.ngui;

import java.util.StringJoiner;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooserExtension {
    private final String defaultExtension;
    private final String[] extensions;
    private final FileNameExtensionFilter filter;

    public FileChooserExtension(String description, String defaultExtension, String... extensions) {
        this.defaultExtension = defaultExtension;
        this.extensions = extensions;

        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        for (String s : extensions) {
            joiner.add("." + s);
        }

        filter = new FileNameExtensionFilter(description.trim() + " " + joiner, extensions);
    }

    public String getPathWithExtension(String path) {
        for (String extension : extensions) {
            if (path.endsWith("." + extension)) {
                return path;
            }
        }

        return path + "." + defaultExtension;
    }

    public String getFileType(String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }

    public FileNameExtensionFilter getFilter() {
        return filter;
    }
}
