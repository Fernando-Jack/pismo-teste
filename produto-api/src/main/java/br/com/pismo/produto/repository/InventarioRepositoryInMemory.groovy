package br.com.pismo.produto.repository

import br.com.pismo.produto.entity.InventarioItem

class InventarioRepositoryInMemory implements InventarioRepository {

	def items = []
	
	def public ProdutoRepositoryInMemory(){
		loadInitialData()
	}
	
	@Override
	public int addItem(String category, nextHandler) {
		def item = new InventarioItem(category)
		items << item		
		return item.id;
	}

	@Override
	public InventarioItem getInventarioItemById(int id, nextHandler) {
		return items.find{it.id == id};
	}

	@Override
	public int consumeInventarioItemByCategory(String category, nextHandler) {
		def item = items.find{it.category == category}
		
		if (item == null)
			return -1
		
		items.removeIf{it.id == item.id}
		
		return item.id		
	}
	
	def private loadInitialData(){
		items << new InventarioItem("xbox")
		items << new InventarioItem("xbox")
		items << new InventarioItem("playstation")
		items << new InventarioItem("playstation")
	}

	@Override
	public void getAll(Object nextHandler) {
		// TODO Auto-generated method stub
		
	}

}
