package br.com.pismo.produto.repository

import java.util.List
import br.com.pismo.produto.entity.Produto

class ProdutoRepositoryInMemory implements ProdutoRepository {
	
	def products = []
	
	def public ProdutoRepositoryInMemory(){
		loadInitialData()
	}

	@Override
	public int createProduto(expectedName, expectedPrice, expectedCategory) {
		def produto = new Produto(expectedName, expectedPrice, expectedCategory)
		products << produto		
		return produto.id;
	}

	@Override
	public Produto getProdutoById(Object id) { 
		return products.find{it.id == id};
	}
	
	def private loadInitialData(){		
		products << new Produto("Xbox", 1000, "xbox")		
		products << new Produto("Playstation", 2000, "playstation")
	}
	
	@Override
	public List<Produto> getAll() {
		products		
	}

	@Override
	public void removeAll() {
		products.removeAll{1==1}		
	}

	@Override
	public void removeById(Object id) {
		products.removeIf{it.id == id}		
	}

}
