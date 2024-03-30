package softuni.exam.models.dto;

import com.google.gson.annotations.Expose;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ImportCountryDto {

    @Expose
    @NotNull
    @Size(min = 3, max = 30)
    private String name;
    @Expose
    @Size(min = 3, max = 30)
    private String capital;

    public ImportCountryDto() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }
}
