package com.soundbyte.model;

import com.soundbyte.util.ErrorHandler;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.kc7bfi.jflac.metadata.SeekPoint;
import org.kc7bfi.jflac.metadata.SeekTable;

/*
 * no caching for now
 * TODO:
 * load on disk db into vector
 * close conn
 * create in mem db using
 * Connection cxn = DriverManager.getConnection("jdbc:sqlite::memory:");
 * set up schema
 * insert vector contents into in mem db
 * always reload from in mem db
 * when user adds songs add them to in mem db
 * reload table from in mem db, display table, then
 * commit changes to on disk db using worker thread (async IO)
 */
final class SongDatabaseDriver
{
    private static final String DB_NAME = "library.sqlite";
    private static final File databaseFile = new File(DB_NAME);
    private static final ImportLog importLogger = ImportLog.getInstance();

    // Singleton
    private static final SongDatabaseDriver instance = new SongDatabaseDriver();
    /**
     * A list of each song's absolute file path indexed according to the song's
     * corresponding position in the song table. Cleared and updated each time
     * the library is loaded from the database and each time a search is
     * performed.
     */
    private final List<String> pathList;
    private Connection con;

    private SongDatabaseDriver()
    {
        pathList = new ArrayList<>();
        if (!databaseFile.exists())
        {
            createDatabase();
        }
    }

    private void ensureConnection()
    {
        if (con == null)
        {
            try
            {
                Class.forName("org.sqlite.JDBC");
                con = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            }
            catch (ClassNotFoundException | SQLException e)
            {
                ErrorHandler.error(
                        "could not initialize local connection to song database",
                        e);
            }
        }
    }

    public static SongDatabaseDriver getInstance()
    {
        return instance;
    }

    /**
     * Creates the database file as well as the songs table
     */
    private void createDatabase()
    {
        try
        {
            databaseFile.createNewFile();
            ensureConnection();
            createTable();
        }
        catch (IOException ex)
        {
            ErrorHandler.error("failed to create database file", ex);
        }
    }

    private void createTable()
    {
        final String SCHEMA = "CREATE TABLE songs ("
                + "path TEXT PRIMARY KEY,"
                + " name TEXT,"
                + " artist TEXT,"
                + " album TEXT,"
                + " track TEXT,"
                + " length TEXT"
                + " year TEXT,"
                + " bitrate INTEGER,"
                + " genre TEXT,"
                + " disc TEXT)";
        Statement stmt = null;
        try
        {
            stmt = con.createStatement();
            stmt.setQueryTimeout(30);
            stmt.executeUpdate(SCHEMA);
        }
        catch (SQLException e)
        {
            ErrorHandler.error("failed to initialize song library", e);
        }
        finally
        {
            if (stmt != null)
            {
                closeStatement(stmt);
            }
        }
    }

    public void insertData(String path, String name, String artist,
            String album, String trackNum, String length, String year,
            int bitrate, String genre, String discNum, InputStream seekTable)
    {
        PreparedStatement stmt = null;
        try
        {
            ensureConnection();
            stmt = con.prepareStatement("INSERT INTO songs values("
                    + "'" + sanitizeInput(path) + "',"
                    + "'" + sanitizeInput(name) + "',"
                    + "'" + sanitizeInput(artist) + "',"
                    + "'" + sanitizeInput(album) + "',"
                    + "'" + sanitizeInput(trackNum) + "',"
                    + "'" + length + "',"
                    + "'" + sanitizeInput(year) + "',"
                    + bitrate + ","
                    + "'" + sanitizeInput(genre) + "',"
                    + "'" + sanitizeInput(discNum) + "',"
                    + "?);");
            stmt.setQueryTimeout(30);
            stmt.setBytes(1, getSeekTableBytes(seekTable));
            stmt.execute();
            importLogger.logSuccess(path);
        }
        catch (SQLException e)
        {
            importLogger.logFailure(path);
        }
        finally
        {
            if (stmt != null)
            {
                closeStatement(stmt);
            }
        }
    }

