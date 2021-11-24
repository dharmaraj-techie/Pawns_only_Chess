package chess

open class ChessPiece {
    override fun toString(): String {
        return " "
    }
}

enum class Color(val colorChar: String, val stringName: String) {
    WHITE("W", "White"),
    BLACK("B","Black")
}

enum class PLAYER(val color: Color, var playerName: String = "") {
    WHITE_PLAYER(Color.WHITE),
    BLACK_PLAYER(Color.BLACK)
}


enum class GameStatus {
    FORWARD,
    CAPTURE,
    NPASS,
    INVALID,
    WIN,
}

