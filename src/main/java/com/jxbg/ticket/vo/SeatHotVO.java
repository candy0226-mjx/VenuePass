package com.jxbg.ticket.vo;

import com.jxbg.ticket.entity.Seat;
import com.jxbg.ticket.entity.Venue;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeatHotVO {

    private Long seatId;

    private Long venueId;

    private String venueName;

    private Long areaId;

    private String seatNo;

    private String name;

    private String resourceType;

    private BigDecimal basePrice;

    private Long hotScore;

    public static SeatHotVO of(Seat seat, Venue venue, Long hotScore) {
        SeatHotVO vo = new SeatHotVO();
        vo.setSeatId(seat.getId());
        vo.setVenueId(seat.getVenueId());
        vo.setVenueName(venue == null ? null : venue.getName());
        vo.setAreaId(seat.getAreaId());
        vo.setSeatNo(seat.getSeatNo());
        vo.setName(seat.getName());
        vo.setResourceType(seat.getResourceType());
        vo.setBasePrice(seat.getBasePrice());
        vo.setHotScore(hotScore);
        return vo;
    }
}
