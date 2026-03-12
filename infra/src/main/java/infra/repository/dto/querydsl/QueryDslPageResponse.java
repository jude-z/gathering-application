package infra.repository.dto.querydsl;


import lombok.Builder;
import lombok.Getter;
import page.PageInfo;

import java.util.List;

@Getter
public class QueryDslPageResponse<T>{
    private List<T> content;
    private long pageNum;
    private long pageSize;
    private long totalCount;
    private long totalPage;
    private boolean isEmpty;

    @Builder
    private QueryDslPageResponse(List<T> content, int pageNum, int pageSize, long totalCount, int totalPage,boolean isEmpty){
        this.content = content;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = totalPage;
        this.isEmpty = isEmpty;

    }

    public static <T> QueryDslPageResponse<T> of(List<T> content, PageInfo<List<T>> info){
        return QueryDslPageResponse.<T>builder()
                .content(content)
                .pageNum(info.getPageNum())
                .pageSize(info.getPageSize())
                .totalCount(info.getTotalCount())
                .totalPage(info.getTotalPage())
                .isEmpty(content.isEmpty())
                .build();

    }
}
