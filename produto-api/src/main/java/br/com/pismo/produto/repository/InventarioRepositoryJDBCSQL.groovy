package br.com.pismo.produto.repository

import io.vertx.core.Launcher
import io.vertx.core.json.JsonArray
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.sql.SQLConnection
import io.vertx.ext.sql.UpdateResult

import java.util.stream.Collectors

import br.com.pismo.produto.entity.InventarioItem

class InventarioRepositoryJDBCSQL implements InventarioRepository {

	def private jdbc
	def private connection
	def private vertxLogger = LoggerFactory.getLogger(Launcher.class.getName());

	def public InventarioRepositoryJDBCSQL(jdbc){
		vertxLogger.info("Your InventarioRepositoryJDBCSQL is started!");
		this.jdbc = jdbc
		createDB()
	}

	@Override
	public void getAll(Object nextHandler) {
		selectAll(connection, nextHandler)
	}

	@Override
	public int addItem(String category, nextHandler) {
		def item = new InventarioItem(category)
		insert(item, connection, nextHandler)
		return 0
	}

	@Override
	public InventarioItem getInventarioItemById(int id, nextHandler) {
		select(id.toString(), connection, nextHandler)
		return null
	}

	@Override
	public int consumeInventarioItemByCategory(String category, nextHandler) {
		consume(category, connection, nextHandler)
		return 0
	}

	def createDB(){
		jdbc.getConnection({ ar ->
			connection = ar.result()
			connection.execute(
					"CREATE TABLE IF NOT EXISTS inventario (id INTEGER IDENTITY, category varchar(100), status varchar(1));DELETE FROM inventario;", { a ->
						if (a.failed()) {
							print a.cause()
							connection.close();
							return;
						}
					});
		});
	}

	private void insert(InventarioItem item, SQLConnection connection, nextHandler) {
		String sql = "INSERT INTO inventario (category, status) VALUES ?, ?"
		connection.updateWithParams(sql,
				new JsonArray().add(item.getCategory()).add("D"), { ar ->
					if (ar.failed()) {
						vertxLogger.info(ar.cause())
						nextHandler(-1)
						connection.close();
						return;
					}

					UpdateResult result = ar.result();
					def w = new InventarioItem()

					w.with{
						it.id = result.getKeys().getInteger(0)
						it.category = item.getCategory()
					}
					nextHandler(w)
				});
	}

	private void select(String id, SQLConnection connection, nextHandler) {
		connection.queryWithParams("SELECT * FROM inventario WHERE id=?", new JsonArray().add(id), { ar ->
			if (ar.failed()) {
				nextHandler(-1);
			} else {
				if (ar.result().getNumRows() >= 1) {
					def item = new InventarioItem(ar.result().getRows().get(0))
					nextHandler(item)
				} else {
					nextHandler(-1);
				}
			}
		});
	}

	private void selectAll(SQLConnection connection, nextHandler) {		
		connection.queryWithParams("SELECT * FROM inventario WHERE status = ?", new JsonArray().add("D"), { result ->
			if(result.failed()){
				nextHandler(-1)
			}else{
				def produtos = result.result().getRows().stream().map({new InventarioItem(it)}).collect(Collectors.toList())
				nextHandler(produtos)
			}
		});
	}

	private void consume(String category, SQLConnection connection, nextHandler) {
		connection.queryWithParams("SELECT TOP 1 * FROM inventario WHERE category=? and status = ?", new JsonArray().add(category).add("D"), { ar ->
			if (ar.failed()) {
				print ar.cause()
				nextHandler(-1);
			} else {
				if (ar.result().getNumRows() >= 1) {
					def item = new InventarioItem(ar.result().getRows().get(0))

					String sql = "UPDATE inventario SET status=? WHERE id=?"
					connection.updateWithParams(
							sql,
							new JsonArray()
							.add("V")
							.add(item.getId().toString()), {update ->
								if (update.failed()) {
									nextHandler("Cannot update the item")
									return
								}
								if (update.result().getUpdated() == 0) {
									nextHandler("item not found")
									return
								}
								def p = new InventarioItem()
								p.with{
									it.id =	item.getId()
									it.category = category
								}
								nextHandler(p)
							}
							)
				} else {
					nextHandler(-1);
				}
			}
		});
	}
}
