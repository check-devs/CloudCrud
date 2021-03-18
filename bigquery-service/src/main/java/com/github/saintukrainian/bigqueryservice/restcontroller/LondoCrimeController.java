package com.github.saintukrainian.bigqueryservice.restcontroller;

import com.github.saintukrainian.bigqueryservice.entities.LondonCrime;
import com.github.saintukrainian.bigqueryservice.service.LondonCrimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/london-crimes")
@RequiredArgsConstructor
public class LondoCrimeController {

  private final LondonCrimeService londonCrimeService;

  @GetMapping("/{year}")
  public List<LondonCrime> getLondonCrimesByYear(@PathVariable int year)
      throws InterruptedException {
    return londonCrimeService.getLondonCrimesByYear(year);
  }

  @GetMapping("/most-criminal-category/{year}")
  public LondonCrime getTheMostCriminalCategoryByYear(@PathVariable int year) throws InterruptedException {
    return londonCrimeService.getTheMostCriminalCategoryByYear(year);
  }
}
