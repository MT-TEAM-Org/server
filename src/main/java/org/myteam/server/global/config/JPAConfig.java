package org.myteam.server.global.config;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.H2Templates;

import jakarta.persistence.EntityManager;

import jakarta.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * querydsl 사용 환경을 위해 추가
 */
@Configuration
public class JPAConfig {
    @PersistenceContext
    private EntityManager entityManager;



    @Bean
    public JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(entityManager);
    }


    @Bean
    public JPASQLQuery<?> sqlQueryFactory(){


        return new JPASQLQuery<>(entityManager, H2Templates.builder().build());
    }
}
