package com.microservice.manager.services.service;

import com.microservice.manager.dto.AuthenticationRequest;
import com.microservice.manager.dto.CompanyDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface CompanyService {
    CompanyDTO findById(Integer id);
    CompanyDTO findByIdHr(Integer id);
    CompanyDTO getCompanyByIdManager(Integer id);
    CompanyDTO create(CompanyDTO company, MultipartFile multipartFile);
    CompanyDTO update(CompanyDTO company, MultipartFile multipartFile);
    CompanyDTO updateCompany(CompanyDTO company);

    void deleteById(Integer id);
    List<CompanyDTO> getCompanyDTOs();
    List<CompanyDTO> getCompanyByType(String type);
    CompanyDTO setHRToCompany(AuthenticationRequest authenticationRequest,Integer idCompany);
    CompanyDTO deleteHRToCompany(Integer idHR, Integer idCompany);
    CompanyDTO setManagerToCompany(AuthenticationRequest authenticationRequest,Integer id);
}
