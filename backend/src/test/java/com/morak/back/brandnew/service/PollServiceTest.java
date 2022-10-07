package com.morak.back.brandnew.service;

import static com.morak.back.brandnew.AuthFixture.모락;
import static com.morak.back.brandnew.AuthFixture.에덴;
import static com.morak.back.brandnew.AuthFixture.엘리;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.morak.back.auth.domain.Member;
import com.morak.back.auth.domain.MemberRepository;
import com.morak.back.brandnew.PollCreateRequest;
import com.morak.back.brandnew.PollResponse;
import com.morak.back.brandnew.domain.NewPoll;
import com.morak.back.brandnew.domain.NewPollItem;
import com.morak.back.brandnew.repository.NewPollRepository;
import com.morak.back.poll.domain.PollStatus;
import com.morak.back.poll.ui.dto.PollResultRequest;
import com.morak.back.team.domain.Team;
import com.morak.back.team.domain.TeamMember;
import com.morak.back.team.domain.TeamMemberRepository;
import com.morak.back.team.domain.TeamRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PollServiceTest {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final NewPollRepository pollRepository;
    private final PollService pollService;

    private Team morak;
    private Member eden;

    @Autowired
    public PollServiceTest(TeamRepository teamRepository, MemberRepository memberRepository, TeamMemberRepository teamMemberRepository, NewPollRepository pollRepository) {
        this.teamRepository = teamRepository;
        this.memberRepository = memberRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.pollRepository = pollRepository;
        this.pollService = new PollService(memberRepository, pollRepository);
    }

    @BeforeEach
    void setUp() {
        morak = teamRepository.save(모락);
        eden = memberRepository.save(에덴);
        teamMemberRepository.save(TeamMember.builder().member(eden).team(morak).build());
    }

    @Test
    void 투표를_생성하고_저장한다() {
        // given
        List<String> subjects = List.of("볼링", "보드게임");
        PollCreateRequest request = PollCreateRequest.builder()
                .title("모락 회의")
                .anonymous(true)
                .allowedPollCount(2)
                .closedAt(LocalDateTime.now().plusDays(1))
                .subjects(subjects)
                .build();
        // when
        pollService.createPoll(morak.getCode(), eden.getId(), request);

        // then
        NewPoll poll = pollRepository.findById(1L).orElseThrow();
        List<NewPollItem> pollItems = poll.getPollItems();

        Assertions.assertAll(
                () -> assertThat(poll.getId()).isNotNull(),
                () -> assertThat(poll.getPollInfo().getStatus()).isEqualTo(PollStatus.OPEN),
                () -> assertThat(pollItems.get(0).getSubject()).isEqualTo("볼링"),
                () -> assertThat(pollItems.get(1).getSubject()).isEqualTo("보드게임")
        );
    }

    @Test
    void 코드로_투표를_조회한다() {
        // given
        List<String> subjects = List.of("볼링", "보드게임");
        PollCreateRequest request = PollCreateRequest.builder()
                .title("모락 회의")
                .anonymous(true)
                .allowedPollCount(2)
                .closedAt(LocalDateTime.now().plusDays(1))
                .subjects(subjects)
                .build();

        pollService.createPoll(morak.getCode(), eden.getId(), request);

        NewPoll poll = pollRepository.findById(1L).orElseThrow();

        // when
        PollResponse response = pollService.findPoll(morak.getCode(), eden.getId(), poll.getPollInfo().getCode());

        // then
        assertThat(response.getTitle()).isEqualTo("모락 회의");
    }

    @Test
    void 투표를_진행한다() {
        // given
        List<String> subjects = List.of("볼링", "보드게임");
        PollCreateRequest request = PollCreateRequest.builder()
                .title("모락 회의")
                .anonymous(true)
                .allowedPollCount(2)
                .closedAt(LocalDateTime.now().plusDays(1))
                .subjects(subjects)
                .build();

        String pollCode = pollService.createPoll(morak.getCode(), eden.getId(), request);
        NewPoll poll = pollRepository.findByCode(pollCode).orElseThrow();
        List<NewPollItem> pollItems = poll.getPollItems();

        // when
        List<PollResultRequest> requests = List.of(new PollResultRequest(pollItems.get(0).getId(), "그냥!"));
        pollService.doPoll(morak.getCode(), eden.getId(), pollCode, requests);

        // then
        pollRepository.flush();
        assertThat(pollItems.get(0).getSelectMembers().getValues().get(에덴)).isEqualTo("그냥!");
    }

    @Test
    void 투표_항목을_재선택한다() {
        // given
        Member ellie = memberRepository.save(엘리);

        List<String> subjects = List.of("볼링", "보드게임");
        PollCreateRequest request = PollCreateRequest.builder()
                .title("모락 회의")
                .anonymous(true)
                .allowedPollCount(2)
                .closedAt(LocalDateTime.now().plusDays(1))
                .subjects(subjects)
                .build();

        String pollCode = pollService.createPoll(morak.getCode(), eden.getId(), request);

        NewPoll poll = pollRepository.findByCode(pollCode).orElseThrow();
        List<NewPollItem> pollItems = poll.getPollItems();

        PollResultRequest pollItem1 = new PollResultRequest(pollItems.get(0).getId(), "그냥!");
        PollResultRequest pollItem2 = new PollResultRequest(pollItems.get(1).getId(), "볼링 비싸요!");

        // when
        List<PollResultRequest> requests = List.of(pollItem1, pollItem2);
        pollService.doPoll(morak.getCode(), eden.getId(), pollCode, requests);
        pollRepository.flush();

        List<PollResultRequest> boardRequests = List.of(pollItem1, pollItem2);
        pollService.doPoll(morak.getCode(), ellie.getId(), pollCode, boardRequests);
        pollRepository.flush();

        List<PollResultRequest> boardRequests2 = List.of(pollItem2);
        pollService.doPoll(morak.getCode(), ellie.getId(), pollCode, boardRequests2);
        pollRepository.flush();

        // then
        assertAll(
                () -> assertThat(pollItems.get(0).getSelectMembers().getValues().get(엘리)).isNull(),
                () -> assertThat(pollItems.get(1).getSelectMembers().getValues().get(엘리)).isEqualTo("볼링 비싸요!")
        );
    }
}
