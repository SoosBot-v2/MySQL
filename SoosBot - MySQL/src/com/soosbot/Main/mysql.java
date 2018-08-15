package com.soosbot.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class mysql {
	private static Connection con = null;
	public mysql(String Username, String Password, String Database, String Hostname, int Port) {
		connect(Username, Password, Database, Hostname, Port);
	}
	
	public static Connection getCon() { return con; }
	
	private void connect(String Username, String Password, String Database, String Hostname, int Port) {
		String conStr = "jdbc:mysql://" + Hostname + ":" + Port + "/" + Database + "?user="+Username+"&password="+Password;
		try {
			if (con != null && !con.isClosed())
				throw new Exception("Connection already established!");
			
			con = DriverManager.getConnection(conStr);
		    final ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor();
		    thread.scheduleAtFixedRate(new Runnable() {
		        @Override
		        public void run() {
		        	try {
						antiTimeoutLoop();
					} catch (SQLException | InterruptedException e) {
						e.printStackTrace();
					}
		        }
		    }, 0, 1, TimeUnit.SECONDS);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public static ResultSet Query(String sql, String ...args) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(sql);
		for (int i = 0; i < args.length; i++)
			stmt.setString(i+1, args[i]);
		
		return stmt.executeQuery();
	}
	
	public static boolean Exec(String sql, String ...args) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(sql);
		for (int i = 0; i < args.length; i++)
			stmt.setString(i+1, args[i]);
		
		return stmt.execute();
	}
	
	private void antiTimeoutLoop() throws SQLException, InterruptedException {
		while (true) {
			if (con.isClosed())
				break;
			PreparedStatement stmt = con.prepareStatement("SELECT 1+1");
			stmt.execute();
			Thread.sleep(3000);
		}
	}
}