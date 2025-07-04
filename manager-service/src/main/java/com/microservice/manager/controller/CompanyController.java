package com.microservice.manager.controller;


import com.microservice.manager.dto.ApiResponse;
import com.microservice.manager.dto.AuthenticationRequest;
import com.microservice.manager.dto.CompanyDTO;
import com.microservice.manager.services.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/manager")
@RestController
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/company/create")
    public ResponseEntity<ApiResponse<CompanyDTO>> create(@ModelAttribute CompanyDTO companyDTO,@RequestPart(value="image",required = false) MultipartFile image){
        CompanyDTO companyDTO1 = companyService.create(companyDTO,image);
        return ResponseEntity.ok(new ApiResponse<>(true, "Company created successfully", companyDTO1));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PostMapping("/manager/company/update")
    public ResponseEntity<ApiResponse<CompanyDTO>> update(@RequestPart("companyDTO") CompanyDTO companyDTO,@RequestPart(value="image",required = false) MultipartFile image){
        CompanyDTO companyDTO1 = companyService.update(companyDTO, image);
        return ResponseEntity.ok(new ApiResponse<>(true, "Company updated successfully", companyDTO1));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/manager/sethrtocompany")
    public ResponseEntity<ApiResponse<CompanyDTO>> setHeadToCompany(@RequestBody AuthenticationRequest authenticationRequest, @RequestParam Integer idCompany){
        CompanyDTO companyDTO = companyService.setHRToCompany(authenticationRequest, idCompany);
        return ResponseEntity.ok(new ApiResponse<>(true, "Head updated successfully", companyDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/company/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam Integer id){
        companyService.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Company deleted successfully", "ok"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/manager/setmaanagertocompany")
    public ResponseEntity<ApiResponse<CompanyDTO>> setManagerToCompany(@RequestBody AuthenticationRequest authenticationRequest, @RequestParam Integer idCompany){
        CompanyDTO companyDTO = companyService.setManagerToCompany(authenticationRequest, idCompany);
        return ResponseEntity.ok(new ApiResponse<>(true, "Head updated successfully", companyDTO));
    }

    @GetMapping("/user/company/getbyid")
    public ResponseEntity<ApiResponse<CompanyDTO>> getById(@RequestParam Integer id){
        CompanyDTO companyDTO = companyService.findById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Company retrieved successfully", companyDTO));
    }

    @GetMapping("/user/company/getcompany")
    public ResponseEntity<ApiResponse<List<CompanyDTO>>> getAllCompanies(){
        List<CompanyDTO> companyDTOs = companyService.getCompanyDTOs();
        return ResponseEntity.ok(new ApiResponse<>(true, "Companies retrieved successfully", companyDTOs));
    }

    @GetMapping("/user/company/getcompanybytype")
    public ResponseEntity<ApiResponse<List<CompanyDTO>>> getCompanyByType(@RequestParam String type){
        List<CompanyDTO> companyDTOs = companyService.getCompanyByType(type);
        return ResponseEntity.ok(new ApiResponse<>(true, "Companies retrieved successfully by type", companyDTOs));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping("/company/getcompanybyidmanager")
    public ResponseEntity<ApiResponse<CompanyDTO>> getCompanyByManagerId(@RequestParam Integer managerId){
        CompanyDTO companyDTOs = companyService.getCompanyByIdManager(managerId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Companies retrieved successfully by manager id", companyDTOs));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping("/hr/findByIdHr")
    public ResponseEntity<ApiResponse<CompanyDTO>> findByIdHr(@RequestParam Integer id){
        CompanyDTO companyDTO = companyService.findByIdHr(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Company retrieved successfully by HR id", companyDTO));
    }

}
