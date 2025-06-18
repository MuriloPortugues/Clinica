package aula2604.model.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity

public class Paciente extends Pessoa {

    @NotBlank
    private String telefone;
    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consulta;



    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<Consulta> getConsulta() {
        return consulta;
    }

    public void setConsulta(List<Consulta> consulta) {
        this.consulta = consulta;
    }
}
