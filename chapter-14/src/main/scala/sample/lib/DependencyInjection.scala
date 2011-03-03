package sample.lib

/**
 * Common classes and traits
 */

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

/** 
 * Cake pattern of implementation
 */
  object CakeExample {
    // enclosing object is simply to stop type conflicts
    
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
  }
  
  
  object FunctionCurrenyExample {
    trait BookService {
      val lookupBook: BookRepository => String => Option[Book] = 
        repository => isbn => repository.lookup(isbn)
      
      val addBook: BookRepository => Book => Unit =
        repository => book => repository.add(book)
    }
    
    class DefaultBookRepository extends BookRepository {
      def lookup(isbn: String): Option[Book] = Library.books.find(_.isbn == isbn)
      def add(book: Book): Unit = {}
    }
    
    object ExampleBookService  extends BookService {
      val lookup = lookupBook(new DefaultBookRepository)
      val add = addBook(new DefaultBookRepository) 
    }
    
    object UsageExample {
      import ExampleBookService._ 
      val x = lookup("1234")
    }
    
  }
  
  
  