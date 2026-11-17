package filey.modelo.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionBD {
    private static final String URL =
        "jdbc:mysql://localhost:3306/filey?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Merida";
    private static final String ARCHIVO = "db.properties";

    private ConexionBD() { }

    /** Usuario de solo lectura (Observador). */
    public static Connection obtener() throws SQLException {
        return conectar("FILEY_DB_USER", "FILEY_DB_PASS");
    }

    /** Usuario con permiso de escritura (Gestor). */
    public static Connection obtenerEscritura() throws SQLException {
        return conectar("FILEY_DB_USER_W", "FILEY_DB_PASS_W");
    }

    private static Connection conectar(String varUsuario, String varClave) throws SQLException {
        String usuario = leer(varUsuario);
        String clave = leer(varClave);
        if (usuario == null || clave == null) {
            throw new IllegalStateException("Faltan " + varUsuario + " y " + varClave
                    + ". Se buscó en las variables de entorno y en: "
                    + new File(ARCHIVO).getAbsolutePath());
        }
        return DriverManager.getConnection(URL, usuario, clave);
    }

    /** Busca primero en las variables de entorno y luego en db.properties. */
    private static String leer(String nombre) {
        String valor = System.getenv(nombre);
        if (valor != null) {
            return valor;
        }
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(ARCHIVO)) {
            props.load(in);
        } catch (IOException e) {
            return null;
        }
        return props.getProperty(nombre);
    }
}