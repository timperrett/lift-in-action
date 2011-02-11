package sample.model

import java.util.Date
import javax.persistence._

@Entity
class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id : Long = _
  
  @Column(unique = true, nullable = false)
  var title : String = ""
  
  @Temporal(TemporalType.DATE)
  @Column(nullable = true)
  var published : Date = new Date()
  
  @ManyToOne(optional = false)
  var author : Author = _
}
