package org.buildmlearn.toolkit.model;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by anupam on 29/2/16.
 */
public class SavedApi {

    private File file;

    private String name;
    private String date;
    private String type;
    private String author;
    private String fullPath;

    public SavedApi(File file, String fileName, long date, String fullPath) {

        this.file = file;
        this.fullPath = fullPath;
        this.date = formatDate(date);
        this.type = type;
        String[] data = fileName.split("-by-");
        try {
            this.name = data[0];
            this.author = data[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            this.name = fileName;
            this.author = "Unknown";
        }
        this.name = this.name.replaceAll("-", " ");
        this.author = this.author.replaceAll("-", " ");
        try {
            this.author = this.author.substring(0, this.author.indexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            /* Do nothing, special case when file is without .buildmlearn extension*/
        }
    }

    private String formatDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");
        return sdf.format(time);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = formatDate(date);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFullPath() {
        return fullPath;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
