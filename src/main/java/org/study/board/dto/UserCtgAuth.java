package org.study.board.dto;

import lombok.Data;

@Data
public class UserCtgAuth {
    private long userId;
    private int ctgNo;
    private boolean canRead;
    private boolean canWrite;
}
