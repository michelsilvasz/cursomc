package com.michelalves.cursomc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.michelalves.cursomc.domain.Cidade;
import com.michelalves.cursomc.domain.Cliente;
import com.michelalves.cursomc.domain.Endereco;
import com.michelalves.cursomc.domain.enums.TipoCliente;
import com.michelalves.cursomc.dto.ClienteDTO;
import com.michelalves.cursomc.dto.ClienteNewDTO;
import com.michelalves.cursomc.repositories.CidadeRepository;
import com.michelalves.cursomc.repositories.ClienteRepository;
import com.michelalves.cursomc.repositories.EnderecoRepository;
import com.michelalves.cursomc.services.exception.DataIntegrityException;
import com.michelalves.cursomc.services.exception.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repo;
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	
	public Cliente find(Integer id) {
		Cliente obj = repo.findOne(id);
		if(obj == null) {
			throw new ObjectNotFoundException("Objeto n�o encontrado! Id: "+ id + ", Tipo: "+ Cliente.class.getName());
		}
		return obj;
	}
	
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		enderecoRepository.save(obj.getEnderecos());
		return obj;
	}
	
	public Cliente update(Cliente obj) {
		Cliente  newObj = find(obj.getId());
		updateData(newObj, obj);
		return repo.save(newObj);
	}
	
	public void delete(Integer id) {
		find(id);
		try {
			repo.delete(id);
		}catch(DataIntegrityViolationException e) {
			throw new DataIntegrityException("N�o � poss�vel excluir porque h� entidades relacionadas");
		}
	}
	
	public List<Cliente> findAll(){
		return repo.findAll();
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction){
		PageRequest pageRequest = new PageRequest(page, linesPerPage, Direction.valueOf(direction), orderBy);
		
		return repo.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null);
	}
	
	public Cliente fromDTO(ClienteNewDTO objDto) {
		 Cliente cli = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(), TipoCliente.toEnum(objDto.getTipo()));
		 Cidade cid = new Cidade(objDto.getCidadeId(), null, null);
		 Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), cli, cid);
		 cli.getEnderecos().add(end);
		 cli.getTelefone().add(objDto.getTelefone1());
		 if(objDto.getTelefone2() != null) {
			 cli.getTelefone().add(objDto.getTelefone2());
		 }
		 if(objDto.getTelefone3() != null) {
			 cli.getTelefone().add(objDto.getTelefone3());
		 }
		 return cli;
		 
	}
	
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
}
