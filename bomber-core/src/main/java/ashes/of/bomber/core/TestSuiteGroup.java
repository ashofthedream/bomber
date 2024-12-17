package ashes.of.bomber.core;

import java.util.List;

public record TestSuiteGroup(String name, List<TestSuite<?>> testSuites) {

}
