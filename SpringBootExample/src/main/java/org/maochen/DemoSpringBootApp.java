package org.maochen;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.MongoFlyway;
import org.maochen.mongodb.MongoDbConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Created by mguan on 12/27/16.
 */
@SpringBootApplication
public class DemoSpringBootApp extends SpringBootServletInitializer {

  @Value("${spring.data.h2.url}")
  private String h2DbUrl;

  @Value("${spring.data.h2.username}")
  private String h2UserName;

  @Value("${spring.data.h2.password}")
  private String h2Password;

  @Autowired
  private MongoDbConfig mongoDbConfig;

  /**
   * h2Flyway migration.
   */
  @Bean
  public Flyway flyway() throws Exception {
    Flyway flyway = new Flyway();
    flyway.setLocations("db/migration/sql");
    flyway.setEncoding(StandardCharsets.UTF_8.name());
    flyway.setDataSource(h2DbUrl, h2UserName, h2Password);
    flyway.migrate();

    MongoFlyway mongoFlyway = new MongoFlyway();

    Properties props = new Properties();
    props.setProperty("flyway.locations", "db/migration/js");
    props.setProperty("flyway.baselineOnMigrate", "true");
    props.setProperty("flyway.validateOnMigrate", "true");
    props.setProperty("flyway.mongoUri", "mongodb://" + mongoDbConfig.mongo().getAddress().toString() + "/" + mongoDbConfig.mongoTemplate().getDb().getName());
    props.setProperty("flyway.placeholders.first_name", "Alice");
    mongoFlyway.configure(props);
    mongoFlyway.migrate();

    return flyway;
  }

  public static void main(String[] args) {
    SpringApplication.run(DemoSpringBootApp.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(this.getClass());
  }
}
