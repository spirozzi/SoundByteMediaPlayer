package com.soundbyte.model.table;

import java.util.Vector;

public interface NowPlayingTableObserver
{
	public void updateNowPlayingTable(Vector<Vector<Object>> data,
			Vector<Object> cols);
}
