package service_desk_api.api.dto;

import java.time.LocalDateTime;

import service_desk_api.api.model.Status;
import service_desk_api.api.model.Prioridade;
import service_desk_api.api.model.Categoria;

public record ChamadoResponse(
		Long id,
		String titulo,
		String descricao,
		Status status,
		Prioridade prioridade,
		Categoria categoria,
		LocalDateTime criadoEm,
		LocalDateTime atualizadoEm,
		LocalDateTime concluidoEm
		) {
}
