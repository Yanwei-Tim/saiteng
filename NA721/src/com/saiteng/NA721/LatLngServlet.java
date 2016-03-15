package com.saiteng.NA721;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.saiteng.NA721.dao.GetResponseData;

/**
 * 接收请求信标经纬度的http请求，从数据库beacon中查找出指定信标的最新位置信息返回。
 */
@WebServlet("/LatLng")
public class LatLngServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
      * @see HttpServlet#HttpServlet()
      */
    public LatLngServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	  * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	  */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String phonenum=request.getParameter("phonenum");
		GetResponseData getdata1 = new GetResponseData();
		String str = getdata1.getLatLngData(phonenum);
		response.getWriter().append(str);
	}

	/**
	  * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	  */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
