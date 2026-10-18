package com.mbaigo.datecentre.swingApp.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
     List<String> saveFiles(List<MultipartFile> files);
}
