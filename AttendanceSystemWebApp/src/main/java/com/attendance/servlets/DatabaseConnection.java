package com.attendance.servlets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
	
	public Connection getConnection() {
		Connection connection = null;

		// establish the connection
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendancesystem",
					"root", "MDouglas2kay");
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
		}
		
		return connection;
	}
	
}
