package org.myteam.server.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.domain.BoardType;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.repository.BoardCountRepository;
import org.myteam.server.board.repository.BoardRepository;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.repository.ImprovementCountRepository;
import org.myteam.server.improvement.repository.ImprovementRepository;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.myteam.server.inquiry.repository.InquiryCountRepository;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.MemberService;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.news.repository.NewsRepository;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.myteam.server.news.newsCount.repository.NewsCountRepository;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeCount;
import org.myteam.server.notice.repository.NoticeCountRepository;
import org.myteam.server.notice.repository.NoticeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final MemberJpaRepository memberJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardRepository boardRepository;
    private final BoardCountRepository boardCountRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeCountRepository noticeCountRepository;
    private final ImprovementRepository improvementRepository;
    private final ImprovementCountRepository improvementCountRepository;
    private final NewsRepository newsRepository;
    private final NewsCountRepository newsCountRepository;
    private final InquiryCountRepository inquiryCountRepository;
    private final InquiryRepository inquiryRepository;
    private final MemberService memberService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        memberService.create(
                MemberSaveRequest.builder()
                        .email("test@naver.com")
                        .password("123123a")
                        .tel("01012345678")
                        .nickname("테스트 유저")
                        .build()
        );

        Member admin = Member.builder()
                .email("admin@naver.com")
                .password(passwordEncoder.encode("123123a"))
                .tel("01012345678")
                .nickname("테스트 관리자")
                .role(MemberRole.ADMIN)
                .type(MemberType.LOCAL)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.ACTIVE)
                .build();

        Member user = memberJpaRepository.findByEmail("test@naver.com").get();

        log.info("User 저장 완료 - publicId: {}", user.getPublicId());

        createImprovement(user);
        createInquiry(user);
//        createNotice(admin);
        createBoard(user, BoardType.BASEBALL, CategoryType.FREE, "title", "contetnt");
        createNews(1, NewsCategory.BASEBALL, 0);

        log.info("member data 초기화 완료");
    }
    protected Board createBoard(Member member, BoardType boardType, CategoryType categoryType, String title,
                                String content) {

        Board board = Board.builder()
                .member(member)
                .boardType(boardType)
                .categoryType(categoryType)
                .title(title)
                .content(content)
                .link("https://www.naver.com")
                .createdIp("127.0.0.1")
                .thumbnail("http://localhost:9000/devbucket/inage/1235.png")
                .build();

        boardRepository.save(board);

        BoardCount boardCount = BoardCount.builder()
                .board(board)
                .recommendCount(0)
                .commentCount(0)
                .viewCount(0)
                .build();

        boardCountRepository.save(boardCount);

        return board;
    }

    protected News createNews(int index, NewsCategory category, int count) {
        News savedNews = newsRepository.save(News.builder()
                .title("기사타이틀" + index)
                .category(category)
                .thumbImg("www.test.com")
                .postDate(LocalDateTime.now())
                .source("www.test.com")
                .content("뉴스본문")
                .build());

        NewsCount newsCount = NewsCount.builder()
                .recommendCount(count)
                .commentCount(count)
                .viewCount(count)
                .build();

        newsCount.updateNews(savedNews);

        newsCountRepository.save(newsCount);

        return savedNews;
    }

    protected Notice createNotice(Member member) {
        Notice notice = Notice.builder()
                .member(member)
                .title("공지사하아앙 제목")
                .content("공지공지공지")
                .createdIP("0.0.0.1")
                .imgUrl(null)
                .build();

        noticeRepository.save(notice);

        NoticeCount noticeCount = NoticeCount.builder()
                .notice(notice)
                .recommendCount(0)
                .commentCount(0)
                .viewCount(0)
                .build();

        noticeCountRepository.save(noticeCount);

        return notice;
    }

    protected Inquiry createInquiry(Member member) {
        Inquiry inquiry = Inquiry.builder()
                .content("문의사항ㅇㅇㅇ")
                .member(member)
                .clientIp("0.0.0.1")
                .createdAt(LocalDateTime.now())
                .isAdminAnswered(false)
                .build();

        inquiryRepository.save(inquiry);

        InquiryCount inquiryCount = InquiryCount.createCount(inquiry);
        inquiryCountRepository.save(inquiryCount);

        return inquiry;
    }

    protected Improvement createImprovement(Member member) {
        Improvement improvement = Improvement.builder()
                .member(member)
                .title("개선요처어엉ㅇ 제목")
                .content("개선개선개선")
                .createdIP("0.0.0.1")
                .imgUrl(null)
                .build();
        improvementRepository.save(improvement);

        ImprovementCount improvementCount = ImprovementCount.createImprovementCount(improvement);
        improvementCountRepository.save(improvementCount);

        return improvement;
    }
}