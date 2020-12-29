package scorekeep;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Alexander Pribytok
 * Date: 21.12.2020.
 */
public class AWSDynamodbDatabaseExtension implements BeforeAllCallback, AfterAllCallback {

    private DynamoDBProxyServer server;

    public AWSDynamodbDatabaseExtension() {
        System.setProperty("sqlite4java.library.path", "native-libs");
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        String port = "5787";
        server = ServerRunner.createServerFromCommandLineArgs(new String[]{"-inMemory", "-port", port});
        server.start();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
