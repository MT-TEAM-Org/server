package org.myteam.server.news.news.dto.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewsCommentSearchDto {
	private Long newsCommentId;
	private String comment;
	private String imageUrl;
}
