class PieChart extends Component {
    static finalize($element, $root) {
        let properties = Component.getProperties($element);
        let canvas = $element.find('canvas')[0];

        new Chart(canvas, {
            type: 'pie',
            options: {
                maintainAspectRatio: false,
                events: ['mousemove', 'touchstart'],
            },
            data: {
                labels: properties.labels,
                datasets: [{
                    label: '',
                    data: properties.values,
                    backgroundColor: properties.colors,
                }],
            }
        });
    }
}

Component.register(PieChart);
