package org.myteam.server.match.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PercentageUtilTest {

	@DisplayName("두개의 숫자를 입력받아 퍼센트를 계산하다.")
	@Test
	void getCalculatedPercentages() {
		assertThat(PercentageUtil.getCalculatedPercentages(1, 2))
			.isEqualTo(new int[] {33, 67});
	}
}
