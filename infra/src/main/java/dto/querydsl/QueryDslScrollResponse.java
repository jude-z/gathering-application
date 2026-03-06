package dto.querydsl;


import dto.ScrollInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class QueryDslScrollResponse <T>{
    private List<T> content;
    private long pageNum;
    private long pageSize;
    private boolean hasNext;

    @Builder
    private QueryDslScrollResponse(List<T> content,long pageNum, long pageSize,boolean hasNext){
        this.content = content;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.hasNext = hasNext;

    }

    public static <T> QueryDslScrollResponse<T> of(List<T> content, ScrollInfo info){
        return QueryDslScrollResponse.<T>builder()
                .content(content)
                .pageNum(info.getPageNum())
                .pageSize(info.getPageSize())
                .hasNext(info.isHasNext())
                .build();

    }
}
