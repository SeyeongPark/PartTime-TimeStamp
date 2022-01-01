package com.spring.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.spring.entity.TimeStamp;
import com.spring.entity.User;
import com.spring.repository.*;
@Controller
public class TimeStampController {
	
	@Autowired
	private TimeStampRepository timeRepo;
	
	@GetMapping("/add/timestamp")
	public String getAddTimeStamp() {
		return "add-timestamp";
	}
	
	@PostMapping("/add/timestamp")
	public String addTimeStamp(@Valid TimeStamp timestamp, @CurrentSecurityContext(expression="authentication?.name")
    String username , Model model) {
		Date date = new Date();
		
		TimeStamp user = timeRepo.findActiveTSByEmail(username);
		User currentUser = timeRepo.findCurrentUserByEmail(username);
		// If the user want to "start" TimeStamp
		if(user == null || user.getEndTime() != null){
			timestamp.setStartTime(date);	
			timestamp.setWorkplaceId(currentUser.getWorkplaceId());	
		}
		
		// If the user want to "end" TimeStamp
		else if(user != null && user.getEndTime() == null) {
			
			
			timestamp.setTimeStampId(user.getTimeStampId());
			timestamp.setWorkplaceId(user.getWorkplaceId());
			timestamp.setStartTime(user.getStartTime());
			timestamp.setEndTime(date);
		}
		
		timestamp.setUserName(username);
		timeRepo.save(timestamp);
		
		model.addAttribute("timestamps", timeRepo.findAll());
		return "timestamps";
	}
	
	@GetMapping("/timestamps")
	public String getViewTimeStamps(Model model) {
		model.addAttribute("timestamps", timeRepo.findAll());
		return "timestamps";
	}
	
	@GetMapping("/timestamp/delete/{id}")
	public String deleteTimeStamp(@PathVariable("id") int id, Model model) {
		TimeStamp timestamp = timeRepo.findById(id)
				.orElseThrow(()-> new IllegalArgumentException("Invalid timestamp ID, " + id));
		
		timeRepo.delete(timestamp);
		model.addAttribute("timestamps", timeRepo.findAll());

		return "timestamps";
	}
}

