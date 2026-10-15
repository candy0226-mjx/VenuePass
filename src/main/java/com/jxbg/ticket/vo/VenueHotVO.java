package com.jxbg.ticket.vo;

import com.jxbg.ticket.entity.Venue;
import lombok.Data;

@Data
public class VenueHotVO {

    private Long venueId;

    private String name;

    private String address;

    private String description;

    private String contactPhone;

    private Integer status;

    private Long viewCount;

    public static VenueHotVO of(Venue venue, Long viewCount) {
        VenueHotVO vo = new VenueHotVO();
        vo.setVenueId(venue.getId());
        vo.setName(venue.getName());
        vo.setAddress(venue.getAddress());
        vo.setDescription(venue.getDescription());
        vo.setContactPhone(venue.getContactPhone());
        vo.setStatus(venue.getStatus());
        vo.setViewCount(viewCount);
        return vo;
    }
}
