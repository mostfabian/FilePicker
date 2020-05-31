package ir.bankecode.easyfilepicker.utils;

import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FileUtils {

    public static List<File> getFileListByDirPath(String path, FileFilter filter, boolean hideEmptyDirs) {
        File directory = new File(path);
        File[] files = directory.listFiles(filter);
        if (files == null) {
            return new ArrayList<>();
        }
        List<File> result = new ArrayList<>();
        for (File file : files) {
            if (hideEmptyDirs) {
                if (!file.isDirectory()) result.add(file);
                else if (dirSize(file, filter) > 0) result.add(file);
            } else {
                result.add(file);
            }
        }
        Collections.sort(result, new FileComparator());
        return result;
    }

    public static String cutLastSegmentOfPath(String path) {
        if (path.length() - path.replace("/", "").length() <= 1) {
            return "/";
        }
        String newPath = path.substring(0, path.lastIndexOf("/"));
        // We don't need to list the content of /storage/emulated
        if (newPath.equals("/storage/emulated")) {
            newPath = "/storage";
        }
        return newPath;
    }

    public static String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private static long dirSize(File dir, FileFilter filter) {
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles(filter);
            if (fileList != null) {
                for(int i = 0; i < fileList.length; i++) {
                    // Recursive call if it's a directory
                    if(fileList[i].isDirectory()) {
                        result += dirSize(fileList[i], filter);
                    } else {
                        // Sum the file size in bytes
                        result += fileList[i].length();
                    }
                }
            }
            return result;
        }
        return 0;
    }
}
