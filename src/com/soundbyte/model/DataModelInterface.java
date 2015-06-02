package com.soundbyte.model;

import com.soundbyte.model.table.NowPlayingTableObserver;
import com.soundbyte.model.table.SongTableObserver;
import java.io.File;
import javax.swing.table.DefaultTableModel;

public interface DataModelInterface
{
    public void registerObserver(SongTableObserver o);

    public void removeObserver(SongTableObserver o);

    public void registerObserver(NowPlayingTableObserver o);

    public void removeObserver(NowPlayingTableObserver o);

    public void performSearch(String sqlWhereClause);

    public void reloadSongTable();

    public DefaultTableModel getSongTableModel();

    public DefaultTableModel getNowPlayingTableModel();

    public int addToNowPlayingTable(int[] songTableRowIndexes);

    public void clearNowPlayingTable();

    public void addSongToDatabase(File f);
}
