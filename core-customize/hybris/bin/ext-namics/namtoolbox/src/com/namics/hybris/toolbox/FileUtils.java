package com.namics.hybris.toolbox;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.util.StringUtils;

public class FileUtils {
    /**
     * Gibt den Pfad zum temporären directory zurück.
     */
    public static String getTemporaryDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Zieht aus einem Pfad bestehenden aus Directory + Filename das Directory heraus, wobei am Schluss kein Slash steht. Windows-Slashs (\)
     * werden in UNIX-Slashs (/) umgewandelt.
     */
    public static String getDirectoryFromPath(final String originalPath) {
        // Pfad in 'directory' und 'filename' aufteilen
        final String path = originalPath.replace('\\', '/');
        final int positionOfLastSlash = path.lastIndexOf('/');
        return (positionOfLastSlash > 1) ? path.substring(0, positionOfLastSlash) : "";
    }

    /**
     * Zieht aus einem Pfad bestehenden aus Directory + Filename den Filename heraus. Windows-Slashs (\) werden in UNIX-Slashs (/)
     * umgewandelt.
     */
    public static String getFileNameFromPath(final String originalPath) {
        // Pfad in 'directory' und 'filename' aufteilen
        final String path = originalPath.replace('\\', '/');
        final int positionOfLastSlash = path.lastIndexOf('/');
        return (positionOfLastSlash > 1) ? path.substring(positionOfLastSlash + 1) : path;
    }

    /**
     * Führt zwei Strings Directory + Filename zusammen, wobei gewährleistet wird, dass zwischen dem Directory und dem Filename ein und nur
     * ein Slash steht. Windows-Slashs (\) werden in UNIX-Slashs (/) umgewandelt.
     */
    public static String concatDirectoryAndFilename(String directory, String filename) {
        directory = directory.replace('\\', '/');
        filename = filename.replace('\\', '/');

        if (filename.charAt(0) == '/') {
            filename = filename.substring(1);
        }

        if (directory != null && StringUtils.hasText(directory) && !(directory.charAt(directory.length() - 1) == '/')) {
            return directory + "/" + filename;
        } else {
            return directory + filename;
        }

    }

    /**
     * Führt zwei Directories, wobei gewährleistet wird, dass zwischen dem Directory und dem Filename ein und nur ein Slash steht.
     * Windows-Slashs (\) werden in UNIX-Slashs (/) umgewandelt.
     */
    public static String concatDirectories(String directory1, String directory2) {
        directory1 = FileUtils.getNormalizedFilepath(directory1);
        directory2 = FileUtils.getNormalizedFilepath(directory2);

        if (directory1.endsWith("/")) {
            directory1 = directory1.substring(0, directory1.length() - 1);
        }

        if (StringUtils.hasText(directory1) && directory2.charAt(0) == '/') {
            directory2 = directory2.substring(1);
        }

        return directory1 + "/" + directory2;
    }

    /**
     * Gibt die Erweiterung der Datei zurück, wie z.B. "gif". Die Erweiterung wird jeweils ohne vorangehender Punkt zurückgegeben.
     */
    public static String getFileExtension(final String filepath) {
        String extension = filepath;
        if (extension.lastIndexOf('.') != -1) {
            extension = extension.substring(extension.lastIndexOf('.') + 1);
        }
        return extension;
    }

    /**
     * Gibt den Dateinamen ohne Pfad und Erweiterung zurück.
     */
    public static String getFilenameWithoutExtension(final String filepath) {
        String filename = getFileNameFromPath(filepath);
        if (filename.lastIndexOf('.') != -1) {
            filename = filename.substring(0, filename.lastIndexOf('.'));
        }
        return filename;
    }

    /**
     * Gibt den Pfad zurück. Es werden jedoch alle Backslashs ('\') in normale Slashs ('/') umgewandelt und anschliessend doppelte Slashs
     * ('//') herausgenommen.
     */
    public static String getNormalizedFilepath(final String filepath) {
        String newFilepath;
        if (filepath.startsWith("\\\\") || filepath.startsWith("//")) {
            newFilepath = filepath.substring(0, 2) + StringOperation.replace(filepath.substring(2).replace('\\', '/'), "//", "/");
        } else {
            newFilepath = StringOperation.replace(filepath.replace('\\', '/'), "//", "/");
        }

        newFilepath = StringUtils.cleanPath(newFilepath);
        return newFilepath;
    }

    /**
     * Kopiert eine Datei von einem Ort an ein anderen Ort. Falls die Datei am neuen Ort bereits existiert, wird diese überschrieben!!
     * 
     * @throws java.io.IOException
     *             Wenn beim Kopieren etwas schiefging.
     */
    public static void copyFile(final String sourceFilepath, final String destinationFilepath) throws IOException {
        final File source = new File(sourceFilepath);
        final File dest = new File(destinationFilepath);

        if (dest.exists()) {
            dest.delete();
        }

        final FileInputStream sourceFileStream = new FileInputStream(source);
        final FileOutputStream destFileStream = new FileOutputStream(dest);

        final BufferedInputStream sourceStream = new BufferedInputStream(sourceFileStream);
        final BufferedOutputStream destStream = new BufferedOutputStream(destFileStream);

        try {
            int readChar;
            while ((readChar = sourceStream.read()) != -1) {
                destStream.write(readChar);
            }
        } finally {
            destStream.close();
            destFileStream.close();

            sourceStream.close();
            sourceFileStream.close();
        }

    }

    /**
     * Schreibt den Text von <code>text</code> in ein File unter <code>path</code>. Falls die Datei bereits existiert, wird sie
     * überschrieben.
     */
    public static void writeFile(final String text, final String path) throws IOException {
        final File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        final Writer outputWriter = new BufferedWriter(new FileWriter(file));
        outputWriter.write(text);
        outputWriter.close();
    }

