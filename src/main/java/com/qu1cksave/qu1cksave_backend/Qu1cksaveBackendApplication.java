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
//  - Something about the order of the fields being passed to the constructor
//    -- Seems like it should be fine, since the comment I put before was in
//       ResponseUserDto and not in any of the RequestDtos. It seems that my
//       custom mapper was causing the issue, which shouldn't be a problem
//       with the request bodies I send
//  - Render.com set secrets Docker (DONE)
//    -- https://render.com/docs/docker#docker-specific-features
//       + Env vars set in Render will be passed as build args to the image, so
//         it's not recommended to do this. Need to use secret files instead
//         * NOTE: I JUST WENT WITH THIS
//       + Docker secret files: https://render.com/docs/docker-secrets
//    -- https://community.render.com/t/cant-find-secrets-in-docker-build/5939
//    -- https://community.render.com/t/deploying-docker-containers-with-environment-variables-help/12635
//    -- https://community.render.com/t/secrets-in-secret-file-env-automatically-makes-environment-variables/3598
//  - Access .env in Spring Boot
//  ...
//  ...
//  ...
//  FILES THAT USE ENV VARS:
//  - application.properties
//  - Qu1cksaveBackendConfiguration
//  - APIKeyFilter
//  - JWTFilter
//  - S3Service
//  - UserService
//  - JobService

// When running the app:
// 1.) Might need to do File > Invalidate Caches
//     - I only needed to do this at the beginning, and the error might have
//       been due to something else
// 1.5) Gradle (right sidebar elephant icon) -> Sync All Gradle Projects
// 2.) Set environment variables
//       NEVER USED, but I can add ?useSSL=false at end if needed (self explanatory)
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
//      docker compose -f docker-compose-db.yml up -d
//      	- To run the DB image
//      docker compose -f docker-compose-db.yml down
//
//		OLD:
//			docker-compose down		(If you want to reset. Then compose up again)
// 			docker-compose up -d
//          *** After the update, docker compose is now used instead of docker-compose
//
// 4.) ./gradlew bootRun		To run
// 5.) Go to http://localhost:8080 (Assuming that the frontend server is running)
//     - To start the frontend, just cd frontend_folder and enter npm run dev
//
// ****** For tests:  ./gradlew clean test --info (More info)   	OR		 ./gradlew clean test
//
//
//
// ========== Starting the backend in a container (for development) ===========
// Source: Had to get help from Chat GPT since it's difficult to find
//   Dockerized Spring Boot w/ Gradle info in the official docs
// - Though, I used "docker init" to create the .dockerignore and just
//   deleted the generated Dockerfile (uses Maven T_T) and compose file.
//   + It also generated a README.Docker.md, which I just left here
//
// IMPORTANT:
//   In order for Docker containers to communicate with each other locally,
//     they need be running in the same Docker network.
//   - We can't use localhost since localhost within a container refers to the
//     container itself, not the host machine
//   Docker Compose automatically creates a default network for all services in
//     your docker-compose.yml. You can connect your manually-run container to
//     that network.
//
// IMPORTANT:
//   The Spring Boot app container won't use localhost to access the database
//
// ****** After running the postgres docker container ******
// I can just set the POSTGRES DB, USER, and PASSWORD when running the Spring
//   Boot app in a container with the dev database
// - These are the only ones the compose file uses
//
// 1.) Find the default network name:
//       docker network ls
// 2.) Check that the container is running within the network
//       docker network inspect qu1cksave-backend_default
// 3.) Build the image for the Spring Boot app
//       docker build -t qu1cksave-backend-app .
// 4.) Run the container
//       docker run -p 8080:8080 --network qu1cksave-backend_default --env-file .env qu1cksave-backend-app
//       - Note that the env file is used only for the container since it
//            doesn't work when running with gradlew bootRun
//       - qu1cksave-backend-docker-postgres is the container_name in the yml file
//         -- POSTGRES_HOST needs to be the container name so that they can
//           communicate with each other within the network
//
// ****** Running production mode locally with the container ********
// Just build and run the container
// 1.) docker build -t qu1cksave-backend-app .
// 2.) docker run -p 8080:8080 --env-file .env qu1cksave-backend-app
//     - Make sure to set env vars to use prod env vars



