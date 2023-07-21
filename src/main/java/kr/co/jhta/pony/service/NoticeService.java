package kr.co.jhta.pony.service;

import java.util.List;

import kr.co.jhta.pony.dto.NoticeDTO;


public interface NoticeService {
	public List<NoticeDTO> selectAll(int startNo, int endNo);

	public void addOne(NoticeDTO dto);
	
	public NoticeDTO selectOne(int bno);
	
	public int getTotal();


	void increaseHits(int noticeNo);
}