package com.github.saintukrainian.bigqueryservice.restcontroller;

import com.github.saintukrainian.bigqueryservice.entities.Hospital;
import com.github.saintukrainian.bigqueryservice.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hospitals")
@RequiredArgsConstructor
public class HospitalController {

  private final HospitalService hospitalService;
  @GetMapping("/")
  public List<Hospital> getHospitals() throws InterruptedException {
    return hospitalService.getHospitals();
  }
}
