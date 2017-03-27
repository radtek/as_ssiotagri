package com.ssiot.remote.yun.manage.task;

import android.graphics.Bitmap;

import com.ssiot.fish.HeadActivity;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class UploadFileBaseActivity extends HeadActivity{
    
    public boolean saveImage(Bitmap photo, String spath) {
        try {
            File f = new File(spath);
            if (!f.exists()){
                if (!f.getParentFile().exists()){
                    f.getParentFile().mkdirs();
                }
            }
            new File(spath).createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public String uploadimg(String remotePath, File file, String fileName){//commons-net-3.3.jar
        String remoteFileName = fileName;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect("121.40.242.120", 21);
            boolean loginResult = ftpClient.login("IOTweb", "13338626457");
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);//如果缺省该句 传输txt正常 但图片和其他格式的文件传输可能会出现未知异常
            ftpClient.enterLocalPassiveMode();

            ftpClient.changeWorkingDirectory(remotePath);//
            FTPFile[] files = ftpClient.listFiles(remotePath);
            int extraInt = 1;
            while(fileExists(remoteFileName, files)){//防止重复
                remoteFileName += "_" + extraInt;
                extraInt ++;
            }

            FileInputStream fis = new FileInputStream(file);
            ftpClient.storeFile(remoteFileName, fis);

            return remoteFileName;
        } catch (Exception e) {
            System.out.println("fail to upload files : " + e.getMessage());
        } finally {
            if (ftpClient != null && ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    System.out.println("ftp fails to disconnect : "
                            + e.getMessage());
                }
            }
        }
        return "";
    }
    
    private boolean fileExists(String fileName, FTPFile[] files){
        if (null != files && files.length > 0){
            for (int i = 0; i < files.length; i ++){
                if (fileName.equals(files[i].getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}