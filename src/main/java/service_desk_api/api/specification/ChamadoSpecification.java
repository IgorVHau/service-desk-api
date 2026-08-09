package service_desk_api.api.specification;

import org.springframework.data.jpa.domain.Specification;

import service_desk_api.api.model.Categoria;
import service_desk_api.api.model.Chamado;
import service_desk_api.api.model.Prioridade;
import service_desk_api.api.model.Status;

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

}
