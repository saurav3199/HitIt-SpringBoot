package com.hitservices.webservice;


import java.util.ArrayList;

import com.hitservices.webservice.entity.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HitItController {

	@Autowired
	private HitService hitService;

	@GetMapping(path="/")
	public String ping() {
		return "pong";
	}
	
	@PostMapping(path="/hitServices", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseTransfer Service(@RequestBody ArrayList<HitService> hitServices){
		
		long outputTimeTaken = 0;
		for(HitService service: hitServices) {
			outputTimeTaken += service.requestExecutionTime();
		}
		return new ResponseTransfer(Long.toString(outputTimeTaken));
	}

	@PostMapping(path = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Record> addRecord(@RequestBody Record record) {
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(hitService.addRecord(record));
	}
	
}