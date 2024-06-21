package dueuno.elements.monitoring

import dueuno.elements.core.ElementsController

class MonitoringController implements ElementsController {

    def index() {
        redirect uri: '/monitoring'
    }

}
