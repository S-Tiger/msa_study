package com.example.catalogservice.service;

import com.example.catalogservice.entity.CatalogEntity;
import com.example.catalogservice.repository.CatalogRepository;
import com.example.catalogservice.service.CatalogService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@RequiredArgsConstructor
@Service
public class CatalogServiceImpl implements CatalogService {

    private final CatalogRepository catalogRepository;

    @Override
    public Iterable<CatalogEntity> getAllCatalogs() {
        return catalogRepository.findAll();
    }
}
