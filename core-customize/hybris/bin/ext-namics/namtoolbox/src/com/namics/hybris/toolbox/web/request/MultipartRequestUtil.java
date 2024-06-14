package com.namics.hybris.toolbox.web.request;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.namics.hybris.toolbox.FileUtils;

public class MultipartRequestUtil {

    /**
     * Method assume, that this is a multipart request with a file within as form element.
     * 
     * @param formFieldName
     *            The name of the form field, where the file is stored.
     * @param request
     *            <code>HttpServletRequest</code>, must be a <code>MultipartHttpServletRequest</code> request or a
     *            <code>HttpServletRequestWrapper</code> with an <code>MultipartHttpServletRequest</code> request within.
     * @throws IllegalStateException
     *             If the request is not a <code>MultipartHttpServletRequest</code>.
     * @return a <code>MultipartFile</code> file or <code>null</code>, if no file was attached.
     */
    public static MultipartFile extractMultipartFileFromRequest(final HttpServletRequest request, final String formFieldName) throws IllegalStateException {
        HttpServletRequest localRequest = null;
        MultipartHttpServletRequest multipartRequest = null;

        if (request instanceof MultipartHttpServletRequest) {
            localRequest = request;
        } else if (request instanceof HttpServletRequestWrapper) {
            final HttpServletRequestWrapper requestWrapper = (HttpServletRequestWrapper) request;
            localRequest = (HttpServletRequest) requestWrapper.getRequest();
        } else {
            throw new IllegalStateException("request was not of type multipart or a request wrapper.");
        }

        if (localRequest instanceof MultipartHttpServletRequest) {
            multipartRequest = (MultipartHttpServletRequest) localRequest;
        } else {
            throw new IllegalStateException("request was not of type multipart.");
        }

        final MultipartFile multipartFile = multipartRequest.getFile(formFieldName);

        return multipartFile;
    }

    /**
     * <p>
     * Stores a multipart attachment to the server file system.
     * </p>
     * <p>
     * If a file with same name already exists, a new file name is built.
     * </p>
     * <p>
     * The files are stored in the java temporary directory.
     * </p>
     * 
     * @param multipartFile
     *            The multipart file to save on the server file system.
     * @return The file (location), where the multipart content has been stored in the server file system
     * @throws IOException
     *             When the file couldn't be read/write.
     */
    public static File storeMultipartFileToFilesystem(final MultipartFile multipartFile) throws IOException {
        final String filename = multipartFile.getOriginalFilename();
        final String filenameWithoutExtension = FileUtils.getFilenameWithoutExtension(filename);
        final String extension = FileUtils.getFileExtension(filename);
        final boolean hasExtension = StringUtils.hasText(extension);
        String suffix = "";
        final String path = FileUtils.concatDirectories(System.getProperty("java.io.tmpdir"), "/multipartData/");

        final File directory = FileUtils.checkAndCreateDirectory(path);

        File file = null;

        int i = 0;
        do {
            if (i > 0) {
                suffix = String.valueOf(i);
            }

            final String loopFilename = filenameWithoutExtension + suffix + (hasExtension ? "." + extension : "");
            file = new File(FileUtils.concatDirectoryAndFilename(directory.getAbsolutePath(), loopFilename));
            i++;
        } while (file.exists());

        multipartFile.transferTo(file);

        return file;
    }

}
