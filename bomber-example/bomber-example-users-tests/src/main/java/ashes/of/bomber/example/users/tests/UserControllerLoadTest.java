package ashes.of.bomber.example.users.tests;

import ashes.of.bomber.annotations.BeforeTestSuite;
import ashes.of.bomber.annotations.LoadTestCase;
import ashes.of.bomber.annotations.LoadTestSettings;
import ashes.of.bomber.annotations.LoadTestSuite;
import ashes.of.bomber.annotations.Throttle;
import ashes.of.bomber.example.dto.CreateUserRequest;
import ashes.of.bomber.example.users.client.UsersClient;
import ashes.of.bomber.tools.Stopwatch;
import ashes.of.bomber.tools.Tools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@LoadTestSuite(name = "UserController")
@LoadTestSettings(time = 30, totalIterations = 5000)
@Throttle(threshold = 100, shared = true)
public class UserControllerLoadTest {
    private static final Logger log = LogManager.getLogger();

    private final Random random = new Random();
    private final UsersClient usersClient;

    public UserControllerLoadTest(UsersClient usersClient) {
        this.usersClient = usersClient;
    }

    @BeforeTestSuite(onlyOnce = true)
    public void beforeAll() {
        log.info("Create 1000 test users");
        for (int i = 0; i < 1000; i++) {
            if ((i + 1) % 100 == 0) {
                log.debug("Created {} users", i);
            }

            usersClient.createUser(new CreateUserRequest().setUsername("username" + (i + 1)))
                    .block();
        }
    }

    @LoadTestCase(async = true)
    public void createUser(Tools tools) {
        Stopwatch stopwatch = tools.stopwatch("createUser");
        usersClient.createUser(new CreateUserRequest().setUsername("username#" + random.nextLong()))
                .subscribe(user -> {
                    // todo add additional checks
                    stopwatch.success();
                }, stopwatch::fail);
    }

    @LoadTestCase(async = true)
    public void getUserById(Tools tools) {
        Stopwatch stopwatch = tools.stopwatch("getUsers");
        usersClient.getUser(random.nextInt(1000) + 1)
                .subscribe(user -> {
                    // todo add additional checks
                    stopwatch.success();
                }, stopwatch::fail);
    }

    @LoadTestCase
    public void getUserWithAccounts(Tools tools) {
        Stopwatch stopwatch = tools.stopwatch("getUserWithAccounts");
        usersClient.getUser(random.nextInt(1000) + 1)
                .subscribe(user -> {
                    // todo add additional checks
                    stopwatch.success();
                }, stopwatch::fail);
    }

    @LoadTestCase
    public void getAllUsers(Tools tools) {
        Stopwatch stopwatch = tools.stopwatch("getAllUsers");
        usersClient.getUsers()
                .subscribe(users -> {
                    // todo add additional checks
                    stopwatch.success();
                }, stopwatch::fail);
    }

    @LoadTestCase
    public void getAllUsersSync() {
        var userId = random.nextInt(1000);
        var user = usersClient.getUsers()
                .block();
    }
}
