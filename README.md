HotDraw
=======


##[JHotDraw](https://github.com/gulrich/HotDraw/tree/master/JHotDraw%20[Java]) [Java]##
* Based on the 6th version of [JHotDraw](http://www.jhotdraw.org/)
* Remove the unused classes and functionalities
* Use java collections instead of the custom collections (collection factory)

##Translation from JHotDraw to SHotDraw##
* Port tests into Scala, as they are not used by any class outside the test package
* Port the rest of the code into Scala
* Use IntelliJ for a first translation and modify the obtained code to solve compilation and other problems. The main problems are:
  * nulls and returns everywhere.
  * Java collections instead of Scala collections
  * The java for loops are automatical translated to:
  ` {
    var i: Int
    while(i < array.size) {
      //do something
      i += 1; i - 1
    }
    }
   `
  * Change the Java collections to Scala collections [done]
  * Change the Java constructors into Scala constructors (cannot use super(...) in Scala)

##[SHotDraw](https://github.com/gulrich/HotDraw/tree/master/SHotDraw%20[Scala]) [Scala]##
* Fix remaining errors and bugs
* Create NullObjects (e.g. NullFigure) to initialize fields, instead of using null
* Use ArrayBuffer instead of scala List to replace java ArrayList
* Attributes: use fields instead of Map[name: String, attribute: Any]
  * Event. use virtual classes
* Remove code from the figure objects to put it in a super Trait, in order to reduce the code duplication.
  * Create for instance a RectangularFigure for Figure with a rectangular DisplayBox (Rectangle, Ellipse, Triangle, etc.)
* Remove HandleEnumerator and FigureEnumerator
* Replace Iterator with Seq
* Code cleaning:
  * Some Options left: In UndoManager, probably need a NoUndoable. In IconKit and ToolButton, need a NoImage. Similar in DNDHelper.
  * Replace setX/getX by x/x_=
  * Use type inference, no need for type ascription in val tracker: MediaTracker = new MediaTracker(this) (I know this probably comes from the IntelliJ translator)
  * There are quite few "import java.lang.Object" and "extends Object" clauses, which are not necessary.
  * See whether you can replace of the interface X, class AbstractX implements X pattern by a single trait X.
  * Some objects contain serialVersionUID some don't. I don't really know what it necessary here and what not.
  * Look whether you can lift some of the static stuff. For example, Figure.EMPTY_RECTANGLE looks like it can be lifted into a Recangle.Empty object, since it can be useful not only in the context of Figures. There also is a Handle.HANDLESIZE. I guess that should not be hardcoded in the framework.
  * The general pattern followed by many Scala users seems to be to put parenthesis for methods where the side effect is important. This is usually true for all methods returning Unit. For example, in ConnectionFigure, there are methods updateConnection, disconnectEnd and so on that should probably read: updateConnection()
