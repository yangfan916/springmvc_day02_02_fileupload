package com.yangfan.controller;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * @author yangfan
 * @version 1.0
 * @description
 */
@Controller
@RequestMapping("user")
public class UserController {

    /**
     * 传统文件上传
     * @return
     */
    @RequestMapping("fileUpload1")
    public String fileUpload1(HttpServletRequest request) throws Exception {
        System.out.println("文件上传......");

        //使用fileUpload组件完成文件上传
        //上传的位置
        String path = request.getSession().getServletContext().getRealPath("/uploads/");
        //判断该路径是否存在
        File file = new File(path);
        if(!file.exists()){
            //创建该文件夹
            file.mkdirs();
        }

        //解析request对象，获取上传文件项
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        //解析request
        List<FileItem> fileItems = upload.parseRequest(request);
        //遍历
        for(FileItem fileItem : fileItems){
            //判断当前item是不是上传文件项
            if(fileItem.isFormField()){
                //说明是普通表单项
            }else {
                //上传文件项
                //获取上传文件的名称
                String fileName = fileItem.getName();
                fileName = UUID.randomUUID().toString().replace("-", "") + "_" + fileName;
                //完成文件上传
                fileItem.write(new File(path, fileName));
                //删除临时文件
                fileItem.delete();
            }
        }
        return "success";
    }

    /**
     * springmvc文件上传
     * @return
     */
    @RequestMapping("fileUpload2")
    public String fileUpload2(HttpServletRequest request, MultipartFile upload) throws Exception {
        System.out.println("sprigmvc文件上传......");

        //使用fileUpload组件完成文件上传
        //上传的位置
        String path = request.getSession().getServletContext().getRealPath("/uploads/");
        //判断该路径是否存在
        File file = new File(path);
        if(!file.exists()){
            //创建该文件夹
            file.mkdirs();
        }

        //上传文件项
        //获取上传文件的名称
        String filename = upload.getOriginalFilename();
        filename = UUID.randomUUID().toString().replace("-", "") + "_" + filename;
        //完成文件上传
        upload.transferTo(new File(path, filename));
        return "success";
    }

    /**
     * 跨服务器文件上传
     * @return
     */
    @RequestMapping("fileUpload3")
    public String fileUpload3(MultipartFile upload) throws Exception {
        System.out.println("跨服务器文件上传......");

        String path = "http://localhost:9090/file_upload_server_war_exploded/uploads/";

        //上传文件项
        //获取上传文件的名称
        String filename = upload.getOriginalFilename();
        filename = UUID.randomUUID().toString().replace("-", "") + "_" + filename;

        //创建客户端对象
        Client client = Client.create();
        //和图片服务器进行连接
        WebResource resource = client.resource(path + filename);
        //完成文件上传
        resource.put(upload.getBytes());

        return "success";
    }
}
