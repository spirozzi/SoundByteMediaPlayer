package com.soundbyte.model;

import com.soundbyte.model.table.NowPlayingEntry;
import com.soundbyte.model.table.NowPlayingTableModel;
import com.soundbyte.model.table.NowPlayingTableObserver;
import com.soundbyte.model.table.SongTableModel;
import com.soundbyte.model.table.SongTableObserver;
import com.soundbyte.util.MP3Tag;
import entagged.audioformats.exceptions.CannotReadException;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class SoundByteModel implements DataModelInterface
{
    private static final ImportLog importLogger = ImportLog.getInstance();
    private static final SongDatabaseDriver driver = SongDatabaseDriver.
            getInstance();

    // Singleton
    private static final SoundByteModel instance = new SoundByteModel();
    // Observers
    private final List<SongTableObserver> songTableObservers;
    private final List<NowPlayingTableObserver> nowPlayingTableObservers;
    // Table models for song table and Now Playing table
    private final SongTableModel songTableModel;
    private final NowPlayingTableModel nowPlayingTableModel;

    private SoundByteModel()
    {
        songTableObservers = new ArrayList<>();
        nowPlayingTableObservers = new ArrayList<>();
        // initialize table model and data from sqlite database
        songTableModel = new SongTableModel(driver.getFullLibraryContents());
        // now playing table will always be empty on startup
        nowPlayingTableModel = new NowPlayingTableModel();
    }

    public static SoundByteModel getInstance()
    {
        return instance;
    }

    @Override
    public int addToNowPlayingTable(int[] songTableRowIndexes)
    {
        // return this value to inform the view of which song to start playing
        // first after adding more than one song to the now playing table
        final int firstSongAddedIndex = songTableModel.getDataVector().size();
        // Get rows in now playing table
        final Vector<Vector<Object>> nowPlayingTableRows = nowPlayingTableModel.
                getDataVector();
        for (int rowIndex : songTableRowIndexes)
        {
            String title = songTableModel.getValueAt(rowIndex, 0).toString();
            String artist = songTableModel.getValueAt(rowIndex, 1).toString();
            // Get path of the song from db driver, which maintains path info
            String path = driver.getSongPath(rowIndex);
            // Create and add a new row "Title - Artist" to now playing table
            final Vector<Object> newEntry = new Vector<>();
            newEntry.add(new NowPlayingEntry(title, artist, path));
            nowPlayingTableRows.add(newEntry);
        }
        // Push changes to the now playing table to observers
        notifyNowPlayingTableObservers(nowPlayingTableRows);
        return firstSongAddedIndex;
    }

    @Override
    public void addSongToDatabase(File f)
    {
        try
        {
            // extract tags
            final MP3Tag tag = new MP3Tag(f);
            // create seek table
            InputStream is = null;
            // put data in database
            driver.insertData(f.getAbsolutePath(), tag.getTitle(), tag.
                    getArtist(), tag.getAlbum(), tag.getTrackNum(), tag.
                    getLength(), tag.getYear(), tag.getBitrate(),
                    tag.getGenre(), tag.getDiscNum(), is);
        }
        catch (CannotReadException ex)
        {
            importLogger.logFailure(f.getAbsolutePath());
        }
    }

    @Override
    public DefaultTableModel getSongTableModel()
    {
        return songTableModel;
    }

    @Override
    public DefaultTableModel getNowPlayingTableModel()
    {
        return nowPlayingTableModel;
    }

    @Override
    public void registerObserver(SongTableObserver o)
    {
        songTableObservers.add(o);
    }

    @Override
    public void removeObserver(SongTableObserver o)
    {
        songTableObservers.remove(o);
    }

    private void notifySongTableObservers()
    {
        for (SongTableObserver o : songTableObservers)
        {
            o.updateSongTable(driver.getFullLibraryContents(), SongTableModel.
                    getColumnNames());
        }
    }

    @Override
    public void registerObserver(NowPlayingTableObserver o)
    {
        nowPlayingTableObservers.add(o);
    }

    @Override
    public void removeObserver(NowPlayingTableObserver o)
    {
        nowPlayingTableObservers.remove(o);
    }

    private void notifyNowPlayingTableObservers(Vector<Vector<Object>> data)
    {
        for (NowPlayingTableObserver o : nowPlayingTableObservers)
        {
            o.updateNowPlayingTable(data, NowPlayingTableModel.getColumnNames());
        }
    }

    @Override
    public void reloadSongTable()
    {
        notifySongTableObservers();
    }

    @Override
    public void clearNowPlayingTable()
    {
        notifyNowPlayingTableObservers(new Vector<Vector<Object>>());
    }

    @Override
    public void performSearch(String sqlWhereClause)
    {
        // TODO: perform search then call notify with table data
        for (SongTableObserver o : songTableObservers)
        {
            o.updateSongTable(driver.getSearchResults(sqlWhereClause),
                    SongTableModel.getColumnNames());
        }
    }
}
