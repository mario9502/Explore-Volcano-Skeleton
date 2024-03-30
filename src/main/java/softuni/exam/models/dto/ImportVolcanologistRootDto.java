package softuni.exam.models.dto;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "volcanologists")
@XmlAccessorType
public class ImportVolcanologistRootDto {

    @XmlElement(name = "volcanologist")
    private List<ImportVolcanologistDto> volcanologists;

    public ImportVolcanologistRootDto() {
        this.volcanologists = new ArrayList<>();
    }

    public List<ImportVolcanologistDto> getVolcanologistsList() {
        return volcanologists;
    }

    public void setVolcanologists(List<ImportVolcanologistDto> volcanologists) {
        this.volcanologists = volcanologists;
    }
}
