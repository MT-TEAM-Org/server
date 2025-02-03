package org.myteam.server;

import org.myteam.server.news.controller.NewsCommentController;
import org.myteam.server.news.controller.NewsController;
import org.myteam.server.news.service.NewsCommentReadService;
import org.myteam.server.news.service.NewsCommentService;
import org.myteam.server.news.service.NewsReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@WebMvcTest(controllers = {
	NewsController.class,
	NewsCommentController.class
})
@MockBean(JpaMetamodelMappingContext.class)
public abstract class ControllerTestSupport {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	protected NewsReadService newsReadService;

	@MockBean
	protected NewsCommentService newsCommentService;

	@MockBean
	protected NewsCommentReadService newsCommentReadService;

}

