package org.myteam.server.aop.count;

import org.myteam.server.report.domain.DomainType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CountView {
    DomainType domain(); // ex: DomainType.BOARD
    String idParam(); // "boardId", "newsId"
}
