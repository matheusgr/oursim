package br.edu.ufcg.lsd.gridsim.input;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import br.edu.ufcg.lsd.gridsim.Job;

public class GWAWorkload implements Workload {

    private Connection conn;
    private ResultSet rs;
    private Job currentJob = null;
    private int timeStart = -1;
    private int timeEnd = -1;
    private String source;

    public GWAWorkload(String stringFile, String source) throws SQLException {

	File file = new File(stringFile);
	this.source = source;

	if (!file.isFile()) {
	    throw new RuntimeException("File doesn't exist: " + stringFile);
	}

	try {
	    Class.forName("org.sqlite.JDBC");
	} catch (ClassNotFoundException e) {
	    throw new RuntimeException("Missing SQLite JDBC library.");
	}

	conn = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
    }

    public void setIntervalInclusive(int start, int end, int simulationStartTime) {
	assert start != -1;
	assert end != -1;
	assert end > start;
	timeStart = start;
	timeEnd = end;
    }

    public void start() throws SQLException {
	Statement stat = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
	conn.setAutoCommit(true);

	// JobID = 1, SubmitTime = 2, RunTime = 3, NProc = 4, UserID = 5,
	// OrigSiteID = 6, Type = 7
	// Types: SEQ, BOT, MPI, MPI_BOT

	rs = stat.executeQuery("SELECT JobID, SubmitTime, RunTime, NProc, UserID, OrigSiteID, Type FROM Jobs WHERE " + getTimeSQLCondition()
		+ "NPROC > 0 AND Runtime >= 0 ORDER BY SubmitTime, JobID;");
    }

    private String getTimeSQLCondition() {
	if (timeStart != -1) {
	    return "SubmitTime >= " + timeStart + " AND SubmitTime <= " + timeEnd + " AND ";
	}
	return "";
    }

    public HashSet<String> getPeers(String... typesInclusive) throws SQLException {
	HashSet<String> result = new HashSet<String>();
	Statement stat = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
	conn.setAutoCommit(true);

	String typeQuery = "";

	if (typesInclusive == null) {
	    typeQuery = "";
	} else {
	    StringBuffer sb = new StringBuffer(" AND (");
	    for (int i = 0; i < typesInclusive.length - 1; i++) {
		sb.append(" Type = \"" + typesInclusive[i] + "\" OR ");
	    }
	    typeQuery = sb.toString() + " Type = \"" + typesInclusive[typesInclusive.length - 1] + "\")";
	}

	rs = stat.executeQuery("SELECT OrigSiteID FROM Jobs WHERE " + getTimeSQLCondition() + "NPROC > 0 AND Runtime >= 0 " + typeQuery
		+ " GROUP BY OrigSiteID " + "ORDER BY MAX(NProc) DESC;");

	while (rs.next()) {
	    result.add(getSiteName(rs.getString(1)));
	}
	stat.close();
	return result;
    }

    private String getSiteName(String origSiteID) {
	return source + ": " + origSiteID;
    }

    private Job createJob() throws SQLException {
	if (currentJob == null) {
	    if (!rs.next()) {
		return null;
	    }

	    // JobID = 1, SubmitTime = 2, RunTime = 3, NProc = 4, UserID = 5,
	    // OrigSiteID = 6, Type = 7
	    // Types: SEQ, BOT, MPI, MPI_BOT

	    // currentJob = new Job(rs.getInt(1), rs.getInt(2) -
	    // simulationStartTime,
	    // rs.getInt(3), rs.getInt(4), rs
	    // .getString(5), getSiteName(rs.getString(6)), rs.getString(7),
	    // source);
	}
	return currentJob;
    }

    /*
     * (non-Javadoc)
     * 
     * @see br.edu.ufcg.lsd.gridsim.input.WorkLoad#peek()
     */
    public Job peek() {
	try {
	    createJob();
	    return currentJob;
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}

    }

    public Job poll() {
	try {
	    createJob();
	    Job tmpJob = currentJob;
	    currentJob = null;
	    return tmpJob;
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see br.edu.ufcg.lsd.gridsim.input.WorkLoad#close()
     */
    public void close() {
	try {
	    rs.close();
	    conn.close();
	} catch (SQLException e) {
	    // Tried our best.
	}
    }

}
