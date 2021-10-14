package com.hitservices.webservice;

import com.hitservices.webservice.entity.Record;
import com.hitservices.webservice.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class HitService {

	private String url;
	private Boolean isParallel;
	private Integer count;

	@Autowired
	private RecordRepository recordRepository;
	public HitService() {

	}

	public HitService(String url, Boolean isParallel, String count) {
		super();
		this.url = url;
		this.isParallel = isParallel;
		this.count = Integer.valueOf(count);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getIsParallel() {
		return isParallel;
	}

	public void setIsParallel(Boolean isParallel) {
		this.isParallel = isParallel;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = Integer.valueOf(count);
	}

	@Override
	public String toString() {
		return "HitService [url=" + url + ", isParallel=" + isParallel + ", count=" + count + "]";
	}

	private HttpRequest getAllRequests(){
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(this.url))
				.header("accept", "application/json")
				.build();
		return request;
	}
	
	private void executeSequential(HttpClient client, HttpRequest request) {

		for(int i = 0 ; i < this.count; i++) {
			try {
				client.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void executeParallel(HttpClient client, HttpRequest request) {
		
		List<CompletableFuture<HttpResponse<String>>> asyncResponse = new ArrayList<>();

		for(int i = 0 ; i < this.count; i++) {
			asyncResponse.add(client.sendAsync(request, HttpResponse.BodyHandlers.ofString()));
		}
		CompletableFuture.allOf(asyncResponse.toArray(new CompletableFuture[asyncResponse.size()])).join();
	}
	
	// Perform request on URL basis of isParallel value
	public long requestExecutionTime() {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = getAllRequests();
		long startTime = System.currentTimeMillis();
		if(this.isParallel) executeParallel(client, request); else executeSequential(client, request);
		long timeTaken = System.currentTimeMillis() - startTime;
		return timeTaken;

	}

	public List<Record> getAllRecords() {
		return recordRepository.findAll();
	}

	public Record getRecordById(String id) {
		Optional<Record> record = recordRepository.findById(id);
		return record.orElse(null);
	}

	public Record addRecord(Record record) {
		return recordRepository.save(record);
	}

	public Record editRecord(Record record, String id) {
		Optional<Record> returnRecord = recordRepository.findById(id);
		return returnRecord.orElse(null);
	}

	public Boolean deleteRecord(String id) {
		Optional<Record> returnRecord = recordRepository.findById(id);
		if (returnRecord.isPresent()) {
			recordRepository.deleteById(id);
			return true;
		}
		return false;
	}
}
