package ashes.of.bomber.core;

import ashes.of.bomber.configuration.Configuration;
import ashes.of.bomber.methods.TestCaseMethod;

public record TestCase<T>(String name, boolean async, TestCaseMethod<T> testCase, Configuration configuration) {

}
