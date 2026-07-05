package com.example.netflix_clone.dto.response;

import com.example.netflix_clone.entity.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponse {
    private Long id;
    private String title;
    private String description;
    private Integer year;
    private String rating;
    private Integer duration;
    private String src;
    private String poster;
    private boolean publish;
    private List<String> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isInWatchList;

    public static VideoResponse fromEntity(Video video) {
         VideoResponse response = new VideoResponse(
                video.getId(),
                video.getTitle()
                ,video.getDescription(),
                video.getYear(),
                video.getRating(),
                video.getDuration(),
                video.getSrc(),
                video.getPoster(),
                video.isPublished(),
                video.getCategories(),
                video.getCreatedAt(),
                video.getUpdatedAt(),
                video.getIsInWatchList()
        );
        if(video.getIsInWatchList() != null){
            video.setIsInWatchList(video.getIsInWatchList());
        }
        return response;
    }
}
