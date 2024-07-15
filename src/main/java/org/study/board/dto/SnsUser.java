package org.study.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SnsUser {
    private Long idx;
    private String snsId;
    private String snsType;
    private String snsConnectDate;
    private Long gno;
    private String snsName;
}
