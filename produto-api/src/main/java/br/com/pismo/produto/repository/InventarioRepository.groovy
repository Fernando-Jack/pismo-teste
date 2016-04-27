package br.com.pismo.produto.repository;

import br.com.pismo.produto.entity.InventarioItem

public interface InventarioRepository {
	
	def int addItem(String category, nextHandler)
	def InventarioItem getInventarioItemById(int id, nextHandler)
	def int consumeInventarioItemByCategory(String category, nextHandler)
	def void getAll(nextHandler)

}
