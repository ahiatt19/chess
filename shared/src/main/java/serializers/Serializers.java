package serializers;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.*;

import java.lang.reflect.Type;

public class Serializers {
    public static Gson createSerializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Register the ChessGame deserializer
        gsonBuilder.registerTypeAdapter(ChessGame.class, new JsonDeserializer<ChessGame>() {
            @Override
            public ChessGame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();

                // Deserialize ChessBoard from the JSON
                ChessBoard chessBoard = context.deserialize(jsonObject.get("game"), ChessBoard.class);
                ChessGame game = new ChessGame();
                game.setBoard(chessBoard);

                if (jsonObject.has("currentTeamTurn")) {
                    String currentTurn = jsonObject.get("currentTeamTurn").getAsString();
                    game.setTeamTurn(ChessGame.TeamColor.valueOf(currentTurn));
                }
                if (jsonObject.has("gameOver")) {
                    boolean gameOver = jsonObject.get("gameOver").getAsBoolean();
                    game.setGameOver(gameOver);
                }

                return game;
            }
        });
        return gsonBuilder.create();
    }

    public static class ChessGameSerializer implements JsonSerializer<ChessGame> {
        @Override
        public JsonElement serialize(ChessGame chessGame, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            // Serialize the current team turn
            jsonObject.addProperty("currentTeamTurn", chessGame.getTeamTurn().toString());

            // Serialize the chess board (game)
            JsonElement boardJson = context.serialize(chessGame.getBoard());
            jsonObject.add("game", boardJson);

            return jsonObject;
        }
    }
}
