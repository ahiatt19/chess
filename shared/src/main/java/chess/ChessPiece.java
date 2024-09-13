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

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
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
                    if (isvalidmove(board, myPosition, moves, r, c)) break;
                }
                //System.out.println("BREAKS");
                //left up diagonal
                for (int r = myPosition.getRow() + 1, c = myPosition.getColumn() - 1; r < 9 && c > 0; r++, c--) {
                    if (isvalidmove(board, myPosition, moves, r, c)) break;
                }
                //bottom left diagonal
                for (int r = myPosition.getRow() - 1, c = myPosition.getColumn() - 1; r > 0 && c > 0; r--, c--) {
                    if (isvalidmove(board, myPosition, moves, r, c)) break;
                }
                //bottom right diagonal
                for (int r = myPosition.getRow() - 1, c = myPosition.getColumn() + 1; r > 0 && c < 9; r--, c++) {
                    if (isvalidmove(board, myPosition, moves, r, c)) break;
                    
                }
                break;
            case ROOK:
                //UP
                for (int r = myPosition.getRow() + 1; r < 9; r++) {
                    if (isvalidmove(board, myPosition, moves, r, myPosition.getColumn())) break;
                }
                //RIGHT
                for (int c = myPosition.getColumn() + 1; c < 9; c++) {
                    if (isvalidmove(board, myPosition, moves, myPosition.getRow(), c)) break;
                }
                //DOWN
                for (int r = myPosition.getRow() - 1; r > 0; r--) {
                    if (isvalidmove(board, myPosition, moves, r, myPosition.getColumn())) break;
                }
                //LEFT
                for (int c = myPosition.getColumn() - 1; c > 0; c--) {
                    if (isvalidmove(board, myPosition, moves, myPosition.getRow(), c)) break;
                }
        }

        return moves;
    }

    public boolean isvalidmove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int r, int c) {
        if (board.isEmpty(new ChessPosition(r, c))) {
            //System.out.println("EMPTY");
            moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
        } else if (board.getPiece(new ChessPosition(r, c)).pieceColor != pieceColor) {
            //System.out.println("INVALID");
            moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
            return true;
        } else if (board.getPiece(new ChessPosition(r, c)).pieceColor == pieceColor) {
            //System.out.println("SAME INVALID");
            return true;
        }
        return false;
    }
}
