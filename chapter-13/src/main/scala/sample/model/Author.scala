package sample.model

import javax.persistence._
import javax.validation.constraints.Size

@Entity
class Author {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id : Long = _
  
  @Size(min = 3, max = 20)
  @Column(unique = true, nullable = false)
  var name : String = ""

  @OneToMany(mappedBy = "author", targetEntity = classOf[Book])
  var books : java.util.Set[Book] = new java.util.HashSet[Book]()
}
