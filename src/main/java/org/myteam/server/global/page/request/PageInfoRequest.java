package org.myteam.server.global.page.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageInfoRequest {

	@Schema(description = "페이지 번호")
	private int page = 1;
	@Schema(description = "각 페이지 컨텐츠 갯수")
	private int size = 12;

	public PageInfoRequest(int page, int size) {
		this.page = page;
		this.size = size;
	}
}
