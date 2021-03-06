
package net.daw.service;

import com.google.gson.Gson;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.http.HttpServletRequest;
import net.daw.bean.ReplyBean;
import net.daw.bean.TipoproductoBean;
import net.daw.bean.ProductoBean;
import net.daw.connection.publicinterface.ConnectionInterface;
import net.daw.constant.ConnectionConstants;
import net.daw.dao.ProductoDao;
import net.daw.factory.ConnectionFactory;
import net.daw.helper.EncodingHelper;
import net.daw.helper.ParameterCook;

public class ProductoService {
	HttpServletRequest oRequest;
	String ob = null;

	public ProductoService(HttpServletRequest oRequest) {
		super();
		this.oRequest = oRequest;
		ob = oRequest.getParameter("ob");
	}

	public ReplyBean get() throws Exception {
		ReplyBean oReplyBean;
		ConnectionInterface oConnectionPool = null;
		Connection oConnection;

		try {
			Integer id = Integer.parseInt(oRequest.getParameter("id"));
			oConnectionPool = ConnectionFactory.getConnection(ConnectionConstants.connectionPool);
			oConnection = oConnectionPool.newConnection();
			ProductoDao oProductoDao = new ProductoDao(oConnection, ob);
			ProductoBean oProductoBean = oProductoDao.get(id,1);
			Gson oGson = new Gson();
			oReplyBean = new ReplyBean(200, oGson.toJson(oProductoBean));
		} catch (Exception ex) {
			throw new Exception("ERROR: Service level: get method: " + ob + " object", ex);
		} finally {
			oConnectionPool.disposeConnection();
		}

		return oReplyBean;

	}

	public ReplyBean remove() throws Exception {
		ReplyBean oReplyBean;
		ConnectionInterface oConnectionPool = null;
		Connection oConnection;
		try {
			Integer id = Integer.parseInt(oRequest.getParameter("id"));
			oConnectionPool = ConnectionFactory.getConnection(ConnectionConstants.connectionPool);
			oConnection = oConnectionPool.newConnection();
			ProductoDao oProductoDao = new ProductoDao(oConnection, ob);
			int iRes = oProductoDao.remove(id);
			oReplyBean = new ReplyBean(200, Integer.toString(iRes));
		} catch (Exception ex) {
			throw new Exception("ERROR: Service level: remove method: " + ob + " object", ex);
		} finally {
			oConnectionPool.disposeConnection();
		}
		return oReplyBean;

	}

	public ReplyBean getcount() throws Exception {
		ReplyBean oReplyBean;
		ConnectionInterface oConnectionPool = null;
		Connection oConnection;
		try {
			oConnectionPool = ConnectionFactory.getConnection(ConnectionConstants.connectionPool);
			oConnection = oConnectionPool.newConnection();
			ProductoDao oProductoDao = new ProductoDao(oConnection, ob);
			int registros = oProductoDao.getcount();
			Gson oGson = new Gson();
			oReplyBean = new ReplyBean(200, oGson.toJson(registros));
		} catch (Exception ex) {
			throw new Exception("ERROR: Service level: getcount method: " + ob + " object", ex);
		} finally {
			oConnectionPool.disposeConnection();
		}

		return oReplyBean;

	}

	public ReplyBean create() throws Exception {
		ReplyBean oReplyBean;
		ConnectionInterface oConnectionPool = null;
		Connection oConnection;
		try {
			String strJsonFromClient = oRequest.getParameter("json");
			Gson oGson = new Gson();
			ProductoBean oProductoBean = new ProductoBean();
			oProductoBean = oGson.fromJson(strJsonFromClient, ProductoBean.class);
			oConnectionPool = ConnectionFactory.getConnection(ConnectionConstants.connectionPool);
			oConnection = oConnectionPool.newConnection();
			ProductoDao oProductoDao = new ProductoDao(oConnection, ob);
			oProductoBean = oProductoDao.create(oProductoBean);
			oReplyBean = new ReplyBean(200, oGson.toJson("Productos creados correctamente"));
		} catch (Exception ex) {
			oReplyBean = new ReplyBean(500,
					"ERROR: " + EncodingHelper.escapeQuotes(EncodingHelper.escapeLine(ex.getMessage())));
		} finally {
			oConnectionPool.disposeConnection();
		}
		return oReplyBean;
	}

