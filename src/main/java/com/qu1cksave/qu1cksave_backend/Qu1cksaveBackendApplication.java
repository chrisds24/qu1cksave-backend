package com.qu1cksave.qu1cksave_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Qu1cksaveBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(Qu1cksaveBackendApplication.class, args);
	}
}
// TODO:
//  Search these:
//  - 413 Payload Too Large Spring Boot
//    Set request body size limit Spring Boot
//  - Something about the order of the fields being passed to the constructor
//    -- Seems like it should be fine, since the comment I put before was in
//       ResponseUserDto and not in any of the RequestDtos. It seems that my
//       custom mapper was causing the issue, which shouldn't be a problem
//       with the request bodies I send
//
// TODO: Packaging a Spring Boot app in a Docker container
//  - https://docs.spring.io/spring-boot/reference/packaging/index.html

// When running the app:
// 1.) Might need to do File > Invalidate Caches
// 2.) Set environment variables
//       OLD:
//		 - export POSTGRES_HOST=jdbc:postgresql://localhost:5432/dev
//         -- (OLD Node version) POSTGRES_HOST=localhost
//         -- Can add ?useSSL=false at end if needed (self explanatory)
//
//		 ****** NOTE: It's fine to put these here, since they're only used for
//         development.
//       export POSTGRES_HOST=localhost
//             - The docker-compose.yml uses this
//             - Also, the spring.datasource.url
//		 export POSTGRES_PORT=5432
//       export POSTGRES_DB=dev
//		 export POSTGRES_USER=postgres
//		 export POSTGRES_PASSWORD=postgres
//              ****** Shouldn't really matter if I leave this here, but just not gonna do it
//       export ACCESS_TOKEN=???
//       export API_KEY=???
//		 export BUCKET_NAME=???
//		 export BUCKET_REGION=???
//		 export BUCKET_ACCESS_KEY=???
//		 export BUCKET_SECRET_ACCESS_KEY=???
//       export ENV_TYPE=DEV
//       - Set to PROD if in production. Otherwise, can simply not set it.
// 3.) Run postgres docker container
//		docker-compose down		(If you want to reset. Then compose up again)
// 		docker-compose up -d
//      To run a specific one:
//        docker-compose -f docker-compose-test.yml up -d
//        docker-compose -f docker-compose-test.yml down
//
//		docker compose		After the update, use this now instead of docker-compose
//
// 4.) ./gradlew bootRun		To run
// 5.) Go to http://localhost:8080/api/v0/job?id=269a3d55-4eee-4a2e-8c64-e1fe386b76f8
//
// For tests:  ./gradlew clean test --info		(More info)   	OR		 ./gradlew clean test

// In terminal log: No active profile set, falling back to 1 default profile: "default"
// - https://stackoverflow.com/questions/68171743/eclipse-no-active-profile-set-falling-back-to-default-profiles-default
//   - So we can set dev, prod, etc. environments

// Setting environment variables:
// - https://unix.stackexchange.com/questions/56444/how-do-i-set-an-environment-variable-on-the-command-line-and-have-it-appear-in-c
// - https://stackoverflow.com/questions/62119161/adding-environment-variables-to-springs-application-properties
//   -- Input "export POSTGRES_HOST=myvalue" in terminal to set environment variables
//   -- Setting it in Intellij doesn't seem to work

