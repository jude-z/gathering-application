package infra.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ScrollInfo {
    private int pageNum;
    private int pageSize;
    private boolean hasNext;

    @Builder
    private ScrollInfo(int pageNum, int pageSize, boolean hasNext){
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
    }

    public static ScrollInfo of(int pageNum, int pageSize, boolean hasNext){
        return ScrollInfo.builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasNext(hasNext)
                .build();
    }



}
