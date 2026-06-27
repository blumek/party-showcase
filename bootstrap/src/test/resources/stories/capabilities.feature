Feature: Party capabilities

  Scenario: Grant a capability and read it back
    Given a registered person named "Marie" "Curie" born "1867-11-07" known as "marie"
    When I grant the "MedicalImaging" capability to party "marie" with grade "SENIOR" rank 3
    Then the response status is 201
    When I fetch the granted capability for party "marie"
    Then the response status is 200
    And the capability has kind "MedicalImaging"
    And the first capability scope has dimension "GRADE"
