package be.howest.ti.mars.logic.data;

import org.h2.tools.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.logging.Logger;

/*
MBL: this is only a starter class to use a H2 database.
To make this class useful, please complete it with the topics seen in the module OOA & SD
- Make sure the conf/config.json properties are correct.
- The h2 web console is available at http://localhost:9000
- The h2 database file is located at ~/mars-db
- Hint:
  - Mocking this repository is not needed. Create database creating and population script in plain SQL.
    Use the @Before or @Before each (depending on the type of test) to quickly setup a fully populated db.
 */
public class MarsConnection {
    private static final MarsConnection INSTANCE = new MarsConnection();
    private static final Logger logger = Logger.getLogger(MarsConnection.class.getName());
    private Server dbWebConsole;
    private String username;
    private String password;
    private String url;

    private MarsConnection() { }

    public static MarsConnection getInstance() {
        return INSTANCE;
    }

    public void cleanUp() {
        dbWebConsole.stop();
    }

    public static void configure(String url, String username, String password, int console)
            throws SQLException {
        INSTANCE.username = username;
        INSTANCE.password = password;
        INSTANCE.url = url;
        INSTANCE.dbWebConsole = Server.createWebServer(
                "-ifNotExists",
                "-webPort", String.valueOf(console)).start();
        try{
            initDatabase();
        }catch (IOException ex){
            logger.warning(ex.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(INSTANCE.url,INSTANCE.username, INSTANCE.password);
    }

    private static void executeScript(String filename) throws IOException, SQLException{
        String createDbSql = readFile(filename);
        try(
            Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(createDbSql)
        ){
            stmt.executeUpdate();
        }
    }

    private static String readFile(String filename) throws IOException {
        Path file = Path.of(filename);
        return Files.readString(file);
    }

    public static void initDatabase() throws SQLException, IOException {
        executeScript("src/main/resources/h2/setupDB.sql");
    }
}
