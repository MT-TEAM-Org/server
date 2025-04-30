package org.myteam.server.support;

import org.myteam.server.match.match.controller.MatchController;
import org.myteam.server.match.match.service.MatchReadService;
import org.myteam.server.match.matchPrediction.controller.MatchPredictionController;
import org.myteam.server.match.matchPrediction.service.MatchPredictionReadService;
import org.myteam.server.match.matchPrediction.service.MatchPredictionService;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.MemberService;
import org.myteam.server.news.news.controller.NewsController;
import org.myteam.server.news.news.service.NewsReadService;
import org.myteam.server.news.newsCount.controller.NewsCountController;
import org.myteam.server.news.newsCount.service.NewsCountService;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberReadService;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberService;
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
	NewsCountController.class,
	MatchController.class,
	MatchPredictionController.class
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
	protected NewsCountService newsCountService;
	@MockBean
	protected NewsCountMemberReadService newsCountMemberReadService;
	@MockBean
	protected NewsCountMemberService newsCountMemberService;
	@MockBean
	protected MemberService memberService;
	@MockBean
	protected MemberJpaRepository memberJpaRepository;
	@MockBean
	protected MatchReadService matchReadService;
	@MockBean
	protected MatchPredictionReadService matchPredictionReadService;
	@MockBean
	protected MatchPredictionService matchPredictionService;

}

