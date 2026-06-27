package dev.blumek.party.e2e;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("stories")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "dev.blumek.party.e2e")
class CucumberE2EIT {
}
