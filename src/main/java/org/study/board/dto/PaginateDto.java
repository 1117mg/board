package org.study.board.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class PaginateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int totalSize;				// 총 항목 수
    private int totalPage;				// 총 페이지 수

    private int pageNo = 1;				// 현재 페이지 번호
    private final int pageSize;				// 페이지 당 항목 수
    private String pageName = "page";	// 페이지 파라미터명

    private String boardType;           // 게시판 타입

    private boolean pageLoop = false;	// 페이지 순환 여부(pageNo가 totalPage보다 클 경우 pageNo 설정 방법)

    private int pageOffset;				// 현재 페이지 이전 항목 수
    private int pageFinal;				// 현재 페이지 마지막 항목 수

    private final int nationSize;				// 페이지 네비게이션 묶음 당 수
    private int nationBegin;			// 페이지 네비게이션 시작 번호
    private int nationClose;			// 페이지 네비게이션 끝 번호

    private String params;				// 파라미터(key1=value1&key2=value2)

    public PaginateDto(int pageSize, int nationSize) {
        this.pageSize = pageSize;
        this.nationSize = nationSize;
    }

    public PaginateDto(int pageSize) {
        this(pageSize, 0);
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageLoop(boolean pageLoop) {
        this.pageLoop = pageLoop;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;

        this._calc();
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public void _calc() {
        if (pageSize <= 0) return;

        // 전체 페이지 수 계산
        totalPage = (int) Math.ceil(totalSize / (double) pageSize);
        if (totalPage < 1) {
            totalPage = 1;
        }

        // 현재 페이지 번호 확인
        if (pageNo < 1) {
            pageNo = 1;
        }
        if (totalPage < pageNo) {
            pageNo = (pageLoop ? 1 : totalPage);
        }

        // 페이지 번호 계산
        pageOffset = (pageNo - 1) * pageSize;
        pageFinal = pageOffset + pageSize;
        if (pageFinal > totalSize) {
            pageFinal = totalSize;
        }

        // 페이지 네비게이션 계산
        if (nationSize > 0) {
            int currentPageNation = (int) Math.ceil(pageNo / (double) nationSize); // 현재 페이지 네이션 번호

            nationBegin = (currentPageNation - 1) * nationSize + 1;
            nationClose = currentPageNation * nationSize;

            if (nationClose > totalPage) {
                nationClose = totalPage;
            }

            // 이전 버튼이나 다음 버튼이 비활성화 되어야 하는지 확인
            if (pageNo <= 1) {
                nationBegin = 1;
            }
            if (pageNo >= totalPage) {
                nationClose = totalPage;
            }
        }
    }


    @Override
    public String toString() {
        return "PaginateDto [totalSize=" + totalSize + ", totalPage=" + totalPage + ", pageNo=" + pageNo + ", pageSize="
                + pageSize + ", pageOffset=" + pageOffset + ", pageFinal=" + pageFinal + ", nationSize=" + nationSize
                + ", nationBegin=" + nationBegin + ", nationClose=" + nationClose + "]";
    }

}
