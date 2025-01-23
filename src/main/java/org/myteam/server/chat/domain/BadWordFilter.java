package org.myteam.server.chat.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BadWordFilter {

    private List<String> filteredWords = new ArrayList<>();
    private final Map<String, Pattern> filterPatterns = new HashMap<>();
    private final String[] delimiters = {" ", ",", ".", "!", "?", "@", "1"}; // 구분자 리스트
    private final String substituteValue = "*"; // 치환할 문자
    private String delimiterPattern;

    public BadWordFilter() {
        delimiterPattern = buildDelimiterPattern();
    }

    /**
     * 필터링 단어 로드
     */
    public void loadFilteredWords(List<String> words) {
        log.info("필터링 단어 데이터 로드 시작");
        this.filteredWords = new ArrayList<>(words);
        compileBadWordPatterns();
        log.info("필터링 단어 데이터 로드 완료");
    }

    /**
     * 필터링 단어 추가
     */
    public void addFilteredWord(String word) {
        filteredWords.add(word);
        addPatterns(word);
        log.info("필터링 단어 추가: {}", word);
    }

    /**
     * 필터링 단어 삭제
     */
    public void removeFilteredWord(String word) {
        filteredWords.remove(word);
        deletePattern(word);
        log.info("필터링 단어 삭제: {}", word);
    }

    /**
     * 메시지에서 비속어 필터링
     */
    public String filterMessage(String message) {
        String result = message;
        for (Map.Entry<String, Pattern> entry : filterPatterns.entrySet()) {
            Pattern pattern = entry.getValue();
            result = pattern.matcher(result).replaceAll(matchedWord ->
                    substituteValue.repeat(matchedWord.group().length()));
        }
        return result;
    }

    /**
     * 필터링 단어를 기반으로 패턴 컴파일
     */
    private void compileBadWordPatterns() {
        filterPatterns.clear();
        filteredWords.forEach(this::addPatterns);
    }

    /**
     * 필터링 단어에 대한 패턴 추가
     */
    private void addPatterns(String word) {
        if (!filterPatterns.containsKey(word)) {
            String regexPattern = String.join(delimiterPattern, word.split(""));
            filterPatterns.put(word, Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE));
        }
    }

    /**
     * 필터링 단어에 대한 패턴 삭제
     */
    private void deletePattern(String word) {
        if (filterPatterns.remove(word) != null) {
            log.info("패턴 삭제 완료: {}", word);
        } else {
            log.warn("삭제하려는 단어가 존재하지 않습니다: {}", word);
        }
    }

    /**
     * 구분자 패턴 빌드
     */
    private String buildDelimiterPattern() {
        return Arrays.stream(delimiters)
                .map(Pattern::quote)
                .collect(Collectors.joining("", "[", "]*"));
    }
}
