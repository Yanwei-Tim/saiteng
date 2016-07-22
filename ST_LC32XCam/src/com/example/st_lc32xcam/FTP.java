package com.example.st_lc32xcam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.saiteng.st_lc32xcam.utils.Result;
import com.saiteng.st_lc32xcam.utils.Util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * FTP封装类.
 *  
 * @author cui_tao
 */
public class FTP {
    /**
     * 服务器名.
     */
    private String hostName;

    /**
     * 用户名.
     */
    private String userName;

    /**
     * 密码.
     */
    private String password;

    /**
     * FTP连接.
     */
    private FTPClient ftpClient;

    /**
     * FTP列表.
     */
    private List<FTPFile> list;

    /**
     * FTP根目录.
     */
    public static final String REMOTE_PATH = "//";

    /**
      * FTP当前目录.
      */
    private String currentPath = "";

    /**
     * 统计流量.
     */
    private double response;
    
    String remote; // 远程文件
	String local; // 本地文件
	String fileName;//wenjianming
	private Thread mThread;
	private long process;
	private boolean interceptFlag = false; // 中断标记
	private Context context;
    /**
     * 构造函数.
     * @param host hostName 服务器名
     * @param user userName 用户名
     * @param pass password 密码
     */
    public FTP(String host, String user, String pass,Context context) {
        this.hostName = host;
        this.userName = user;
        this.password = pass;
        this.ftpClient = new FTPClient();
        this.list = new ArrayList<FTPFile>();
        this.context = context;
    }

    /**
     * 打开FTP服务.
     * @throws IOException 
     */
    public void openConnect() {
        // 中文转码
        ftpClient.setControlEncoding("UTF-8");
        int reply; // 服务器响应值
        // 连接至服务器
        try {
			ftpClient.connect(hostName,21);
		
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        // 登录到服务器
        ftpClient.login(userName, password);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        } else {
            // 获取登录信息
            FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftpClient.configure(config);
            // 使用被动模式设为默认
            ftpClient.enterLocalPassiveMode();
            // 二进制文件支持
            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
            System.out.println("login");
        }
        } catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * 关闭FTP服务.
     * @throws IOException 
     */
    public void closeConnect() throws IOException {
        if (ftpClient != null) {
            // 登出FTP
            ftpClient.logout();
            // 断开连接
            ftpClient.disconnect();
            System.out.println("logout");
        }
    }

