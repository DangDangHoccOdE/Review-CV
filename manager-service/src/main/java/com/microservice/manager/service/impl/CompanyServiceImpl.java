package com.microservice.manager.service.impl;

import com.microservice.manager.dto.ApiResponse;
import com.microservice.manager.dto.AuthenticationRequest;
import com.microservice.manager.dto.AuthenticationResponse;
import com.microservice.manager.dto.CompanyDTO;
import com.microservice.manager.exception.CustomException;
import com.microservice.manager.exception.Error;
import com.microservice.manager.model.Company;
import com.microservice.manager.openFeign.ImageClient;
import com.microservice.manager.openFeign.UserClient;
import com.microservice.manager.repository.CompanyRepository;
import com.microservice.manager.service.inter.CompanyService;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ImageClient imageClient;

    @Autowired
    private UserClient userClient;

    private Integer getGenerationId() {
        UUID uuid = UUID.randomUUID();
        return (int) (uuid.getMostSignificantBits() & 0x7FFFFFFF);
    }

    private Company convertToModel(CompanyDTO companyDTO) {
        return modelMapper.map(companyDTO, Company.class);
    }

    private CompanyDTO convertToDto(Company company) {
        return modelMapper.map(company, CompanyDTO.class);
    }

    private List<CompanyDTO> convertToDtoList(List<Company> companyList) {
        return companyList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private Company save(CompanyDTO companyDTO, MultipartFile multipartFile){
        log.info("Inserting company");
        System.out.println(companyDTO.toString());
        String url="";
        if(multipartFile != null){
            url = imageClient.save(multipartFile).getData().getUrl();
        }

        try{
            Company company = Company.builder()
                    .id(getGenerationId())
                    .name(companyDTO.getName())
                    .type(companyDTO.getType())
                    .description(companyDTO.getDescription())
                    .street(companyDTO.getStreet())
                    .city(companyDTO.getCity())
                    .phone(companyDTO.getPhone())
                    .email(companyDTO.getEmail())
                    .country(companyDTO.getCountry())
                    .url(url)
                    .build();
            return companyRepository.save(company);
        }catch (DuplicateKeyException e){
            throw new CustomException(Error.MONGO_DUPLICATE_KEY_ERROR);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public CompanyDTO findById(Integer id) {
        log.info("Get company by id: {}", id);
        return convertToDto(companyRepository.findById(id)
                .orElseThrow(()-> new CustomException(Error.COMPANY_NOT_FOUND)));
    }

    @Override
    public CompanyDTO findByIdHr(Integer id) {
        try{
            log.info("Fetching company by id HR: {}", id);
            Query query = new Query();
            query.addCriteria(Criteria.where("idHr").is(id));
            Company company = mongoTemplate.findOne(query, Company.class);
            if(company == null){
                throw new CustomException(Error.COMPANY_NOT_FOUND);
            }
            System.out.println(company.toString());
            return convertToDto(company);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public CompanyDTO findCompanyByIdManager(Integer id) {
        try{
            log.info("Fetching company by id manager: {}", id);
            Query query = new Query();
            query.addCriteria(Criteria.where("idManager").is(id));
            Company company = mongoTemplate.findOne(query, Company.class);
            if(company == null){
                throw new CustomException(Error.COMPANY_NOT_FOUND);
            }
            System.out.println(company.toString());
            return convertToDto(company);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public CompanyDTO create(CompanyDTO company, MultipartFile file) {
        return convertToDto(save(company, file));
    }

    @Override
    public CompanyDTO update(CompanyDTO company) {
        try{
            log.info("Updating company by id: {}", company.getId());
            System.out.println(company.toString());
            return convertToDto(companyRepository.save(convertToModel(company)));
        }catch (MongoWriteException e){
            throw new CustomException(Error.MONGO_WRITE_CONCERN_ERROR);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public void deleteById(Integer id) {
        try{
            log.info("Deleting company by id: {}", id);
            companyRepository.deleteById(id);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<CompanyDTO> getCompanyDTOs() {
        try{
            log.info("Fetching all companyDTOs");
            Query query = new Query();
            query.limit(50);
            return convertToDtoList(mongoTemplate.find(query, Company.class));
        }catch (DataAccessException e){
            return Collections.emptyList();
        }
    }

    @Override
    public List<CompanyDTO> getCompanyByType(String type) {
        try{
            log.info("Fetching companies by type: {}", type);
            Query query = new Query();
            query.addCriteria(Criteria.where("type").regex(type));
            query.limit(20);
            return convertToDtoList(mongoTemplate.find(query, Company.class));
        }catch (DataAccessException e){
            return Collections.emptyList();
        }
    }

    @Override
    public CompanyDTO setHRToCompany(AuthenticationRequest authenticationRequest, Integer idCompany) {
        try{
            log.info("Setting HR to Company by idCompany: {}, idHR: {}", idCompany,authenticationRequest.getEmail());
            authenticationRequest.setRole("hr");
            ApiResponse<AuthenticationResponse> authenticationResponseApiResponse = userClient.signUp(authenticationRequest);
            CompanyDTO companyDTO = findById(idCompany);
            if(companyDTO.getIdHR() == null){
                companyDTO.setIdHR((new ArrayList<>()));
            }
            List<Integer> idHR = companyDTO.getIdHR();
            idHR.add(authenticationResponseApiResponse.getData().getUser().getId());
            companyDTO.setIdHR(idHR);
            return update(companyDTO);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public CompanyDTO deleteHRToCompany(Integer idHR, Integer idCompany) {
        try{
            log.info("Deleting HR from Company by idHR: {}, idHR: {}", idHR,idCompany);
            CompanyDTO companyDTO = findById(idCompany);
            List<Integer> idHr = companyDTO.getIdHR();
            idHr.remove(idHR);
            companyDTO.setIdHR(idHr);
            return update(companyDTO);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public CompanyDTO setManagerToCompany(AuthenticationRequest authenticationRequest, Integer id) {
        authenticationRequest.setRole("manager");
        ApiResponse<AuthenticationResponse> authenticationResponseApiResponse = userClient.signUp(authenticationRequest);
        CompanyDTO companyDTO = findById(id);
        companyDTO.setIdManager(authenticationResponseApiResponse.getData().getUser().getId());
        return convertToDto(companyRepository.save(convertToModel(companyDTO)));
    }
}
