package dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PageableInfo {
    private int offset;
    private int limit;

    @Builder
    private PageableInfo(int offset, int limit){
        this.offset = offset;
        this.limit = limit;
    }

    public static PageableInfo of(int offset, int limit){
        return PageableInfo.builder()
                .offset(offset)
                .limit(limit)
                .build();
    }
}
