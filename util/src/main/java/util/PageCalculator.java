package util;


import infra.dto.PageInfo;
import infra.dto.PageableInfo;

public class PageCalculator {
    public static PageableInfo toPageableInfo(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return PageableInfo.of(offset, pageSize);
    }
    public static PageableInfo toDefaultPageableInfo(){
        return PageCalculator.toPageableInfo(1,1);
    }


    public static <T> PageInfo<T> toPageInfo(T content, int offset, int limit, long totalCount, int elementSize){
        int pageNum = (offset / limit) + 1;
        int totalPage = (int) Math.ceil((double) totalCount / limit);
        return PageInfo.of(content,pageNum, limit, totalCount, totalPage, elementSize);
    }

    public static <T> PageInfo<T> toPageInfo(T content, PageableInfo pageableInfo, long totalCount, int elementSize){
        int offset = pageableInfo.getOffset();
        int limit = pageableInfo.getLimit();
        int pageNum = (offset / limit) + 1;
        int totalPage = (int) Math.ceil((double) totalCount / limit);
        return PageInfo.of(content,pageNum, limit, totalCount, totalPage, elementSize);
    }
}
