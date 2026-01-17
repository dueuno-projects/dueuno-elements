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
     * Saves a file part to the filesystem. If a pathname is provided, the file is saved there;
     * otherwise, a temporary file is created.
     *
     * @param request the HttpServletRequest
     * @param partName the name of the file part to save
     * @param pathname optional path where the file should be saved; if null, a temporary file is created
     * @return the saved File object, or null if the part does not exist or is not a file
     */
    static File partToFile(HttpServletRequest request, String partName, String pathname = null) {
        Part part = getPart(request, partName)
        if (!part) {
            return null
        }

        if (!isFilePart(part)) {
            log.warn("Part '${partName}' is not a file")
            return null
        }

        File file
        if (pathname) {
            String safeFilename = FileUtils.buildSafeFilename(pathname)
            file = new File(safeFilename)

        } else {
            String safeFilename = FileUtils.buildSafeFilename(part.submittedFileName)
            file = File.createTempFile("upload-", "-" + safeFilename)
            pathname = file.absolutePath
        }

        part.write(pathname)
        return file
    }

}
