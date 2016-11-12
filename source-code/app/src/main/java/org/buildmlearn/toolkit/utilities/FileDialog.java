package org.buildmlearn.toolkit.utilities;

import android.app.Activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import org.buildmlearn.toolkit.ToolkitApplication;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @brief Programmed dialog box to select file from storage.
 * <p/>
 * Created by Anupam (opticod) on 30/5/16.
 */
public class FileDialog {

    private static final String PARENT_DIR = "..";
    private final Activity activity;
    private final ListenerList<FileSelectListener> fileListenerList = new ListenerList<>();
    private String[] fileList;
    private File currentPath;
    private String fileEndsWith;

    public FileDialog(Activity activity) {
        this.activity = activity;
        loadFileList(new ToolkitApplication().getDir());
    }

    private AlertDialog createFileDialog() {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(currentPath.getPath());
        builder.setItems(fileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String fileChosen = fileList[which];
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) {
                    loadFileList(chosenFile);
                    dialog.cancel();
                    dialog.dismiss();
                    showDialog();
                } else fireFileSelectedEvent(chosenFile);
            }
        });

        dialog = builder.show();
        return dialog;
    }

    public void addFileListener(FileSelectListener listener) {
        fileListenerList.add(listener);
    }

    public void showDialog() {
        createFileDialog().show();
    }

    private void fireFileSelectedEvent(final File file) {
        fileListenerList.fireEvent(new ListenerList.FireHandler<FileSelectListener>() {
            public void fireEvent(FileSelectListener listener) {
                listener.fileSelected(file);
            }
        });
    }

    private void loadFileList(File path) {
        this.currentPath = path;
        List<String> r = new ArrayList<>();
        if (path.exists()) {
            if (path.getParentFile() != null) r.add(PARENT_DIR);
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    if (!sel.canRead()) return false;
                    else {
                        boolean endsWith = fileEndsWith == null || filename.toLowerCase().endsWith(fileEndsWith);
                        return endsWith || sel.isDirectory();
                    }
                }
            };
            String[] fileList1 = path.list(filter);
            if (fileList1 == null) {
                return;
            }
            Collections.addAll(r, fileList1);
        }
        fileList = r.toArray(new String[r.size()]);
    }

    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) return currentPath.getParentFile();
        else return new File(currentPath, fileChosen);
    }

    public void setFileEndsWith() {
        this.fileEndsWith = ".txt";
    }

    public interface FileSelectListener {
        void fileSelected(File file);
    }
}

class ListenerList<L> {
    private final List<L> listenerList = new ArrayList<>();

    public void add(L listener) {
        listenerList.add(listener);
    }

    public void fireEvent(FireHandler<L> fireHandler) {
        List<L> copy = new ArrayList<>(listenerList);
        for (L l : copy) {
            fireHandler.fireEvent(l);
        }
    }

    public interface FireHandler<L> {
        void fireEvent(L listener);
    }
}
