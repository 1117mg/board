package org.study.board.dto;

import lombok.Data;

@Data
public class Category {
    private int ctgNo; // 카테고리 고유 번호
    private String ctgNm; // 카테고리 이름
    private int ctgSort; // 카테고리 정렬 순서
    private boolean ctgUseYn; // 카테고리 사용 여부
    private String ctgPno; // 부모 카테고리 고유 번호
}