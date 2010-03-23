package br.edu.ufcg.lsd.gridsim.output;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import br.edu.ufcg.lsd.gridsim.GlobalScheduler;
import br.edu.ufcg.lsd.gridsim.Job;

public class DatabaseOutput implements InterfaceOutput {

    private Connection conn;
    private PreparedStatement stat;
    private int count;

    public DatabaseOutput(String fileName) throws SQLException {
        File file = new File(fileName);

        if (file.isFile()) {
            file.delete();
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Missing SQLite JDBC library.");
        }

        conn = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
        Statement stat = conn.createStatement();
        conn.setAutoCommit(false);
        
        // JobID = 1, SubmitTime = 2, RunTime = 3, NProc = 4, UserID = 5, OrigSiteID = 6, Type = 7
        // Types: SEQ, BOT, MPI, MPI_BOT
        
        try {
            stat
                .execute("CREATE TABLE Jobs (JobID INTEGER, SubmitTime INTEGER, WaitTime INTEGER, RunTime INTEGER, NProc INTEGER, OrigSiteID VARCHAR, Type VARCHAR, Waste INTEGER, Utilization REAL, Source VARCHAR);");            
            conn.commit();
        } catch (Exception e) {
            
        }
        
        this.stat = conn.prepareStatement("INSERT INTO Jobs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    }

    public void close() throws SQLException {
        stat.executeBatch();
        conn.commit();
        conn.close();
    }

    private void addJob(Job job, Double utilization) {
    	// JobID INTEGER, SubmitTime INTEGER, WaitTime INTEGER, RunTime INTEGER, 
    	// NProc INTEGER, OrigSiteID VARCHAR, Type VARCHAR, Waste INTEGER, Utilization REAL)
        count++;
        try {
            stat.setInt(1, job.getJobId());
            stat.setInt(2, job.getSubmitTime());
            stat.setInt(3, job.getWaitedTime());
            stat.setInt(4, job.getRunTime());
            stat.setInt(5, job.getNProc());
            stat.setString(6, job.getOrigSite());
//            stat.setString(7, job.getType());
            stat.setInt(8, job.getWastedTime());
            stat.setDouble(9, utilization);
//            stat.setString(10, job.getSource());
            stat.addBatch();
            if (count == 1024) {
                stat.executeBatch();
                conn.commit();
                count = 0;
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    
    @Override
    public void finishJob(int time, GlobalScheduler gs, Job job) {
    	//System.out.println(">>> finishJob.. " +  job.getJobId() + " " + job.getSource());
        addJob(job, gs.getUtilization());
    }
    
    @Override
    public void submitJob(int time, GlobalScheduler grid, Job job) {
    	//System.out.println(">>> Submitjob ... " + job.getType() + " " + job.getJobId() + " " + job.getSource());
        // Not Used (see finishJob(..))
    }

	@Override
	public void startJob(int time, String grid, Job job) {
		// TODO Auto-generated method stub
		
	}

}