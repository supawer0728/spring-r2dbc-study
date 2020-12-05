package org.parfait.study.springr2dbc.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryBuilder;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.boot.autoconfigure.r2dbc.EmbeddedDatabaseConnection;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;

@Profile("database-ms")
@RequiredArgsConstructor
@Configuration
public class R2dbcMasterSlaveConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "r2dbc.master")
    public R2dbcProperties masterR2dbcProperties() {
        return new R2dbcProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "r2dbc.read-only")
    public R2dbcProperties readOnlyR2dbcProperties() {
        return new R2dbcProperties();
    }

    @Primary
    @Bean(destroyMethod = "dispose")
    public ConnectionPool masterConnectionFactory(R2dbcProperties masterR2dbcProperties, ResourceLoader resourceLoader,
                                                  ObjectProvider<ConnectionFactoryOptionsBuilderCustomizer> customizers) {
        return createConnectionFactory(masterR2dbcProperties, resourceLoader.getClassLoader(), customizers.orderedStream().collect(Collectors.toList()));
    }

    @Bean(destroyMethod = "dispose")
    public ConnectionPool readOnlyConnectionFactory(@Qualifier("readOnlyR2dbcProperties") R2dbcProperties readOnlyR2dbcProperties,
                                                    ResourceLoader resourceLoader,
                                                    ObjectProvider<ConnectionFactoryOptionsBuilderCustomizer> customizers) {
        return createConnectionFactory(readOnlyR2dbcProperties, resourceLoader.getClassLoader(), customizers.orderedStream().collect(Collectors.toList()));
    }

    @Primary
    @Bean
    public DatabaseClient masterDatabaseClient(ConnectionFactory masterConnectionFactory) {
        return DatabaseClient.create(masterConnectionFactory);
    }

    @Bean
    public DatabaseClient readOnlyDatabaseClient(@Qualifier("readOnlyConnectionFactory") ConnectionFactory readOnlyConnectionFactory) {
        return DatabaseClient.create(readOnlyConnectionFactory);
    }

    @Primary
    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory masterConnectionFactory) {
        return new R2dbcEntityTemplate(masterConnectionFactory);
    }

    @Bean
    public R2dbcEntityTemplate readOnlyR2dbcEntityTemplate(@Qualifier("readOnlyConnectionFactory") ConnectionFactory readOnlyConnectionFactory) {
        return new R2dbcEntityTemplate(readOnlyConnectionFactory);
    }

    public static ConnectionPool createConnectionFactory(R2dbcProperties properties, ClassLoader classLoader,
                                                         List<ConnectionFactoryOptionsBuilderCustomizer> optionsCustomizers) {
        ConnectionFactory connectionFactory = ConnectionFactoryBuilder.of(properties, () -> EmbeddedDatabaseConnection.get(classLoader))
                                                                      .configure((options) -> {
                                                                          for (ConnectionFactoryOptionsBuilderCustomizer optionsCustomizer : optionsCustomizers) {
                                                                              optionsCustomizer.customize(options);
                                                                          }
                                                                      }).build();

        R2dbcProperties.Pool pool = properties.getPool();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        ConnectionPoolConfiguration.Builder builder = ConnectionPoolConfiguration.builder(connectionFactory);
        map.from(pool.getMaxIdleTime()).to(builder::maxIdleTime);
        map.from(pool.getMaxLifeTime()).to(builder::maxLifeTime);
        map.from(pool.getMaxAcquireTime()).to(builder::maxAcquireTime);
        map.from(pool.getMaxCreateConnectionTime()).to(builder::maxCreateConnectionTime);
        map.from(pool.getInitialSize()).to(builder::initialSize);
        map.from(pool.getMaxSize()).to(builder::maxSize);
        map.from(pool.getValidationQuery()).whenHasText().to(builder::validationQuery);
        map.from(pool.getValidationDepth()).to(builder::validationDepth);
        return new ConnectionPool(builder.build());
    }
}
