package org.myteam.server.global.config;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
    public CriteriaBuilderFactory criteriaBuilderFactory() {
        CriteriaBuilderConfiguration config = Criteria.getDefault();

        return config.createCriteriaBuilderFactory(entityManager.getEntityManagerFactory());
    }

    @Bean
    public BlazeJPAQueryFactory blazeJPAQueryFactory() {

        return new BlazeJPAQueryFactory(entityManager, criteriaBuilderFactory());
    }

    @Bean
    public JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
