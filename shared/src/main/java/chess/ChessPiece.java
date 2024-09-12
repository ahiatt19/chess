package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }



    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        switch (type) {
            case BISHOP:
                //right up diagonal
                for (int r = myPosition.getRow() + 1, c = myPosition.getColumn() + 1; r < 9 && c < 9; r++, c++) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
                }
                //left up diagonal
                for (int r = myPosition.getRow() + 1, c = myPosition.getColumn() - 1; r < 9 && c > 0; r++, c--) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
                }
                //bottom left diagonal
                for (int r = myPosition.getRow() - 1, c = myPosition.getColumn() - 1; r > 0 && c > 0; r--, c--) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
                }
                //bottom right diagonal
                for (int r = myPosition.getRow() - 1, c = myPosition.getColumn() + 1; r > 0 && c < 9; r--, c++) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
                }
                break;
        }
        return moves;
    }
}
