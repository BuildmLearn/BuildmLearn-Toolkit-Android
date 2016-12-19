package org.buildmlearn.toolkit.model;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * @brief Model class for holding the details of a Saved File
 * <p/>
 * Created by Abhishek on 01-06-2015.
 */
public class SavedProject {

    private final String fullPath;
    private File file;
    private String name;
    private String date;
    private String time;
    private String type;
    private String author;
    private long unformattedDate;
    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public SavedProject(File file, String fileName, long date, String type, String fullPath) {

        this.file = file;
        this.fullPath = fullPath;
        this.date = formatDate(date);
        this.time = formatTime(date);
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

    private String formatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
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

    public String getTime() {
        return time;
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

    public long getUnformattedDate() {
        return unformattedDate;
    }
}
