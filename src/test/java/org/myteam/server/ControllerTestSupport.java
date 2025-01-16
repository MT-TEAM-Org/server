package org.myteam.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.myteam.server.article.controller.ArticleController;
import org.myteam.server.article.service.ArticleReadService;
import org.myteam.server.article.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = {
    ArticleController.class,
})
@MockBean(JpaMetamodelMappingContext.class)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected ArticleService articleService;

    @MockBean
    protected ArticleReadService articleReadService;

}

