package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportVolcanologistDto;
import softuni.exam.models.dto.ImportVolcanologistRootDto;
import softuni.exam.models.entity.Volcano;
import softuni.exam.models.entity.Volcanologist;
import softuni.exam.repository.VolcanoRepository;
import softuni.exam.repository.VolcanologistRepository;
import softuni.exam.service.VolcanologistService;
import softuni.exam.util.ValidationUtilImpl;
import softuni.exam.util.XmlParserImpl;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class VolcanologistServiceImpl implements VolcanologistService {

    private final String VOLCANOLOGISTS_FILE_PATH = "src/main/resources/files/xml/volcanologists.xml";
    private final VolcanologistRepository volcanologistRepository;
    private final VolcanoRepository volcanoRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtilImpl validator;
    private final XmlParserImpl xmlParser;

    public VolcanologistServiceImpl(VolcanologistRepository volcanologistRepository, VolcanoRepository volcanoRepository, ModelMapper modelMapper, ValidationUtilImpl validator, XmlParserImpl xmlParser) {
        this.volcanologistRepository = volcanologistRepository;
        this.volcanoRepository = volcanoRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return this.volcanologistRepository.count() > 0;
    }

    @Override
    public String readVolcanologistsFromFile() throws IOException {
        return new String(Files.readAllBytes(Path.of(VOLCANOLOGISTS_FILE_PATH)));
    }

    @Override
    public String importVolcanologists() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        ImportVolcanologistRootDto rootDto = this.xmlParser.fromFile(this.readVolcanologistsFromFile(), ImportVolcanologistRootDto.class);

        for (ImportVolcanologistDto dto : rootDto.getVolcanologistsList()) {

            Optional<Volcanologist> optionalVolcanologist =
                    this.volcanologistRepository.findByFirstNameAndLastName(dto.getFirstName(), dto.getLastName());
            Optional<Volcano> optionalVolcano = this.volcanoRepository.findById(dto.getVolcano());

            if (!this.validator.isValid(dto) || optionalVolcanologist.isPresent() || optionalVolcano.isEmpty()) {
                sb.append("Invalid volcanologist");
                sb.append(System.lineSeparator());
                continue;
            }

            Volcanologist volcanologist = this.modelMapper.map(dto, Volcanologist.class);
            volcanologist.setVolcano(optionalVolcano.get());
            this.volcanologistRepository.saveAndFlush(volcanologist);
            sb.append(String.format("Successfully imported volcanologist %s %s",
                    volcanologist.getFirstName(), volcanologist.getLastName()));
            sb.append(System.lineSeparator());
        }


        return sb.toString();
    }
}