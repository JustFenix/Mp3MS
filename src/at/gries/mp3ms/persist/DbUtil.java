/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.gries.mp3ms.persist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {
//	private static final String USERNAME = "remoteAdmin"; //"dbuser";
//  private static final String PASSWORD = "x8635Shell"; //"dbpassword";
//  private static final String CONN_STRING = 
//          "jdbc:mysql://192.168.1.111/hwg?zeroDateTimeBehavior"
//          + "=CONVERT_TO_NULL&serverTimezone=MET";

  private static final String USERNAME = "dbuser";
  private static final String PASSWORD = "dbpassword";
  private static final String CONN_STRING =  "jdbc:mysql://localhost/mp3ee?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=MET&useSSL=false";
          //= "jdbc:mysql://localhost/employee?zeroDateTimeBehavior"
          //+ "=CONVERT_TO_NULL&serverTimezone=MET";

  /**
   * Stellt die Verbindung zur Datenbank her.
   *
   * @return Connection zur Verbindung mit der Datenbank
   * @throws SQLException
   * @throws java.lang.ClassNotFoundException
   */
  public static Connection getConnection() throws SQLException, ClassNotFoundException {
      Class.forName("com.mysql.cj.jdbc.Driver");
      return DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
  }

  /**
   * Erstellt eine SQL Exception mit erweiterten Parametern.
   *
   * Message ErrorCode SQLState
   *
   * @param e
   */
  public static void processException(SQLException e) {
      System.err.println("Error Message: " + e.getMessage());
      System.err.println("Error Code " + e.getErrorCode());
      System.err.println("SQL state: " + e.getSQLState());
  }
}
