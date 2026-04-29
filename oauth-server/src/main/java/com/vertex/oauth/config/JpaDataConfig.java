package com.vertex.oauth.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaDataConfig {

    @Bean
    @Lazy
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDatabaseChangeLogTable("Z_LIQ_CHANGELOG");
        liquibase.setDatabaseChangeLogLockTable("Z_LIQ_CHANGELOG_LOCK");
        liquibase.setChangeLog("classpath:db/changelog/liquibase-master.xml");
        liquibase.setDataSource(dataSource);
        return liquibase;
    }
}