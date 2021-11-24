package chess

class Pawn(val id:Int, val color: Color, var x1: Int, var y1:Int): ChessPiece() {
    override fun toString(): String {
        return color.colorChar
    }
}