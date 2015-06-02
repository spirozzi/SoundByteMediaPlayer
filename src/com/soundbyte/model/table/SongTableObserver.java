package com.soundbyte.model.table;

import java.util.Vector;

public interface SongTableObserver
{
	public void updateSongTable(Vector<Vector<Object>> data,
			Vector<Object> cols);
}
