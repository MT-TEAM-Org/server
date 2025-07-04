package org.myteam.server.util.slack.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SlackService {

	private final String webhookUrl;
	private final RestTemplate restTemplate;

	public SlackService(RestTemplate restTemplate, @Value("${slack.webhook.url}") String webhookUrl) {
		this.restTemplate = restTemplate;
		this.webhookUrl = webhookUrl;
	}

	@Async
	public void sendSlackNotification(String message) {
		String payload = String.format("{\"text\":\"%s\"}", message);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		HttpEntity<String> request = new HttpEntity<>(payload, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				webhookUrl,
				HttpMethod.POST,
				request,
				String.class
			);

			if (response.getStatusCode().is2xxSuccessful()) {
				log.info("Slack notification sent successfully!");
			} else {
				log.error("Failed to send Slack notification. Status code: {}", response.getStatusCode());
			}

		} catch (HttpClientErrorException e) {
			log.error("Slack API error: {}", e.getResponseBodyAsString(), e);
		} catch (Exception e) {
			log.error("Unexpected error while sending Slack notification", e);
		}
	}
}
