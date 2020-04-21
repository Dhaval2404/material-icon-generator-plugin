package com.github.dhaval2404.material_icon_generator.util;


import com.intellij.openapi.util.io.FileUtilRt;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * File Utility
 * <p>
 * Created by Dhaval Patel on 24 January 2019.
 */
public class FileUtil {

    /**
     * Get Temporary File Directory
     *
     * @return Temp directory path
     * @throws IOException if failed to create directory
     */
    public static File getRootDir() throws IOException {
        return FileUtilRt.createTempDirectory("material_icon_generator", "");
    }

    /**
     * Download remote file
     *
     * @param rootDir Directory where file will be downloaded
     * @param url String URL
     * @return Zip File
     */
    public static File downloadFile(File rootDir, String url) throws IOException {
        File file = new File(rootDir, FilenameUtils.getName(url));
        FileUtils.copyURLToFile(new URL(url), file);
        return file;
    }

    /**
     * Download icon zip, extract and return icon directory path
     *
     * @param iconUrl Icon URL
     * @return File icon directory path
     * @throws IOException if failed to download image or extract icons
     */
    public static File getIconDirectory(String iconUrl) throws IOException {
        //Get Root Dir
        File rootDir = getRootDir();
        System.out.println("Root Dir: "+rootDir.getAbsolutePath());

        //Get Icon Zip
        File iconZip = downloadFile(rootDir, iconUrl);
        System.out.println("Zip File: "+iconZip.getAbsolutePath());

        //Extract Zip file
        new ZipFile(iconZip).extractAll(rootDir.getPath());

        //Delete zip file. Will only need extracted images
        iconZip.delete();

        //There is extra res folder in zip
        return rootDir;
    }

}