    /**
     * Schreibt den Text von <code>text</code> in ein File unter <code>path</code>. Falls die Datei bereits existiert, wird sie
     * überschrieben.
     */
    public static void writeFile(final InputStream inputStream, final String path) throws IOException {

        final File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        final OutputStream out = new FileOutputStream(file);
        final byte buf[] = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
    }

    /**
     * Liesst ein File in einen String ein.
     */
    public static String readFile(final File file) throws java.io.IOException {
        final StringBuffer fileData = new StringBuffer(1000);
        final FileReader fileReader = new FileReader(file);
        final BufferedReader reader = new BufferedReader(fileReader);
        try {
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                final String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
        } finally {
            reader.close();
            fileReader.close();
        }
        return fileData.toString();
    }

    /**
     * Liesst ein File in einen String ein.
     */
    public static String readFile(final InputStream input) throws java.io.IOException {
        final StringBuffer fileData = new StringBuffer(1000);
        final InputStreamReader inputReader = new InputStreamReader(input);
        final BufferedReader reader = new BufferedReader(inputReader);
        try {
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                final String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
        } finally {
            reader.close();
            inputReader.close();
        }
        return fileData.toString();
    }

    /**
     * Wandelt einen Pfad wie z.B. '773/imageFilename.jpg' in einen Dateinamen ohne Verzeichnis, z.B. '773_imageFilename.jpg'. Diese Methode
     * wird benötigt, wenn Dateien aus mehreren Verzeichnissen in ein Verzeichnis gelegt werden sollen, ohne das es Probleme mit doppelten
     * Dateinamen geben soll.
     */
    public static String getFlatFilename(final String filepath) {
        return FileUtils.getNormalizedFilepath(filepath).replace('/', '_');
    }

    /**
     * Prüft, ob ein Verzeichnis bereits existiert und erzeugt ein Verzeichnis, wenn noch keines existiert.<br>
     * Funktioniert auch, wenn mehrere Verzeichnisse noch nicht existieren, sie werden der Reihe nach angelegt.<br>
     * Diese Methode kann nicht mit einem Servername und einer lokalen Laufwerkbezeichnung aufgerufen werden. Ein String wie
     * '\\okeanos\D:\meinVerzeichnis würde eine <code>IOException</code> werfen. <br>
     * Mit einem Share ist es jedoch ohne Probleme möglich, ein String wie '\\okeanos\Laufwerk_D\meinVerzeichnis würde das gewünschte
     * Verzeichnis erstellen.
     */
    public static File checkAndCreateDirectory(final String directory) throws IOException {
        final String normalizedDirectory = FileUtils.getNormalizedFilepath(directory);
        final File directoryFile = new File(normalizedDirectory);
        if (!directoryFile.exists()) {
            final boolean successfulCreated = directoryFile.mkdirs();
            if (!successfulCreated) {
                throw new IOException("There were problems creating the directory '" + normalizedDirectory + "'.");
            }
        }

        // String serverName;
        // String directoryWithoutServerName;
        // if(directory.startsWith("\\\\") || directory.startsWith("//")) {
        // serverName = normalizedDirectory.substring(0, normalizedDirectory.substring(2).indexOf('/') + 2);
        // directoryWithoutServerName = normalizedDirectory.substring(serverName.length() + 1);
        // }
        // else {
        // serverName = "";
        // directoryWithoutServerName = normalizedDirectory;
        // }
        //
        // String [] directoyTokkens = StringUtils.tokenizeToStringArray(directoryWithoutServerName,"/",false,true);
        // String growingDirectoryStructure = serverName;
        //
        // //Wird bereits initialisiert, falls die
        // //for-Schlaufe kein einziges mal durchgeführt
        // //würde.
        // File directoryFile = new File(directory);
        // for(int i=0; i<directoyTokkens.length; i++) {
        // growingDirectoryStructure += "/" + directoyTokkens[i];
        //
        // directoryFile = new File(growingDirectoryStructure);
        // directoryFile.isFile();
        // if(!directoryFile.exists()) {
        // boolean successfulCreated = directoryFile.mkdir();
        // if(!successfulCreated) {
        // throw new IOException("There were problems creating the directory '" + directoryFile + "'." +
        // "The problem occured at the directory '" + growingDirectoryStructure +"'.");
        // }
        // }
        // }

        return directoryFile;
    }

    /**
     * Prüft, ob ein File bereits existiert und löscht die vorhandene Datei, wenn sie existiert.
     */
    public static File checkAndDeleteFile(final String filepath) throws IOException {
        final File fileToDelete = new File(filepath);

        if (fileToDelete.exists()) {
            fileToDelete.delete();
        }

        return new File(filepath);
    }

    /**
     * Returns a Resource for the <code>filepath</code> attribute. If the <code>filepath</code> starts with "classpath:", the method makes a
     * instance of <code>ClassPathResource</code>, otherwise a <code>FileSystemResource</code>.
     * 
     * @param throwExceptionIfNotExists
     *            Throws an exception, if the file doesn't exist and this parameter is <code>true</code>. If set to <code>false</code>, the
     *            file is create.
     * @see ClassPathResource
     * @see FileSystemResource
     */
    public static Resource createResourceFromFilepath(final String filepath, final boolean throwExceptionIfNotExists) throws FileNotFoundException, IOException {

        final ResourceEditor resEditor = new ResourceEditor();
        resEditor.setAsText(filepath);
        final Resource resource = (Resource) resEditor.getValue();

        if (!resource.exists() && throwExceptionIfNotExists) {
            throw new FileNotFoundException(resource.getFile().getAbsolutePath());
        }

        return resource;
    }

}
