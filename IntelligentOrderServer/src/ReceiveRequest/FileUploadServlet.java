package ReceiveRequest;

import java.io.File;  
import java.io.IOException;  
import java.io.PrintWriter;  
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Iterator;  
import java.util.List;  

import javax.servlet.ServletException;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
 

import org.apache.commons.fileupload.FileItem;  
import org.apache.commons.fileupload.FileUploadException;  
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;  
import org.apache.commons.fileupload.disk.DiskFileItemFactory;  
import org.apache.commons.fileupload.servlet.ServletFileUpload;  

import com.sun.xml.xsom.impl.scd.Iterators.Map;

import DataBaseLibrary.DBManager;
  
public class FileUploadServlet extends HttpServlet {  
  
public FileUploadServlet() {  
   super();  
}  
  
public void destroy() {  
   super.destroy(); // Just puts "destroy" string in log  
   // Put your code here  
}  
  
public void doGet(HttpServletRequest request, HttpServletResponse response)  
    throws ServletException, IOException {  
   doPost(request, response);  
}  
  
private static final String METHOD="method";
//private UserBase _userData;
private final String _basePath="E:\\MyEclipseProgram\\InviteWebService\\WebRoot\\resource\\";


private HttpServletRequest _request=null;
private HttpServletResponse _response=null;

public void doPost(HttpServletRequest request, HttpServletResponse response)  
    throws ServletException, IOException {  

	_request=request;
	_response=response;
	request.setCharacterEncoding("UTF-8");
	
//	_userData=UserUtils.getInstance().getUserDataFromDataBase(
//			URLDecoder.decode(request.getHeader("userName"), "utf-8"));
	
	
	//�û������¼
//	System.out.println(_userData.getUserName()+":�����ϴ�ͼƬ!!");
	
	
   final long MAX_SIZE = 30 * 1024 * 1024;// �����ϴ��ļ����Ϊ 30M  
   // �����ϴ����ļ���ʽ���б�  
   final String[] allowedExt = new String[] { "jpg", "jpeg", "gif", "txt",  
     "doc", "docx", "mp3", "wma", "m4a","png" };  
   response.setContentType("text/html");  
   // �����ַ�����ΪUTF-8, ����֧�ֺ�����ʾ  
   response.setCharacterEncoding("UTF-8");  
  
   // ʵ����һ��Ӳ���ļ�����,���������ϴ����ServletFileUpload  
   DiskFileItemFactory dfif = new DiskFileItemFactory();  
   dfif.setSizeThreshold(4096);// �����ϴ��ļ�ʱ������ʱ����ļ����ڴ��С,������4K.���ڵĲ��ֽ���ʱ����Ӳ��  
   dfif.setRepository(new File(request.getSession().getServletContext().getRealPath("/")  
     + "ImagesUploadTemp"));// ���ô����ʱ�ļ���Ŀ¼,web��Ŀ¼�µ�ImagesUploadTempĿ¼  
  
   // �����Ϲ���ʵ�����ϴ����  
   ServletFileUpload sfu = new ServletFileUpload(dfif);  
   // ��������ϴ��ߴ�  
   sfu.setSizeMax(MAX_SIZE);  
  
   PrintWriter out = response.getWriter();  
   // ��request�õ� ���� �ϴ�����б�  
   List fileList = null;  
   try {  
    fileList = sfu.parseRequest(request);  
   } catch (FileUploadException e) {// �����ļ��ߴ�����쳣  
    if (e instanceof SizeLimitExceededException) {  
     out.println("�ļ��ߴ糬���涨��С:" + MAX_SIZE + "�ֽ�<p />");  
     out.println("<a href=\"upload.html\" target=\"_top\">����</a>");  
     return;  
    }  
    e.printStackTrace();  
   }  
   // û���ļ��ϴ�  
   if (fileList == null || fileList.size() == 0) {  
    out.println("��ѡ���ϴ��ļ�<p />");  
    out.println("<a href=\"upload.html\" target=\"_top\">����</a>");  
    return;  
   }  
   // �õ������ϴ����ļ�  
   Iterator fileItr = fileList.iterator();  
   // ѭ�����������ļ�  
   while (fileItr.hasNext()) {  
    FileItem fileItem = null;  
    String path = null;  
    long size = 0;  
    // �õ���ǰ�ļ�  
    fileItem = (FileItem) fileItr.next();  
    // ���Լ�form�ֶζ������ϴ�����ļ���(<input type="text" />��)  
    if (fileItem == null || fileItem.isFormField()) {  
    	continue;  
    }  
    // �õ��ļ�������·��  
    path = fileItem.getName();     
    // �õ��ļ��Ĵ�С  
    size = fileItem.getSize();  
    if ("".equals(path) || size == 0) {  
     out.println("��ѡ���ϴ��ļ�<p />");  
     out.println("<a href=\"upload.html\" target=\"_top\">����</a>");  
     return;  
    }  
  
    // �õ�ȥ��·�����ļ���  
    String t_name = path.substring(path.lastIndexOf("\\") + 1);  
    // �õ��ļ�����չ��(����չ��ʱ���õ�ȫ��)  
    String t_ext = t_name.substring(t_name.lastIndexOf(".") + 1);  
    // �ܾ����ܹ涨�ļ���ʽ֮����ļ�����  
    int allowFlag = 0;  
    int allowedExtCount = allowedExt.length;  
    for (; allowFlag < allowedExtCount; allowFlag++) {  
     if (allowedExt[allowFlag].equals(t_ext))  
      break;  
    }  
    if (allowFlag == allowedExtCount) {  
     out.println("���ϴ��������͵��ļ�<p />");  
     for (allowFlag = 0; allowFlag < allowedExtCount; allowFlag++)  
      out.println("*." + allowedExt[allowFlag]  
        + "&nbsp;&nbsp;&nbsp;");  
     out.println("<p /><a href=\"upload.html\" target=\"_top\">����</a>");  
     return;  
    }  
  
    long now = System.currentTimeMillis();  
    // ����ϵͳʱ�������ϴ��󱣴���ļ���  
    String prefix = String.valueOf(now);  
    // ����������ļ�����·��,������web��Ŀ¼�µ�ImagesUploadedĿ¼��  
    String u_name = request.getSession().getServletContext().getRealPath("/") + "ImagesUploadTemp/"  
      + prefix + "." + t_ext;  
    
    //self customize
//    String url=_basePath+_userData.getUserName()+"\\"; ����bug���²�ˢ��eclipse�͵ò����ļ�
    String url=request.getSession().getServletContext().getRealPath("/")
    		+"Resource\\";
    String resPath=methodBranch(request);
    url+=resPath.substring(0,resPath.indexOf("\\"));
    File file=new File(url);
    if(!file.exists()&&!file.isDirectory())
    {
    	file.mkdir();
    }
//    url=url+prefix+"."+t_ext;
    url+=resPath.substring(resPath.indexOf("\\"));
    url=url+"."+t_ext;
    
    System.out.println("���յ�·��"+url);
    
    //fix database data
//    String imgResource=_userData.getUserName()+"/"+prefix+"."+t_ext;
//    updateDataBaseData(imgResource);
    
    
    
	try {
				// �����ļ�
		fileItem.write(new File(url));
		out.println("�ļ��ϴ��ɹ�. �ѱ���Ϊ: " + prefix + "." + t_ext
				+ " &nbsp;&nbsp;�ļ���С: " + size + "�ֽ�<p />");
		out.println("<a href=\"upload.html\" target=\"_top\">�����ϴ�</a>");
	} catch (Exception e) {
		e.printStackTrace();
	}
			// _isUserExsit=false;
			// _userData=null;
   }
   
   _request=null;
   _response=null;
  
}  

public void init() throws ServletException {  
   // Put your code here  
} 

public void updateDataBaseData(String url)
{
	Connection connection=new DBManager().getConnection();
	Statement stm=null;
	
	try {
		stm=connection.createStatement();
//		stm.execute("update user set img_resource='"+url+"' where name='"
//				+_userData.getUserName()+"'");
		
		stm.close();
		connection.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	

}

public String methodBranch(HttpServletRequest request)
{
	String methodName=null;
	try {
		methodName = URLDecoder.decode(request.getHeader(METHOD),"utf-8");
	} catch (UnsupportedEncodingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	String rootPath=null;
	
	if(methodName.equals("uploadHeadImage"))
	{
		try {
			String id=URLDecoder.decode(request.getHeader("id"), "utf-8");
			rootPath=new FileUploadMethods().UploadHeadImageByID(request);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	if(methodName.equals("uploadOrderItemImage"))
	{
		rootPath=new FileUploadMethods().uploadOrderItemImage(request);
	}
	
	

	
//	if(methodName.equals("getOrderItemImageByID"))
//	{
//		try {
//			String id=URLDecoder.decode(request.getHeader("userName"), "utf-8");
//			rootPath=new FileUploadMethods().
//					getOrderItemImageByID(request);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	if(methodName.equals("UploadHeadImageByID"))
//	{
//		try {
//			String id=URLDecoder.decode(request.getHeader("id"), "utf-8");
//			rootPath=new FileUploadMethods().UploadHeadImageByID(request);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	return rootPath;
}


}  