// ============================================================================
// ============================= USEFUL NOTES =================================
// ============================================================================

// In terminal log: No active profile set, falling back to 1 default profile: "default"
// - https://stackoverflow.com/questions/68171743/eclipse-no-active-profile-set-falling-back-to-default-profiles-default
//   - So we can set dev, prod, etc. environments

// Setting environment variables:
// - https://unix.stackexchange.com/questions/56444/how-do-i-set-an-environment-variable-on-the-command-line-and-have-it-appear-in-c
// - https://stackoverflow.com/questions/62119161/adding-environment-variables-to-springs-application-properties
//   -- Input "export POSTGRES_HOST=myvalue" in terminal to set environment variables
//   -- Setting it in Intellij doesn't seem to work
//
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


// -----------------------------------------------------------------------
// -----------------------------------------------------------------------
// ------------------------ DOCKER NOTES ---------------------------------
// -----------------------------------------------------------------------
// -----------------------------------------------------------------------

// ------------------------ Docker Docs ---------------------------------
// https://docs.docker.com/?_gl=1*jf7xwa*_gcl_au*ODAxMjUyNDMyLjE3NTIwMDc2NzQ.*_ga*NjA5MzAyNjQ4LjE3NTA2NTYxOTA.*_ga_XJWPQMJYHQ*czE3NTIwMDk5MjQkbzMkZzEkdDE3NTIwMDk5MzYkajQ4JGwwJGgw
// - There's a .dockerignore file
// https://docs.docker.com/get-started/docker-concepts/the-basics/what-is-an-image/
// - Talks about official docker images and layers
// https://docs.docker.com/get-started/docker-concepts/the-basics/what-is-docker-compose/
// - Docker Compose
//   -- More on https://docs.docker.com/compose/
// https://docs.docker.com/get-started/docker-concepts/building-images/writing-a-dockerfile/
// - Dockerfile: This is what I'll need
// - Seems like we don't want to run as the root user
// - https://docs.docker.com/reference/dockerfile/
//   -- Dockerfile reference
// - https://docs.docker.com/reference/cli/docker/init/
//   -- docker init to quickly build a Dockerfile
//      + WARNING !!!!!!    If any of the files already exist, a prompt appears
//        and provides a warning as well as giving you the option to overwrite
//        all the files. If docker-compose.yaml already exists instead of
//        compose.yaml, docker init can overwrite it, using docker-compose.yaml
//        as the name for the Compose file
// https://docs.docker.com/get-started/docker-concepts/building-images/multi-stage-builds/
// - Has Spring Boot example, but using multi-stage builds
//   -- They used "FROM eclipse-temurin:21.0.2_13-jdk-jammy" to get the image
// https://docs.docker.com/guides/java/containerize/
// - Containerize a Java (Spring) app using Docker
// https://docs.docker.com/build/concepts/dockerfile/
// - Dockerfile overview
//   -- REALLY GOOD SUMMARY
// - https://docs.docker.com/reference/dockerfile/#shell-and-exec-form
//   -- Shell vs exec form CMD (shell = CMD in "array", exec = CMD like a terminal command)
// - https://docs.docker.com/build/concepts/context/
//   -- Build context
//   -- has .dockerignore examples
//      + https://github.com/moby/patternmatcher/tree/main/ignorefile
//        * More on dockerignore pattern matching logic
// https://docs.docker.com/reference/dockerfile/
// - Has almost everything about Dockerfiles
// - Allowing the build container to access secret values
//		RUN --mount=type=secret
//		This mount type allows the build container to access secret values, such as tokens or private keys, without baking them into the image.
//
//		By default, the secret is mounted as a file. You can also mount the secret as an environment variable by setting the env option
// - TODO: Look at Dockerfile examples at the very bottom
// - ENV
//   -- The ENV instruction sets the environment variable <key> to the value <value>. This value
//      will be in the environment for all subsequent instructions in the build stage and can be
//      replaced inline in many as well
//   -- The environment variables set using ENV will persist when a container is run from the resulting image
//   -- TODO: Should I use RUN --mount=type=secret or ENV for secrets, API keys, etc.???
//  - ADD vs COPY: COPY seems to be used more
//  - COPY
//    -- You can specify multiple source files or directories with COPY. The last argument must always be the destination.
//    -- TODO: Look at the "Source" section, to review how to specify paths
//
// https://docs.docker.com/build/building/best-practices/
// - Best practices

