class DateTimeField extends Control {

    static initialize($element, $root) {
        let properties = Component.getProperties($element);
        let dateFormat = _21_.user.invertedMonth ? 'MM/dd/yyyy' : 'dd/MM/yyyy';
        let timeFormat = _21_.user.twelveHours ? 'hh:mm' : 'HH:mm';
        let hourCycle = _21_.user.twelveHours ? 'h12' : 'h23';
        let startOfTheWeek = _21_.user.firstDaySunday ? 0 : 1;

        let options = {
            stepping: properties.timeStep ?? 1,
            allowInputToggle: false,
            display: {
                theme: 'light',
            },
            localization: {
                locale: _21_.user.language,
                format: dateFormat + ' ' + timeFormat,
                hourCycle: hourCycle,
                startOfTheWeek: startOfTheWeek,
            },
            restrictions: {},
        }

        let td = new tempusDominus.TempusDominus($element.parent()[0], options);
        td.dates.parseInput = DateTimeField.parseInput;
        $element.data('td', td);
    }

    static finalize($element, $root) {
        let properties = Component.getProperties($element);
        let options = {
            restrictions: {
                minDate: properties.min ? new Date(properties.min) : undefined,
                maxDate: properties.max ? new Date(properties.max) : undefined,
            }
        };
        let td = $element.data('td');
        td.updateOptions(options);

        $element.off('focus').on('focus', Control.onFocus);
        $element.off('paste').on('paste', Control.onPaste);
        $element.off('keypress').on('keypress', DateTimeField.onKeyPress);
        $element.off('change.td').on('change.td', DateTimeField.onChange);
        $element.off('show.td').on('show.td', DateTimeField.onShow);
        $element.parent().off('error.td').on('error.td', DateTimeField.onError);

        Transition.submitEvent($element, 'load');
    }

    static parseInput(value) {
        let year;
        let month;
        let day;
        let hour;
        let minute;

        value = value.replaceAll('/', '');
        value = value.replaceAll(' ', '');
        value = value.replaceAll(':', '');

        if (value.length <= 2) {
            let today = new Date();
            year = today.getFullYear().toString();
            month = (today.getMonth() + 1).toString().padStart(2, '0');
            day = value.padStart(2, '0');
            hour = '00';
            minute = '00';

        } else if (value.length == 4) {
            let today = new Date();
            year = today.getFullYear().toString();
            month = value.substring(2, 4);
            day = value.substring(0, 2);
            hour = '00';
            minute = '00';

        } else if (_21_.user.invertedMonth) {
            year = value.substring(4, 8);
            month = value.substring(0, 2);
            day = value.substring(2, 4);
            hour = value.substring(8, 10);
            minute = value.substring(10, 12);

        } else {
            year = value.substring(4, 8);
            month = value.substring(2, 4);
            day = value.substring(0, 2);
            hour = value.substring(8, 10);
            minute = value.substring(10, 12);
        }

        let dateTime = new tempusDominus.DateTime(year + '-' + month + '-' + day + 'T' + hour + ':' + minute);
        return dateTime;
    }

    static onError(event) {
        let $element = $(event.currentTarget).find('input');
        DateTimeField.setValue($element, {value: null}, false);
    }

    static onChange(event) {
        let $element = $(event.currentTarget);

        if (event.oldDate || event.date) {
            $element.data('td-clear', event.oldDate ? false : true);
            Transition.submitEvent($element, 'change');
        }
    }

    static onShow(event) {
        let $element = $(event.currentTarget);
        DateTimeField.setError($element, null);

        let autoPopulate = Control.getProperty($element, 'autoPopulate');
        if (!autoPopulate && $element.data('td-clear')) $element.val('');
        $element.data('td-clear', false);
    }

    static onKeyPress(event) {
        if (event.key == 'Enter') {
            TextField.onEnter(event);
            return;
        }

        let $element = $(event.currentTarget);
        DateTimeField.setError($element, null);

        let properties = Component.getProperties($element);
        let value = Control.getEventValue($element, event);

        let pattern = new RegExp(properties.pattern);
        let isValidValue = value.match(pattern);
        if (!isValidValue) {
            event.preventDefault();
        }

        Transition.submitEvent($element, 'keypress');
    }

    static setValue($element, valueMap, trigger = true) {
        let td = $element.data('td');
        let value = valueMap.value;

        if (!value) {
            if (!trigger) $element.off('change.td');
            td.dates.clear();
            if (!trigger) $element.on('change.td', DateTimeField.onChange);
            return;
        }

        let dateTime = new tempusDominus.DateTime(
            value.year,
            value.month - 1,
            value.day,
            value.hour ?? 0,
            value.minute ?? 0,
            value.second ?? 0
        );

        if (!trigger) $element.off('change.td');
        td.dates.setValue(dateTime);
        if (!trigger) $element.on('change.td', DateTimeField.onChange);
    }

    static getValue($element) {
        let value = $element.val();
        if (!value) {
            return null;
        }

        let td = $element.data('td');
        let date = td.dates.parseInput(value);

        if (date) return {
            type: 'DATETIME',
            value: {
                year: date.year,
                month: date.month + 1,
                day: date.date,
                hour: date.hours,
                minute: date.minutes,
                second: date.seconds,
            }
        }

        return null;
    }

    static setReadonly($element, value) {
        let td = $element.data('td');
        if (value) {
            td.disable();
        } else {
            td.enable();
        }
    }

    static setVisible($element, value) {
        if (value == null || value == true) {
            $element.closest('.input-group').removeClass('invisible');
        } else {
            $element.closest('.input-group').addClass('invisible');
        }
    }

    static setDisplay($element, value) {
        if (value == null || value == true) {
            $element.closest('.input-group').removeClass('d-none');
        } else {
            $element.closest('.input-group').addClass('d-none');
        }
    }

    static setMin($element, value) {
        let dateTime = new Date(
            value?.year ?? 1900,
            value?.month ? value?.month - 1 : 0,
            value?.day ?? 1,
            value?.hour ?? 0,
            value?.minute ?? 0
        );

        let options = {
            restrictions: {
                minDate: value ? dateTime : undefined,
            }
        };

        let td = $element.data('td');
        td.updateOptions(options);
    }

    static setMax($element, value) {
        let dateTime = new Date(
            value?.year ?? 1900,
            value?.month ? value?.month - 1 : 0,
            value?.day ?? 1,
            value?.hour ?? 0,
            value?.minute ?? 0
        );

        let options = {
            restrictions: {
                maxDate: value ? dateTime : undefined,
            }
        };

        let td = $element.data('td');
        td.updateOptions(options);
    }

    static setError($element, value) {
        let $formField = $element.closest('[data-21-component="FormField"]');
        FormField.setError($formField, value);
    }
}

Control.register(DateTimeField);