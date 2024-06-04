//= require DateTimeField

class TimeField extends DateTimeField {

    static initialize($element, $root) {
        let properties = Component.getProperties($element);
        let timeFormat = _21_.user.twelveHours ? 'hh:mm' : 'HH:mm';
        let hourCycle = _21_.user.twelveHours ? 'h12' : 'h23';

        let options = {
            stepping: properties.timeStep ?? 1,
            allowInputToggle: false,
            display: {
                theme: 'light',
                viewMode: 'clock',
                components: {
                    calendar: false,
                },
            },
            localization: {
                locale: _21_.user.language,
                format: timeFormat,
                hourCycle: hourCycle,
            }
        };

        let td = new tempusDominus.TempusDominus($element.parent()[0], options);
        td.dates.parseInput = TimeField.parseInput;
        $element.data('td', td);
    }

    static parseInput(value) {
        let hour;
        let minute;

        value = value.replaceAll(':', '');

        if (value.length <= 2) {
            hour = value.padStart(2, '0');
            minute = '00';

        } else if (value.length == 3) {
            hour = '0' + value.substring(0, 1);
            minute = value.substring(1, 3);

        } else {
            hour = value.substring(0, 2);
            minute = value.substring(2, 4);
        }

        let intHour = parseInt(hour);
        let intMinute = parseInt(minute);
        if (isNaN(intHour) || intHour > 24) hour = '00';
        if (isNaN(intMinute) || intMinute > 59) minute = '00';

        let dateTime = new tempusDominus.DateTime('1900-01-01T' + hour + ':' + minute);
        return dateTime;
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
            1900, 0, 1,
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
        let time = td.dates.parseInput(value);

        if (time) return {
            type: 'TIME',
            value: {
                hour: time.hours,
                minute: time.minutes,
                second: time.seconds,
            }
        }

        return null;
    }
}

Control.register(TimeField);
