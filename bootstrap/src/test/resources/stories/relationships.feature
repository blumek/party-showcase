Feature: Relationships between parties

  Scenario: Establish an employment relationship and read it back
    Given a registered company named "Globex" known as "employer"
    And a registered company named "Initech" known as "employee"
    When I establish an "Employment" relationship from "employer" as "Employer" to "employee" as "Employee"
    Then the response status is 201
    When I fetch the established relationship from "employer"
    Then the response status is 200
    And the relationship has type "Employment" and to-role "Employee"
