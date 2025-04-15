package com.microservice.manager.service.inter;

import com.microservice.manager.dto.AuthenticationRequest;
import com.microservice.manager.dto.CompanyDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CompanyService {
    CompanyDTO findById(Integer id);
    CompanyDTO findByIdHr(Integer id);
    CompanyDTO findCompanyByIdManager(Integer id);
    CompanyDTO create(CompanyDTO company, MultipartFile file);
    CompanyDTO update(CompanyDTO company);
    void deleteById(Integer id);
    List<CompanyDTO> getCompanyDTOs();
    List<CompanyDTO> getCompanyByType(String type);
    CompanyDTO setHRToCompany(AuthenticationRequest authenticationRequest, Integer id);
    CompanyDTO deleteHRToCompany(Integer idHR, Integer idCompany);
    CompanyDTO setManagerToCompany(AuthenticationRequest authenticationRequest, Integer id);
}
