package ashes.of.bomber.example.accounts.tests;

import ashes.of.bomber.builder.TestSuiteBuilder;
import ashes.of.bomber.example.clients.AccountClient;
import ashes.of.bomber.example.models.User;
import ashes.of.bomber.example.models.requests.CreateAccountsRequest;
import ashes.of.bomber.example.models.requests.CreateAccountsResponse;
import ashes.of.bomber.tools.Tools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;


public class AccountControllerLoadTest {
    private static final Logger log = LogManager.getLogger();

    private final Random random = new Random();
    private final AccountClient accountClient;

    public AccountControllerLoadTest(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    public Random getRandom() {
        return random;
    }

    private long getRandomId(AccountControllerLoadTest context) {
        return random.nextInt(1000) + 1;
    }

    public AccountClient getAccountClient() {
        return accountClient;
    }

    public static void create(TestSuiteBuilder<AccountControllerLoadTest> builder, AccountClient accountClient) {
        builder .name("AccountControllerLoadTest")
                .withContext(new AccountControllerLoadTest(accountClient))
                .beforeSuite(true, AccountControllerLoadTest::beforeSuite)
                .testCase(testCase -> testCase
                        .name("createAccountAndThenGetIt")
                        .async(true)
                        .test(AccountControllerLoadTest::createAccountAndThenGetIt)
                )
                .testCase(testCase -> testCase
                        .name("getAccountsByUser")
                        .async(true)
                        .test(AccountControllerLoadTest::getAccountsByUser)
                )
                .testCase(testCase -> testCase
                        .name("getAccounts")
                        .async(true)
                        .test(AccountControllerLoadTest::getAccounts)
                );
    }

    private static void beforeSuite(AccountControllerLoadTest context) {
        log.info("Create 1000 accounts for test");
        for (long i = 0; i < 1000; i++) {
            if ((i + 1) % 100 == 0) {
                log.debug("Created {} accounts", i);
            }

            context.getAccountClient()
                    .createAccounts(new CreateAccountsRequest().setUser(new User().setId(i + 1)))
                    .block();
        }

    }

    private static void createAccountAndThenGetIt(AccountControllerLoadTest context, Tools tools) {
        var overall = tools.stopwatch();
        var create = tools.stopwatch("create");
        var client = context.getAccountClient();
        client  .createAccounts(new CreateAccountsRequest().setUser(new User().setId(context.getRandomId(context))))
                .map(CreateAccountsResponse::getAccounts)
                .subscribe(accounts -> {
                    create.success();

                    accounts.stream()
                            .findFirst()
                            .ifPresent(createdAccount -> {
                                tools.measure("get", get -> {
                                    client  .getAccount(createdAccount.getId())
                                            .subscribe(account -> {

                                                // todo check that get returned same account

                                                get.success();
                                                overall.success();
                                            }, th -> {
                                                get.fail(th);
                                                overall.fail(th);
                                            });
                                        });
                            });

                }, th -> {
                    create.fail(th);
                    overall.fail(th);
                });
    }

    private static void getAccountsByUser(AccountControllerLoadTest context, Tools tools) {
        var stopwatch = tools.stopwatch();
        context.getAccountClient()
                .getAccountsByUser(context.getRandomId(context))
                .subscribe(account -> stopwatch.success(), stopwatch::fail);
    }

    private static void getAccounts(AccountControllerLoadTest context, Tools tools) {
        var stopwatch = tools.stopwatch();
        context.getAccountClient()
                .getAccounts()
                .subscribe(account -> stopwatch.success(), stopwatch::fail);
    }
}
