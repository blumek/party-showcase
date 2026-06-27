Feature: Relationships between parties

  Scenario: Establish an employment relationship and read it back
    Given a registered company named "Globex" known as "employer"
    And a registered company named "Initech" known as "employee"
    When I establish an "Employment" relationship from "employer" as "Employer" to "employee" as "Employee"
    Then the response status is 201
    When I fetch the established relationship from "employer"
    Then the response status is 200
    And the relationship has type "Employment" and to-role "Employee"

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
