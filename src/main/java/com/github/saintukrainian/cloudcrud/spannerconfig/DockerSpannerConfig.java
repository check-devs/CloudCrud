package com.github.saintukrainian.cloudcrud.spannerconfig;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.*;
import com.google.spanner.admin.database.v1.CreateDatabaseMetadata;
import com.google.spanner.admin.instance.v1.CreateInstanceMetadata;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DockerSpannerConfig {

    private static final DockerClientConfig dockerClientConfig;
    private static final DockerHttpClient dockerHttpClient;
    private static final DockerClient dockerClient;
    private static final Logger logger;
    private static String containerId;
    private static Spanner spanner;
    private static DatabaseId dbId;

    private static final String configId = "emulator-config";
    private static final int nodeCount = 1;
    private static final String instanceId = "test-instance";
    private static final String databaseName = "cloudcrud-testdb";
    private static final String projectId = "test-project";


    static {
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        logger = Logger.getLogger(DockerSpannerConfig.class.getName());

        if (System.getProperty("os.name").contains("Windows")) {
            builder.withDockerHost("tcp://localhost:2375/")
                    .withDockerTlsVerify(true)
                    .withDockerCertPath(System.getenv("HOME") + "/.docker");
        }

        dockerClientConfig = builder.build();
        dockerHttpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(dockerClientConfig.getSSLConfig())
                .build();
        dockerClient = DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient);
    }

    public void setupDocker() throws InterruptedException {

        // setting up docker
        System.setProperty("SPANNER_EMULATOR_HOST", "http://localhost:9010/");

//         pulling emulator image
        logger.info("Pulling emulator image...");
        dockerClient.pullImageCmd("gcr.io/cloud-spanner-emulator/emulator")
                .withAuthConfig(new AuthConfig())
                .exec(new PullImageResultCallback())
                .awaitCompletion(60, TimeUnit.SECONDS);
        logger.info("Emulator image has been pulled!");


        // starting emulator container
        logger.info("Starting container >>>>>>>>");
        ;
        CreateContainerResponse containerResponse = dockerClient.createContainerCmd("gcr.io/cloud-spanner-emulator/emulator:latest")
                .withPortBindings(PortBinding.parse("9010:9010"), PortBinding.parse("9020:9020"))
                .exec();
        containerId = containerResponse.getId();
        dockerClient.startContainerCmd(containerId).exec();
        logger.info("Container with id=" + containerId + " is being executed >>>>>>>>");
    }

    public void setupSpanner() {
        spanner = SpannerOptions.newBuilder()
                .setProjectId(projectId)
                .setEmulatorHost(System.getProperty("SPANNER_EMULATOR_HOST"))
                .setCredentials(NoCredentials.getInstance())
                .build()
                .getService();
        logger.info(System.getProperty("SPANNER_EMULATOR_HOST"));
        InstanceAdminClient instanceAdminClient = spanner.getInstanceAdminClient();

        // Set Instance configuration

        // Create an InstanceInfo object that will be used to create the instance.
        InstanceInfo instanceInfo =
                InstanceInfo.newBuilder(InstanceId.of(projectId, instanceId))
                        .setInstanceConfigId(InstanceConfigId.of(projectId, configId))
                        .setNodeCount(nodeCount)
                        .setDisplayName(instanceId)
                        .build();
        OperationFuture<Instance, CreateInstanceMetadata> operation =
                instanceAdminClient.createInstance(instanceInfo);
        try {
            // Wait for the createInstance operation to finish.
            Instance instance = operation.get();
            logger.info("Instance " + instance.getId() + " was successfully created");
        } catch (ExecutionException e) {
            logger.warning(
                    "Error: Creating instance " + instanceInfo.getId() + " failed with error message " + e.getMessage());
        } catch (InterruptedException e) {
            logger.warning("Error: Waiting for createInstance operation to finish was interrupted");
        }
    }

    public void setupDatabase() {
        dbId = DatabaseId.of(projectId, instanceId, databaseName);
        DatabaseAdminClient dbAdminClient = spanner.getDatabaseAdminClient();


        // creating database
        OperationFuture<Database, CreateDatabaseMetadata> op =
                dbAdminClient.createDatabase(
                        dbId.getInstanceId().getInstance(),
                        dbId.getDatabase(),
                        Arrays.asList(
                                "CREATE TABLE persons (" +
                                        "id INT64, first_name STRING(MAX), " +
                                        "last_name STRING(MAX), " +
                                        "email STRING(MAX)" +
                                        ") PRIMARY KEY (id)",
                                "CREATE TABLE person_details (" +
                                        "details_id INT64, " +
                                        "user_id INT64, " +
                                        "address STRING(MAX), p" +
                                        "hone_number STRING(MAX)" +
                                        ") PRIMARY KEY (details_id)"));

        try {
            // Initiate the request which returns an OperationFuture.
            Database db = op.get();
            logger.info("Created database [" + db.getId() + "]");
        } catch (ExecutionException e) {
            // If the operation failed during execution, expose the cause.
            throw (SpannerException) e.getCause();
        } catch (InterruptedException e) {
            // Throw when a thread is waiting, sleeping, or otherwise occupied,
            // and the thread is interrupted, either before or during the activity.
            throw SpannerExceptionFactory.propagateInterrupt(e);
        }
    }

    public void fillDatabase() {
        // filling database
        DatabaseClient dbClient = spanner.getDatabaseClient(dbId);
        dbClient
                .readWriteTransaction()
                .run(
                        (TransactionRunner.TransactionCallable<Void>) transaction -> {
                            String sql =
                                    "INSERT INTO persons (id, first_name, last_name, email) VALUES "
                                            + "(1, 'Denys', 'Matsenko', 'idanchik47@gmail.com'), "
                                            + "(2, 'Max', 'Basov', 'scratchy@gmail.com'), "
                                            + "(3, 'Kirill', 'Ikumapaii', 'merlodon@gmail.com')";
                            long rowCount = transaction.executeUpdate(Statement.of(sql));
                            logger.info(rowCount + " records inserted.\n");
                            return null;
                        });

        dbClient
                .readWriteTransaction()
                .run(
                        (TransactionRunner.TransactionCallable<Void>) transaction -> {
                            String sql =
                                    "INSERT INTO person_details(details_id, user_id, address, phone_number) VALUES "
                                            + "(1, 1, 'Akademika Valtera,14', '380669410135'), "
                                            + "(2, 2, 'Saltovka, 16', '35849856895'), "
                                            + "(3, 3, 'Moskalevka, 17', '454567856784')";
                            long rowCount = transaction.executeUpdate(Statement.of(sql));
                            logger.info(rowCount + " records inserted.\n");
                            return null;
                        });
    }

    public void stopDocker() {
        logger.info("Shutting down container >>>>>>>>");
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();
        logger.info("Container with id=" + containerId + " has been shut down >>>>>>>>");
    }

    public void closeSpanner() {
        logger.info("Closing Spanner Instance...");
        spanner.close();
        logger.info("Spanner Instance has been closed!");
    }
}
