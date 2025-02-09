package org.myteam.server;

import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.MemberWriteService;
import org.myteam.server.news.news.controller.NewsController;
import org.myteam.server.news.news.service.NewsReadService;
import org.myteam.server.news.newsComment.controller.NewsCommentController;
import org.myteam.server.news.newsComment.service.NewsCommentReadService;
import org.myteam.server.news.newsComment.service.NewsCommentService;
import org.myteam.server.news.newsReply.controller.NewsReplyController;
import org.myteam.server.news.newsReply.service.NewsReplyReadService;
import org.myteam.server.news.newsReply.service.NewsReplyService;
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
	NewsCommentController.class,
	NewsReplyController.class
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

	@MockBean
	protected NewsReplyService newsReplyService;

	@MockBean
	protected NewsReplyReadService newsReplyReadService;

	@MockBean
	protected MemberWriteService memberWriteService;

	@MockBean
	protected MemberJpaRepository memberJpaRepository;

}

