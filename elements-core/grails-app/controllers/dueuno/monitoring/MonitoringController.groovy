package dueuno.monitoring

import dueuno.elements.ElementsController

class MonitoringController implements ElementsController {

    def index() {
        redirect uri: '/monitoring'
    }

}
