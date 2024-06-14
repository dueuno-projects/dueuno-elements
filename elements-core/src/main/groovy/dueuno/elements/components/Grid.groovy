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

        spacing = args.spacing == null ? 0 : args.spacing as Integer
        border = args.border == null ? false : args.border

        // No default for breakpoints since it would require
        // to set all of them everytime
    }

    void setBreakpoints(Integer xs = 12, Integer sm = 6, Integer md = 4, Integer lg = 4, Integer xl = 3, Integer xxl = 2) {
        this.xs = xs
        this.sm = sm
        this.md = md
        this.lg = lg
        this.xl = xl
        this.xxl = xxl
    }

    String getBreakpoints() {
        String result = ""

        if (xs) result += "col-${xs} "
        if (sm) result += "col-sm-${sm} "
        if (md) result += "col-md-${md} "
        if (lg) result += "col-lg-${lg} "
        if (xl) result += "col-xl-${xl} "
        if (xxl) result += "col-xxl-${xxl} "

        return result
    }

}
