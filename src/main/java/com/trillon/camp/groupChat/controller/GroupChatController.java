package com.trillon.camp.groupChat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.trillon.camp.group.dto.CampingGroup;
import com.trillon.camp.group.dto.GroupMember;
import com.trillon.camp.group.service.GroupSerivce;
import com.trillon.camp.groupChat.dto.ChatRoom;
import com.trillon.camp.groupChat.service.GroupChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/groupChat")
@RequiredArgsConstructor
public class GroupChatController {
	
	private final GroupChatService groupChatService;
	private final GroupSerivce groupSerivce;
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	
	@GetMapping("/groupChatList")
	public String groupChatList(Model model,HttpSession session) {
	
		System.out.println("groupChatList");
		String userId = (String) session.getAttribute("loginId");
		// session이 죽은 경우 로그인 페이지로 이동
		if(userId==null) return "redirect:/members/login";
		
		log.info("userId -> "+userId);
		System.out.println("GroupList -> " +groupChatService.selectAllMygroupChatList(userId));
		List<GroupMember> GroupMembers = groupChatService.selectAllChatRoomList(userId);
		List<CampingGroup> campingGroups = groupChatService.selectAllMygroupChatList(userId);
		Map<String, Object> MyGroupMap = new HashMap<>();
		
		for (GroupMember member : GroupMembers) {
			System.out.println(member);
		}
		
		for (CampingGroup campingGroup : campingGroups) {
			System.out.println(campingGroup);
		}
		
		MyGroupMap.put("GroupMember", GroupMembers);
		MyGroupMap.put("campingGroup", campingGroups);
		
		model.addAttribute("MyGroup", MyGroupMap);
		return "/groupChat/groupChatList";
	}
	
	@PostMapping("/createGroup")
	@ResponseBody
	public String createChat(@RequestBody CampingGroup campingGroup) {
		System.out.println("Post : createGroup");
		groupChatService.insertNewGroup(campingGroup);
		
		ChatRoom chatRoom = new ChatRoom();
		Map<String, Object> commandMap = new HashMap<>();
		chatRoom.setRoomId();
		commandMap.put("roomId", chatRoom);
		commandMap.put("groupIdx", groupSerivce.selectNewGampingGroupIdx());
		
		groupChatService.insertNewGroupChat(commandMap);
		
		
//		ChatRoom newChatRoom = chatRoom;
//		newChatRoom.setRoomId();
//		System.out.println(newChatRoom);
//		groupChatService.createNewChatRoom(newChatRoom);
		return "성공";
	}

	
	@GetMapping("/chatRoom")
    public void getRoom(@RequestParam("roomId") String roomId,
    		@RequestParam("groupIdx") String groupIdx, Model model,
    		HttpSession session){
		
        log.info("# 그룹 채팅 방, roomID : " + roomId);
        List<ChatRoom> chatRooms = groupChatService.findRoomById(roomId);
        System.out.println("채팅의 참여 가능 멤버");
        for (ChatRoom Room : chatRooms) {
			System.out.println(Room.getUserId());
		}
        CampingGroup campingGroup = groupSerivce.findCampingGroupByGroupIdx(Integer.valueOf(groupIdx));
        ChatRoom chatRoom  = new ChatRoom();
        chatRoom.setRoomId(chatRooms.get(0).getRoomId());
        
        model.addAttribute("userId", session.getAttribute("loginId"));
        model.addAttribute("roomId",roomId);
        model.addAttribute("room", chatRoom);
        model.addAttribute("groupIdx", groupIdx);
        model.addAttribute("campingGroup",campingGroup);
    }
	
	
	
	@GetMapping("/groupChat")
	public void groupChat2() {
		System.out.println("그룹 쳇 버전 2");
	}
	
}
