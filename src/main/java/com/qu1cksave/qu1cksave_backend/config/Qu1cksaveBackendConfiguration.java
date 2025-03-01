//package com.qu1cksave.qu1cksave_backend.config;
//
//import org.hibernate.SessionFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.orm.hibernate5.HibernateTransactionManager;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@Configuration
//public class Qu1cksaveBackendConfiguration {
//
//    @Bean
//    public PlatformTransactionManager txManager(@Autowired SessionFactory sessionFactory) {
//        return new HibernateTransactionManager(sessionFactory);
//    }
//}
