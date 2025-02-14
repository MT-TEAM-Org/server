package org.myteam.server.upload.domain;

public enum MediaType {
	IMAGE_PNG("image/png"),
	IMAGE_JPEG("image/jpeg"),
	IMAGE_GIF("image/gif"),
	IMAGE_WEBP("image/webp"),
	IMAGE_HEIC("image/heic"),
	VIDEO_MP4("video/mp4"),
	VIDEO_MOV("video/quicktime"),
	VIDEO_WEBM("video/webm");

	private final String value;

	MediaType(String value) {
		this.value = value;
	}

	// 값 반환 메서드
	public String getValue() {
		return value;
	}
}
