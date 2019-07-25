/*************************************************************************
 *  Compilation:  javac Site.java
 *
 *  simple data type for an (row, col) site
 *
 *************************************************************************/

public class Site {
    private int row;
    private int col;

    // initialize board from file
    public Site(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int row() { return row; }
    public int col() { return col; }

    // Manhattan distance between invoking Site and w
    public int manhattanTo(Site other) {
        Site current = this;
        int row1 = other.row();
        int col1 = other.col();
        int row2 = current.row();
        int col2 = current.col();
        return Math.abs(row1 - row2) + Math.abs(col1 - col2);
    }

    // does invoking site equal site w?
    public boolean equals(Object w) {
        Site obj = (Site)w;
        return (manhattanTo(obj) == 0);
    }

    public String toString ()
    {
        return "(" + row + "," + col + ")";
    }
    
     /**
     * Generates a hash code.
     */
    
    public int hashCode()
    {
        return row() * 3737 + col();
    }
}
