//= require DateTimeField

class MonthField extends DateTimeField {

    static initialize($element, $root) {
        let dateFormat = 'MM/yyyy';
        let startOfTheWeek = _21_.user.firstDaySunday ? 0 : 1;

        let options = {
            allowInputToggle: false,
            display: {
                theme: 'light',
                viewMode: 'months',
                components: {
                    clock: false,
                    date: false,
                },
            },
            localization: {
                locale: _21_.user.language,
                format: dateFormat,
                startOfTheWeek: startOfTheWeek,
            },
            restrictions: {},
        };

        let td = new tempusDominus.TempusDominus($element.parent()[0], options);
        td.dates.parseInput = MonthField.parseInput;
        $element.data('td', td);
    }

    static parseInput(value) {
        let year;
        let month;

        value = value.replaceAll('/', '');

        if (value.length <= 2) {
            let today = new Date();
            year = today.getFullYear().toString();
            month = value.padStart(2, '0');

        } else if (value.length == 5) {
            year = value.substring(1, 5);
            month = value.substring(0, 1).padStart(2, '0');

        } else {
            year = value.substring(2, 6);
            month = value.substring(0, 2);
        }

        let dateTime = new tempusDominus.DateTime(year + '-' + month + '-01');
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
            value.year,
            value.month - 1,
            value.day,
            0, 0, 0
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
            type: 'DATE',
            value: {
                year: date.year,
                month: date.month + 1,
                day: date.date,
            }
        }

        return null;
    }
}

Control.register(MonthField);