// ------------------------ Docker w/ Render ---------------------------------
// https://render.com/docs/docker
// - Docker with Render
//   -- https://render.com/docs/docker-secrets
//      + Using secrets with Docker (IMPORTANT)
//   -- Render can build your service's Docker image based on the Dockerfile in your project repo
//   -- Render omits files and directories from your build context based on your .dockerignore file
// https://community.render.com/t/java-springboot-app-start-command/19835
// https://community.render.com/t/running-java-spring-boot-in-docker-container-on-web-services/3232

// ------------------------ Has examples ---------------------------------
// https://docs.docker.com/guides/java/containerize/
// https://spring.io/guides/gs/spring-boot-docker
// https://docs.spring.io/spring-boot/reference/packaging/container-images/dockerfiles.html
// - Looks very useful
// https://www.docker.com/blog/kickstart-your-spring-boot-application-development/

// TODO: ------------------ SKIM AGAIN ----------------
//  https://docs.docker.com/guides/java/containerize/
//  https://www.docker.com/blog/kickstart-your-spring-boot-application-development/
//  https://spring.io/guides/gs/spring-boot-docker
//  - boot2docker is deprecated
//  - https://www.techtarget.com/searchitoperations/definition/Boot2Docker
//    -- Docker Machine is now used
//  - https://www.reddit.com/r/docker/comments/dyxwug/what_is_docker_machine/
//    -- With Docker on Mac, I don't need Virtual Box not Docker Machine
//  https://docs.spring.io/spring-boot/reference/packaging/container-images/dockerfiles.html

// TODO: Spring Boot Gradle Docker
//  https://medium.com/shoutloudz/dockerize-your-spring-boot-application-with-gradle-afcbf8da11bd
//  https://www.reddit.com/r/docker/comments/1e2g7a6/build_spring_boot_in_the_container_or_outside_of/
//  https://stackoverflow.com/questions/71697307/best-practices-while-building-docker-images-for-spring-boot-app-via-gradle
//  https://stackoverflow.com/questions/78455007/dockerfile-for-gradle-spring-boot-application-using-multi-stage
//  https://www.reddit.com/r/SpringBoot/comments/1d5sxlh/sprinboot_gradle_on_docker_how_to_make_hot_reload/
//  Search "spring boot gradle dockerfile site:stackoverflow.com"


// TODO: ./mvnw package in Gradle

// TODO: How to add secrets Docker env




// TODO: ------------- OFF TOPIC, regarding async and sync --------------
//  Search these:
//  - async in spring boot    OR    async in spring mvc
//  - CompletableFuture java
//  - Why aren't services beans?    OR    why are services component instead of bean
//  ------------------------------------
//  - https://www.reddit.com/r/SpringBoot/comments/1abjzuf/spring_boot_weblux_with_kotlin_or_spring_boot_mvc/
//  - https://stackoverflow.com/questions/70997077/spring-web-mvc-vs-spring-webflux-blocking-and-non-blocking
//  - https://stackoverflow.com/questions/46606246/spring-mvc-async-vs-spring-webflux
//  - https://www.baeldung.com/spring-mvc-async-vs-webflux
//  - https://dev.to/jottyjohn/spring-mvc-vs-spring-webflux-choosing-the-right-framework-for-your-project-4cd2
//  - https://www.reddit.com/r/Frontend/comments/1g5nhun/question_regarding_synchronous_and_asynchronous/
//  - https://softwareengineering.stackexchange.com/questions/380536/how-to-decide-if-an-api-should-be-synchronous-or-asynchronous
