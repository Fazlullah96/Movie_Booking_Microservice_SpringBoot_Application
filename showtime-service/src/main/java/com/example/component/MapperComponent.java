package com.example.component;

import com.example.dtos.ShowRequest;
import com.example.dtos.ShowResponse;
import com.example.models.Show;
import com.example.models.ShowSeat;
import org.springframework.stereotype.Component;

@Component
public class MapperComponent {
    public Show toShowEntity(ShowRequest request){
        return Show
                .builder()
                .movieId(request.getMovieId())
                .screenId(request.getScreenId())
                .showDate(request.getShowDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(true)
                .build();
    }

    public ShowResponse toShowResponse(Show show){
        return ShowResponse
                .builder()
                .id(show.getId())
                .movieId(show.getMovieId())
                .screenId(show.getScreenId())
                .startTime(show.getStartTime())
                .endTime(show.getEndTime())
                .showDate(show.getShowDate())
                .isActive(show.getIsActive())
                .build();
    }

}
