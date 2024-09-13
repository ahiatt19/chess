package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }

    public int[] toarrayindex(int row, int col) {
        return new int[]{8 - row, col - 1};
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int[] cor_indexes = toarrayindex(position.getRow(), position.getColumn());
        squares[cor_indexes[0]][cor_indexes[1]] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        //System.out.println("Orig: " + position.getRow() + " " + position.getColumn());
        int[] cor_indexes = toarrayindex(position.getRow(), position.getColumn());
        //System.out.println("Fixed: " + cor_indexes[0] + " " + cor_indexes[1]);
        return squares[cor_indexes[0]][cor_indexes[1]];
    }

    public boolean isEmpty(ChessPosition position) {
        //System.out.println("EMPTY IN BOARD");
        return getPiece(position) == null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }
}
