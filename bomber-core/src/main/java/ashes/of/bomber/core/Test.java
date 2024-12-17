package ashes.of.bomber.core;

public record Test(String testApp, String testSuite, String testCase) {

    public String name() {
        return testApp + "." + testSuite + "." + testCase;
    }
}
