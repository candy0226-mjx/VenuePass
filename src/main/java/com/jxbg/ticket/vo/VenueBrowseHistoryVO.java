package com.jxbg.ticket.vo;

import com.jxbg.ticket.entity.Venue;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class VenueBrowseHistoryVO {

    private Long venueId;

    private String name;

    private String address;

    private LocalDateTime browseTime;

    public static VenueBrowseHistoryVO of(Venue venue, Double score) {
        VenueBrowseHistoryVO vo = new VenueBrowseHistoryVO();
        vo.setVenueId(venue.getId());
        vo.setName(venue.getName());
        vo.setAddress(venue.getAddress());
        if (score != null) {
            vo.setBrowseTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(score.longValue()), ZoneId.systemDefault()));
        }
        return vo;
    }
}
