package service_desk_api.api.dto;

import service_desk_api.api.model.Status;
import service_desk_api.api.model.Prioridade;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import service_desk_api.api.model.Categoria;

public record ChamadoRequest(
		
		@NotBlank(message = "O título é obrigatório.")
		@Size(max = 100, message = "O título deve ter no máximo 100 caractéres.")
		String titulo,
		
		@NotBlank(message = "A descrição é obrigatória.")
		@Size(max = 500, message = "A descrição deve ter no máximo 500 caractéres.")
		String descricao,
		
		@NotNull(message = "O status deve ser preenchido.")
		@Enumerated(EnumType.STRING)
		Status status,
		
		@NotNull(message = "A prioridade deve ser preenchida.")
		@Enumerated(EnumType.STRING)
		Prioridade prioridade,
		
		@NotNull(message = "A categoria deve ser preenchida.")
		@Enumerated(EnumType.STRING)
		Categoria categoria
		) {
}
