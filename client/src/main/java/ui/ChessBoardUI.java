package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class ChessBoardUI {
    //Board Dimensions
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;

    public static void main() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        blackPerspective(out, chessBoard);
        out.println();

        whitePerspective(out, chessBoard);




    }

    public static void printBoards(ChessBoard board) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        blackPerspective(out, board);
        out.println();

        whitePerspective(out, board);
    }

    private static void whitePerspective(PrintStream out, ChessBoard chessBoard) {
        //out.print(chessBoard);

        String[] whiteHeaders = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        drawHeaderFooter(out, whiteHeaders);

        drawChessBoard(out, chessBoard, "white");

        drawHeaderFooter(out, whiteHeaders);
    }

    private static void blackPerspective(PrintStream out, ChessBoard chessBoard) {
        String[] blackHeaders = {" h ", " g ", " f ", " e ", " d ", " c ", " b ", " a "};
        drawHeaderFooter(out, blackHeaders);

        drawChessBoard(out, chessBoard, "black");

        drawHeaderFooter(out, blackHeaders);
    }

    private static void drawHeaderFooter(PrintStream out, String[] headers) {
        setBorder(out);

        out.print("   ");

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            out.print(SMALL_EMPTY);
            drawHeaderFooter(out, headers[boardCol]);
            out.print(SMALL_EMPTY);
            out.print(SMALL_EMPTY);
        }
        out.print("   ");
        setBlack(out);
        out.println();
    }

    private static void drawHeaderFooter(PrintStream out, String headerText) {
        printHeaderText(out, headerText);
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(player);
    }

    private static void printRow(PrintStream out, int row) {
        setBorder(out);
        out.print(" ");

        out.print(row);

        out.print(" ");
        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out, ChessBoard chessBoard, String perspective) {
        switch (perspective) {
            case "white":
                for (int boardRow = 1; boardRow <= BOARD_SIZE_IN_SQUARES; ++boardRow) {
                    int actualRow = (boardRow - 9) * -1;
                    printRow(out, actualRow);

                    drawRowOfSquares(out, boardRow, chessBoard, "white");
                    printRow(out, actualRow);
                    out.println();
                }
                break;
            case "black":
                for (int boardRow = 1; boardRow <= BOARD_SIZE_IN_SQUARES; ++boardRow) {

                    printRow(out, boardRow);

                    drawRowOfSquares(out, boardRow, chessBoard, "black");
                    printRow(out, boardRow);
                    out.println();
                }
                break;
        }

    }

    private static void drawRowOfSquares(PrintStream out, int boardRow, ChessBoard chessBoard, String perspective) {
        for (int squareRow = 1; squareRow <= SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for (int boardCol = 1; boardCol <= BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if ((boardRow + boardCol) % 2 == 0)
                    setLightPink(out);
                else {
                    setDarkPink(out);
                }
                int newRow = (boardRow - 9) * -1;
                int newCol = (boardCol - 9) * -1;
                switch (perspective) {
                    case "white":
                        if (chessBoard.getPiece(new ChessPosition(newRow, boardCol)) == null) {
                            out.print(EMPTY.repeat(1));
                            setBlack(out);
                        } else {
                            printPlayer(out, chessBoard.getPiece(new ChessPosition(newRow, boardCol)));
                            setBlack(out);
                        }
                        break;
                    case "black":
                        if (chessBoard.getPiece(new ChessPosition(boardRow, newCol)) == null) {
                            out.print(EMPTY.repeat(1));
                            setBlack(out);
                        } else {
                            printPlayer(out, chessBoard.getPiece(new ChessPosition(boardRow, newCol)));
                            setBlack(out);
                        }
                        break;
                }
            }
        }
    }

    private static void printPlayer(PrintStream out, ChessPiece chessPiece) {
        switch (chessPiece.getTeamColor()) {
            case ChessGame.TeamColor.BLACK:
                switch (chessPiece.getPieceType()) {
                    case ChessPiece.PieceType.ROOK: out.print(BLACK_ROOK); break;
                    case ChessPiece.PieceType.KNIGHT: out.print(BLACK_KNIGHT); break;
                    case ChessPiece.PieceType.BISHOP: out.print(BLACK_BISHOP); break;
                    case ChessPiece.PieceType.KING: out.print(BLACK_KING); break;
                    case ChessPiece.PieceType.QUEEN: out.print(BLACK_QUEEN); break;
                    case ChessPiece.PieceType.PAWN: out.print(BLACK_PAWN); break;
                }
                break;
            case ChessGame.TeamColor.WHITE:
                switch (chessPiece.getPieceType()) {
                    case ChessPiece.PieceType.ROOK: out.print(WHITE_ROOK); break;
                    case ChessPiece.PieceType.KNIGHT: out.print(WHITE_KNIGHT); break;
                    case ChessPiece.PieceType.BISHOP: out.print(WHITE_BISHOP); break;
                    case ChessPiece.PieceType.KING: out.print(WHITE_KING); break;
                    case ChessPiece.PieceType.QUEEN: out.print(WHITE_QUEEN); break;
                    case ChessPiece.PieceType.PAWN: out.print(WHITE_PAWN); break;
                }
                break;
        }
    }

    private static void setLightPink(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_PINK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setDarkPink(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_PINK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBorder(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(SET_TEXT_BOLD);
    }
}
