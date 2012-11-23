package scala.collection.mutable

import ch.epfl.lamp.cassowary.AbstractVar

class FastMap[K, V >: Null <: AnyRef] extends HashMap[K, V] {
  override def default(k: K) = null

  //final def foreachE[U](f: Entry => U) = { entriesIterator.foreach(f) }

  // An apply that doesn't create Some's internally.
  final override def apply(key: K): V = {
    val e = findEntry(key)
    if (e == null) null
    else e.value
  }

  // no Some's
  override def update(key: K, value: V) {
    val e = findEntry(key)
    if (e == null) { addEntry(new Entry(key, value)) }
    else { val v = e.value; e.value = value }
  }

  final def foreachE[U](f: (K,V) => U) {
    val tab = table
    var idx = tab.length - 1
    var entry: Entry = null
    while(true) {
      while (entry == null) {
        if(idx < 0) return
        entry = tab(idx).asInstanceOf[Entry]
        idx = idx - 1
      }
      f(entry.key, entry.value)
      entry = entry.next
    }
  }
}

class FastSet[A >: Null <: AnyRef] extends HashSet[A] {
  final override def -= (elem: A): this.type = { removeAnyRefEntry(elem); this }

  // copied from HashSet.scala, removed Option creation.
  def removeAnyRefEntry(elem: A) : A = {
    //if (tableDebug) checkConsistent()
    def precedes(i: Int, j: Int) = {
      val d = table.length >> 1
      if (i <= j) j - i < d
      else i - j > d
    }
    var h = index(elemHashCode(elem))
    var entry = table(h)
    while (null != entry) {
      if (entry == elem) {
        var h0 = h
        var h1 = (h0 + 1) % table.length
        while (null != table(h1)) {
          val h2 = index(elemHashCode(table(h1).asInstanceOf[A]))
          //Console.println("shift at "+h1+":"+table(h1)+" with h2 = "+h2+"? "+(h2 != h1)+precedes(h2, h0)+table.length)
          if (h2 != h1 && precedes(h2, h0)) {
            //Console.println("shift "+h1+" to "+h0+"!")
            table(h0) = table(h1)
            h0 = h1
          }
          h1 = (h1 + 1) % table.length
        }
        table(h0) = null
        tableSize -= 1
        nnSizeMapRemove(h0)
        //if (tableDebug) checkConsistent()
        return entry.asInstanceOf[A]
      }
      h = (h + 1) % table.length
      entry = table(h)
    }
    null
  }
}

final class Term(val key: AbstractVar, var value: Double) extends HashEntry[AbstractVar, Term] with Serializable {
  override def toString = chainString

  def chainString = {
    "(kv: " + key + ", " + value + ")" + (if (next != null) " -> " + next.toString else "")
  }
}

abstract class Terms {
  def foreachE[U](f: (AbstractVar, Double) => U)
  def update(key: AbstractVar, value: Double)
  def apply(key: AbstractVar): Double
  def -=(key: AbstractVar): this.type
  def size: Int
  def ++=(xs: Terms): this.type = {
    xs foreachE { (k, v) => this(k) = v }
    this
  }
}

class TermsBuffer extends Terms with Serializable {
  private var vars = new Array[AbstractVar](4)
  private var coeffs = new Array[Double](4)
  private var size0 = 0

  def size = size0

  def -=(key: AbstractVar): this.type = {
    remove(indexForKey(key))
    this
  }

  private def indexForKey(key: AbstractVar): Int = {
    var i = 0
    while(i < size) {
      if (vars(i) == key) return i
      i+= 1
    }
    -1
  }

  override def clone: TermsBuffer = {
    val b = new TermsBuffer
    b.vars = this.vars.clone
    b.coeffs = this.coeffs.clone
    b.size0 = this.size0
    b
  }

  /*private def entryForKey(key: AbstractVar): Term = {
    var i = 0
    while(i < size) {
      val t = this(i)
      if (t.key == key) return t
      i+= 1
    }
    null
  }*/

  def apply(key: AbstractVar): Double = {
    var i = 0
    while(i < size) {
      val v = vars(i)
      if (v == key) return coeffs(i)
      i+= 1
    }
    0
  }

  def update(key: AbstractVar, value: Double) {
    val i = indexForKey(key)
    if (i != -1) coeffs(i) = value
    else {
      ensureSize(size0 + 1)
      vars(size0) = key
      coeffs(size0) = value
      size0 += 1
    }
  }

  protected def ensureSize(n: Int) {
    if (n > vars.length) {
      var newsize = vars.length * 2
      while (n > newsize)
        newsize = newsize * 2

      val newVars = new Array[AbstractVar](newsize)
      compat.Platform.arraycopy(vars, 0, newVars, 0, size0)
      vars = newVars

      val newCoeffs = new Array[Double](newsize)
      compat.Platform.arraycopy(coeffs, 0, newCoeffs, 0, size0)
      coeffs = newCoeffs
    }
  }

  @inline final def foreachE[U](f: (AbstractVar, Double @specialized) => U) = {
    var i = 0
    val len = size
    while (i < len) {
      f(varAt(i), coeffAt(i))
      i += 1
    }
  }

