package br.com.pismo.compra.repository

import io.vertx.core.json.JsonArray
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.SQLConnection
import io.vertx.ext.sql.UpdateResult
import br.com.pismo.compra.entity.Compra

class CompraRepositoryJDBCSQL implements CompraRepository {

	def private jdbc
	def private connection

	def public CompraRepositoryJDBCSQL(JDBCClient jdbc){
		this.jdbc = jdbc
		createDB()
	}

	@Override
	public int createCompra(Object itemId, Object userId, Object price, nextHandler) {
		def compra = new Compra(itemId, userId, price)
		insert(compra, connection, nextHandler)
		return 0
	}


	@Override
	public Compra getCompraById(Object id, nextHandler) {
		select(id.toString(), connection, nextHandler)
		return null
	}

	def createDB(){
		jdbc.getConnection({ ar ->
			connection = ar.result()
			connection.execute(
					"DELETE FROM compra;CREATE TABLE IF NOT EXISTS compra (id INTEGER IDENTITY, itemId integer, userId integer, price integer)", { a ->
						if (a.failed()) {
							print a.cause()
							connection.close();
							return;
						}
					});
		});
	}

	private void insert(Compra compra, SQLConnection connection, nextHandler) {
		String sql = "INSERT INTO compra (itemId, userId, price) VALUES ?, ?, ?";
		connection.updateWithParams(sql,
				new JsonArray().add(compra.getItemId()).add(compra.getUserId()).add(compra.getPrice()), { ar ->
					if (ar.failed()) {
						print ar.cause()
						nextHandler(-1)
						connection.close();
						return;
					}

					UpdateResult result = ar.result();
					def w = new Compra()
					w.with{
						it.id = result.getKeys().getInteger(0)
						it.userId = compra.getUserId()
						it.price = compra.getPrice()
						it.itemId = compra.getItemId()
					}
					nextHandler(w)
				});
	}

	private void select(String id, SQLConnection connection, nextHandler) {
		connection.queryWithParams("SELECT * FROM compra WHERE id=?", new JsonArray().add(id), { ar ->
			if (ar.failed()) {
				print ar.cause()
				nextHandler(-1);
			} else {
				if (ar.result().getNumRows() >= 1) {
					nextHandler(new Compra(ar.result().getRows().get(0)))
				} else {
					nextHandler(-1);
				}
			}
		});
	}
}
