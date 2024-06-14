/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Filemerge {
    private static final Logger LOG = Logger.getLogger(Filemerge.class);

    private static final int FLUSH_DATA_SIZE = 50000;
    /* This is the location where merged file & log file will be generated */
    private static String baseDir = System.getProperty("out.dir") + File.separator;
    // private String baseDir = "D:\\tmp\\temp\\new" + File.separator; // temp for local testing only
    private String file_name_destination = "merge_" + getFormatedDate();
    private String csv_file_path_destination = baseDir + file_name_destination + ".csv";
    private String zip_file_path_destination = baseDir + file_name_destination + ".zip";
    // Key -> file name, value -> number of column in header for that
    private Map<String, Integer> FILENAME_TO_NO_OF_COLUMNS_IN_HEADER_MAP = new HashMap<String, Integer>();
    private int filecount = 1;
    private String baseDirpath;
    private String filenamepattern;

    private static String getFormatedDate() {
        return new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
    }

    public Filemerge(final String baseDirpath, final String filenamepattern) {
        this.baseDirpath = baseDirpath;
        this.filenamepattern = filenamepattern;
    }

    public void perform() {
        LOG.info("Starting ..");
        // k,v --> productid , FactFinderFileData
        StringBuffer data = new StringBuffer();
        final Multimap<String, FileData> multiValueMap = ArrayListMultimap.create();
        final List<String> fileNames = new ArrayList<String>();
        final File[] files = getListofFilesToMerge(baseDirpath, filenamepattern);
        for (File file : files) {
            try {
                LOG.info("Processing file " + file.getAbsolutePath());
                final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                LOG.info("Last Modified date of file is  : " + sdf.format(file.lastModified()));
                LOG.info("Size of file is " + (float) file.length() / (1024 * 1024) + " MB");
                fileNames.add(file.getName());
                readZipFilesV1(file, multiValueMap);
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }

        int count = 0;

        for (String productid : multiValueMap.keySet()) {
            final Collection<FileData> ffDatarows = multiValueMap.get(productid);
            if (ffDatarows.size() == files.length) {
                data.append("\n\"" + productid + "\";");
                for (Iterator<FileData> iterator = ffDatarows.iterator(); iterator.hasNext();) {
                    FileData fileData = iterator.next();
                    data.append(fileData.getRowData() + ";");
                }
                data.deleteCharAt(data.lastIndexOf(";"));
            }
            // Rule: we don't Merge line which is not matching in all files
            else {
                l1: for (String filename : fileNames) {
                    for (FileData ffDatarow : ffDatarows) {
                        if (filename.equals(ffDatarow.getFileName())) {
                            // data.append(ffDatarow.getRowData() + ";");
                            continue l1;
                        }
                    }
                    LOG.info(String.format("ProductId = %s not found in file %s ", productid, filename));
                    // data.append(generateEmptyValues(FILENAME_TO_NO_OF_COLUMNS_IN_HEADER_MAP.get(filename)) + ";");
                }
            }
            if ((++count % FLUSH_DATA_SIZE) == 0 || (count == multiValueMap.keySet().size())) {
                writetofile(csv_file_path_destination, data.toString());
                data = new StringBuffer();
            }

        }

        compressFileAndCleanTargetFile();
        LOG.info("Quiting ..");
    }

    private File[] getListofFilesToMerge(final String baseDirpath, final String filenamePattern) {
        List<File> aList = new ArrayList<File>();
        try {
            // array of files and directory
            String[] paths = new File(baseDirpath).list();
            // for each name in the path array
            for (String path : paths) {
                LOG.debug("going to look file at path = " + path);
                if (path.matches(filenamePattern)) {
                    LOG.debug("file matched with pattern " + filenamePattern);
                    aList.add(new File(baseDirpath + File.separator + path));
                }
            }
        } catch (Exception e) {
            // if any error occurs
            LOG.warn("Exception occurred while getting files to merge", e);
        }

        LOG.info("Going to process following Files. No of files = " + aList.size());
        for (File aFile : aList) {
            LOG.info(aFile.getAbsolutePath());
        }

        return aList.toArray(new File[aList.size()]);
    }

    private void compressFileAndCleanTargetFile() {
        byte[] buffer = new byte[1024];

        try {
            final FileOutputStream fos = new FileOutputStream(zip_file_path_destination);
            final ZipOutputStream zos = new ZipOutputStream(fos);
            final ZipEntry ze = new ZipEntry(file_name_destination);
            zos.putNextEntry(ze);
            final FileInputStream in = new FileInputStream(csv_file_path_destination);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            zos.closeEntry();
            zos.close();
            LOG.info("Done check file at location " + zip_file_path_destination);
            final File file = new File(csv_file_path_destination);
            if (file.exists())
                file.delete();

        } catch (IOException ex) {
            LOG.error("Error occured while compressing file\n" + ex.getMessage());
        }
    }

    private void writetofile(final String destfilepath, final String content) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(destfilepath), true), "UTF8"));
            out.append(content);
            out.flush();
            out.close();
        } catch (IOException ex) {
            LOG.error("Error occured while wring in file\n" + ex.getMessage());
        }
    }

    /**
     * Input to this function is a path of zipped file It returns multi valued hasmap key --> filename value --> each line of the file
     * 
     * @param zipFilePath
     * @param multiValueMap
     */
    private void readZipFilesV1(final File zipFilePath, final Multimap<String, FileData> multiValueMap) {
        try {
            final ZipFile zipfile = new ZipFile(zipFilePath);
            final Enumeration entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry zipentry = (ZipEntry) entries.nextElement();
                LOG.info("Reading inside zip file filename ... " + zipentry.getName());
                final long size = zipentry.getSize();
                if (size > 0) {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(zipfile.getInputStream(zipentry)));
                    String line = reader.readLine(); // Skip header of the file usually 1st row
                    generateHeader(zipFilePath.getName(), line);
                    while ((line = reader.readLine()) != null) {
                        final String[] split = split(line);
                        // System.out.println(line);
                        multiValueMap.put(split[0], new FactFinderFileData(zipFilePath.getName(), split[1]));
                        // break;
                    }
                    reader.close();
                }
            }
            zipfile.close();
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        }

        LOG.info(String.format("Number of products found inside file %s is %s ", zipFilePath.getName(), multiValueMap.keySet().size()));
    }

    /**
     * Generate header also update map with number of column in header
     * 
     * @param filename
     * @param line
     */
    private void generateHeader(final String filename, final String line) {
        if (filecount++ == 1) {
            writetofile(csv_file_path_destination, line);
            FILENAME_TO_NO_OF_COLUMNS_IN_HEADER_MAP.put(filename, getColumnSize(line));
        } else {
            final String[] split = split(line);
            writetofile(csv_file_path_destination, ";" + split[1]);
            FILENAME_TO_NO_OF_COLUMNS_IN_HEADER_MAP.put(filename, getColumnSize(split[1]));
        }
    }

    @SuppressWarnings("unused")
    private String generateEmptyValues(final int times) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < times; i++)
            sb.append("\"\";");
        sb.deleteCharAt(sb.lastIndexOf(";"));
        return sb.toString();
    }

    private int getColumnSize(final String line) {
        return line.length() - line.replace(";", "").length();
    }

    interface FileData {
        public String getFileName();

        public String getRowData();
    }

    private class FactFinderFileData implements FileData {
        private String fileName;
        private String value;

        @Override
        public String getFileName() {
            return fileName;
        }

        @Override
        public String getRowData() {
            return value;
        }

        public FactFinderFileData(String fileName, String value) {
            super();
            this.fileName = fileName;
            this.value = value;
        }

    }

    private String[] split(final String inputText) {
        final String str = inputText.substring(1);
        final String number = str.substring(0, str.indexOf("\""));
        final String body = str.substring(str.indexOf(number) + number.length() + 2);
        final String result[] = { number, body };
        return result;
    }

    public static void main(String[] args) {
        new Filemerge("D:\\tmp\\temp\\new", "[a-zA-Z_0-9.]*zip").perform();
    }
}
