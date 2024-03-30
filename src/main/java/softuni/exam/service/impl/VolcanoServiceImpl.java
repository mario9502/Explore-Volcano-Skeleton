package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportVolcanoDto;
import softuni.exam.models.entity.Volcano;
import softuni.exam.repository.CountryRepository;
import softuni.exam.repository.VolcanoRepository;
import softuni.exam.service.VolcanoService;
import softuni.exam.util.ValidationUtilImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class VolcanoServiceImpl implements VolcanoService {

    private final String VOLCANOES_FILE_PATH = "src/main/resources/files/json/volcanoes.json";
    private final VolcanoRepository volcanoRepository;
    private final CountryRepository countryRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtilImpl validator;
    private final Gson gson;

    public VolcanoServiceImpl(VolcanoRepository volcanoRepository, CountryRepository countryRepository, ModelMapper modelMapper, ValidationUtilImpl validator, Gson gson) {
        this.volcanoRepository = volcanoRepository;
        this.countryRepository = countryRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return this.volcanoRepository.count() > 0;
    }

    @Override
    public String readVolcanoesFileContent() throws IOException {
        return new String(Files.readAllBytes(Path.of(VOLCANOES_FILE_PATH)));
    }

    @Override
    public String importVolcanoes() throws IOException {
        StringBuilder sb = new StringBuilder();
        ImportVolcanoDto[] importVolcanoDtos = this.gson.fromJson(this.readVolcanoesFileContent(), ImportVolcanoDto[].class);

        for (ImportVolcanoDto dto : importVolcanoDtos) {
            Optional<Volcano> optionalVolcano = this.volcanoRepository.findByName(dto.getName());

            if (!this.validator.isValid(dto) || optionalVolcano.isPresent()) {
                sb.append("Invalid volcano");
                sb.append(System.lineSeparator());
                continue;
            }

            Volcano volcano = this.modelMapper.map(dto, Volcano.class);
            volcano.setCountry(this.countryRepository.findById(dto.getCountry()).get());
            this.volcanoRepository.saveAndFlush(volcano);
            sb.append(String.format("Successfully imported volcano %s of type %s", volcano.getName(), volcano.getVolcanoType()));
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    @Override
    public String exportVolcanoes() {
        StringBuilder sb = new StringBuilder();
        this.volcanoRepository.findVolcanoesAbove3000m().forEach(v -> {
            sb.append(String.format("Volcano: %s" + System.lineSeparator() +
                                    "   *Located in: %s" + System.lineSeparator() +
                                    "   **Elevation: %d" + System.lineSeparator() +
                                    "   ***Last eruption on: %s" + System.lineSeparator(),
                    v.getName(), v.getCountry().getName(), v.getElevation(), v.getLastEruption().toString()));

        });

        return sb.toString();
    }
}