package ashes.of.bomber.cli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BomberCliTest {

    @Test
    public void testAll() {
        String[] args = { "--file", "/Users/ashofthedream/Projects/ashes.of/bomber-all/bomber-cli/test-accounts-config.yml", "-t", "1", "-i", "100", "-s", "10" };
        BomberCli.main(args);
    }
}