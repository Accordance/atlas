package com.accordance.atlas.controller;

import com.accordance.atlas.model.JSONRecord;
import com.accordance.atlas.model.JSONResultSet;
import com.accordance.atlas.repository.ApplicationQueryBuilder;
import com.accordance.atlas.repository.ApplicationsRepository;
import com.tinkerpop.blueprints.Vertex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apps")
public class ApplicationsDirectoryController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationsDirectoryController.class);

    @Autowired
    ApplicationsRepository appsRepo;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public JSONResultSet index() {
        List<Vertex> vertices = appsRepo.findApplications();

        return new JSONResultSet(vertices);
    }

    @RequestMapping(value = "/names", method = RequestMethod.GET)
    @ResponseBody
    public JSONResultSet getAppNames() {
        List<Vertex> vertices = appsRepo.findApplications(new ApplicationQueryBuilder().onlyFields(new String[]{"id", "name"}));

        return new JSONResultSet(vertices);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public JSONRecord getAppById(@PathVariable("id") String id) {
        Vertex v = appsRepo.getApplicationById(id);

        return new JSONRecord(v);
    }
	
	@RequestMapping(value = "/deploy/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> activateDeploymentLock(@PathVariable("id") String id) {
        
		boolean deploy = appsRepo.getAppDeploymentStatus(id);
		
		if(!deploy) {
			if(appsRepo.activateDeploymentLock(id))
				return new ResponseEntity<String>("Deployment Lock Activated Successfully", HttpStatus.OK);
			else
				return new ResponseEntity<String>("Application Not Found", HttpStatus.FORBIDDEN);
		}
		else {
			return new ResponseEntity<String>("Deployment Lock is Active", HttpStatus.FORBIDDEN);
		}
    }
	
	@RequestMapping(value = "/release-deploy/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> releaseDeploymentLock(@PathVariable("id") String id) {
		
		boolean deploy = appsRepo.getAppDeploymentStatus(id);
		
		if(!deploy) {
			if(appsRepo.releaseDeploymentLock(id))
				return new ResponseEntity<String>("Deployment Lock Released Successfully", HttpStatus.OK);
			else
				return new ResponseEntity<String>("Application Not Found", HttpStatus.FORBIDDEN);
		}
		else {
			return new ResponseEntity<String>("Deployment Lock is not Active", HttpStatus.FORBIDDEN);
		} 
    }  

}
