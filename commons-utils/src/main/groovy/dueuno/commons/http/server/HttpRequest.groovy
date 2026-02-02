package dueuno.commons.http.server

import dueuno.commons.utils.FileUtils
import dueuno.commons.utils.StringUtils
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.Part

/**
 * Utility class for handling HTTP requests, with a focus on multipart/form-data requests,
 * JSON parts, file uploads, and general parameter reading.
 *
 * This class is designed to simplify common operations in server-side request handling.
 *
 * @author Gianluca Sartori
 */
@Slf4j
@CompileStatic
class HttpRequest {

    /**
     * Checks whether the given HTTP request is a multipart request.
     *
     * @param request the HttpServletRequest to check
     * @return true if the request is multipart, false otherwise
     */
    static Boolean isMultipart(HttpServletRequest request) {
        String contentType = request?.contentType
        return contentType?.toLowerCase()?.startsWith("multipart/")
    }

    /**
     * Retrieves a Part from a multipart request by its name.
     *
     * @param request the HttpServletRequest containing the part
     * @param partName the name of the part to retrieve
     * @return the Part if present, or null if the request is not multipart or the part does not exist
     */
    static Part getPart(HttpServletRequest request, String partName) {
        if (!isMultipart(request)) {
            return null
        }

        return request.getPart(partName)
    }

    /**
     * Checks if a named part exists in the request.
     *
     * @param request the HttpServletRequest
     * @param partName the name of the part to check
     * @return true if the part exists, false otherwise
     */
    static Boolean partExists(HttpServletRequest request, String partName) {
        try {
            return getPart(request, partName) != null

        } catch (Exception e) {
            log.warn("Error reading part '${partName}': ", e)
            return false
        }
    }

    /**
     * Determines whether a Part represents a file upload.
     *
     * @param part the Part to check
     * @return true if the part has a submitted file name, false otherwise
     */
    static Boolean isFilePart(Part part) {
        return part?.submittedFileName != null
    }

    /**
     * Reads the content of a part as a String.
     *
     * @param request the HttpServletRequest
     * @param partName the name of the part to read
     * @return the content of the part as a String, or null if the part does not exist
     */
    static String partToString(HttpServletRequest request, String partName) {
        Part part = getPart(request, partName)
        if (!part) {
            return null
        }

        return part.inputStream.text
    }

    /**
     * Parses the content of a JSON part into a Map.
     *
     * @param request the HttpServletRequest
     * @param partName the name of the JSON part
     * @return a Map representing the JSON content, or an empty map if the part is missing or parsing fails
     */
    static Map partToMap(HttpServletRequest request, String partName) {
        Part jsonPart = getPart(request, partName)
        if (!jsonPart) {
            return [:]
        }

        try {
            String jsonString = StringUtils.inputStreamToString(jsonPart.inputStream)
            return new JsonSlurper().parseText(jsonString) as Map

        } catch (Exception e) {
            log.warn("Error parsing JSON in part '${partName}': ", e)
            return [:]
        }
    }

    /**
     * Saves all uploaded file parts with the given name to the filesystem.
     * Files are saved in the specified path using their original uploaded filenames.
     *
     * @param request the HttpServletRequest
     * @param partName the name of the multipart file field
     * @param path directory where files will be saved; must exist or be creatable
     * @return list of saved File objects (empty if none found)
     */
    static List<File> partToFiles(HttpServletRequest request, String partName, String path = null) {
        if (!isMultipart(request)) {
            return []
        }

        File dir
        if (path) {
            dir = new File(path)
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("Unable to create directory: ${path}")
            }
        } else {
            dir = File.createTempDir('upload')
        }

        List<File> files = []
        for (part in request.parts) {
            if (part.name == partName && isFilePart(part)) {
                String safeFilename = FileUtils.buildSafeFilename(part.submittedFileName)
                File file = new File(dir, safeFilename)
                part.write(file.absolutePath)
                files << file
            }
        }

        return files
    }

}
