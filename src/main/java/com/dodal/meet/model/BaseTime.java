package com.dodal.meet.model;


import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@Getter
// MappedSuperclass : 이 클래스를 상속받는 엔티티들이 필드를 상속 받아 컬럼으로 인식하도록 JPA에서 지원
@MappedSuperclass
// Auditing 기능을 통해 엔티티 생명주기에 따라 Auditing 어노테이션을 인식하여 처리
@EntityListeners(AuditingEntityListener.class)
public class BaseTime {

    // 엔티티가 생성될 때 자동으로 현재 시간 값 할당
    @CreatedDate
    private Timestamp registeredAt;

    // 엔티티가 수정될 때 자동으로 현재 시간 값 할당
    @LastModifiedDate
    private Timestamp updatedAt;
}