  def varAt(i: Int) = vars(i)
  def coeffAt(i: Int) = coeffs(i)

  def remove(n: Int, count: Int) {
    //require(count >= 0, "removing negative number of elements")
    if (n < 0 || n > size0 - count) throw new IndexOutOfBoundsException(n.toString)
    copy(n + count, n, size0 - (n + count))
    reduceToSize(size0 - count)
  }

  protected def copy(m: Int, n: Int, len: Int) {
    compat.Platform.arraycopy(vars, m, vars, n, len)
    compat.Platform.arraycopy(coeffs, m, coeffs, n, len)
  }

  /** Removes the element at a given index position.
   *
   *  @param n  the index which refers to the element to delete.
   *  @return   the element that was formerly at position `n`.
   */
  def remove(n: Int) {
    remove(n, 1)
  }

  def reduceToSize(sz: Int) {
    require(sz <= size0)
    while (size0 > sz) {
      size0 -= 1
      vars(size0) = null
    }
  }
}

class TermsMap private[collection] (contents: HashTable.Contents[AbstractVar, Term])
  extends Terms with collection.mutable.Map[AbstractVar, Double]
   with MapLike[AbstractVar, Double, TermsMap]
   with HashTable[AbstractVar, Term]
   with Serializable {
  initWithContents(contents)

  type Entry = Term

  protected def createNewEntry[B](key: AbstractVar, value: B): Entry = {
    new Term(key, value.asInstanceOf[Double])
  }

  override def empty: TermsMap = new TermsMap
  override def clear() = clearTable()
  override def size: Int = tableSize

  def this() = this(null)

  def get(key: AbstractVar): Option[Double] = {
    val e = findEntry(key)
    if (e == null) None
    else Some(e.value)
  }

  override def put(key: AbstractVar, value: Double): Option[Double] = {
    val e = findEntry(key)
    if (e == null) { addEntry(new Entry(key, value)); None }
    else { val v = e.value; e.value = value; Some(v) }
  }

  override def remove(key: AbstractVar): Option[Double] = {
    val e = removeEntry(key)
    if (e ne null) Some(e.value)
    else None
  }

  def +=(kv: (AbstractVar, Double)): this.type = {
    val e = findEntry(kv._1)
    if (e == null) addEntry(new Entry(kv._1, kv._2))
    else e.value = kv._2
    this
  }

  def -=(key: AbstractVar): this.type = { removeEntry(key); this }

  def iterator = entriesIterator map {e => (e.key, e.value)}

  override def foreach[C](f: ((AbstractVar, Double)) => C): Unit = foreachEntry(e => f(e.key, e.value))

  /* Override to avoid tuple allocation in foreach */
  override def keySet: collection.Set[AbstractVar] = new DefaultKeySet {
    override def foreach[C](f: AbstractVar => C) = foreachEntry(e => f(e.key))
  }

  /* Override to avoid tuple allocation in foreach */
  override def values: collection.Iterable[Double] = new DefaultValuesIterable {
    override def foreach[C](f: Double => C) = foreachEntry(e => f(e.value))
  }

  /* Override to avoid tuple allocation */
  override def keysIterator: Iterator[AbstractVar] = new Iterator[AbstractVar] {
    val iter = entriesIterator
    def hasNext = iter.hasNext
    def next() = iter.next.key
  }

  /* Override to avoid tuple allocation */
  override def valuesIterator: Iterator[Double] = new Iterator[Double] {
    val iter = entriesIterator
    def hasNext = iter.hasNext
    def next() = iter.next.value
  }

  /** Toggles whether a size map is used to track hash map statistics.
   */
  def useSizeMap(t: Boolean) = if (t) {
    if (!isSizeMapDefined) sizeMapInitAndRebuild
  } else sizeMapDisable

  /*private def writeObject(out: java.io.ObjectOutputStream) {
    serializeTo(out, _.value)
  }

  private def readObject(in: java.io.ObjectInputStream) {
    init[Double](in, new Entry(_, _))
  }*/


  // An apply that doesn't create Some's internally.
  final override def apply(key: AbstractVar): Double = {
    val e = findEntry(key)
    if (e == null) 0
    else e.value
  }

  final def term(key: AbstractVar): Term = findEntry(key)

  // no Some's
  override def update(key: AbstractVar, value: Double) {
    val e = findEntry(key)
    if (e == null) { addEntry(new Term(key, value)) }
    else { val v = e.value; e.value = value }
  }

  final def foreachE[U](f: (AbstractVar, Double) => U) {
    val tab = table
    var idx = tab.length - 1
    var entry: Entry = null
    while(true) {
      while (entry == null) {
        if(idx < 0) return
        entry = tab(idx).asInstanceOf[Entry]
        idx = idx - 1
      }
      f(entry.key, entry.value)
      entry = entry.next
    }
  }
}

class FastBuffer[A] extends ArrayBuffer[A](8) {
  override def indexOf[B >: A](elem: B, from: Int): Int = {
    var i = from
    while(i < size0) {
      if(array(i) == elem) return i
      i+=1
    }
    -1
  }
}