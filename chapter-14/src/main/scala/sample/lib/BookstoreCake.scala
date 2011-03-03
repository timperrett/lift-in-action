package sample.lib

case class Book(isbn: String, title: String)

object Library {
  def books = List(
    Book("1234", "Lift in Action"),
    Book("5678", "Scala in Depth")
  )
}

trait BookRepository {
  def lookup(isbn: String): Option[Book]
  def add(book: Book): Unit
}

trait BookService {
  def lookupBook(isbn: String): Option[Book]
  def addBook(book: Book): Unit 
}

trait BookRepositoryComponent {
  val repository: BookRepository
  
  class DefaultBookRepository extends BookRepository {
    def lookup(isbn: String): Option[Book] = Library.books.find(_.isbn == isbn)
    def add(book: Book): Unit = {}
  }
}

trait BookServiceComponent { _: BookRepositoryComponent =>
  val service: BookService
  
  class DefaultBookService extends BookService {
    def lookupBook(isbn: String) = repository.lookup(isbn)
    def addBook(book: Book) = repository.add(book)
  }
}

object BookServiceAssembly extends BookRepositoryComponent with BookServiceComponent {
  val repository = new DefaultBookRepository
  val service = new DefaultBookService
}