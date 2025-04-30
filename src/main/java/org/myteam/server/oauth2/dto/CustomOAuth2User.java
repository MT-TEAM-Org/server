package org.myteam.server.oauth2.dto;

import lombok.Getter;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Getter
public class CustomOAuth2User implements OAuth2User {
    private final String NOT_USED = "Not Used";
    private String username;
    private String role;
    private UUID publicId;
    private MemberStatus status;
    private MemberType type;

    public CustomOAuth2User(String username, String role, UUID publicId,
                            MemberStatus status, MemberType type) {
        this.username = username;
        this.role = role;
        this.publicId = publicId;
        this.status = status;
        this.type = type;
    }
    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role;
            }
        });
        return collection;
    }

    // 미사용
    @Override
    public String getName() {
        return NOT_USED;
    }

    public UUID getPublicId() { return publicId; }
    public MemberStatus getStatus() { return status; }
}
