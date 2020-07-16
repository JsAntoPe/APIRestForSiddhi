package com.siddhiApi.controller;

import com.siddhiApi.entity.Pattern;
import com.siddhiApi.exceptions.NotFoundException;
import com.siddhiApi.services.PatternService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/patterns")
public class PatternsController {

	Logger logger = LoggerFactory.getLogger(PatternsController.class);

	@Autowired
	private final PatternService patternService;

	public PatternsController(PatternService patternService) {
		this.patternService = patternService;
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("")
	public void runPattern(@RequestBody Pattern pattern) { //HttpEntity<String> instead of String
		try {
			patternService.runPattern(pattern);
		} catch (NotFoundException nfe){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, nfe);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
		}
	}

	@GetMapping("")
	public Pattern[] getPatterns(){
		return patternService.getPatterns();
	}

	@GetMapping("/{id}")
	public Pattern getPatterns(@PathVariable String id){
		try {
			return patternService.getPattern(id);
		} catch (NotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, e);
		}
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PutMapping("/{id}")
	public void updatePattern(@PathVariable String id, @RequestBody Pattern patternToUpdate){
		try {
			patternService.updatePattern(id, patternToUpdate);
		} catch (NotFoundException notFoundException){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, notFoundException);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
		}
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void deletePattern(@PathVariable String id){
		try {
			patternService.removePattern(id);
		} catch (NotFoundException notFoundException) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, notFoundException);
		}
	}
}
