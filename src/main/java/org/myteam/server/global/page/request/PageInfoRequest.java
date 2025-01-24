package org.myteam.server.global.page.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageInfoRequest {

	private int page = 1;
	private int size = 12;

	public PageInfoRequest(int page, int size) {
		this.page = page;
		this.size = size;
	}
}
