package com.soundbyte.model.table;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class NowPlayingTableModel extends DefaultTableModel
{
    private static final Vector<Object> columnNames = new Vector<>();

    static
    {
        columnNames.add("Now Playing");
    }

    public NowPlayingTableModel()
    {
        super(new Vector<Vector<Object>>(), columnNames);
    }

    public static Vector<Object> getColumnNames()
    {
        return columnNames;
    }

    /**
     * Disable editing for all values in table
     */
    @Override
    public boolean isCellEditable(int row, int col)
    {
        return false;
    }
}
