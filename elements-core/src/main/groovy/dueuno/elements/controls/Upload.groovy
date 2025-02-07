/*
 * Copyright 2021 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dueuno.elements.controls

import dueuno.elements.core.Control
import dueuno.elements.types.Type
import groovy.transform.CompileStatic
import org.springframework.web.multipart.MultipartFile

import java.nio.file.Paths

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class Upload extends Control {

    List acceptedFiles
    Integer maxFiles
    Integer maxFileSize // MB
    Integer maxThumbnailFilesize // MB
    Integer thumbnailWidth
    Integer thumbnailHeight
    Boolean disablePreviews

    Upload(Map args) {
        super(args)

        valueType = Type.LIST

        acceptedFiles = args.acceptedFiles as List ?: []
        maxFiles = args.maxFiles as Integer ?: null
        maxFileSize = args.maxFileSize as Integer ?: 20
        maxThumbnailFilesize = args.maxThumbnailFilesize as Integer ?: 17 // 26 MPixel images
        thumbnailWidth = args.thumbnailWidth as Integer
        thumbnailHeight = args.thumbnailHeight as Integer
        disablePreviews = args.disablePreviews as Boolean ?: false
    }

    static String getFilename() {
        return getGrailsWebRequest().params._21Upload['filename']
    }

    static void save(String path, String newFilename = null) {
        if (!getGrailsWebRequest().params._21Upload) {
            return
        }

        MultipartFile request = getGrailsWebRequest().params._21Upload as MultipartFile
        String pathname = path + (newFilename ?: filename)
        request.transferTo(Paths.get(pathname))
    }

    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        Map thisProperties = [
                acceptedFiles: acceptedFiles.join(','),
                maxFiles: maxFiles,
                maxFileSize: maxFileSize,
                maxThumbnailFilesize: maxThumbnailFilesize,
                thumbnailWidth: thumbnailWidth,
                thumbnailHeight: thumbnailHeight,
                disablePreviews: disablePreviews,

                messages: [
                        disabled: message('control.upload.disabled'),
                        upload: message('control.upload.message'),
                        tooBig: message('control.upload.file.too.big'),
                        invalidType: message('control.upload.invalid.file.type'),
                        responseError: message('control.upload.response.error'),
                        cancel: message('control.upload.cancel'),
                        cancelConfirmation: message('control.upload.cancel.confirm'),
                        canceled: message('control.upload.canceled'),
                        remove: message('control.upload.remove'),
                        removeConfirmation: message('control.upload.remove.confirm'),
                        maxExceeded: message('control.upload.files.exceeded'),
                ]
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }
}
