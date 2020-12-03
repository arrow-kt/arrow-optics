package arrow.optics

import arrow.core.ListExtensions
import arrow.core.MapInstances
import arrow.core.None
import arrow.core.k
import arrow.core.test.UnitSpec
import arrow.optics.dsl.at
import arrow.optics.extensions.at
import arrow.optics.extensions.each
import arrow.optics.extensions.index
import arrow.optics.extensions.traversal
import io.kotlintest.shouldBe

@optics
data class Street(val number: Int, val name: String) {
  companion object
}

@optics
data class Address(val city: String, val street: Street) {
  companion object
}

@optics
data class Company(val name: String, val address: Address) {
  companion object
}

@optics
data class Employee(val name: String, val company: Company?) {
  companion object
}

@optics
data class CompanyEmployees(val employees: List<Employee>) {
  companion object
}

sealed class Keys
object One : Keys()
object Two : Keys()
object Three : Keys()
object Four : Keys()

@optics
data class Db(val content: Map<Keys, String>) {
  companion object
}

class BoundedTest : UnitSpec() {

  init {

    val john = Employee(
      "John Doe",
      Company("Kategory", Address("Functional city", Street(42, "lambda street")))
    )
    val jane = Employee(
      "Jane Doe",
      Company("Kategory", Address("Functional city", Street(42, "lambda street")))
    )

    val employees = CompanyEmployees(listOf(john, jane).k())

    val db = Db(
      mapOf(
        One to "one",
        Two to "two",
        Three to "three",
        Four to "four"
      ).k()
    )

    "@optics generate DSL properly" {
      Employee.company.address.street.name.modify(
        john,
        String::toUpperCase
      ) shouldBe (
        Employee.company compose
          Company.address compose
          Address.street compose
          Street.name
        ).modify(john, String::toUpperCase)
    }

    "Index enables special Index syntax" {
      ListExtensions.index<Employee>().run {
        CompanyEmployees.employees[1].company.address.street.name.modify(
          employees,
          String::toUpperCase
        )
      } shouldBe (
        CompanyEmployees.employees compose
          ListExtensions.index<Employee>().index(1) compose
          Employee.company compose
          Company.address compose
          Address.street compose
          Street.name
        ).modify(employees, String::toUpperCase)
    }

    "Working with At in Optics should be same as in DSL" {
      MapInstances.at<Keys, String>().run {
        Db.content.at(MapInstances.at(), One).set(db, None)
      } shouldBe (Db.content compose MapInstances.at<Keys, String>().at(One)).set(db, None)
    }

    "Working with Each in Optics should be same as in DSL" {
      MapInstances.each<Keys, String>().run {
        Db.content.every.modify(db, String::toUpperCase)
      } shouldBe (Db.content compose MapInstances.traversal()).modify(db, String::toUpperCase)
    }
  }
}
