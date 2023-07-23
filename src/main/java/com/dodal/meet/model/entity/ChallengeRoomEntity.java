package com.dodal.meet.model.entity;


import com.dodal.meet.controller.request.challengeRoom.ChallengeRoomCreateRequest;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenge_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class ChallengeRoomEntity {

    @Id
    @Column(name = "challenge_room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String content;

    private String thumbnailImgUrl;

    private int recruitCnt;

    private int certCnt;

    private String certContent;

    private String certCorrectImgUrl;

    private String certWrongImgUrl;

    private int bookmarkCnt;

    private String warnContent;

    private int accuseCnt;

    private int userCnt;

    private String noticeContent;

    private Timestamp registeredAt;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "challenge_tag_id")
    private ChallengeTagEntity challengeTagEntity;

    @Builder.Default
    @OneToMany(mappedBy = "challengeRoomEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeUserEntity> challengeUserEntities = new ArrayList<>();

    @PrePersist
    void prePersist() {
        this.bookmarkCnt = 0;
        this.accuseCnt = 0;
        this.userCnt = 1;
        this.registeredAt = Timestamp.from(Instant.now());
    }

    public void addChallengeTagEntity(ChallengeTagEntity challengeTagEntity) {
        this.challengeTagEntity = challengeTagEntity;
        challengeTagEntity.addChallengeRoomEntity(this);
    }
    public static ChallengeRoomEntity dtoToEntity(ChallengeRoomCreateRequest request) {
        return ChallengeRoomEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .recruitCnt(request.getRecruitCnt())
                .certCnt(request.getCertCnt())
                .certContent(request.getCertContent())
                .warnContent(request.getWarnContent())
                .challengeUserEntities(new ArrayList<>())
                .build();
    }

    public void updateImgUrl(String thumbnailImgUrl, String certCorrectImgUrl, String certWrongImgUrl) {
        this.thumbnailImgUrl = thumbnailImgUrl;
        this.certCorrectImgUrl = certCorrectImgUrl;
        this.certWrongImgUrl = certWrongImgUrl;
    }
}
