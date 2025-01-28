package org.myteam.server.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.chat.domain.FilterData;
import org.myteam.server.chat.repository.FilterDataRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FilterWriteService {

    private final FilterDataRepository filterDataRepository;
    private final BadWordFilter badWordFilter;

    /**
     * 애플리케이션 시작 시 필터링 단어 로드
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadFilteredWords() {
        List<String> words = filterDataRepository.findAll()
                .stream()
                .map(FilterData::getWord)
                .collect(Collectors.toList());
        badWordFilter.loadFilteredWords(words);
        log.info("Filtered words loaded: {}", words);
    }

    public void addFilteredWord(String word) {
        filterDataRepository.save(new FilterData(word));
        badWordFilter.addFilteredWord(word);
        log.info("Successfully add Filter");
    }

    public void removeFilteredWord(String word) {
        filterDataRepository.deleteByWord(word);
        badWordFilter.removeFilteredWord(word);
        log.info("Successfully delete Filter");
    }
}