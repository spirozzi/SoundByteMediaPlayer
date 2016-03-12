package com.soundbyte.controller;

import com.soundbyte.model.DataModelInterface;
import java.io.File;

/**
 * The controller accepts input and manipulates the model based on the input
 */
public class UIController implements UIControllerInterface
{
    private final DataModelInterface model;
    private boolean titleSearchFlag;
    private boolean artistSearchFlag;
    private boolean albumSearchFlag;
    private boolean genreSearchFlag;
    private boolean yearSearchFlag;

    public UIController(DataModelInterface model)
    {
        this.model = model;
        titleSearchFlag = false;
        artistSearchFlag = false;
        albumSearchFlag = false;
        genreSearchFlag = false;
        yearSearchFlag = false;
    }

    @Override
    public void addSongsToLibrary(File[] files)
    {
        for (File f : files)
        {
            model.addSongToDatabase(f);
        }
    }

    @Override
    public void addFoldersToLibrary(File[] folders)
    {
        for (File node : folders)
        {
            if (node.isDirectory())
            {
                addFoldersToLibrary(node.listFiles());
            }
            else
            {
                model.addSongToDatabase(node);
            }
        }
    }

    /**
     * Adds the songs in the song table at the given row indexes to the now
     * playing table
     *
     * @param songTableRowIndexes a list of indexes which refer to a specific
     * song in the song table
     *
     * @return an index into the now playing table that points to the first song
     * added
     */
    @Override
    public int addToNowPlaying(int[] songTableRowIndexes)
    {
        return model.addToNowPlayingTable(songTableRowIndexes);
    }

    @Override
    public void openOptions()
    {
        // TODO, open the Edit > Options... dialog box
    }

    @Override
    public void reloadSongTable()
    {
        model.reloadSongTable();
    }

    @Override
    public void search(String query)
    {
        model.performSearch(createWhereClause(query));
    }

    @Override
    public void clearNowPlayingTable()
    {
        model.clearNowPlayingTable();
    }

    @Override
    public void toggleTitleSearchFlag()
    {
        titleSearchFlag = !titleSearchFlag;
    }

    @Override
    public void toggleArtistSearchFlag()
    {
        artistSearchFlag = !artistSearchFlag;
    }

    @Override
    public void toggleAlbumSearchFlag()
    {
        albumSearchFlag = !albumSearchFlag;
    }

    @Override
    public void toggleGenreSearchFlag()
    {
        genreSearchFlag = !genreSearchFlag;
    }

    @Override
    public void toggleYearSearchFlag()
    {
        yearSearchFlag = !yearSearchFlag;
    }

    private String createWhereClause(String query)
    {
        // if no flags are specified, default to searching all fields
        if (!titleSearchFlag && !artistSearchFlag && !albumSearchFlag
                && !genreSearchFlag && !yearSearchFlag)
        {
            return " WHERE name='%' OR artist='%' OR album='%' OR genre='%' OR year='%'".
                    replaceAll("%", query);
        }
        String clause = " WHERE";
        if (titleSearchFlag)
        {
            clause += " name='%' OR";
        }
        if (artistSearchFlag)
        {
            clause += " artist='%' OR";
        }
        if (albumSearchFlag)
        {
            clause += " album='%' OR";
        }
        if (genreSearchFlag)
        {
            clause += " genre='%' OR";
        }
        if (yearSearchFlag)
        {
            clause += " year='%'";
        }
        // Remove extra trailing " OR"
        if (clause.endsWith(" OR"))
        {
            clause = clause.substring(0, clause.length() - 3);
        }
        return clause.replaceAll("%", query);
    }
}
