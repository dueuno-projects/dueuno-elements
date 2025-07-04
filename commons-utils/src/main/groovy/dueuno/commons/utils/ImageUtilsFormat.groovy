package dueuno.commons.utils

import groovy.transform.CompileStatic

@CompileStatic
enum ImageUtilsFormat {
    GIF('gif'),
    PNG('png'),
    JPEG('jpg')

    final String extension

    ImageUtilsFormat(String extension) {
        this.extension = extension
    }

    static ImageUtilsFormat get(String extension) {
        return values().find { it.extension == extension }
    }
}