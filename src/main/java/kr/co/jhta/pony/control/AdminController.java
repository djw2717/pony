package kr.co.jhta.pony.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.jhta.pony.dto.AnswerDTO;
import kr.co.jhta.pony.dto.NoticeDTO;
import kr.co.jhta.pony.dto.QuestionDTO;
import kr.co.jhta.pony.service.AnswerService;
import kr.co.jhta.pony.service.NoticeService;
import kr.co.jhta.pony.service.OrderDetailService;
import kr.co.jhta.pony.service.OrderService;
import kr.co.jhta.pony.service.QuestionService;
import kr.co.jhta.pony.util.PageUtil;

@Controller
public class AdminController {
  
	@Autowired
	NoticeService nservice;
	
	@Autowired
	QuestionService qservice;
	
	@Autowired
	AnswerService aservice;
	
	@Autowired
	OrderDetailService odservice;
	
	@Autowired
	OrderService oservice;
	
	@GetMapping("/admin1")
	public String admin() {
		return "/admin/admin1";
	}

	// 재고 관리 ------------------------------------------------------------
	@GetMapping("/partlist")
	public String partlist() {
		return "/admin/part/partList";
	}
	
	// 주문 목록 ------------------------------------------------------------
	@GetMapping("/adminorderlist")
	public String adminorderlist(Model model, @RequestParam(name="currentPage",defaultValue="1")int currentPage) {
		
		//총게시물수
		int totalNumber = oservice.getTotal();
		
		//페이지당 게시물수
		int recordPerPage = 10;
		
		//총페이지수, 한페이지당 수, 현재 페이지 번호
		Map<String, Object> map = PageUtil.getPageData(totalNumber, recordPerPage, currentPage);
		
		int startNo = (int)map.get("startNo");
		int endNo = (int)map.get("endNo");
		
		model.addAttribute("list",oservice.getAllByAdmin(startNo, endNo));
		model.addAttribute("map", map);
		return "/admin/order/orderList";
		}
	
	
	
	// 고객 문의 ------------------------------------------------------------
	@GetMapping("/questionlist")
	public String questionlist(Model model, @RequestParam(name="currentPage", defaultValue = "1")int currentPage) {
		//총게시물수
		int totalNumber = qservice.getTotal();
		
		//페이지당 게시물수
		int recordPerPage = 10;
		
		//총페이지수, 한페이지당 수, 현재 페이지 번호
		Map<String, Object> map = PageUtil.getPageData(totalNumber, recordPerPage, currentPage);
		
		int startNo = (int)map.get("startNo");
		int endNo = (int)map.get("endNo");
		
		model.addAttribute("list", qservice.selectAllByAdmin(startNo, endNo));
		model.addAttribute("map", map);

		return "/admin/question/questionList";
	}
	
	// 문의글 상세
	@GetMapping("/questiondetail")
	public String questiondetail(@RequestParam("questionNo")int questionNo, Model model) {
		model.addAttribute("detail",qservice.selectOne(questionNo));
		model.addAttribute("detailanswer", aservice.selectOne(questionNo));
		return "/admin/question/questionDetail";
	}
	
	// 답변 달기
	@PostMapping("/answer")
	public String answer(@ModelAttribute AnswerDTO dto,HttpServletRequest req) {
		int questionNo = Integer.parseInt(req.getParameter("questionNo"));
		//기존 게시글 정보 가져오기
		QuestionDTO qdto = qservice.selectOne(questionNo);		
		
		String answer = req.getParameter("answer");
		dto.setAnswerContents(answer);
		dto.setQuestionNo(questionNo);
		
		//답변상태 변경
		qdto.setAnswerStatus("답변완료");
		
		//답변과 게시글 상태 각각 저장
		aservice.insertAnswer(dto);
		qservice.updateAnswerStatus(qdto);
		return "redirect:/questiondetail?questionNo="+questionNo;
	}
	
	//문의글 체크박스 선택 삭제
	@PostMapping("delete")
	public String ajaxTest(HttpServletRequest req) {
		String[] ajaxMsg = req.getParameterValues("valueArr");
		int size = ajaxMsg.length;
		for(int i=0 ; i<size ; i++) {
			qservice.deletecheck(ajaxMsg[i]);
		}
	    return "redirect:/questionlist";
	}
	
