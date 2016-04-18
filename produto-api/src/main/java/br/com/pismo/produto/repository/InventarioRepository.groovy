package br.com.pismo.produto.repository;

import br.com.pismo.produto.entity.InventarioItem

public interface InventarioRepository {
	
	def public int addItem(String category)
	def public InventarioItem getInventarioItemById(int id)
	def public int consumeInventarioItemByCategory(String category)

}
