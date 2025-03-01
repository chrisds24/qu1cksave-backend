package com.qu1cksave.qu1cksave_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Qu1cksaveBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(Qu1cksaveBackendApplication.class, args);
	}
}

// docker-compose up -d		Run the postgres docker container (in a different terminal)
// docker-compose down		To remove the docker-container

// TODO:
// https://www.marcobehler.com/guides/java-databases
// 1.) Spring takes care of configuring SessionFactory for Hibernate, so no
//     need to write code for it.
// 2.) Spring Data understands @Entity, @Column, @Table, etc. and automatically
//     generates repositories. (Gives basic CRUD operations)
// 3.) Can write custom repositories by extending JpaRepository
//     (Ex. public interface UserRepository extends JpaRepository ...)
//     - Need to do this since I have different steps for a transaction
//     - Ex. Creating a job
//       -- Add Resume to resume table. Returns a Resume
//       -- Use that Resume's id as the Job's resume_id
//       -- Add the Job to the job table.
//       -- Add the file to S3.
//       -- Return the Job with the Resume attached.
//     - At any point, I'll need to rollback whenever a step fails
// 4.) Can write custom JPA queries just from the method name
//     - I'll also need this since I like to search for jobs (such as when
//       editing or deleting) using both the Job's id and its member_id
//
// Note: Extending CrudRepository is for Spring Data JDBC
//
// https://www.marcobehler.com/guides/spring-transaction-management-transactional-in-depth
// 1.) Seems like I put @Transactional on the service file, which calls the
//     DAO or repository
// 2.) Need to specify a transaction manager in my Spring configuration
//     (Ex. PlatformTransactionManager, which is defined as a bean in a
//     configuration file.)
//     - Need to create a database-specific or connection-pool specific
//       data source. Have your data source bean return this
//     - The returned transaction manager will take this as an argument
// 3.) Spring Boot automatically sets the @EnableTransactionManagement
//     annotation and creates a PlatformTransactionManager for you - with its
//     JDBC auto-configurations
//     - Regarding the Hibernate integration below, I may not be able to
//       utilize this automatic creation of a Transaction Manager since I'll
//       need to use HibernateTransactionManager. Unless...there's a way to
//       do this with just configs.
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
