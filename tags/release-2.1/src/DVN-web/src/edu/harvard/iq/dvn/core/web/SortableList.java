package edu.harvard.iq.dvn.core.web;


/**
 * The SortableList class is a utility class used by the  data table
 * paginator example.
 *
 */
public abstract class SortableList {

    protected String sortColumnName;
    protected boolean ascending;

    // we only want to resort if the order or column has changed.
    protected String oldSort;
    protected boolean oldAscending;


    protected SortableList() {
        //
    }
    protected SortableList(String defaultSortColumn) {
        sortColumnName = defaultSortColumn;
        ascending = isDefaultAscending(defaultSortColumn);
        oldSort = sortColumnName;
        // make sure sortColumnName on first render
        oldAscending = !ascending;
 
    }
    
    protected void checkSort() {
        // we only want to sortColumnName if the column or ordering has changed.
            if (!oldSort.equals(sortColumnName) ||
                oldAscending != ascending){
                sort();
                oldSort = sortColumnName;
                oldAscending = ascending;
            }
       
        }

    /**
     * Sort the list.
     */
    protected abstract void sort();

    /**
     * Is the default sortColumnName direction for the given column "ascending" ?
     */
    protected abstract boolean isDefaultAscending(String sortColumn);

    /**
     * Gets the sortColumnName column.
     *
     * @return column to sortColumnName
     */
    public String getSortColumnName() {
        return sortColumnName;
    }

    /**
     * Sets the sortColumnName column
     *
     * @param sortColumnName column to sortColumnName
     */
    public void setSortColumnName(String sortColumnName) {
        oldSort = this.sortColumnName;
        this.sortColumnName = sortColumnName;

    }

    /**
     * Is the sortColumnName ascending.
     *
     * @return true if the ascending sortColumnName otherwise false.
     */   public boolean isAscending() {
        return ascending;
    }

    /**
     * Set sortColumnName type.
     *
     * @param ascending true for ascending sortColumnName, false for desending sortColumnName.
     */
    public void setAscending(boolean ascending) {
        oldAscending = this.ascending;
        this.ascending = ascending;
    }
    
    //regular bean stuff
    
}
