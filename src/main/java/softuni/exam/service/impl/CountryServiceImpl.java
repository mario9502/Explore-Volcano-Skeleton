package softuni.exam.service.impl;


import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportCountryDto;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CountryService;
import softuni.exam.util.ValidationUtilImpl;

import javax.swing.text.Style;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class CountryServiceImpl implements CountryService {

    private final String COUNTRIES_FILE_PATH = "src/main/resources/files/json/countries.json";
    private final CountryRepository countryRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtilImpl validator;
    private Gson gson;

    public CountryServiceImpl(CountryRepository countryRepository, ModelMapper modelMapper, ValidationUtilImpl validator, Gson gson) {
        this.countryRepository = countryRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return this.countryRepository.count() > 0;
    }

    @Override
    public String readCountriesFromFile() throws IOException {
        return new String(Files.readAllBytes(Path.of(COUNTRIES_FILE_PATH)));
    }

    @Override
    public String importCountries() throws IOException {
        StringBuilder sb = new StringBuilder();
        ImportCountryDto[] importDtos = this.gson.fromJson(this.readCountriesFromFile(), ImportCountryDto[].class);

        for (ImportCountryDto dto : importDtos) {
            Optional<Country> optionalCountry = this.countryRepository.findByName(dto.getName());

            if (!this.validator.isValid(dto) || optionalCountry.isPresent()) {
                sb.append("Invalid country");
                sb.append(System.lineSeparator());
                continue;
            }

            Country country = this.modelMapper.map(dto, Country.class);
            this.countryRepository.saveAndFlush(country);
            sb.append(String.format("Successfully imported country %s - %s", country.getName(), country.getCapital()));
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }
}
