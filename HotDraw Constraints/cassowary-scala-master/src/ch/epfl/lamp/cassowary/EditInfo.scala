package ch.epfl.lamp.cassowary

final class EditInfo(val constraint: Constraint, val clvEditPlus: SlackVar,
    val clvEditMinus: SlackVar, var prevEditConstant: Double, val index: Int)