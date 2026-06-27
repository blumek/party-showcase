Feature: Party addresses

  Scenario: Record an email address and read it back
    Given a registered person named "Ada" "Lovelace" born "1815-12-10" known as "ada"
    When I record an email "ada@example.com" for party "ada" with purpose "NOTIFICATION"
    Then the response status is 201
    When I fetch the recorded address for party "ada"
    Then the response status is 200
    And the address has kind "EMAIL" and value "ada@example.com"

  Scenario: List the addresses recorded for a party
    Given a registered person named "Grace" "Hopper" born "1906-12-09" known as "grace"
    And an email "grace@example.com" is recorded for party "grace" with purpose "NOTIFICATION"
    And a postal address is recorded for party "grace" with purpose "RESIDENCE"
    When I list the addresses for party "grace"
    Then the response status is 200
    And the address list has size 2
