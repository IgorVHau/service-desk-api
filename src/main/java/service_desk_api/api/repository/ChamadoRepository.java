package service_desk_api.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import service_desk_api.api.model.Chamado;

public interface ChamadoRepository extends 
	JpaRepository<Chamado, Long>,
	JpaSpecificationExecutor<Chamado> {
	

}
