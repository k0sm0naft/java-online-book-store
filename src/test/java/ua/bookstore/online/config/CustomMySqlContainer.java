package ua.bookstore.online.config;

import org.testcontainers.containers.MySQLContainer;

public class CustomMySqlContainer extends MySQLContainer<CustomMySqlContainer> {
    private static final String DB_IMAGE = "mysql:8";
    private static CustomMySqlContainer instance;

    public CustomMySqlContainer() {
        super(DB_IMAGE);
    }

    public static synchronized CustomMySqlContainer getInstance() {
        if (instance == null) {
            instance = new CustomMySqlContainer();
        }
        return instance;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_DB_URL", instance.getJdbcUrl());
        System.setProperty("TEST_DB_USER", instance.getUsername());
        System.setProperty("TEST_DB_PASSWORD", instance.getPassword());
    }
}
