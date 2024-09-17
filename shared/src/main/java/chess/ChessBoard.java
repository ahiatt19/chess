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
        return Arrays.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "|" + squares[0][0] + "|" + squares[0][1] + "|" + squares[0][2] + "|" + squares[0][3] + "|" +
                squares[0][4] + "|" + squares[0][5] + "|" + squares[0][6] + "|" + squares[0][7] + "|" + "\n" +
                "|" + squares[1][0] + "|" + squares[1][1] + "|" + squares[1][2] + "|" + squares[1][3] + "|" +
                squares[1][4] + "|" + squares[1][5] + "|" + squares[1][6] + "|" + squares[1][7] + "|" + "\n" +
                "|" + squares[2][0] + "|" + squares[2][1] + "|" + squares[2][2] + "|" + squares[2][3] + "|" +
                squares[2][4] + "|" + squares[2][5] + "|" + squares[2][6] + "|" + squares[2][7] + "|" + "\n" +
                "|" + squares[3][0] + "|" + squares[3][1] + "|" + squares[3][2] + "|" + squares[3][3] + "|" +
                squares[3][4] + "|" + squares[3][5] + "|" + squares[3][6] + "|" + squares[3][7] + "|" + "\n" +
                "|" + squares[4][0] + "|" + squares[4][1] + "|" + squares[4][2] + "|" + squares[4][3] + "|" +
                squares[4][4] + "|" + squares[4][5] + "|" + squares[4][6] + "|" + squares[4][7] + "|" + "\n" +
                "|" + squares[5][0] + "|" + squares[5][1] + "|" + squares[5][2] + "|" + squares[5][3] + "|" +
                squares[5][4] + "|" + squares[5][5] + "|" + squares[5][6] + "|" + squares[5][7] + "|" + "\n" +
                "|" + squares[6][0] + "|" + squares[6][1] + "|" + squares[6][2] + "|" + squares[6][3] + "|" +
                squares[6][4] + "|" + squares[6][5] + "|" + squares[6][6] + "|" + squares[6][7] + "|" + "\n" +
                "|" + squares[7][0] + "|" + squares[7][1] + "|" + squares[7][2] + "|" + squares[7][3] + "|" +
                squares[7][4] + "|" + squares[7][5] + "|" + squares[7][6] + "|" + squares[7][7] + "|" + "\n";
    }

    public int toarrayindex(int index, char kind) {
        if (kind == 'r')
            return 8 - index;
        if (kind == 'c')
            return index - 1;
        return -225;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int correct_row = toarrayindex(position.getRow(), 'r');
        int correct_col = toarrayindex(position.getColumn(), 'c');
        squares[correct_row][correct_col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        System.out.print("O: " + position.getRow() + " " + position.getColumn() + ", ");
        int correct_row = toarrayindex(position.getRow(), 'r');
        int correct_col = toarrayindex(position.getColumn(), 'c');
        System.out.println("F: " + correct_row + " " + correct_col);
        return squares[correct_row][correct_col];
    }

    public boolean isEmpty(ChessPosition position) {
        return getPiece(position) == null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //WHITE PIECES
        ChessPiece whitepawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        for (int c = 1; c < 9; c++)
            addPiece(new ChessPosition(2, c), whitepawn);
        ChessPiece whiterook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        addPiece(new ChessPosition(1, 1), whiterook);
        addPiece(new ChessPosition(1, 8), whiterook);
        ChessPiece whiteknight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        addPiece(new ChessPosition(1, 2), whiteknight);
        addPiece(new ChessPosition(1, 7), whiteknight);
        ChessPiece whitebishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        addPiece(new ChessPosition(1, 3), whitebishop);
        addPiece(new ChessPosition(1, 6), whitebishop);
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        ChessPiece blackpawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        //BLACK PIECES
        for (int c = 1; c < 9; c++)
            addPiece(new ChessPosition(7, c), blackpawn);
        ChessPiece blackrook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        addPiece(new ChessPosition(8, 1), blackrook);
        addPiece(new ChessPosition(8, 8), blackrook);
        ChessPiece blackknight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        addPiece(new ChessPosition(8, 2), blackknight);
        addPiece(new ChessPosition(8, 7), blackknight);
        ChessPiece blackbishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        addPiece(new ChessPosition(8, 3), blackbishop);
        addPiece(new ChessPosition(8, 6), blackbishop);
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
    }
}
