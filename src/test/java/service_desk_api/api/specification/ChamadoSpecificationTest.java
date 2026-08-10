package service_desk_api.api.specification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import service_desk_api.api.model.Categoria;
import service_desk_api.api.model.Chamado;
import service_desk_api.api.model.Prioridade;
import service_desk_api.api.model.Status;
import service_desk_api.api.repository.ChamadoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ChamadoSpecificationTest {

    @Autowired
    private ChamadoRepository chamadoRepository;

    private Chamado criarChamado(String titulo, LocalDateTime criadoEm) {
        return Chamado.builder()
                .titulo(titulo)
                .descricao("Chamado utilizado no teste de Specification.")
                .status(Status.ABERTO)
                .prioridade(Prioridade.MEDIA)
                .categoria(Categoria.SOLICITACAO)
                .criadoEm(criadoEm)
                .build();
    }

    @DisplayName(value = "Deve filtrar chamados por período de criação")
    @Test
    void deveFiltrarChamadosPorPeriodoDeCriacao() {

        Chamado antesDoPeriodo = criarChamado(
                "Antes do período",
                LocalDateTime.of(2025, 7, 31, 10, 0)
        );

        Chamado dentroDoPeriodo = criarChamado(
                "Dentro do período",
                LocalDateTime.of(2025, 8, 5, 10, 0)
        );

        Chamado depoisDoPeriodo = criarChamado(
                "Depois do período",
                LocalDateTime.of(2025, 8, 10, 10, 0)
        );

        chamadoRepository.saveAll(
                List.of(
                        antesDoPeriodo,
                        dentroDoPeriodo,
                        depoisDoPeriodo
                )
        );

        LocalDateTime inicio = LocalDate.of(2025, 8, 1).atStartOfDay();
        LocalDateTime fim = LocalDate.of(2025, 8, 9).atTime(LocalTime.MAX);

        Specification<Chamado> specification = Specification
                .where(ChamadoSpecification.criadoAPartirDe(inicio))
                .and(ChamadoSpecification.criadoAte(fim));

        List<Chamado> resultado = chamadoRepository.findAll(specification);

        assertEquals(1, resultado.size());
        assertEquals("Dentro do período", resultado.get(0).getTitulo());
    }
}