    private byte[] getSeekTableBytes(InputStream is)
    {
        // The size of a serialized instance of SeekPoint is 121 bytes
        // A SeekTable instance contains an array of SeekPoint and a boolean in
        // its superclass
        final List<Byte> list = new ArrayList<>();
        try
        {
            while (is.available() > 0)
            {
                list.add((byte) is.read());
            }
        }
        catch (IOException e)
        {
            ErrorHandler.error("could not read seektable from file", e);
        }
        byte[] seekPointBytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            seekPointBytes[i] = list.get(i);
        }
        return seekPointBytes;
    }

    private void closeStatement(Statement stmt)
    {
        try
        {
            stmt.close();
        }
        catch (SQLException e)
        {
            ErrorHandler.error("could not close database", e);
        }
    }

    /**
     * Replaces all instances of apostrophes in the argument with tildes for
     * storage in the database. This is necessary because apostrophes are used
     * as string delimiters in SQLite.
     *
     * @param str the string to have its apostrophes replaced with tildes
     *
     * @return the same string with tildes in place of apostrophes
     */
    private String sanitizeInput(final String str)
    {
        return str.contains("'") ? str.replaceAll("'", "`") : str;
    }

    /**
     * Replaces all instances of tildes in the argument with apostrophes for
     * display. This is necessary because tildes were added in place of
     * apostrophes to avoid errors in SQLite statements.
     *
     * @param str the string to have its tildes replaced with apostrophes
     *
     * @return the same string with apostrophes in place of tildes
     */
    private String sanitizeOutput(final String str)
    {
        return str.contains("`") ? str.replaceAll("`", "'") : str;
    }

    public String getSongPath(int songTableIndex)
    {
        return pathList.get(songTableIndex);
    }

    public Vector<Vector<Object>> getFullLibraryContents()
    {
        final Vector<Vector<Object>> rows = new Vector<>(10, 50);
        // Clear path list
        pathList.clear();
        Statement stmt = null;
        try
        {
            ensureConnection();
            stmt = con.createStatement();
            stmt.setQueryTimeout(30);
            ResultSet allSongs = stmt.executeQuery("SELECT * FROM songs");
            while (allSongs.next())
            {
                // Add path to list of paths instead of table model
                pathList.add(sanitizeOutput(allSongs.getString("path")));
                final Vector<Object> songInfo = new Vector<>();
                songInfo.add(sanitizeOutput(allSongs.getString("name")));
                songInfo.add(sanitizeOutput(allSongs.getString("artist")));
                songInfo.add(sanitizeOutput(allSongs.getString("album")));
                songInfo.add(sanitizeOutput(allSongs.getString("track")));
                songInfo.add(allSongs.getString("length"));
                songInfo.add(sanitizeOutput(allSongs.getString("year")));
                songInfo.add(sanitizeOutput(allSongs.getString("disc")));
                songInfo.add(allSongs.getInt("bitrate"));
                songInfo.add(sanitizeOutput(allSongs.getString("genre")));
                rows.add(songInfo);
            }
        }
        catch (SQLException e)
        {
            ErrorHandler.error("failed to retrieve song data from database", e);
        }
        finally
        {
            if (stmt != null)
            {
                closeStatement(stmt);
            }
        }
        return rows;
    }

    public Vector<Vector<Object>> getSearchResults(String whereClause)
    {
        final Vector<Vector<Object>> rows = new Vector<>();
        // Clear path list
        pathList.clear();
        Statement stmt = null;
        try
        {
            ensureConnection();
            stmt = con.createStatement();
            stmt.setQueryTimeout(30);
            ResultSet allSongs = stmt.executeQuery("SELECT * FROM songs"
                    + whereClause);
            while (allSongs.next())
            {
                // Add path to list of paths instead of table model
                pathList.add(sanitizeOutput(allSongs.getString("path")));
                final Vector<Object> songInfo = new Vector<>();
                songInfo.add(sanitizeOutput(allSongs.getString("name")));
                songInfo.add(sanitizeOutput(allSongs.getString("artist")));
                songInfo.add(sanitizeOutput(allSongs.getString("album")));
                songInfo.add(sanitizeOutput(allSongs.getString("track")));
                songInfo.add(allSongs.getString("length"));
                songInfo.add(sanitizeOutput(allSongs.getString("year")));
                songInfo.add(sanitizeOutput(allSongs.getString("disc")));
                songInfo.add(allSongs.getInt("bitrate"));
                songInfo.add(sanitizeOutput(allSongs.getString("genre")));
                rows.add(songInfo);
            }
            if (rows.isEmpty())
            {
                // No search results
                final Vector<Object> info = new Vector<>();
                info.add("No Results");
                rows.add(info);
            }
        }
        catch (SQLException e)
        {
            ErrorHandler.
                    error("failed to retrieve search results from database", e);
        }
        finally
        {
            if (stmt != null)
            {
                closeStatement(stmt);
            }
        }
        return rows;
    }

    private void createSeekTable()
    {
        SeekPoint sp = new SeekPoint(1, 2, 3);
        SeekTable st = new SeekTable(new SeekPoint[]
        {
            sp
        }, false);
        File outputFile = new File("seektable.dat");
        if (outputFile.exists())
        {
            outputFile.delete();
        }
        try (FileOutputStream fos = new FileOutputStream(outputFile);
                ObjectOutputStream ofs = new ObjectOutputStream(fos);)
        {
            ofs.writeObject(st);
        }
        catch (IOException e)
        {

        }
    }

    public SeekTable[] getAllSeekTables()
    {
        final List<SeekTable> list = new ArrayList<>();
        Statement stmt = null;
        ByteArrayInputStream byteStream = null;
        ObjectInputStream ois = null;
        try
        {
            ensureConnection();
            stmt = con.createStatement();
            stmt.setQueryTimeout(30);
            ResultSet allSongs = stmt.executeQuery("SELECT * FROM songs");
            while (allSongs.next())
            {
                byteStream = new ByteArrayInputStream(allSongs.getBytes(
                        "seektable"));
                ois = new ObjectInputStream(byteStream);
                list.add((SeekTable) ois.readObject());
            }
        }
        catch (SQLException | IOException | ClassNotFoundException e)
        {
            ErrorHandler.error("failed to retrieve song data from database", e);
        }
        finally
        {
            if (stmt != null)
            {
                closeStatement(stmt);
            }
            if (byteStream != null)
            {
                try
                {
                    byteStream.close();
                }
                catch (IOException ex)
                {

                }
            }
            if (ois != null)
            {
                try
                {
                    ois.close();
                }
                catch (IOException ex)
                {

                }
            }
        }
        SeekTable[] seekTables = new SeekTable[list.size()];
        list.toArray(seekTables);
        return seekTables;
    }
}
