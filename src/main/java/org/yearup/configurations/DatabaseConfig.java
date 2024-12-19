/**
 * Configuration class for setting up the database connection.
 * This class uses Spring's @Configuration annotation to declare
 * beans and configure the data source.
 */
package org.yearup.configurations;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig
{
    // The BasicDataSource instance used to configure and manage database connections.
    private BasicDataSource basicDataSource;

    /**
     * Bean definition for the BasicDataSource.
     * This method exposes the configured BasicDataSource as a Spring bean,
     * allowing it to be injected wherever needed in the application.
     *
     * @return The configured BasicDataSource instance.
     */
    @Bean
    public BasicDataSource dataSource()
    {
        return basicDataSource;
    }

    /**
     * Constructor for DatabaseConfig.
     * Initializes the BasicDataSource with the provided database connection details.
     *
     * @param url      The database URL, injected from application properties.
     * @param username The database username, injected from application properties.
     * @param password The database password, injected from application properties.
     */
    @Autowired
    public DatabaseConfig(@Value("${datasource.url}") String url,
                          @Value("${datasource.username}") String username,
                          @Value("${datasource.password}") String password)
    {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
    }

}