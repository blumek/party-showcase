Feature: Relationships between parties

  Scenario: Establish an employment relationship and read it back
    Given a registered company named "Globex" known as "employer"
    And a registered company named "Initech" known as "employee"
    When I establish an "Employment" relationship from "employer" as "Employer" to "employee" as "Employee"
    Then the response status is 201
    When I fetch the established relationship from "employer"
    Then the response status is 200
    And the relationship has type "Employment" and to-role "Employee"

  Scenario: List all employees of an employer
    Given a registered company named "Globex" known as "employer"
    And a registered person named "Alice" "Brown" born "1980-01-01" known as "alice"
    And a registered person named "Bob" "Green" born "1981-02-02" known as "bob"
    And an "Employment" relationship from "employer" as "Employer" to "alice" as "Employee"
    And an "Employment" relationship from "employer" as "Employer" to "bob" as "Employee"
    When I list "OUTGOING" "Employment" relationships from "employer"
    Then the response status is 200
    And the relationship list has size 2

  Scenario: Find who employs a person
    Given a registered company named "Globex" known as "employer"
    And a registered person named "Alice" "Brown" born "1980-01-01" known as "alice"
    And an "Employment" relationship from "employer" as "Employer" to "alice" as "Employee"
    When I list "INCOMING" "Employment" relationships from "alice"
    Then the response status is 200
    And the relationship list has size 1

  Scenario: A counterparty can read and end a relationship pointing at them
    Given a registered company named "Globex" known as "employer"
    And a registered company named "Initech" known as "employee"
    When I establish an "Employment" relationship from "employer" as "Employer" to "employee" as "Employee"
    Then the response status is 201
    When I fetch the established relationship from "employee"
    Then the response status is 200
    And the relationship has type "Employment" and to-role "Employee"
    When I end the established relationship from "employee"
    Then the response status is 204

  Scenario: List and end an employment relationship
    Given a registered company named "Globex" known as "employer"
    And a registered company named "Initech" known as "employee"
    When I establish an "Employment" relationship from "employer" as "Employer" to "employee" as "Employee"
    Then the response status is 201
    When I list the relationships from "employer"
    Then the response status is 200
    And the relationship list has size 1
    When I end the established relationship from "employer"
    Then the response status is 204
    When I list the relationships from "employer"
    Then the response status is 200
    And the relationship list has size 0
