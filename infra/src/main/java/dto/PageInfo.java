package dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PageInfo<T>{

    private T content;
    private int pageNum;
    private int pageSize;
    private long totalCount;
    private int totalPage;
    private int elementSize;

    @Builder
    private PageInfo(T content, int pageNum, int pageSize, long totalCount, int totalPage,int elementSize){
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = totalPage;
        this.elementSize = elementSize;
        this.content = content;

    }

    public static <T> PageInfo<T> of(T content,int pageNum, int pageSize, long totalCount, int totalPage,int elementSize){
        return PageInfo.<T>builder()
                .content(content)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .totalCount(totalCount)
                .totalPage(totalPage)
                .elementSize(elementSize)
                .build();
    }



}
