package org.buildmlearn.toolkit.model;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by anupam on 29/2/16.
 */

/**
 * @brief Model used to hold api entries.
 */
public class SavedApi {

    private final File file;
    private final String date;
    private long unformattedDate;
    private String name;
    private String author;
    private String fullPath;

    public SavedApi(File file, String fileName, long date, String fullPath) {

        this.file = file;
        this.unformattedDate = date;
        this.date = formatDate(date);
        this.fullPath = fullPath;
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

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public File getFile() {
        return file;
    }

    public String getFullPath(){return fullPath;}

    public long getUnformattedDate(){return unformattedDate;}

}
