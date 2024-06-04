class BarChart extends Component {
    static finalize($element, $root) {
        let properties = Component.getProperties($element);
        let canvas = $element.find('canvas')[0];

        new Chart(canvas, {
            type: 'bar',
            options: {
                maintainAspectRatio: false,
                events: ['mousemove', 'touchstart'],
                plugins: {
                    legend: {
                        display: false,
                    },
                },
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

Component.register(BarChart);
