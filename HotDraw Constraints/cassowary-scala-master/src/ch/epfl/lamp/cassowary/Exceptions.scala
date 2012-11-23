package ch.epfl.lamp.cassowary

class CLException(msg: String = null) extends RuntimeException(msg)

class ConstraintNotFoundException(cn: Constraint) extends CLException("Constraint not found in the tableau: " + cn)

class NonlinearExpressionException extends CLException("The resulting expression would be nonlinear")

class NotEnoughStaysException extends CLException("There are not enough stays to give specific values to every variable")

class RequiredFailureException extends CLException("A required constraint cannot be satisfied")

class TooDifficultException extends CLException("The constraints are too difficult to solve")