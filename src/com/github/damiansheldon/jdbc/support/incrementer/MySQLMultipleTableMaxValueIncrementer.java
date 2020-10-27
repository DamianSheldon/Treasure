package com.github.damiansheldon.jdbc.support.incrementer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.incrementer.AbstractColumnMaxValueIncrementer;

/**
 * 
 * Inspired by MySQLMaxValueIncrementer, support generate primary key for multiple table.
 * 
 * <pre class="code">create table tab (id int unsigned not null primary key, text varchar(100));
 *	create table tab_sequence (tab varchar(255) not null, value int not null);
 *	insert into tab_sequence values("tab", 0);</pre>
 * @author meiliang
 *
 */
public class MySQLMultipleTableMaxValueIncrementer extends AbstractColumnMaxValueIncrementer {

	public static final String DEFAULT_INCREMENTER_NAME = "tab_sequence";
	
	public static final String DEFAULT_COLUMN_NAME_OF_TABLE = "tab";

	/** The next id to serve. */
	private long nextId = 0;

	/** The max id to serve. */
	private long maxId = 0;

	/** Whether or not to use a new connection for the incrementer. */
	private boolean useNewConnection = true;
	
	private String columnNameOfTable;
	
	private String columnValueOfTable;
	
	/**
	 * 
	 * @param dataSource the DataSource to use
	 * @param incrementerName the name of the sequence table to use
	 * @param columnNameOfTable the name of the column record table that use primary key
	 * @param columnValueOfTable  the value of table that use primary key
	 * @param columnName the name of the column in the sequence table to use
	 */
	public MySQLMultipleTableMaxValueIncrementer(DataSource dataSource, String incrementerName, String columnNameOfTable, String columnValueOfTable, String columnName) {		
		super(dataSource, incrementerName, columnName);
		
		assert columnNameOfTable != null;
		assert columnValueOfTable.length() > 0;
		this.columnNameOfTable = columnNameOfTable;
		
		assert columnValueOfTable != null;
		assert columnValueOfTable.length() > 0;
		
		this.columnValueOfTable = columnValueOfTable;
	}

	/**
	 * Set whether to use a new connection for the incrementer.
	 * <p>{@code true} is necessary to support transactional storage engines,
	 * using an isolated separate transaction for the increment operation.
	 * {@code false} is sufficient if the storage engine of the sequence table
	 * is non-transactional (like MYISAM), avoiding the effort of acquiring an
	 * extra {@code Connection} for the increment operation.
	 * <p>Default is {@code true} since Spring Framework 5.0.
	 * @since 4.3.6
	 * @see DataSource#getConnection()
	 */
	public void setUseNewConnection(boolean useNewConnection) {
		this.useNewConnection = useNewConnection;
	}
	
	@Override
	protected synchronized long getNextKey() throws DataAccessException {
		if (this.maxId == this.nextId) {
			/*
			* If useNewConnection is true, then we obtain a non-managed connection so our modifications
			* are handled in a separate transaction. If it is false, then we use the current transaction's
			* connection relying on the use of a non-transactional storage engine like MYISAM for the
			* incrementer table. We also use straight JDBC code because we need to make sure that the insert
			* and select are performed on the same connection (otherwise we can't be sure that last_insert_id()
			* returned the correct value).
			*/
			Connection con = null;
			Statement stmt = null;
			boolean mustRestoreAutoCommit = false;
			try {
				if (this.useNewConnection) {
					con = getDataSource().getConnection();
					if (con.getAutoCommit()) {
						mustRestoreAutoCommit = true;
						con.setAutoCommit(false);
					}
				}
				else {
					con = DataSourceUtils.getConnection(getDataSource());
				}
				stmt = con.createStatement();
				if (!this.useNewConnection) {
					DataSourceUtils.applyTransactionTimeout(stmt, getDataSource());
				}

				String columnName = getColumnName();
				
				String whereCondition = " where " + columnNameOfTable + " = \"" + columnValueOfTable + "\";";
				
				// Retrieve the new max of the sequence column...
				ResultSet rs = stmt.executeQuery("select " + columnName + " from " + getIncrementerName() + whereCondition);
				if (!rs.next()) {
					throw new DataAccessResourceFailureException("fail to retrieve the max of the sequence column");
				}
				this.maxId = rs.getLong(1) + getCacheSize();
				
				// Increment the sequence column...
				
				try {
					stmt.executeUpdate("update " + getIncrementerName() + " set " + columnName +
							" = " + this.maxId + whereCondition);
				}
				catch (SQLException ex) {
					throw new DataAccessResourceFailureException("Could not increment " + columnName + " for " +
							getIncrementerName() + " sequence table", ex);
				}
				finally {
					JdbcUtils.closeResultSet(rs);
				}
				
				this.nextId = this.maxId - getCacheSize() + 1;
			}
			catch (SQLException ex) {
				throw new DataAccessResourceFailureException("Could not obtain last_insert_id()", ex);
			}
			finally {
				JdbcUtils.closeStatement(stmt);
				if (con != null) {
					if (this.useNewConnection) {
						try {
							con.commit();
							if (mustRestoreAutoCommit) {
								con.setAutoCommit(true);
							}
						}
						catch (SQLException ignore) {
							throw new DataAccessResourceFailureException(
									"Unable to commit new sequence value changes for " + getIncrementerName());
						}
						JdbcUtils.closeConnection(con);
					}
					else {
						DataSourceUtils.releaseConnection(con, getDataSource());
					}
				}
			}
		}
		else {
			this.nextId++;
		}
		return this.nextId;
	}

}
