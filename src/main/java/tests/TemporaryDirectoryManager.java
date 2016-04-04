package tests;

import java.io.File;

/**
 * Created by sambaumgarten on 4/3/16
 */
public class TemporaryDirectoryManager {
    public static void createTempDir() {
        File file = new File("tmp");
        file.mkdir();
    }

    public static void deleteTempDir() {
        File file = new File("tmp");
        deleteDir(file);
    }

    // http://stackoverflow.com/questions/3775694/deleting-folder-from-java
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) return false;
            }
        }
        // The directory is now empty or this is a file so delete it
        return dir.delete();
    }
}
