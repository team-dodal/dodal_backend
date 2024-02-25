package com.dodal.meet.model.entity;


import com.dodal.meet.controller.request.challengeroom.ChallengeRoomCreateRequest;
import com.dodal.meet.controller.request.challengeroom.ChallengeRoomUpdateRequest;
import com.dodal.meet.model.BaseTime;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenge_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class ChallengeRoomEntity extends BaseTime {

    @Id
    @Column(name = "challenge_room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Long hostId;

    @Column(nullable = false, length = 16)
    private String hostNickname;

    private String hostProfileUrl;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = true, length = 255)
    private String thumbnailImgUrl;

    @Column(nullable = false, length = 50)
    private int recruitCnt;

    @Column(nullable = false, length = 50)
    private int certCnt;

    @Column(nullable = false, length = 500)
    private String certContent;

    @Column(nullable = true, length = 255)
    private String certCorrectImgUrl;

    @Column(nullable = true, length = 255)
    private String certWrongImgUrl;

    @Column(nullable = false, length = 50)
    private int bookmarkCnt;

    @Column(nullable = false, length = 50)
    private int accuseCnt;

    @Column(nullable = false, length = 50)
    private int userCnt;

    @Column(nullable = true, length = 255)
    private String noticeTitle;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "challenge_tag_id")
    private ChallengeTagEntity challengeTagEntity;

    @Builder.Default
    @OneToMany(mappedBy = "challengeRoomEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeUserEntity> challengeUserEntities = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "challengeRoomEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeNotiEntity> challengeNotiEntities = new ArrayList<>();

    @PrePersist
    void prePersist() {
        this.bookmarkCnt = 0;
        this.accuseCnt = 0;
        this.userCnt = 1;
    }

    public void addChallengeTagEntity(ChallengeTagEntity challengeTagEntity) {
        this.challengeTagEntity = challengeTagEntity;
        challengeTagEntity.addChallengeRoomEntity(this);
    }

    public void updateNotiTitle(String notiTitle) {
        this.noticeTitle = notiTitle;
    }

    public void updateBookmark(int num) {
        this.bookmarkCnt += num;
    }

    public void updateUserCnt(int num) {
        this.userCnt += num;
    }

    public static ChallengeRoomEntity newInstance(ChallengeRoomCreateRequest request) {
        return ChallengeRoomEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .recruitCnt(request.getRecruitCnt())
                .certCnt(request.getCertCnt())
                .certContent(request.getCertContent())
                .challengeUserEntities(new ArrayList<>())
                .thumbnailImgUrl(request.getThumbnailImgUrl())
                .certCorrectImgUrl(request.getCertCorrectImgUrl())
                .certWrongImgUrl(request.getCertWrongImgUrl())
                .build();
    }

    public void updateUserInfo(UserEntity userEntity) {
        this.hostId = userEntity.getId();
        this.hostProfileUrl = userEntity.getProfileUrl();
        this.hostNickname = userEntity.getNickname();
    }

    public void updateChallengeRoom(ChallengeRoomUpdateRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.recruitCnt = request.getRecruitCnt();
        this.certCnt = request.getCertCnt();
        this.certContent = request.getCertContent();
        this.thumbnailImgUrl = request.getThumbnailImgUrl();
        this.certCorrectImgUrl = request.getCertCorrectImgUrl();
        this.certWrongImgUrl = request.getCertWrongImgUrl();
    }

    public void updateDefaultImgUrl(String defaultImgUrl) {
        this.thumbnailImgUrl = defaultImgUrl;
    }
}
