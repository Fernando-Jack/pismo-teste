package br.com.pismo.produto.repository

import br.com.pismo.produto.entity.Produto

public interface ProdutoRepository {
	
	def int createProduto(expectedName, expectedPrice, expectedCategory, nextHandler)
	def Produto getProdutoById(id, nextHandler)
	def List<Produto> getAll(nextHandler)
	def void removeAll(nextHandler)
	def void removeById(id, nextHandler)
	def Produto update(produto, nextHandler)
	
}
