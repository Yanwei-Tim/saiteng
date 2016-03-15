package com.saiteng.NA721;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.saiteng.NA721.dao.GetResponseData;

/**
 * Servlet implementation class GroupServlet
 */
@WebServlet("/GroupServlet")
public class GroupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private List<Object> list = new ArrayList<Object>();
	private String arr=null;

    /**
      * Default constructor. 
      */
    public GroupServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GetResponseData getdata = new GetResponseData();
		String phonenum=request.getParameter("phonenum");
		list.clear();
		if(phonenum!=null){//�����������Ⱥ���Ա
			
			list = getdata.getGroupData(phonenum);
			arr="";
			for(int i=0;i<list.size();i++){
				arr = arr+list.get(i)+",";
			}
			response.getWriter().append(arr);
		}else{//���ض��������Ⱥ���ڵ����г�Ա
			list = getdata.allGroupData();
			arr="";
			for(int i=0;i<list.size();i++){
				arr = arr+list.get(i)+",";
			}
			response.getWriter().append(arr);
		}
	}
	/**
	  * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	  */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