	//답변 삭제
	@GetMapping("/deleteanswer")
	public String deleteanswer(@ModelAttribute AnswerDTO dto,HttpServletRequest req) {
		int questionNo = Integer.parseInt(req.getParameter("questionNo"));
		//기존 게시글 정보 가져오기
		QuestionDTO qdto = qservice.selectOne(questionNo);		
		
		qdto.setAnswerStatus("미답변");
		aservice.deleteOne(questionNo);
		qservice.updateAnswerStatus(qdto);
		return "redirect:/questiondetail?questionNo="+questionNo;
	}
	
	//답변 수정 페이지
	@GetMapping("/modifyanswer")
	public String modifyAnswer(Model model, @RequestParam("questionNo")int questionNo) {
		//기존 답변 정보 가져오기
		model.addAttribute("detail",qservice.selectOne(questionNo));
		model.addAttribute("detailanswer", aservice.selectOne(questionNo));
		return "/admin/question/answerModify";
	}

	//답변 수정
	@PostMapping("/modifyanswer")
	public String modifyAnswerOk(@ModelAttribute AnswerDTO dto, HttpServletRequest req, @RequestParam("questionNo")int questionNo) {
		String contents = req.getParameter("answer");
		dto.setAnswerContents(contents);
		aservice.modifyAnswer(dto);
		return "redirect:/questiondetail?questionNo="+questionNo;
	}
	
	// 공지사항 (유경님 코드)----------------------------------------------------
	@GetMapping("/noticewrite")
	public String noticeWriteForm() {
		return "/admin/notice/adminNoticeWriteForm";
	}
	
	//게시글 목록
	@GetMapping("/adminnotice")
	public String notice(Model model, @RequestParam(name="currentPage",defaultValue="1")int currentPage) {
	
	//총게시물수
	int totalNumber = nservice.getTotal();
	
	//페이지당 게시물수
	int recordPerPage = 10;
	
	//총페이지수, 한페이지당 수, 현재 페이지 번호
	Map<String, Object> map = PageUtil.getPageData(totalNumber, recordPerPage, currentPage);
	
	int startNo = (int)map.get("startNo");
	int endNo = (int)map.get("endNo");
	
	model.addAttribute("list",nservice.selectAll(startNo, endNo));
	model.addAttribute("map", map);
	return "/admin/notice/adminNoticeList";
	}
	
	//글 상세 페이지
	@GetMapping("/admindetail")
	public String detail(@RequestParam("noticeNo")int noticeNo,Model model) {
		model.addAttribute("detail",nservice.selectOne(noticeNo));
		nservice.increaseHits(noticeNo);
		
		return "/admin/notice/adminNoticeDetail";
	}
	
	//글 작성 저장
	@PostMapping("/adminnoticewrite")
	public String noticeWriteOk(@ModelAttribute NoticeDTO dto,HttpServletRequest req) {
		String contents = req.getParameter("contents");
		String title = req.getParameter("title");
		dto.setNoticeContents(contents);
		dto.setNoticeTitle(title);
		nservice.addOne(dto);
	
		return "redirect:/adminnotice";
	}
	
	//글 수정 페이지
	@GetMapping("/adminmodify")
	public String modifyform(@RequestParam("noticeNo") int noticeNo, Model model) {
		model.addAttribute("dto", nservice.selectOne(noticeNo));
		return "/admin/notice/adminNoticeModify";
	}
	
	//글 수정
	@PostMapping("/adminmodify")
	public String modifyOk(@ModelAttribute NoticeDTO dto, HttpServletRequest req) {
		String contents = req.getParameter("contents");
		String title = req.getParameter("title");
		dto.setNoticeContents(contents);
		dto.setNoticeTitle(title);
		nservice.modifyOne(dto);
		return "redirect:/adminnotice";
	}
	
	//글 삭제
	@GetMapping("/admindelete")
	public String deleteOk(@ModelAttribute NoticeDTO dto) {
		nservice.deleteOne(dto);
		return "redirect:/adminnotice";
	}
}
