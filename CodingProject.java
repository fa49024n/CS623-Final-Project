import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Faryal Alam - Coding Project using PostgreSQL
 */

public class CodingProject {

	public static void main(String args[]) throws SQLException, IOException, 
	ClassNotFoundException {

		// Load the PostgreSQL driver
		// Connect to the default database with credentials
		// You will have to change your credentials
		Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb", "postgres", "postgres");

		// For atomicity
		conn.setAutoCommit(false);

		// For isolation
		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

		Statement stmt1 = null;
		try {
			// Create statement object
			stmt1 = conn.createStatement();
			
			
			//Creating the tables product, stock, and depot
			stmt1.executeUpdate ("create table product(prod_id char(10), pname varchar(20), price integer)");
			stmt1.executeUpdate ("create table stock(prod_id char(10), dep_id char(20), quantity integer)");
			stmt1.executeUpdate ("create table depot(dep_id char(10), addr char(20), volume integer)");

			
			// Inserting data into Table Product
			stmt1.executeUpdate("insert into product values ('p1', 'tape', 2.5)");
			stmt1.executeUpdate("insert into product values ('p2', 'tv', 250)");
			stmt1.executeUpdate("insert into product values ('p3', 'vcr', 80)");
			

			//Inserting data into Table stock
			stmt1.executeUpdate("insert into stock values ('p1', 'd1', 1000)");
			stmt1.executeUpdate("insert into stock values ('p1', 'd2',-100)");
			stmt1.executeUpdate("insert into stock values ('p1', 'd4', 1200)");
			stmt1.executeUpdate("insert into stock values ('p3', 'd1', 3000)");
			stmt1.executeUpdate("insert into stock values ('p3', 'd4', 2000)");
			stmt1.executeUpdate("insert into stock values ('p2', 'd4', 1500)");
			stmt1.executeUpdate("insert into stock values ('p2', 'd1', -400)");
			stmt1.executeUpdate("insert into stock values ('p2', 'd2', 2000)");
			
			//Inserting data into Table depot
			stmt1.executeUpdate("insert into depot values ('d1', 'New York', 9000)");
			stmt1.executeUpdate("insert into depot values ('d2', 'Syracuse', 6000)");
			stmt1.executeUpdate("insert into depot values ('d4', 'New York', 2000)");
			
			//Creating Primary key for table product 
			stmt1.executeUpdate("ALTER TABLE Product ADD CONSTRAINT pk_product PRIMARY KEY (prod_id)");
			stmt1.executeUpdate("ALTER TABLE Product ADD CONSTRAINT ck_product_price CHECK (price > 0)");
			
			//Creating Primary key for table depot 
			stmt1.executeUpdate("ALTER TABLE Depot ADD CONSTRAINT pk_depot PRIMARY KEY (dep_id)");
			
			//Creating Primary key for table stock as well as foreign key constraints
			stmt1.executeUpdate("ALTER TABLE Stock ADD CONSTRAINT pk_stock PRIMARY KEY (prod_id, dep_id)");
			stmt1.executeUpdate("ALTER TABLE Stock ADD CONSTRAINT fk_stock_depot FOREIGN KEY (dep_id) REFERENCES depot(dep_id) ON UPDATE CASCADE");
			stmt1.executeUpdate("ALTER TABLE Stock ADD CONSTRAINT fk_stock_product FOREIGN KEY (prod_id) REFERENCES Product (prod_id) ON UPDATE CASCADE");

			
			//This is a prepared statement that is updating product p1 in product table to "pp1"
			String updateProductString = "update product set prod_id = ? where prod_id = ?";
				PreparedStatement updateProductStmt = conn.prepareStatement(updateProductString);
				updateProductStmt.setString(1,"pp1");
				updateProductStmt.setString(2, "p1");
				updateProductStmt.executeUpdate();
			
			//This is a prepared statement that is adding new data into the product table
			String addProductString = "insert into product values (?, ?, ?)";
					PreparedStatement addProductStmt = conn.prepareStatement(addProductString);
					addProductStmt.setString(1,"p100");
					addProductStmt.setString(2, "cd");
					addProductStmt.setInt(3, 5);
					addProductStmt.executeUpdate();

			//This is a prepared statement that is adding new data into the stock table
			String addStockString = "insert into stock values (?, ?, ?)";
					PreparedStatement addStockStmt = conn.prepareStatement(addStockString);
					addStockStmt.setString(1,"p100");
					addStockStmt.setString(2, "d2");
					addStockStmt.setInt(3, 50);
					addStockStmt.executeUpdate();
			
					
			//Lisiting all data from table product
			ResultSet rs = stmt1.executeQuery ("select * from product");
			while (rs.next() ){
				System.out.println(rs.getString("prod_id") + ", " + rs.getString("pname") + ", " + rs.getInt("price"));		
			}
			//Lisiting all data from table stock
			ResultSet rs1 = stmt1.executeQuery ("select * from stock");
			while (rs1.next() ){
				System.out.println(rs1.getString("prod_id") + ", " + rs1.getString("dep_id") + ", " + rs1.getInt("quantity"));		
			}
				
	
		} catch (SQLException e) {
			System.out.println("An exception was thrown");
			e.printStackTrace();
			// For atomicity
			conn.rollback();
			stmt1.close();
			conn.close();
			return;
		}
		conn.commit();
		stmt1.close();
		conn.close();
	}
}
