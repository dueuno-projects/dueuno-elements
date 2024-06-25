package dueuno.elements.components

import dueuno.elements.core.Component
import groovy.transform.CompileStatic

@CompileStatic
class Grid extends Component {

    Integer xs
    Integer sm
    Integer md
    Integer lg
    Integer xl
    Integer xxl

    Integer spacing
    Boolean border

    Grid(Map args) {
        super(args)

        spacing = args.spacing == null ? 2 : args.spacing as Integer
        border = args.border == null ? false : args.border

        // No default for breakpoints since it would require
        // to set all of them everytime
        xs = args.xs as Integer
        sm = args.sm as Integer
        md = args.md as Integer
        lg = args.lg as Integer
        xl = args.xl as Integer
        xxl = args.xxl as Integer
    }

    GridColumn addColumn(Map args = [:]) {
        args['class'] = GridColumn
        args['id'] = "${id}-${components.size()}"
        args['grid'] = this

        GridColumn column = addComponent(args)
        return column
    }

    void setBreakpoints(Integer xs = 12, Integer sm = 6, Integer md = 4, Integer lg = 4, Integer xl = 3, Integer xxl = 2) {
        this.xs = xs
        this.sm = sm
        this.md = md
        this.lg = lg
        this.xl = xl
        this.xxl = xxl
    }

}
