package application;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connexion {
	
	private static String login = "root";  
	 private static String password = "";  
	 private static String url="jdbc:mysql://localhost/dep_db"; 
	 private static Connection cn = null;
		
	 
	 static { 
		 
		 try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				
			}catch (ClassNotFoundException ex){
				System.out.println("probleme de chargement du driver");
				System.exit(1);
				
			}
	
		 try { 
		 cn=(Connection) DriverManager.getConnection(url,login,password);
		 System.out.println("connecter");
		 } 
		
		 catch (SQLException e) { e.printStackTrace();}
	} 
	 public static Connection getCn(){    
		 return cn;}
	 }


