package ashes.of.bomber.watcher;

import ashes.of.bomber.core.Stage;
import ashes.of.bomber.core.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

public class Log4jWatcher implements Watcher {
    private static final Logger log = LogManager.getLogger(new StringFormatterMessageFactory());

    @Override
    public void watch(State state) {
        ThreadContext.put("stage", state.getStage().name());
        ThreadContext.put("testSuite", state.getTestSuite());
        ThreadContext.put("testCase", state.getTestCase());

        if (state.getStage() == Stage.Rest || state.getTestCase() == null) {
            log.info("waiting...");
            return;
        }

        long totalInv = state.getSettings().getTotalInvocationsCount();
        long currentInv = totalInv - state.getRemainInvocations();

        double totalSecs = state.getSettings().getTime().getSeconds();
        double elapsedSecs = (state.getCaseElapsedTime() / 100) / 10.0;

        StringBuilder tp = new StringBuilder();
        StringBuilder ip = new StringBuilder();

        double count = 20;
        for (int i = 0; i < count; i++) {
            ip.append(currentInv > (i / count * totalInv) ? 'x' : '.');
            tp.append(elapsedSecs > (i / count * totalSecs) ? 'x' : '.');
        }

        log.info("%5.1fs [%s] %5.1fs | %,12d [%s] %,12d | %,8d",
                elapsedSecs,
                tp.toString(),
                totalSecs,
                currentInv,
                ip.toString(),
                totalInv,
                state.getErrorCount());
    }
}