	// Metodo para crear varios productos de manera aleatoria
	public ReplyBean crearDatos() throws Exception {
		ReplyBean oReplyBean;
		ConnectionInterface oConnectionPool = null;
		Connection oConnection;
		String[] desc = { "Cuchillo", "Navaja", "Tijera", "Hacha", "Machete", "Katana", "Lanza", "Espada", "Pu�al",
				"Estilete" };
		Integer[] tipoProducto = { 160, 155, 170, 169, 157 };

		String[] codigo = { "8a7ddff", "7as9d", "dasf77sf", "987dff", "cs9df", "1d7fsaf9", "7sdfw8ef", "68fsadf8",
				"6asd7", "894xa9" };
		int[] id_tipoProducto = {252, 261, 276, 282, 300};
		Random randomDesc = new Random();
		Random randomCodigo = new Random();
		String foto = "foto";
		int nuevosRegistros = 50;
		int maxDatos = 5;
		try {
			String strJsonFromClient = oRequest.getParameter("json");
			Gson oGson = new Gson();
			ProductoBean oProductoBean = new ProductoBean();
			TipoproductoBean oTipoproductoBean;
			oProductoBean = oGson.fromJson(strJsonFromClient, ProductoBean.class);
			oConnectionPool = ConnectionFactory.getConnection(ConnectionConstants.connectionPool);
			oConnection = oConnectionPool.newConnection();
			ProductoDao oProductoDao = new ProductoDao(oConnection, ob);
			for (int i = 0; i < nuevosRegistros; i++) {
				oProductoBean = new ProductoBean();
				oTipoproductoBean = new TipoproductoBean();
				
				int randDesc = randomDesc.nextInt(10);
				int randCodigo = randomCodigo.nextInt(10);
				int existencias = ThreadLocalRandom.current().nextInt(0, 3000 + 1);
				double precio = ThreadLocalRandom.current().nextInt(1, 1000 + 1);
				
				oProductoBean.setCodigo(codigo[randCodigo]);
				oProductoBean.setDesc(desc[randDesc]);
				oProductoBean.setExistencias(existencias);
				oProductoBean.setPrecio((float) precio);
				oProductoBean.setFoto(foto);
				
				oTipoproductoBean.setId(id_tipoProducto[randomMath(maxDatos)]);
				oProductoBean.setObj_TipoproductoBean(oTipoproductoBean);
				oProductoBean = oProductoDao.create(oProductoBean);
			}
			oReplyBean = new ReplyBean(200, oGson.toJson("Productos creados correctamente"));
		} catch (Exception ex) {
			throw new Exception("ERROR: No entra", ex);
		} finally {
			oConnectionPool.disposeConnection();
		}
		return oReplyBean;
	}
	
	private int randomMath(int number) {
        return (int) (Math.random() * number);
    }
	
	public ReplyBean update() throws Exception {
		int iRes = 0;
		ReplyBean oReplyBean = null;
		ConnectionInterface oConnectionPool = null;
		Connection oConnection;
		try {
			String strJsonFromClient = oRequest.getParameter("json");
			Gson oGson = new Gson();
			ProductoBean oProductoBean = new ProductoBean();
			oProductoBean = oGson.fromJson(strJsonFromClient, ProductoBean.class);
			oConnectionPool = ConnectionFactory.getConnection(ConnectionConstants.connectionPool);
			oConnection = oConnectionPool.newConnection();
			ProductoDao oProductoDao = new ProductoDao(oConnection, ob);
			iRes = oProductoDao.update(oProductoBean);
			// oReplyBean.setStatus(200);
			// oReplyBean.setJson(Integer.toString(iRes));
		} catch (Exception ex) {
			throw new Exception("ERROR: Service level: update method: " + ob + " object", ex);
		} finally {
			oConnectionPool.disposeConnection();
		}
		return oReplyBean;
	}

	public ReplyBean getpage() throws Exception {
        ReplyBean oReplyBean;
        ConnectionInterface oConnectionPool = null;
        Connection oConnection;
        try {
            Integer iRpp = Integer.parseInt(oRequest.getParameter("rpp"));
            Integer iPage = Integer.parseInt(oRequest.getParameter("page"));
            HashMap<String, String> hmOrder = ParameterCook.getOrderParams(oRequest.getParameter("order"));
            oConnectionPool = ConnectionFactory.getConnection(ConnectionConstants.connectionPool);
            oConnection = oConnectionPool.newConnection();
            ProductoDao oProductoDao = new ProductoDao(oConnection, ob);
            ArrayList<ProductoBean> alProductoBean = oProductoDao.getpage(iRpp, iPage,hmOrder,1);
            Gson oGson = new Gson();
            oReplyBean = new ReplyBean(200, oGson.toJson(alProductoBean));
        } catch (Exception ex) {
            throw new Exception("ERROR: Service level: getpage method: " + ob + " object", ex);
        } finally {
            oConnectionPool.disposeConnection();
        }

        return oReplyBean;
	}
}
