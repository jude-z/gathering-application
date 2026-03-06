package dto.querydsl;


import dto.PageInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class QueryDslPageResponse<T>{
    private List<T> content;
    private long pageNum;
    private long pageSize;
    private long totalCount;
    private long totalPage;

    @Builder
    private QueryDslPageResponse(List<T> content, int pageNum, int pageSize, long totalCount, int totalPage){
        this.content = content;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = totalPage;

    }

    public static <T> QueryDslPageResponse<T> of(List<T> content, PageInfo info){
        return QueryDslPageResponse.<T>builder()
                .content(content)
                .pageNum(info.getPageNum())
                .pageSize(info.getPageSize())
                .totalCount(info.getTotalCount())
                .totalPage(info.getTotalPage())
                .build();

    }
}
