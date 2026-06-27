Feature: Party registration and search

  Scenario: Register a person and read it back from the database
    When I register a person named "Ada" "Lovelace" born "1815-12-10"
    Then the response status is 201
    When I fetch that party
    Then the response status is 200
    And the party has kind "PERSON" and display name "Ada Lovelace"

  Scenario: Assign a role to a person
    Given a registered person named "Grace" "Hopper" born "1906-12-09"
    When I assign the role "Customer" to that party
    Then the response status is 200
    And the party roles contain "Customer"

  Scenario: Find a person by an assigned role
    Given a registered person named "Ada" "Lovelace" born "1815-12-10"
    And the role "Auditor" is assigned to that party
    When I search parties by role "Auditor"
    Then the response status is 200
    And the search returns 1 party with kind "PERSON"

  Scenario: Find a company by type and name
    Given a registered company named "Acme Industries"
    When I search parties by type "COMPANY" and name "Acme Industries"
    Then the response status is 200
    And the search returns 1 party with display name "Acme Industries"

  Scenario: Register an organization unit and read it back
    When I register an organization unit named "Research Division"
    Then the response status is 201
    When I fetch that party
    Then the response status is 200
    And the party has kind "ORGANIZATION_UNIT" and display name "Research Division"

  Scenario: Register an identifier and find the party by it
    Given a registered person named "Alan" "Turing" born "1912-06-23"
    When I register a "NATIONAL" identifier "19120623123" for that party
    Then the response status is 200
    And the party identifiers contain "19120623123"
    When I search parties by identifier "19120623123"
    Then the response status is 200
    And the search returns 1 party with kind "PERSON"
