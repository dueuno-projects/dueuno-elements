package dueuno.elements.components

import dueuno.elements.core.Component
import groovy.transform.CompileStatic

@CompileStatic
class Grid extends Component {

    Integer col
    Integer colSm
    Integer colMd
    Integer colLg
    Integer colXl
    Integer colXxl

    Integer spacing
    Boolean border

    Grid(Map args) {
        super(args)

        spacing = args.spacing == null ? 0 : args.spacing as Integer
        border = args.border == null ? false : args.border
    }

    String getColSpecs() {
        String result = ""

        if (col) result += "col-${col} "
        if (colSm) result += "col-sm-${colSm} "
        if (colMd) result += "col-md-${colMd} "
        if (colLg) result += "col-lg-${colLg} "
        if (colXl) result += "col-xl-${colXl} "
        if (colXxl) result += "col-xxl-${colXxl} "

        return result
    }

}
