package br.com.pismo.produto.repository

import java.util.List
import br.com.pismo.produto.entity.Produto

class ProdutoRepositoryInMemory implements ProdutoRepository {
	
	def products = []
	
	def public ProdutoRepositoryInMemory(){
		loadInitialData()
	}

	@Override
	public int createProduto(expectedName, expectedPrice, expectedCategory, nextHandler) {
		def produto = new Produto(expectedName, expectedPrice, expectedCategory)
		products << produto		
		return produto.id;
	}

	@Override
	public Produto getProdutoById(Object id, nextHandler) { 
		return products.find{it.id == id};
	}
	
	def private loadInitialData(){		
		products << new Produto("Xbox", 1000, "xbox")		
		products << new Produto("Playstation", 2000, "playstation")
	}
	
	@Override
	public List<Produto> getAll(nextHandler) {
		products		
	}

	@Override
	public void removeAll(nextHandler) {
		products.removeAll{1==1}		
	}

	@Override
	public void removeById(Object id, nextHandler) {
		products.removeIf{it.id == id}		
	}

	@Override
	public Produto update(Object produto, nextHandler) {		
		this.removeById(produto.getId())
		products << produto
		return produto;
	}

}
