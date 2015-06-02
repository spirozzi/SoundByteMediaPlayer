package com.soundbyte.model.table;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class SongTableModel extends DefaultTableModel
{
	private static final Vector<Object> columnNames = new Vector<>();

	static
	{
		columnNames.add("Title");
		columnNames.add("Artist");
		columnNames.add("Album");
		columnNames.add("Track");
		columnNames.add("Length");
		columnNames.add("Year");
		columnNames.add("Disc #");
		columnNames.add("Bitrate");
		columnNames.add("Genre");
	}

	public SongTableModel(Vector<Vector<Object>> rows)
	{
		super(rows, columnNames);
	}

	public static Vector<Object> getColumnNames()
	{
		return columnNames;
	}

	/**
	 * Disable editing for all values in table
	 *
	 * @return false because no values can be edited
	 */
	@Override
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}
}
