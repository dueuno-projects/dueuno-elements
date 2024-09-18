class Upload extends Control {

    static initialize($element, $root) {
        let properties = Control.getProperties($element);

        let options = {
            paramName: '_21Upload',
            url: 'not/set',
            addRemoveLinks: true,
            thumbnailWidth: properties.thumbnailWidth ?? _21_.user.fontSize * 7,
            thumbnailHeight: properties.thumbnailHeight ?? _21_.user.fontSize * 7,

            maxThumbnailFilesize: properties.maxThumbnailFilesize,
            maxFileSize: properties.maxFileSize, // MB
            maxFiles: properties.maxFiles,
            acceptedFiles: properties.acceptedFiles,

            dictDisabled: properties.messages.disabled,
            dictDefaultMessage: properties.messages.upload,
            dictFileTooBig: properties.messages.tooBig,
            dictInvalidFileType: properties.messages.invalidType,
            dictResponseError: properties.messages.responseError,
            dictCancelUpload: properties.messages.cancel,
            dictCancelUploadConfirmation: properties.messages.cancelConfirmation,
            dictUploadCanceled: properties.messages.canceled,
            dictRemoveFile: properties.messages.remove,
            dictRemoveFileConfirmation: properties.messages.removeConfirmation,
            dictMaxFilesExceeded: properties.messages.maxExceeded,

            // Don't ask me why but this is the only way to
            // register/trigger the events with Dropzone
            init: Upload.registerEvents,
        };

        $element.dropzone(options);
    }

    static finalize($element, $root) {
        let componentEvent = Component.getEvent($element, 'upload');
        if (!componentEvent) {
            return;
        }

        let url = Transition.buildUrl(componentEvent);
        let queryString = Transition.buildQueryString(componentEvent);
        $element[0].dropzone.options.url = url + queryString;
    }

    static registerEvents() {
        let $element = this;
        $element.on('addedfile', Upload.onAddFile);
        $element.on('removedfile', Upload.onRemoveFile);
        $element.on('success', Upload.onSuccess);
        $element.on('error', Upload.onError);
    }

    static onAddFile(file) {
        let $element = $(this.element);

        let componentEvent = Component.getEvent($element, 'upload');
        if (componentEvent.loading) {
            Transition.showLoadingScreen(true);
        }

        Transition.triggerEvent($element, 'addfile');
    }

    static onRemoveFile(file) {
        let $element = $(this.element);
        Transition.triggerEvent($element, 'removefile');
    }

    static onSuccess(file) {
        let $element = $(this.element);
        Transition.triggerEvent($element, 'success');
    }

    static onError(file) {
        let $element = $(this.element);
        Transition.showLoadingScreen(false);
        Transition.triggerEvent($element, 'error');
    }

    static setValue($element, valueMap, trigger = true) {
        // no-op
    }

    static getValue($element) {
        let dropzone = $element[0].dropzone;
        if (!dropzone) {
            return {
                type: 'LIST',
                value: [],
            }
        }

        let results = [];
        let files = dropzone.getAcceptedFiles();
        for (let file of files) {
            results.push(file.upload.filename);
        }

        return {
            type: 'LIST',
            value: results,
        }
    }

    static clear($element) {
        $element[0].dropzone.removeAllFiles();
    }

    static setReadonly($element, value) {
        Component.setReadonly($element, value);
        let dropzone = $element[0].dropzone;
        dropzone.disable();

        let $button = $element.find('button');
        $button.html(dropzone.options.dictDisabled);
    }

}

Control.register(Upload);