// USEFUL NOTES:
// https://www.marcobehler.com/guides/java-databases
// 1.) Spring takes care of configuring SessionFactory for Hibernate, so no
//     need to write code for it.
//     - UPDATE: I can unwrap an entityManager (autowired) to get it
// 2.) Spring Data understands @Entity, @Column, @Table, etc. and automatically
//     generates repositories. (Gives basic CRUD operations)
// 3.) Can write custom repositories by extending JpaRepository
//     (Ex. public interface UserRepository extends JpaRepository ...)
//     - I need to create the repository even if I'm not making custom queries
// 4.) Can write custom JPA queries just from the method name
//     - I'll also need this since I like to search for jobs (such as when
//       editing or deleting) using both the Job's id and its member_id
//
// Note: Extending CrudRepository is for Spring Data JDBC
//
// https://www.marcobehler.com/guides/spring-transaction-management-transactional-in-depth
// 1.) Put @Transactional on the service file, which calls the
//     DAO or repository
//     - Need to do this since I have different steps for a transaction
//     - Ex. Creating a job
//       -- Add Resume to resume table. Returns a Resume
//       -- Use that Resume's id as the Job's resume_id
//       -- Add the Job to the job table.
//       -- Add the file to S3.
//       -- Return the Job with the Resume attached.
//     - So at any point, I'll need to rollback whenever a step fails
// 2.) Need to specify a transaction manager in my Spring configuration
//     (Ex. PlatformTransactionManager, which is defined as a bean in a
//     configuration file.)
//     - Need to create a database-specific or connection-pool specific
//       data source. Have your data source bean return this
//     - The returned transaction manager will take this as an argument
//     - UPDATE (not from this tutorial)
//       -- Different transaction managers take different parameters
//       -- Ex. JpaTransactionManager takes an EntityManagerFactory
//              HibernateTransactionManager takes a SessionFactory
// 3.) Spring Boot automatically sets the @EnableTransactionManagement
//     annotation and creates a PlatformTransactionManager for you - with its
//     JDBC auto-configurations
//     - Regarding the Hibernate integration below, I won't be able to
//       utilize this automatic creation of a Transaction Manager since I'll
//       need to use JpaTransactionManager.
// 4.) There's code here to integrate Hibernate with @Transactional
// 5.) Instead of using a DataSourcePlatformTransactionManager in your Spring
//     configuration, you will be using a HibernateTransactionManager (if using
//     plain Hibernate) or JpaTransactionManager (if using Hibernate through
//     JPA).

// https://stackoverflow.com/questions/3880563/what-transaction-manager-to-use-jpa-spring
// - PlatformTransactionManager interface is the key abstraction in the Spring
//   API providing essential methods for controlling transaction operations at
//   run-time: begin, commit and rollback.
// - DataSourceTransactionManager, JpaTransactionManager, and
//   HibernateTransactionManager are implementations of
//   PlatformTransactionManager
// https://stackoverflow.com/questions/71838340/spring-transactional-annotation-between-hibernatetransactionmanager-and-jpatra
// - yes indeed I'm using spring data jpa and interface repositories that
//   reduce a lot of boilerplate code so yeah I had to add it
// - Basically that is it. As you are using JPA (through Spring Data JPA) you
//   need the JpaTransactionManager... If you were only using plain Hibernate
//   the hibernate one would work, but as you aren't you need the other one

// https://stackoverflow.com/questions/65035232/spring-data-jpa-hibernate-marking-methods-as-transactional
// - Useful info on how to properly use @Transactional
// https://stackoverflow.com/questions/10394857/how-to-use-transactional-with-spring-data
// - Talks about @Transactional(readOnly = true) for select queries
// https://stackoverflow.com/questions/54326306/what-is-the-use-of-transactional-with-jpa-and-hibernate
// - Mentions that @Transactional is generally written at the service level

// Could be useful:
// https://stackoverflow.com/questions/45852508/how-to-create-custom-datasource-in-spring-boot-spring-data

