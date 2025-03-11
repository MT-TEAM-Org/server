package org.myteam.server.news.newsComment.dto.repository;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.util.ClientUtils;

@Getter
@NoArgsConstructor
public class NewsCommentDto {

	@Schema(description = "뉴스 댓글 ID")
	private Long newsCommentId;
	@Schema(description = "뉴스 ID")
	private Long newsId;
	@Schema(description = "뉴스 작성자")
	private NewsCommentMemberDto memberDto;
	@Schema(description = "뉴스 댓글")
	private String comment;

	@Schema(description = "댓글 이미지")
	private String imgUrl;
	@Schema(description = "뉴스 댓글 작성시 IP")
	private String ip;
	@Schema(description = "뉴스 댓글 날짜")
	private LocalDateTime createTime;
	@Schema(description = "뉴스 댓글 추천수")
	private int recommendCount;
	@Schema(description = "뉴스 댓글 추천 여부")
	private boolean isRecommend;

	public NewsCommentDto(Long newsCommentId, Long newsId, NewsCommentMemberDto memberDto, String comment,
						  String imgUrl, String ip, LocalDateTime createTime, int recommendCount, boolean isRecommend) {
		this.newsCommentId = newsCommentId;
		this.newsId = newsId;
		this.memberDto = memberDto;
		this.comment = comment;
		this.imgUrl = imgUrl;
		this.ip = ClientUtils.maskIp(ip);
		this.createTime = createTime;
		this.recommendCount = recommendCount;
		this.isRecommend = isRecommend;
	}
}
