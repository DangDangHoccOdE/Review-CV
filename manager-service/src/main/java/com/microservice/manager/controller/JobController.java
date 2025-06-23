package com.microservice.manager.controller;

import com.microservice.manager.dto.ApiResponse;
import com.microservice.manager.dto.JobDTO;
import com.microservice.manager.services.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/manager")
@RestController
public class JobController {
    @Autowired
    private JobService jobService;

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PostMapping("/hr/job/create")
    public ResponseEntity<ApiResponse<JobDTO>> create(@RequestBody JobDTO job) {
        JobDTO createdJob = jobService.create(job);
        return ResponseEntity.ok(new ApiResponse<>(true, "Job created", createdJob));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PostMapping("/hr/job/update")
    public ResponseEntity<ApiResponse<JobDTO>> update(@RequestBody JobDTO job) {
        JobDTO updatedJob = jobService.update(job);
        return ResponseEntity.ok(new ApiResponse<>(true, "Job updated", updatedJob));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PostMapping("/hr/job/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam Integer id) {
        jobService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Job deleted", "Ok"));
    }

    @PutMapping("/user/job/apply")
    public ResponseEntity<ApiResponse<JobDTO>> apply(@RequestParam Integer jobDTO, @RequestParam Integer idProfile) {
        JobDTO job = jobService.applyJob(jobDTO, idProfile);
        return ResponseEntity.ok(new ApiResponse<>(true, "Job applied", job));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PutMapping("/hr/job/accept")
    public ResponseEntity<ApiResponse<JobDTO>> accept(@RequestParam Integer jobDTO, @RequestParam Integer idProfile) {
        JobDTO job = jobService.acceptProfile(jobDTO, idProfile);
        return ResponseEntity.ok(new ApiResponse<>(true, "Job accepted", job));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PutMapping("/hr/job/reject")
    public ResponseEntity<ApiResponse<JobDTO>> reject(@RequestParam Integer jobDTO, @RequestParam Integer idProfile) {
        JobDTO job = jobService.rejectProfile(jobDTO, idProfile);
        return ResponseEntity.ok(new ApiResponse<>(true, "Job rejected", job));
    }

    @GetMapping("/user/job/findbyid")
    public ResponseEntity<ApiResponse<JobDTO>> getById(Integer id) {
        JobDTO job = jobService.findById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Job found", job));
    }

    @GetMapping("/user/job/getall")
    public ResponseEntity<ApiResponse<List<JobDTO>>> getAll() {
        List<JobDTO> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(new ApiResponse<>(true, "Jobs found", jobs));
    }

    @GetMapping("/user/job/getjobbycompany")
    public ResponseEntity<ApiResponse<List<JobDTO>>> getJobByCompany(@RequestParam Integer id) {
        List<JobDTO> jobs = jobService.getJobByCompany(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Jobs found", jobs));
    }

    @GetMapping("/user/job/getjobpending")
    public ResponseEntity<ApiResponse<List<JobDTO>>> getJobPending(@RequestParam Integer id) {
        List<JobDTO> jobs = jobService.getJobByPrfilePending(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Jobs pending found", jobs));
    }

    @GetMapping("/user/job/getjobaccepted")
    public ResponseEntity<ApiResponse<List<JobDTO>>> getJobAccepted(@RequestParam Integer id) {
        List<JobDTO> jobs = jobService.getJobByProfileAccepted(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Jobs accepted found", jobs));
    }

    @GetMapping("/user/job/getnewjob")
    public ResponseEntity<ApiResponse<List<JobDTO>>> getNewJob(@RequestParam Integer id) {
        List<JobDTO> jobs = jobService.getNewJob(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "New jobs found", jobs));
    }

}