// Session factory setup:
// https://docs.spring.io/spring-framework/reference/data-access/orm/hibernate.html
// - As of Spring Framework 6.0, Spring requires Hibernate ORM 5.5+ for
//   Spring’s HibernateJpaVendorAdapter as well as for a native Hibernate
//   SessionFactory setup. We recommend Hibernate ORM 5.6 as the last feature
//   branch in that Hibernate generation. Hibernate ORM 6.x is only supported as
//   a JPA provider (HibernateJpaVendorAdapter). Plain SessionFactory setup with
//   the orm.hibernate5 package is not supported anymore. We recommend Hibernate
//   ORM 6.1/6.2 with JPA-style setup for new development projects.
// https://stackoverflow.com/questions/25063995/spring-boot-handle-to-hibernate-sessionfactory
// - AFAIK Spring Boot does not autoconfigure a Hibernate SessionFactory.
//   It creates a JPA EntityManagerFactory
// - ME: There’s also code in this link regarding using the
//   EntityManagerFactory to setup a Session Factory
// https://stackoverflow.com/questions/55276436/how-to-handle-hibernate-sessionfactory-in-spring-boot
// - Another one that says to use JPA instead of Hibernate
// https://stackoverflow.com/questions/43895643/how-to-autowire-hibernate-sessionfactory-in-spring-boot
// - This API has changed. You now need to use
//   Session session = entityManager.unwrap(Session.class);
//   SessionFactory sessionFactory = session.getSessionFactory()
// https://stackoverflow.com/questions/29226099/access-sessionfactory-from-spring-boot-application
// https://stackoverflow.com/questions/72532622/using-transactional-in-spring-boot-with-hibernate


// LINKS:
// https://stackoverflow.com/questions/21968965/disable-table-recreation-in-spring-boot-application
// - This has something about setting the data source in application.properties
//   instead of writing code for it
// https://stackoverflow.com/questions/63641919/what-is-the-difference-between-ddl-auto-and-hbm2ddl-auto
// - Disabling table creation


// ERROR 1: Factory method 'dataSource' threw exception with message: URL must start with 'jdbc'
//   SOLUTION:    POSTGRES_HOST=jdbc:postgresql://localhost:5432/dev
// ERROR 2: Factory method 'dataSource' threw exception with message: Failed to load driver class org.postgresql.Driver in either of HikariConfig class loader or Thread context classloader
//   https://stackoverflow.com/questions/66910657/spring-jpa-postgres-cannot-load-driver-class-org-postgresql-driver
// 	 - Invalidate caches in Intellij (File>Invalidate Caches/Restart)
// 	 https://stackoverflow.com/questions/73554099/cannot-load-driver-class-org-postgresql-driver
// 	 - SOLUTION: Add PostgreSQL dependency to Gradle
// 	   -- https://stackoverflow.com/questions/63222401/how-to-connect-postgresql-in-gradle-project
//        + How to add dependency to Gradle
//     -- https://stackoverflow.com/questions/67984274/springboot-cannot-load-driver-class-org-postgresql-driver
//        + Use postgresql 42.2.23 ???
//        + UPDATE: Postgres allows us to not include version numbers, which I
//			did.
//	ERROR 3 (SOLVED):
//	Database JDBC URL [Connecting through datasource 'HikariDataSource (HikariPool-1)']
//	Database driver: undefined/unknown
//	What's causing this the log above?
//	https://stackoverflow.com/questions/58632697/spring-boot-postgresql-driver-cannot-be-located-in-classpath
//	- To set postgresql driver
//	- spring.datasource.driver-class-name=org.postgresql.Driver
//	- DOESN'T FIX
//	https://www.reddit.com/r/SpringBoot/comments/1h7v33r/i_am_getting_this_error/
//	https://stackoverflow.com/questions/79304244/spring-boot-database-driver-unknown-undefined-error-while-connecting-to-mysq
//	- I get the same error as these people
//  Add useSSL:    POSTGRES_HOST=jdbc:postgresql://localhost:5432/dev?useSSL=false
//  - DOESN'T FIX
//  Maybe the version of the postgresql dependency is the problem?
//  - I removed the dependency version
// 	   -- It was implementation("org.postgresql:postgresql:42.7.2")
//     -- Now its implementation("org.postgresql:postgresql")
//     -- DOESN'T FIX
//  https://stackoverflow.com/questions/79246915/problems-with-hibernate-startup-logging-after-adding-jpa-with-database-in-spring
// 	- SOLUTION: Says that there is no issue
