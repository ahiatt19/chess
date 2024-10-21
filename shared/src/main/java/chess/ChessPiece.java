package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
        return getColorPiece();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(pieceColor);
        result = 31 * result + Objects.hashCode(type);
        return result;
    }

    public String getColorPiece() {
        if (getPieceType() == PieceType.KNIGHT) {
            if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
                    return "n";}
            return "N";
        }
        if (getPieceType() == PieceType.PAWN) {
            if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
                return "p";}
            return "P";
        }
        if (getPieceType() == PieceType.KING) {
            if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
                return "k";}
            return "K";
        }
        if (getPieceType() == PieceType.QUEEN) {
            if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
                return "q";}
            return "Q";
        }
        if (getPieceType() == PieceType.BISHOP) {
            if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
                return "b";}
            return "B";
        }
        if (getPieceType() == PieceType.ROOK) {
            if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
                return "r";}
            return "R";
        }
        return " ";
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
                for (int r = myPosition.getRow() + 1, c = myPosition.getColumn() + 1; r < 9 && c < 9; r++, c++) {
                    if (isvalidmove(board, myPosition, moves, r, c)) {break;}}
                for (int r = myPosition.getRow() + 1, c = myPosition.getColumn() - 1; r < 9 && c > 0; r++, c--) {
                    if (isvalidmove(board, myPosition, moves, r, c)) {break;}}
                for (int r = myPosition.getRow() - 1, c = myPosition.getColumn() - 1; r > 0 && c > 0; r--, c--) {
                    if (isvalidmove(board, myPosition, moves, r, c)) {break;}}
                for (int r = myPosition.getRow() - 1, c = myPosition.getColumn() + 1; r > 0 && c < 9; r--, c++) {
                    if (isvalidmove(board, myPosition, moves, r, c)) {break;}}break;
            case ROOK:
                for (int r = myPosition.getRow() + 1; r < 9; r++) {
                    if (isvalidmove(board, myPosition, moves, r, myPosition.getColumn())) {break;}}
                for (int c = myPosition.getColumn() + 1; c < 9; c++) {
                    if (isvalidmove(board, myPosition, moves, myPosition.getRow(), c)) {break;}}
                for (int r = myPosition.getRow() - 1; r > 0; r--) {
                    if (isvalidmove(board, myPosition, moves, r, myPosition.getColumn())) {break;}}
                for (int c = myPosition.getColumn() - 1; c > 0; c--) {
                    if (isvalidmove(board, myPosition, moves, myPosition.getRow(), c)) {break;}}break;
            case PAWN:
                if (pieceColor == ChessGame.TeamColor.WHITE) {
                    if (myPosition.getRow() == 2) {
                        if (board.isEmpty(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) &&
                                board.isEmpty(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn()))) {
                            pawnisvalidmove(board, myPosition, moves, myPosition.getRow() + 2, myPosition.getColumn(), true);}}
                    if (pawncankill(board, myPosition.getRow() + 1, myPosition.getColumn() - 1)) {
                        pawnisvalidmove(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() - 1, true);}
                    if (pawncankill(board, myPosition.getRow() + 1, myPosition.getColumn() + 1)) {
                        pawnisvalidmove(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() + 1, true);}
                    pawnisvalidmove(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn(), false);
                } else if (pieceColor == ChessGame.TeamColor.BLACK) {
                    if (myPosition.getRow() == 7) {
                        if (board.isEmpty(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) &&
                                board.isEmpty(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn()))) {
                            pawnisvalidmove(board, myPosition, moves, myPosition.getRow() - 2, myPosition.getColumn(), true);}}
                    if (pawncankill(board, myPosition.getRow() - 1, myPosition.getColumn() - 1)) {
                        pawnisvalidmove(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() - 1, true); }
                    if (pawncankill(board, myPosition.getRow() - 1, myPosition.getColumn() + 1)) {
                        pawnisvalidmove(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() + 1, true); }
                    pawnisvalidmove(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn(), false);}break;
            case QUEEN:
                for (int r = myPosition.getRow() + 1; r < 9; r++) {
                    if (isvalidmove(board, myPosition, moves, r, myPosition.getColumn())) {break;}}
                for (int c = myPosition.getColumn() + 1; c < 9; c++) {
                    if (isvalidmove(board, myPosition, moves, myPosition.getRow(), c)) {break;}}
                for (int r = myPosition.getRow() - 1; r > 0; r--) {
                    if (isvalidmove(board, myPosition, moves, r, myPosition.getColumn())) {break;}}
                for (int c = myPosition.getColumn() - 1; c > 0; c--) {
                    if (isvalidmove(board, myPosition, moves, myPosition.getRow(), c)) {break;}}
                for (int r = myPosition.getRow() + 1, c = myPosition.getColumn() + 1; r < 9 && c < 9; r++, c++) {
                    if (isvalidmove(board, myPosition, moves, r, c)) {break;}}
                for (int r = myPosition.getRow() + 1, c = myPosition.getColumn() - 1; r < 9 && c > 0; r++, c--) {
                    if (isvalidmove(board, myPosition, moves, r, c)) {break;}}
                for (int r = myPosition.getRow() - 1, c = myPosition.getColumn() - 1; r > 0 && c > 0; r--, c--) {
                    if (isvalidmove(board, myPosition, moves, r, c)) {break;}}
                for (int r = myPosition.getRow() - 1, c = myPosition.getColumn() + 1; r > 0 && c < 9; r--, c++) {
                    if (isvalidmove(board, myPosition, moves, r, c)) {break;}}break;
            case KING:
                if (myPosition.getRow() + 1 < 9) {
                    isvalidmove(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn());
                    if (myPosition.getColumn() - 1 > 0) {
                        isvalidmove(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() - 1);}
                    if (myPosition.getColumn() + 1 < 9){
                        isvalidmove(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() + 1);}}
                if (myPosition.getRow() - 1 > 0) {
                    isvalidmove(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn());
                    if (myPosition.getColumn() - 1 > 0){
                        isvalidmove(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() - 1);}
                    if (myPosition.getColumn() + 1 < 9){
                        isvalidmove(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() + 1);}}
                if (myPosition.getColumn() + 1 < 9) {
                    isvalidmove(board, myPosition, moves, myPosition.getRow(), myPosition.getColumn() + 1);}
                if (myPosition.getColumn() - 1 > 0) {
                    isvalidmove(board, myPosition, moves, myPosition.getRow(), myPosition.getColumn() - 1);}break;
            case KNIGHT:
                if (myPosition.getRow() + 2 < 9) {
                    if (myPosition.getColumn() - 1 > 0) {
                        isvalidmove(board, myPosition, moves, myPosition.getRow() + 2, myPosition.getColumn() - 1);}
                    if (myPosition.getColumn() + 1 < 9) {
                        isvalidmove(board, myPosition, moves, myPosition.getRow() + 2, myPosition.getColumn() + 1);}}
                if (myPosition.getRow() - 2 > 0) {
                    if (myPosition.getColumn() - 1 > 0) {
                        isvalidmove(board, myPosition, moves, myPosition.getRow() - 2, myPosition.getColumn() - 1);}
                    if (myPosition.getColumn() + 1 < 9) {
                        isvalidmove(board, myPosition, moves, myPosition.getRow() - 2, myPosition.getColumn() + 1);}}
                if (myPosition.getColumn() + 2 < 9 ) {
                    if (myPosition.getRow() - 1 > 0) {
                        isvalidmove(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() + 2);}
                    if (myPosition.getRow() + 1 < 9) {
                        isvalidmove(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() + 2);}}
                if (myPosition.getColumn() - 2 > 0 ) {
                    if (myPosition.getRow() - 1 > 0) {
                        isvalidmove(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() - 2);}
                    if (myPosition.getRow() + 1 < 9){
                        isvalidmove(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() - 2);}}break;}
        return moves;
    }

    public boolean pawncankill(ChessBoard board, int r, int c) {
        //System.out.println(r + "," + c + " " + !board.isEmpty(new ChessPosition(r, c)));
        if (r < 9 && c < 9 && r > 0 && c > 0) {
            return !board.isEmpty(new ChessPosition(r, c)) && board.getPiece(new ChessPosition(r, c)).pieceColor != pieceColor;
        }
        return false;
    }

    public boolean pawnisvalidmove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int r, int c, boolean diagonal ) {
        //pawn move forward logic
        if (type == PieceType.PAWN && board.isEmpty(new ChessPosition(r, c)) && (pieceColor == ChessGame.TeamColor.WHITE && r == 8) ||
                (pieceColor == ChessGame.TeamColor.BLACK && r == 1)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(r, c), PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, new ChessPosition(r, c), PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, new ChessPosition(r, c), PieceType.ROOK));
            moves.add(new ChessMove(myPosition, new ChessPosition(r, c), PieceType.KNIGHT));
        } else if (board.isEmpty(new ChessPosition(r, c))) {
            moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
        } else if (board.getPiece(new ChessPosition(r, c)).pieceColor != pieceColor && diagonal) {
                //System.out.println("INVALID");
                moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
        } return false;
    }

    public boolean isvalidmove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int r, int c) {
        //pawn move forward logic
        if (board.isEmpty(new ChessPosition(r, c))) {
            //System.out.println("IS EMPTY");
            moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
        } else if (board.getPiece(new ChessPosition(r, c)).pieceColor != pieceColor) {
            //System.out.println("INVALID");
            moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
            return true;
        } else {return board.getPiece(new ChessPosition(r, c)).pieceColor == pieceColor;}
        return false;
    }
}
