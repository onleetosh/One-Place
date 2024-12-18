package org.yearup.data.mysql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * Base class for DAO (Data Access Object) classes interacting with a MySQL database.
 * Provides a common method for obtaining a database connection.
 */
public abstract class MySqlDaoBase
{
    // DataSource instance that is used to obtain database connections
    private DataSource dataSource;


    /**
     * Constructor to initialize the MySqlDaoBase with a DataSource.
     * @param dataSource The DataSource used to obtain database connections.
     */
    public MySqlDaoBase(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    /**
     * Gets a connection to the database using the DataSource.
     * @return A valid database Connection object.
     * @throws SQLException If there is an error while obtaining the connection.
     */
    protected Connection getConnection() throws SQLException
    {
        // Get and return the connection from the DataSource
        return dataSource.getConnection();
    }
}
