package br.com.pismo.produto.service

import br.com.pismo.produto.entity.InventarioItem
import br.com.pismo.produto.repository.InventarioRepository

class InventarioService {
	
	def private InventarioRepository inventarioRepository;
	
	def InventarioService(InventarioRepository inventarioRepository){
		this.inventarioRepository = inventarioRepository
	}	
	
	public int addItem(String category) {
		inventarioRepository.addItem(category);
	}
	
	public InventarioItem getInventarioItemById(int id) {
		inventarioRepository.getInventarioItemById(id)
	}
	
	public int consumeInventarioItem(String category) {
		inventarioRepository.consumeInventarioItemByCategory(category)
	}

}
