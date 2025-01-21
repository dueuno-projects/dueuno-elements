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
            sorter: data => data.sort((a, b) => a.text.localeCompare(b.text)),
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
                    searchEvent.params = {
                        [controlId]: params.term ? params.term.replaceAll('%', '*') : '',
                    };
                    let submitEvent = Transition.build21Params(searchEvent);
                    return submitEvent;
                },
                processResults: function (data) {
                    let transition = Transition.fromHtml(data);
                    let optionCommand = transition.commands.findLast(it => it.component == controlId && it.property == 'options');
                    if (optionCommand) {
                        let options = optionCommand.value.value ?? {};
                        let results = [];

                        for (let [key, value] of Object.entries(options)) {
                            let option = {id: key, text: value};
                            results.push(option);
                        }

                        return {results: results};
                    }
                }
            }
        }

        $element.select2(options);
    }

    static finalize($element, $root) {
        $element.off('select2:select select2:unselect').on('select2:select select2:unselect', Select.onChange);
        $element.off('select2:open').on('select2:open', Select.onOpen);

        Transition.triggerEvent($element, 'load');
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
        let select2Values = $element.select2('data');

        // In case of user clear we align the control value
        if (select2Values.length == 0) {
            let valueMap = {
                type: 'TEXT',
                value: null,
            };
            $element.data('21-value', valueMap);
            $element.val(valueMap.value);
            $element.trigger('change');
        }

        Transition.triggerEvent($element, 'change');
    }

    static setValue($element, valueMap, trigger = true) {
        if (!trigger) $element.off('select2:select select2:unselect');

        let searchEvent = Component.getEvent($element, 'search');
        let loadEvent = Component.getEvent($element, 'load');
        let hasOptions = Select.hasOptions($element);
        if (searchEvent && loadEvent && !hasOptions) {
            Select.setOptions($element, {[valueMap.value]: '...'});
            if (trigger) {
                Transition.submit(loadEvent);
            }
        }

        $element.data('21-value', valueMap);
        $element.val(valueMap.value);
        $element.trigger('change');

        if (!trigger) $element.on('select2:select select2:unselect', Select.onChange);
    }

    static getValue($element) {
        let properties = Component.getProperties($element);
        let select2Values = $element.select2('data');
        let loadEvent = Component.getEvent($element, 'load');
        if (loadEvent && select2Values.length == 0) {
            // If no value have been manually selected from the user
            let controlValue = $element.data('21-value');
            return controlValue;
        }

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

    static hasOptions($element) {
        return $element.children('option').length;
    }

    static setOptions($element, options) {
        let valueMap = Select.getValue($element);

        if (!options) {
            valueMap.value = null;
            Select.setValue($element, valueMap, false);
            return;
        }

        $element.empty();
        let isValueInOptions = false;
        for (let [key, value] of Object.entries(options)) {
            $element.append(new Option(value, key, false, false));
            if (key == valueMap.value) {
                isValueInOptions = true
            }
        }

        if (isValueInOptions) {
            let properties = Component.getProperties($element);
            let optionsCount = $element.children('option').length;
            if (!properties['autoSelect'] || optionsCount > 1 || properties['nullable']) {
                // Select2 automatically selects the first item on ajax loading
                // so we need to implement an inverse logic
                Select.setValue($element, valueMap, false);
            }
        } else {
            valueMap.value = null;
            Select.setValue($element, valueMap, false);
        }
    }

    static getReadonly($element) {
        return $element.prop('disabled');
    }

    static setReadonly($element, value) {
        Component.setReadonly($element, value);
        $element.prop('disabled', value);

        let $actions = $element.closest('.input-group').find('a');
        Component.setReadonly($actions, value);
    }

}

Control.register(Select);
