package br.com.pismo.produto.service

import br.com.pismo.produto.entity.Produto
import br.com.pismo.produto.repository.ProdutoRepository


class ProdutoService {

	def ProdutoRepository repository

	public ProdutoService(ProdutoRepository repository){
		this.repository = repository
	}

	def  int createProduto(expectedName, expectedPrice, expectedCategory){
		repository.createProduto(expectedName, expectedPrice, expectedCategory)
	}

	def  Produto getProdutoById(id){
		repository.getProdutoById(id)
	}

	def  removeAll(){
		repository.removeAll()
	}

	def  removeById(id){
		repository.removeById(id)
	}

	def  List<Produto> getAllProdutos(){
		repository.getAll()
	}
}
