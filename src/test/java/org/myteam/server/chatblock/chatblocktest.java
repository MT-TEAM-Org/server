package org.myteam.server.chatblock;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.myteam.server.chat.block.domain.MemberBlock;
import org.myteam.server.chat.block.dto.request.BlockRequest;
import org.myteam.server.chat.block.service.BlockService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.support.IntegrationTestSupport;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.myteam.server.chat.block.dto.request.BlockRequest.*;
import static reactor.core.publisher.Mono.when;

public class chatblocktest extends IntegrationTestSupport {


    @Autowired
    BlockService blockService;
    Member blocker;
    List<Member> blockedList;
    @BeforeEach
    void setting(){
        blockedList=new ArrayList<>();
       Mockito.when(securityReadService.getMember())
               .thenReturn(blocker);
        blocker=createMember(0);
        for(int i=0;10>i;i++){
            Member m=createMember(i+1);
           blockedList.add(m);
        }
    }

    @Test
    void blockAddSuccess(){

       blockedList.stream()
               .forEach(x->{
                   blockService.banUser(x.getPublicId());
               });
        List<MemberBlock> memberBlocks=memberBlockRepository.findAll();
        Assertions.assertThat(memberBlocks.size()).isEqualTo(10);
    }
    @Test
    void blockDelSuccess(){
        blockedList.stream()
                .forEach(x->{
                    BlockUserRequest blockUserRequest=
                            new BlockUserRequest(x.getPublicId());
                    blockService.banUser(x.getPublicId());
                });
        blockService.unblockUser(blockedList.get(0).getPublicId());
        List<MemberBlock> memberBlocks=memberBlockRepository.findAll();
        Assertions.assertThat(memberBlocks.size()).isEqualTo(9);
    }


}
