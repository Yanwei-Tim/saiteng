package com.saiteng.NA721;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.saiteng.NA721.dao.GetResponseData;

/**
 * Servlet implementation class LocusdetailsServlet
 */
@WebServlet("/LocusdetailsServlet")
public class LocusdetailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LocusdetailsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String time=request.getParameter("time");
		String Time = time.substring(0, 14);
		GetResponseData getdata = new GetResponseData();
		List<Object> list =getdata.getDetailsLocus(Time);
		String str_new="";
		for(int i=0;i<list.size();i++){
			str_new =str_new+ list.get(i)+",";
		}
		response.getWriter().append(str_new);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
