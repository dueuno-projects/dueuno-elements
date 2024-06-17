class Select extends Control {

    static initialize($element, $root) {
        let controlId = Component.getId($element);
        let properties = Component.getProperties($element);
        let hasButtons = $element.parent().has('a, .component-help').exists();

        let options = {
            theme: 'bootstrap-5',
            dropdownParent: $root,
            language: _21_.user.language,
            multiple: properties['multiple'],
            placeholder: properties['multiple'] ? null : properties['placeholder'],
            minimumResultsForSearch: properties['search'] ? 0 : -1,
            allowClear: properties['multiple'] ? false : properties['allowClear'],
            dropdownAutoWidth : true,
            width: hasButtons ? 'auto' : '100%',
            language: {
                inputTooShort: function (args) {
                    var remainingChars = args.minimum - args.input.length;
                    var message = properties.text['inputTooShort'].replace('{0}', remainingChars);
                    return message;
                },
                errorLoading: function() {
                    return properties.text['errorLoading'];
                },
                noResults: function() {
                    return properties.text['noResults'];;
                },
                searching: function() {
                    return properties.text['searching'];;
                },
            },
        };

        let searchEvent = Component.getEvent($element, 'search');
        if (searchEvent) {
            options.minimumInputLength = properties['searchMinInputLength'];
            options.ajax = {
                url: Transition.buildUrl(searchEvent),
                data: function (params) {
                    let _21Params = Transition.build21Params(searchEvent);
                    let input = params.term ? params.term.replaceAll('%', '*') : '';
                    let query = {
                        [controlId]: input,
                    };

                    let submitEvent = Object.assign({}, _21Params, query);
                    return submitEvent;
                },
                processResults: function (data) {
                    let transition = Transition.fromHtml(data);
                    let options = transition.commands[0].value.value;
                    let results = [];

                    for (let [key, value] of Object.entries(options)) {
                        let option = {id: key, text: value};
                        results.push(option);
                    }

                    return {results: results};
                }
            }
        }

        $element.select2(options);
    }

    static finalize($element, $root) {
        $element
            .off('select2:select select2:clear')
            .on('select2:select select2:clear', Select.onChange);
        $element
            .off('select2:open')
            .on('select2:open', Select.onOpen);

        Transition.submitEvent($element, 'load');
    }

    static isInitialized($element) {
        return false;
    }

    // We need this to auto-focus the text input
    static onOpen(event) {
        let selectId = event.target.id;
        let $select = $(".select2-search__field[aria-controls='select2-" + selectId + "-results']");
        $select.each(function (key, value){
            value.focus();
        })
    }

    static onChange(event) {
        let $element = $(event.currentTarget);
        Transition.submitEvent($element, 'change');
    }

    static setValue($element, valueMap, trigger = true) {
        if (!trigger) $element.off('select2:select select2:clear');

        let searchEvent = Component.getEvent($element, 'search');
        let loadEvent = Component.getEvent($element, 'load');
        if (searchEvent && loadEvent) {
            Select.setOptions($element, {[valueMap.value]: '...'});
            $element.val(valueMap.value);
            if (trigger) {
                Transition.submit(loadEvent);
            }

        } else {
            $element.val(valueMap.value);
        }

        $element.trigger('change');

        if (!trigger) $element.on('select2:select select2:clear', Select.onChange);
    }

    static getValue($element) {
        let properties = Component.getProperties($element);
        let select2Values = $element.select2('data');

        let ids = [];
        for (let value of select2Values) {
            ids.push(value['id']);
        }

        let result = {};
        if (ids.length == 0) {
            result.type = 'TEXT';
            result.value = null;

        } else if (ids.length == 1 && !properties['multiple']) {
            result.type = 'TEXT';
            result.value = ids[0];

        } else {
            result.type = 'LIST';
            result.value = ids;
        }

        return result;
    }

    static setOptions($element, options, selection = null) {
        $element.empty();

        if (!options) {
            return;
        }

        let properties = Component.getProperties($element);
        for (let [key, value] of Object.entries(options)) {
            $element.append(new Option(value, key, false, false));
        }

        let searchEvent = Component.getEvent($element, 'search');
        if (!searchEvent) {
            Select.setValue($element, {value: selection});
        }
    }

    static getReadonly($element) {
        return $element.prop("disabled");
    }

    static setReadonly($element, value) {
        Component.setProperty($element, 'readonly', value);
        $element.prop("disabled", value);
    }
}

Control.register(Select);
