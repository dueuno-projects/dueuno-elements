package dueuno.elements.components

import dueuno.elements.Component
import dueuno.exceptions.ArgsException
import groovy.transform.CompileStatic

@CompileStatic
class GridColumn extends Component {

    Grid grid

    Integer xs
    Integer sm
    Integer md
    Integer lg
    Integer xl
    Integer xxl

    GridColumn(Map args) {
        super(args)

        grid = ArgsException.requireArgument(args, 'grid') as Grid

        // No default for breakpoints since it would require
        // to set all of them everytime
        xs = args.xs as Integer
        sm = args.sm as Integer
        md = args.md as Integer
        lg = args.lg as Integer
        xl = args.xl as Integer
        xxl = args.xxl as Integer
    }

    String getBreakpoints() {
        String result = ""

        if (xs) result += "col-${xs} " else if (grid.xs) result += "col-${grid.xs} "
        if (sm) result += "col-sm-${sm} " else if (grid.sm) result += "col-sm-${grid.sm} "
        if (md) result += "col-md-${md} " else if (grid.md) result += "col-md-${grid.md} "
        if (lg) result += "col-lg-${lg} " else if (grid.lg) result += "col-lg-${grid.lg} "
        if (xl) result += "col-xl-${xl} " else if (grid.xl) result += "col-xl-${grid.xl} "
        if (xxl) result += "col-xxl-${xxl} " else if (grid.xxl) result += "col-xxl-${grid.xxl} "

        return result
    }

}
