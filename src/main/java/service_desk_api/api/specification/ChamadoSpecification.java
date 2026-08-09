package service_desk_api.api.specification;

import org.springframework.data.jpa.domain.Specification;

import service_desk_api.api.model.Categoria;
import service_desk_api.api.model.Chamado;
import service_desk_api.api.model.Prioridade;
import service_desk_api.api.model.Status;

import java.time.LocalDateTime;

public final class ChamadoSpecification {
	
	private ChamadoSpecification() {
		
	}
	
	public static Specification<Chamado> comStatus(Status status) {
		return (root, query, criteriaBuilder) 
				-> status == null 
					? criteriaBuilder.conjunction()
					: criteriaBuilder.equal(root.get("status"), status);
	}

	public static Specification<Chamado> comPrioridade(Prioridade prioridade) {
		return (root, query, criteriaBuilder) 
				-> prioridade == null 
					? criteriaBuilder.conjunction()
					: criteriaBuilder.equal(root.get("prioridade"), prioridade);
	}
	
	public static Specification<Chamado> comCategoria(Categoria categoria) {
		return (root, query, criteriaBuilder) 
				-> categoria == null 
					? criteriaBuilder.conjunction()
					: criteriaBuilder.equal(root.get("categoria"), categoria);
	}

	public static Specification<Chamado> criadoAPartirDe(LocalDateTime dataInicial) {
		return (root, query, criteriaBuilder) ->
				dataInicial == null
					? criteriaBuilder.conjunction()
					: criteriaBuilder.greaterThanOrEqualTo(root.get("criadoEm"), dataInicial);
	}

	public static Specification<Chamado> criadoAte(LocalDateTime dataFinal) {
		return (root, query, criteriaBuilder) ->
				dataFinal == null
						? criteriaBuilder.conjunction()
						: criteriaBuilder.lessThanOrEqualTo(root.get("criadoEm"), dataFinal);
	}

}
