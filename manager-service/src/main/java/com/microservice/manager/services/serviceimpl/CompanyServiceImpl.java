package com.microservice.manager.services.serviceimpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.manager.dto.ApiResponse;
import com.microservice.manager.dto.AuthenticationRequest;
import com.microservice.manager.dto.AuthenticationResponse;
import com.microservice.manager.dto.CompanyDTO;
import com.microservice.manager.exception.CustomException;
import com.microservice.manager.exception.Error;
import com.microservice.manager.models.Company;
import com.microservice.manager.openfeign.ImageClient;
import com.microservice.manager.openfeign.UserClient;
import com.microservice.manager.repository.CompanyRepository;
import com.microservice.manager.services.service.CompanyService;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoWriteException;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    private CompanyDTO convertToDTO(Company company) {
        return modelMapper.map(company, CompanyDTO.class);
    }

    private List<CompanyDTO> convertToListDTO(List<Company> companies) {
        return companies.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    private Company save(CompanyDTO companyDTO, MultipartFile multipartFile) {
        log.info("Inserting company");
        System.out.println(companyDTO.toString());
        String url = "";
        if (multipartFile != null) {
            url = imageClient.save(multipartFile).getData().getUrl();
        }
        try {
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
            return companyRepository.insert(company);
        } catch (DuplicateKeyException e) {
            throw new CustomException(Error.MONGO_DUPLICATE_KEY_ERROR);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public CompanyDTO findById(Integer id) {
        log.info("Get company by id: {}", id);
        return convertToDTO(companyRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.COMPANY_NOT_FOUND)));
    }

    @Override
    public CompanyDTO create(CompanyDTO companyDTO, MultipartFile multipartFile) {
        return convertToDTO(save(companyDTO, multipartFile));
    }

    @Override
    public CompanyDTO update(CompanyDTO companyDTO, MultipartFile multipartFile) {
        try {
            log.info("Updating Company by id: {}", companyDTO.getId());
            System.out.println(companyDTO.toString());
            String url = "";
            if (multipartFile != null) {
                url = imageClient.save(multipartFile).getData().getUrl();
            }
            companyDTO.setUrl(url);
            return convertToDTO(companyRepository.save(convertToModel(companyDTO)));
        } catch (MongoWriteException e) {
            throw new CustomException(Error.MONGO_WRITE_CONCERN_ERROR);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public CompanyDTO updateCompany(CompanyDTO companyDTO) {
        try {
            System.out.println(companyDTO.toString());
            return convertToDTO(companyRepository.save(convertToModel(companyDTO)));
        } catch (MongoWriteException e) {
            throw new CustomException(Error.MONGO_WRITE_CONCERN_ERROR);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public void deleteById(Integer id) {
        try {
            log.info("Deleting Company by id: {}", id);
            companyRepository.deleteById(id);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<CompanyDTO> getCompanyDTOs() {
        try {
            log.info("Fetching all CompanyDTOs");
            Query query = new Query();
            query.limit(50);
            return convertToListDTO(mongoTemplate.find(query, Company.class));
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<CompanyDTO> getCompanyByType(String type) {
        try {
            log.info("Fetching companies by type: {}", type);
            Query query = new Query();
            query.addCriteria(Criteria.where("type").regex(type));
            query.limit(20);
            return convertToListDTO(mongoTemplate.find(query, Company.class));
        } catch (MongoCommandException e) {
            throw new CustomException(Error.MONGO_QUERY_EXECUTION_ERROR);
        } catch (DataAccessException e) {
            System.err.println("Error while fetching companies by type: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public CompanyDTO setHRToCompany(AuthenticationRequest authenticationRequest, Integer idCompany) {
        try {
            log.info("Setting HR to Company by idCompany: {}, idHR: {}", idCompany, authenticationRequest.getEmail());
            authenticationRequest.setRole("hr");
            ApiResponse<AuthenticationResponse> authenticationResponseApiResponse = userClient
                    .signUp(authenticationRequest);
            CompanyDTO companyDTO = findById(idCompany);
            if (companyDTO.getIdHR() == null) {
                companyDTO.setIdHR(new ArrayList<>());
            }
            List<Integer> idHR = companyDTO.getIdHR();
            idHR.add(authenticationResponseApiResponse.getData().getUser().getId());
            companyDTO.setIdHR(idHR);
            return updateCompany(companyDTO);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public CompanyDTO deleteHRToCompany(Integer idHR, Integer idCompany) {
        try {
            log.info("Deleting HR from Company by idHR: {}, idCompany: {}", idHR, idCompany);
            CompanyDTO companyDTO = findById(idCompany);
            List<Integer> idHr = companyDTO.getIdHR();
            idHr.remove(idHR);
            companyDTO.setIdHR(idHr);
            return updateCompany(companyDTO);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public CompanyDTO setManagerToCompany(AuthenticationRequest authenticationRequest, Integer id) {
        authenticationRequest.setRole("manager");
        ApiResponse<AuthenticationResponse> authenticationResponseApiResponse;

        try {
            authenticationResponseApiResponse = userClient.signUp(authenticationRequest);
        } catch (FeignException.Conflict e) {
            // Trích xuất JSON từ body trả về
            String content = e.contentUTF8();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(content);
                String errorMessage = root.path("data").path("message").asText();

                if ("Email already exists".equals(errorMessage)) {
                    throw new RuntimeException("EMAIL_EXISTS");
                }
            } catch (IOException ex) {
                throw new RuntimeException("FAILED_TO_PARSE_ERROR_BODY");
            }

            throw new RuntimeException("SIGNUP_FAILED");
        }

        CompanyDTO companyDTO = findById(id);
        companyDTO.setIdManager(authenticationResponseApiResponse.getData().getUser().getId());
        return convertToDTO(companyRepository.save(convertToModel(companyDTO)));
    }

    @Override
    public CompanyDTO getCompanyByIdManager(Integer id) {
        try {
            log.info("Fetching company by id manager: {}", id);
            Query query = new Query();
            query.addCriteria(Criteria.where("idManager").is(id));
            Company company = mongoTemplate.findOne(query, Company.class);
            if (company == null) {
                throw new CustomException(Error.COMPANY_NOT_FOUND);
            }
            return convertToDTO(company);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public CompanyDTO findByIdHr(Integer id) {
        try {
            log.info("Fetching company by id HR: {}", id);
            Query query = new Query();
            query.addCriteria(Criteria.where("idHr").is(id));
            Company company = mongoTemplate.findOne(query, Company.class);
            if (company == null) {
                throw new CustomException(Error.COMPANY_NOT_FOUND);
            }
            System.out.println(company.toString());
            return convertToDTO(company);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }
}
