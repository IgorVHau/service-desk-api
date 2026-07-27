package service_desk_api.api.mapper;

import service_desk_api.api.model.Chamado;
import service_desk_api.api.dto.ChamadoResponse;
import service_desk_api.api.dto.ChamadoRequest;

public final class ChamadoMapper {
	
	private ChamadoMapper() {
		
	}
	
	public static ChamadoResponse paraResponse(Chamado chamado) {
		return new ChamadoResponse(
				chamado.getId(),
				chamado.getTitulo(),
				chamado.getDescricao(),
				chamado.getStatus(),
				chamado.getPrioridade(),
				chamado.getCategoria(),
				chamado.getCriadoEm(),
				chamado.getAtualizadoEm(),
				chamado.getConcluidoEm()
				);
	}
	
	public static Chamado paraEntidade(ChamadoRequest request) {
		return Chamado.builder()
				.titulo(request.titulo())
				.descricao(request.descricao())
				.status(request.status())
				.prioridade(request.prioridade())
				.categoria(request.categoria())
				.build();
	}

}
