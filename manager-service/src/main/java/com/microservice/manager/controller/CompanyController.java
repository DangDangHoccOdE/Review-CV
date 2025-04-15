package com.microservice.manager.controller;

import com.microservice.manager.dto.ApiResponse;
import com.microservice.manager.dto.AuthenticationRequest;
import com.microservice.manager.dto.CompanyDTO;
import com.microservice.manager.service.inter.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/manager")
@RestController
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @PostMapping("/admin/company/create")
    public ResponseEntity<ApiResponse<CompanyDTO>> create(@ModelAttribute CompanyDTO companyDTO, @RequestPart(value = "image",required = false)MultipartFile multipartFile){
        CompanyDTO companyDTO1 = companyService.create(companyDTO, multipartFile);
        return ResponseEntity.ok(new ApiResponse<>(true,"Company created successfully",companyDTO1));
    }

    @PostMapping("/manager/company/update")
    public ResponseEntity<ApiResponse<CompanyDTO>> update(@RequestBody CompanyDTO companyDTO){
        CompanyDTO companyDTO1 = companyService.update(companyDTO);
        return ResponseEntity.ok(new ApiResponse<>(true,"Company updated successfully",companyDTO1));
    }

    @PutMapping("/manager/setHRtoCompany")
    public ResponseEntity<ApiResponse<CompanyDTO>> setHRtoCompany(@RequestBody AuthenticationRequest authenticationRequest,@RequestParam Integer companyId){
        CompanyDTO companyDTO = companyService.setHRToCompany(authenticationRequest, companyId);
        return ResponseEntity.ok(new ApiResponse<>(true,"Company set HR successfully",companyDTO));
    }

    @PostMapping("/admin/company/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam Integer id){
        companyService.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true,"Company deleted successfully",null));
    }

    @PutMapping("/manager/setManagerToCompany")
    public ResponseEntity<ApiResponse<CompanyDTO>> setManagerToCompany(@RequestBody AuthenticationRequest authenticationRequest, @RequestParam Integer companyId){
        CompanyDTO companyDTO = companyService.setManagerToCompany(authenticationRequest, companyId);
        return ResponseEntity.ok(new ApiResponse<>(true,"Company set Manager successfully",companyDTO));
    }

    @GetMapping("/user/company/getById")
    public ResponseEntity<ApiResponse<CompanyDTO>> getById(@RequestParam Integer id){
        CompanyDTO companyDTO= companyService.findById(id);
        return ResponseEntity.ok(new ApiResponse<>(true,"Company retrieved successfully",companyDTO));
    }

    @GetMapping("/user/company/getAllCompany")
    public ResponseEntity<ApiResponse<List<CompanyDTO>>> getAllCompany(){
        List<CompanyDTO> companyDTOS = companyService.getCompanyDTOs();
        return ResponseEntity.ok(new ApiResponse<>(true,"Companies retrieved successfully",companyDTOS));
    }

    @GetMapping("/user/company/getCompanyByType")
    public ResponseEntity<ApiResponse<List<CompanyDTO>>> getCompanyByType(@RequestParam String type){
        List<CompanyDTO> companyDTOS = companyService.getCompanyByType(type);
        return ResponseEntity.ok(new ApiResponse<>(true,"Companies retrieved successfully by type",companyDTOS));
    }

    @GetMapping("/company/getCompanyByIdManager")
    public ResponseEntity<ApiResponse<CompanyDTO>> getCompanyByManagerId(@RequestParam Integer managerId){
        CompanyDTO companyDTOs = companyService.findCompanyByIdManager(managerId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Companies retrieved successfully by manager id", companyDTOs));
    }

    @GetMapping("/hr/findByIdHr")
    public ResponseEntity<ApiResponse<CompanyDTO>> findByIdHr(@RequestParam Integer id){
        CompanyDTO companyDTO = companyService.findByIdHr(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Company retrieved successfully by HR id", companyDTO));
    }


}
