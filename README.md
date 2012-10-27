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
* Remove HandleEnumerator and FigureEnumerator and replace them with Seq
