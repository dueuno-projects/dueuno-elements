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

            dictDefaultMessage: properties.messages.messageDefault,
            dictFileTooBig: properties.messages.messageTooBig,
            dictInvalidFileType: properties.messages.messageInvalidType,
            dictResponseError: properties.messages.messageResponseError,
            dictCancelUpload: properties.messages.messageCancel,
            dictCancelUploadConfirmation: properties.messages.messageCancelConfirmation,
            dictUploadCanceled: properties.messages.messageCanceled,
            dictRemoveFile: properties.messages.messageRemove,
            dictRemoveFileConfirmation: properties.messages.messageRemoveConfirmation,
            dictMaxFilesExceeded: properties.messages.messageMaxExceeded,

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

        Transition.submitEvent($element, 'addfile');
    }

    static onRemoveFile(file) {
        let $element = $(this.element);
        Transition.submitEvent($element, 'removefile');
    }

    static onSuccess(file) {
        let $element = $(this.element);
        Transition.submitEvent($element, 'success');
    }

    static onError(file) {
        let $element = $(this.element);
        Transition.showLoadingScreen(false);
        Transition.submitEvent($element, 'error');
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
}

Control.register(Upload);
