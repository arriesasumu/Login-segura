package dbupdate; // Define el paquete al que pertenece esta clase

// Importación de clases necesarias para trabajar con JDBC (conexión a base de datos)
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Importación de la librería para encriptar contrase?as usando BCrypt
import org.mindrot.jbcrypt.BCrypt;

public class DBUpdate {

    // Constantes para la conexión a la base de datos
    static final String DB_URL = "jdbc:mysql://localhost:3306/seguridad_db"; // URL del servidor de BD
    static final String DB_USER = "root";    // Usuario de la base de datos
    static final String DB_PASSWORD = "arries2016"; // Contrase?a del usuario

    public static void main(String[] args) {
        // Declaración de variables para la conexión y ejecución de sentencias SQL
        Connection connection = null;
        PreparedStatement pstmt = null;
        String sql = ""; // Para almacenar sentencias SQL
        String encriptedPassword; // Para almacenar contrase?as encriptadas

        try {
            // Cargar el driver JDBC de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver de la base de datos cargado.");

            // Establecer conexión con la base de datos
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Conexión Correcta");

            // Agregar la columna failed_attempts a la tabla 'users'
            sql = "alter table users add column failed_attempts int default 0";
            pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
            System.out.println("Columna failed_attempts a?adida.");

            // Agregar la columna last_attempt a la tabla 'users'
            sql = "alter table users add column last_attempt timestamp";
            pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
            System.out.println("Columna last_attempt a?adida.");

            // Encriptar y actualizar la contrase?a del usuario 'admin'
            encriptedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt(12)); // Encriptar con BCrypt (12 es la complejidad)
            System.out.println("La contrase?a 'admin123' encriptada es: " + encriptedPassword);

            sql = "update users set password = ? where username = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, encriptedPassword); // Contrase?a encriptada
            pstmt.setString(2, "admin");           // Usuario 'admin'
            pstmt.executeUpdate();
            System.out.println("Contrase?a del usuario 'admin' actualizada.");

            // Encriptar y actualizar la contrase?a del usuario 'usuario'
            encriptedPassword = BCrypt.hashpw("pass123", BCrypt.gensalt(12));
            System.out.println("La contrase?a 'pass123' encriptada es: " + encriptedPassword);

            sql = "update users set password = ? where username = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, encriptedPassword); // Contrase?a encriptada
            pstmt.setString(2, "usuario");         // Usuario 'usuario'
            pstmt.executeUpdate();
            System.out.println("Contrase?a del usuario 'usuario' actualizada.");

        } catch (ClassNotFoundException ex) {
            // Error si no se puede cargar el driver JDBC
            System.err.println("Error al cargar el Driver de la base de datos: " + ex.getMessage());

        } catch (SQLException ex) {
            // Error si falla la conexión o ejecución de SQL
            System.err.println("Error al conectar a la base de datos o al ejecutar el SQL: " + ex.getMessage());

        } finally {
            // Cerrar recursos (PreparedStatement y Connection) para evitar fugas de memoria
            try {
                if(pstmt != null){ pstmt.close(); }
                if(connection != null){ connection.close(); } 
            } catch (SQLException ex){
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
                System.err.println("Error al cerrar el PreparedStatement: " + ex.getMessage());
            }
        }
    }
}

