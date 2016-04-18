package br.com.pismo.produto.repository

import br.com.pismo.produto.entity.Produto

public interface ProdutoRepository {
	
	def int createProduto(expectedName, expectedPrice, expectedCategory)
	def Produto getProdutoById(id)
	def List<Produto> getAll()
	def void removeAll()
	def void removeById(id)
}
