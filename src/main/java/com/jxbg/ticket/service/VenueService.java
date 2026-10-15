package com.jxbg.ticket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxbg.ticket.entity.Venue;
import com.jxbg.ticket.vo.VenueBrowseHistoryVO;
import com.jxbg.ticket.vo.VenueHotVO;

import java.util.List;

public interface VenueService extends IService<Venue> {

    Venue getVenueDetail(Long id);

    Venue getVenueDetail(Long userId, Long id);

    List<VenueBrowseHistoryVO> listBrowseHistory(Long userId, Integer limit);

    Long createVenue(Venue venue);

    void updateVenue(Long id, Venue venue);

    void disableVenue(Long id);
}
