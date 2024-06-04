package custom

import dueuno.elements.components.Header
import dueuno.elements.contents.ContentBlank
import dueuno.elements.core.Page

class CustomPage extends Page {

    Header header
    ContentBlank content

    CustomPage(Map args) {
        super(args)

        viewPath = '/custom/'

        header = createComponent(Header)
        content = createComponent(ContentBlank, 'content')
    }

}
