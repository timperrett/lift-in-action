package sample.model

import javax.persistence._
import javax.validation.constraints.Size

@Entity
class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id : Long = _
  
  @Column(nullable = false)
  var balance : Long = 0L
  
  // helpers
  def deposit(value: Long) = updateBalence(value)
  def withdraw(value: Long) = updateBalence(-value)
  private def updateBalence(value: Long) = balance = (balance + value)
}
