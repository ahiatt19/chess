package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private static ChessBoard game;
    private static TeamColor currentTeamTurn;

    public ChessGame() {
        game = new ChessBoard();
        game.resetBoard();
        currentTeamTurn = TeamColor.WHITE;
    }

    @Override
    public String toString() {
        return "ChessGame{ " +
                "game=" + game +
                "} Current_Turn= " + currentTeamTurn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(game, chessGame.game);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(game);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamTurn;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getNextTeamColor(TeamColor teamColor) {
        if (teamColor == TeamColor.BLACK) {
            return TeamColor.WHITE;
        }
        return TeamColor.BLACK;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = game.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(game, startPosition);
        TeamColor color = piece.getTeamColor();
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (isInCheck(color)) {
            for (ChessMove move : moves) {
                ChessPiece tempPiece = game.getPiece(move.getEndPosition());
                game.addPiece(move.getEndPosition(), piece);
                game.removePiece(move.getStartPosition());
                if (!isInCheck(color)) {
                    validMoves.add(move);
                }
                game.removePiece(move.getEndPosition());
                game.addPiece(move.getEndPosition(), tempPiece);
                game.addPiece(move.getStartPosition(), piece);
            }
        }else {
            for (ChessMove m : moves) {
                ChessPosition currPosition = m.getStartPosition();
                game.removePiece(currPosition);
                ChessPiece tempPiece = game.getPiece(m.getEndPosition());
                game.addPiece(m.getEndPosition(), piece);
                //System.out.println(game);
                if (!isInCheck(color)) {
                    validMoves.add(m);
                }
                game.removePiece(m.getEndPosition());
                game.addPiece(m.getEndPosition(), tempPiece);
                game.addPiece(currPosition, piece);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        try {
            boolean goodMove = false;
            //System.out.println(game);
            Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
            for (ChessMove m : validMoves) {
                if (m.getEndPosition().getRow() == move.getEndPosition().getRow() &&
                        m.getEndPosition().getColumn() == move.getEndPosition().getColumn()) {
                    goodMove = true;
                    break;
                }
            }
            if (goodMove) {
                if (game.getPiece(move.getStartPosition()).getTeamColor() != currentTeamTurn) {
                    throw new chess.InvalidMoveException("It is " + currentTeamTurn + "'s turn");
                }
                else if (game.getPiece(move.getStartPosition()).getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
                    game.addPiece(move.getEndPosition(),
                            new ChessPiece(game.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece()));
                }
                else {game.addPiece(move.getEndPosition(), game.getPiece(move.getStartPosition()));}
                game.removePiece(move.getStartPosition());
                currentTeamTurn = getNextTeamColor(currentTeamTurn);
            }
            else {throw new chess.InvalidMoveException("That Move is Not Valid");}
        } catch (NullPointerException n) {
            throw new chess.InvalidMoveException("Invalid Move");
        }
    }
    /**
     * This function is to check where all the pieces are on the board for a certain team.
     * */
    public Collection<ChessPosition> getTeamPositions(TeamColor color) {
        Collection<ChessPosition> positions = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int u = 1; u < 9; u++) {
                if (game.getPiece(new ChessPosition(i, u)) != null && game.getPiece(new ChessPosition(i, u)).getTeamColor() == color) {
                    positions.add(new ChessPosition(i, u));
                }
            }
        }
        return positions;
    }

    public boolean getChessPiece(ChessPosition pos) {
        return game.getPiece(pos) != null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> teamPositions = getTeamPositions(teamColor);
        TeamColor enemyColor = getNextTeamColor(teamColor);
        ChessPosition kingPosition = new ChessPosition(0, 0);
        //Find the kings position for the teamColor
        for (ChessPosition pos : teamPositions) {
            if (game.getPiece(pos).getPieceType() == ChessPiece.PieceType.KING) {
                kingPosition = pos;
            }
        }
        Collection<ChessPosition> enemyPositions = getTeamPositions(enemyColor);
        for (ChessPosition enemyPos : enemyPositions) {
            ChessPiece enemyPiece = game.getPiece(enemyPos);
            Collection<ChessMove> pieceMoves = enemyPiece.pieceMoves(game, enemyPos);
            for (ChessMove move : pieceMoves) {
                ChessPosition enemyMove = move.getEndPosition();
                if (enemyMove.getRow() == kingPosition.getRow() && enemyMove.getColumn() == kingPosition.getColumn()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;}
        Collection<ChessPosition> teamPositions = getTeamPositions(teamColor);
        for (ChessPosition pos : teamPositions) {
            Collection<ChessMove> moves = validMoves(pos);
            for (ChessMove m : moves) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor) & !isInCheckmate(teamColor)) {
            Collection<ChessPosition> teamPositions = getTeamPositions(teamColor);
            for (ChessPosition pos : teamPositions) {
                for (ChessMove m : validMoves(pos)) {
                    return false;
                }
            }
        }
        return !isInCheckmate(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        game = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return game;
    }
}
