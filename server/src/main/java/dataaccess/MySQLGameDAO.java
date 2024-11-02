package dataaccess;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MySQLGameDAO implements UserDAO, AuthDAO, GameDAO {

    public MySQLGameDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException ex) {
                System.out.println("didn't connect to DB");
            }
    }

    public void clearUsers() throws DataAccessException {
        var sql = "TRUNCATE users";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", sql, e.getMessage()));
        }
    }


    public void clearAuths() throws DataAccessException {
        var sql = "TRUNCATE authentication";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", sql, e.getMessage()));
        }
    }


    public void clearGames() throws DataAccessException {
        var sql = "TRUNCATE games";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", sql, e.getMessage()));
        }
    }


    public void createUser(UserData u) throws DataAccessException {
        var sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql)) {
                String hashedPassword = BCrypt.hashpw(u.password(), BCrypt.gensalt());
                ps.setString(1, u.username());
                ps.setString(2, hashedPassword);
                ps.setString(3, u.email());
                ps.executeUpdate();

            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", sql, e.getMessage()));
        }
    }


    public UserData getUser(String username) throws DataAccessException {
        var sql = "SELECT username, password, email FROM users WHERE username=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var password = rs.getString("password");
                        var email = rs.getString("email");
                        return new UserData(username, password, email);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", sql, e.getMessage()));
        }
    }


    public void createAuth(AuthData a) throws DataAccessException {
        var sql = "INSERT INTO authentication (username, authToken) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, a.username());
                ps.setString(2, a.authToken());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", sql, e.getMessage()));
        }
    }


    public AuthData getAuth(String authToken) throws DataAccessException {
        var sql = "SELECT username, authToken FROM authentication WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var username = rs.getString("username");
                        return new AuthData(username, authToken);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", sql, e.getMessage()));
        }
    }


    public void deleteAuth(String authToken) throws DataAccessException {
        var sql = "DELETE FROM authentication WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", sql, e.getMessage()));
        }
    }

    public int createGame(String gameName) throws DataAccessException {
        var sql = "INSERT INTO games (gameName, game) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, gameName);

                var serializer = new Gson();
                ChessGame chessGame = new ChessGame();
                //System.out.println(chessGame.game);
                var chessGameString = serializer.toJson(chessGame.game);
                ps.setString(2, chessGameString);

                ps.executeUpdate();
                //ps.executeQuery();
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    generatedKeys.next();
                    return generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to insert into database: %s, %s", sql, e.getMessage()));
        }
    }


    public ArrayList<GameData> listGames() throws DataAccessException {
        return new ArrayList<>();
    }



    public GameData getGame(int gameID) throws DataAccessException {
        var sql = "SELECT gameID, white_username, black_username, gameName, game FROM games WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, Integer.toString(gameID));
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var whiteUsername = rs.getString("white_username");
                        var blackUsername = rs.getString("black_username");
                        var gameName = rs.getString("gameName");
                        var jsonGame = rs.getString("game");

                        var serializer = new Gson();
                        var gameFromJson = serializer.fromJson(jsonGame, ChessGame.class);
                        //System.out.println(gameFromJson.getBoard().getPiece(new ChessPosition(1, 1)));
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, gameFromJson);
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", sql, e.getMessage()));
        }

    }


    public void updateGame(GameData gameData) throws DataAccessException {
        var sql = "UPDATE games SET white_username=?, black_username=?, game=? WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, gameData.whiteUsername());
                ps.setString(2, gameData.blackUsername());

                var serializer = new Gson();
                var chessGameString = serializer.toJson(gameData.game());
                ps.setString(3, chessGameString);
                ps.setInt(4, gameData.gameID());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", sql, e.getMessage()));
        }
    }


    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                `id` int NOT NULL AUTO_INCREMENT,
                `username` varchar(225) NOT NULL,
                `password` varchar(225) NOT NULL,
                `email` varchar(225),
                PRIMARY KEY (`id`),
                INDEX(`username`)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS authentication (
                `id` int NOT NULL AUTO_INCREMENT,
                `username` varchar(225) NOT NULL,
                `authToken` varchar(225) NOT NULL,
                PRIMARY KEY (`id`),
                INDEX(`username`),
                INDEX(`authToken`)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
                `gameID` int NOT NULL AUTO_INCREMENT,
                `white_username` varchar(225),
                `black_username` varchar(225),
                `gameName` varchar(225) NOT NULL,
                `game` varchar(1000) NOT NULL,
                PRIMARY KEY (`gameID`),
                INDEX(`gameName`)
            );
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