    /**
     * 列出FTP下所有文件.
     * @param remotePath 服务器目录
     * @return FTPFile集合
     * @throws IOException 
     */
    public List<FTPFile> listFiles(String remotePath) {
        // 获取文件
        FTPFile[] files;
		try {
			files = ftpClient.listFiles(remotePath);
		
        // 遍历并且添加到集合
        for (FTPFile file : files) {
            list.add(file);
            Log.d("------", file+"");
        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return list;
    }

    /**
     * 下载.
     * @param remotePath FTP目录
     * @param fileName 文件名
     * @param localPath 本地目录
     * @return Result
     * @throws IOException 
     */
    public void download(String strRemote, String strLocal,String fileName){
		this.remote = strRemote;
		this.local = strLocal; 
		this.fileName = fileName;
		mThread = new Thread(mRunnable);
		mThread.start();

	}
 // 在进程中执行导出操作
 	private Runnable mRunnable = new Runnable() {
 		@Override
 		public void run() {
 			try {
 				// 设置被动模式
 				ftpClient.enterLocalPassiveMode();
 				// 设置以二进制方式传输
 				ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
 				// DownloadStatus result;
 				// 检查远程文件是否存在
 				FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("GBK"), "iso-8859-1"));
 				if (files.length != 1) {
 					System.out.println("远程文件不存在");
 					// return DownloadStatus.Remote_File_Noexist;
 				}
 				long lRemoteSize = files[0].getSize();
 				File f = new File(local);
 				// 本地存在文件，进行断点下载
 				if (f.exists()) {
 					File f1 = new File(local+"/"+fileName);
 					if(f1.exists()){
 						ProgressView.getProgressHandler().sendEmptyMessage(6);
 					}else{
 					    OutputStream out = new FileOutputStream("/mnt/sdcard/CAM/"+fileName);
						InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"), "iso-8859-1"));
						byte[] bytes = new byte[1024];
						long step = lRemoteSize / 100;
						process = 0;
						long localSize = 0L;
						int c;
						while ((c = in.read(bytes)) != -1) {
							if (interceptFlag) {
								break;
							}
							out.write(bytes, 0, c);
							localSize += c;
							long nowProcess = localSize / step;
							if (nowProcess > process) {
								process = nowProcess;
								if (process % 1 == 0){
									System.out.println("下载进度：" + process);
									ProgressView.getProgressHandler().sendEmptyMessage(3);
								}
								
							}
						}
						in.close();
						out.close();
	
						if (c <= 0) {
							ProgressView.getProgressHandler().sendEmptyMessage(4);
						}					
						
						boolean isDo = ftpClient.completePendingCommand();
						if (isDo) {
							// 下载完成;
							ProgressView.getProgressHandler().sendEmptyMessage(4);
						} else {
							// 下载失败;
							ProgressView.getProgressHandler().sendEmptyMessage(5);
						}
 					}
 				} else {
 					if(f.mkdirs()){
 						OutputStream out = new FileOutputStream("/mnt/sdcard/CAM/"+fileName);
 						InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"), "iso-8859-1"));
 						byte[] bytes = new byte[1024];
 						long step = lRemoteSize / 100;
 						process = 0;
 						long localSize = 0L;
 						int c;
 						while ((c = in.read(bytes)) != -1) {
 							if (interceptFlag) {
 								break;
 							}
 							out.write(bytes, 0, c);
 							localSize += c;
 							long nowProcess = localSize / step;
 							if (nowProcess > process) {
 								process = nowProcess;
 								if (process % 1 == 0)
 									System.out.println("下载进度：" + process);
 						       
 							}
 						}
 						in.close();
 						out.close();
 	
 						if (c <= 0) {
 							ProgressView.getProgressHandler().sendEmptyMessage(4);
 						}					
 						
 						boolean isDo = ftpClient.completePendingCommand();
 						if (isDo) {
 							// 下载完成;
 							ProgressView.getProgressHandler().sendEmptyMessage(4);
 						} else {
 							// 下载失败;
 							ProgressView.getProgressHandler().sendEmptyMessage(5);
 						}
 				}
 				}
 				// return result;
 			} catch (IOException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
 		}
 	};
    

    /**
     * 上传.
     * @param localFile 本地文件
     * @param remotePath FTP目录
     * @return Result
     * @throws IOException 
     */
    public Result uploading(File localFile, String remotePath) throws IOException {
        boolean flag = true;
        Result result = null;
        // 初始化FTP当前目录
        currentPath = remotePath;
        // 初始化当前流量
        response = 0;
        // 二进制文件支持
        ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        // 使用被动模式设为默认
        ftpClient.enterLocalPassiveMode();
        // 设置模式
        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
        // 改变FTP目录
        ftpClient.changeWorkingDirectory(REMOTE_PATH);
        // 获取上传前时间
        Date startTime = new Date();
        // 上传单个文件
        flag = uploadingSingle(localFile);
        // 获取上传后时间
        Date endTime = new Date();
        // 返回值
        result = new Result(flag, Util.getFormatTime(endTime.getTime() - startTime.getTime()), Util.getFormatSize(response));
        return result;
    }

    /**
     * 上传单个文件.
     * @param localFile 本地文件
     * @return true上传成功, false上传失败
     * @throws IOException 
     */
    private boolean uploadingSingle(File localFile) throws IOException {
        boolean flag = true;
        // 创建输入流
        InputStream inputStream = new FileInputStream(localFile);
        // 统计流量
        response += (double) inputStream.available() / 1;
        // 上传单个文件
        flag = ftpClient.storeFile(localFile.getName(), inputStream);
        // 关闭文件流
        inputStream.close();
        return flag;
    }

    
    /**
     * 删除文件.
     * @param remotePath Ftp目录
     * @param fileName 
     * 
     */
    public boolean deleteSingle(final String remotePath,final String fileName){
    	boolean deleteflag = false;
    	
				try {
					
					// 初始化FTP当前目录
					currentPath = remotePath;
					// 初始化当前流量
					response = 0;
					// 更改FTP目录
					ftpClient.changeWorkingDirectory(remotePath);
					deleteflag = ftpClient.deleteFile(fileName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
    	return deleteflag;
    }
   
	

}
