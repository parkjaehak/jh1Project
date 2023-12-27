package Jh1.project1.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * FAST : 빠른배송
 */
@Data
@AllArgsConstructor
public class DeliveryCode {
    private String code;// 시스템용
    private String displayName; // 고객용
}
