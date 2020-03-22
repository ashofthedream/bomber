package ashes.of.bomber.watcher;

import ashes.of.bomber.core.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProgressWatcher implements Watcher {
    private static final Logger log = LogManager.getLogger();

    @Override
    public void testSuiteStart(State state) {
        log.warn("testSuiteStart. stage: {}, testCase: {}",
                state.getStage(), state.getTestSuite());
    }

    @Override
    public void watch(State state) {
        log.info("stage: {}, testSuite: {}, testCase: {}. time elapsed: {}ms, remain: {}ms. it remain: {}, errors: {}",
                state.getStage(), state.getTestSuite(), state.getTestCase(),
                state.getCaseElapsedTime(),
                state.getCaseRemainTime(),
                state.getRemainInvocations(),
                state.getErrorCount() );
    }

    @Override
    public void testSuiteEnd(State state) {
        log.warn("testSuiteEnd. stage: {}, testCase: {}",
                state.getStage(), state.getTestSuite());
    }
}
