package com.eli.product.elimvc.common;

import com.eli.product.elimvc.annotation.Controller;
import com.eli.product.elimvc.annotation.Qualifier;
import com.eli.product.elimvc.annotation.RequestMapping;
import com.eli.product.elimvc.annotation.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 * @description 前端控制器
 */
public class DispatcherServlet extends HttpServlet{
    //存放类的全限定名
    List<String> classFullyQualifiedName = new ArrayList<String>();
    //存放实例化bean
    Map<String,Object> beans = new HashMap<String,Object>();
    //存放handler方法映射
    Map<String,Object> handlerMap = new HashMap<String,Object>();
    public void init(){
        //1 扫描包
        packageScan("com.eli.product.elimvc.business");
        //2 实例化类
        instantiation();
        //3 依赖注入
        di();
        //4 请求地址和方法映射
        handlerMapper();
    }

    private void handlerMapper() {
        if(beans.entrySet().isEmpty()){
            System.out.println("------handlerMapper:beans为空------");
            return;
        }
        for (Map.Entry<String,Object> entry:beans.entrySet()) {
            Object instance = entry.getValue();
            Class clazz = instance.getClass();
            if (clazz.isAnnotationPresent(Controller.class)) {
                //拿controller配置的url信息
                RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
                String classPath = requestMapping.value();
                //找到所有方法上配置的url信息
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
                        String methodValue = methodRequestMapping.value();
                        handlerMap.put(classPath + methodValue, method);
                    } else {
                        continue;
                    }
                }
            }
        }
    }

    private void di() {
        if(beans.entrySet().isEmpty()){
            System.out.println("------di:beans为空------");
            return;
        }
        for (Map.Entry<String,Object> entry:beans.entrySet()){
            Object instance = entry.getValue();
            Class clazz = instance.getClass();
            if(clazz.isAnnotationPresent(Controller.class)){
                Field[] fields = clazz.getDeclaredFields();
                for (Field field:fields){
                    if (field.isAnnotationPresent(Qualifier.class)){
                        //设置允许访问私有成员
                        field.setAccessible(true);
                        Qualifier qualifier = (Qualifier)field.getAnnotation(Qualifier.class);
                        Object object = beans.get(qualifier.value());
                        try {
                            //对实例instance注入依赖的object的实例
                            field.set(instance,object);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void instantiation(){
        if (classFullyQualifiedName == null || classFullyQualifiedName.isEmpty()){
            System.out.println("------instantiation:classFullyQualifiedName为空");
            return;
        }
        for (String className:classFullyQualifiedName){
            try {
                Class clazz = Class.forName(className.replace(".class",""));
                //判断类是否包含@Controller注解
                if (clazz.isAnnotationPresent(Controller.class)){
                    //实例化
                    Object instance = clazz.newInstance();
                    //获取控制层的请求根路径，并映射控制层
                    RequestMapping requestMapping = (RequestMapping)clazz.getAnnotation(RequestMapping.class);
                    beans.put(requestMapping.value(),instance);
                }else if (clazz.isAnnotationPresent(Service.class)) {
                    Object instance = clazz.newInstance();
                    Service service = (Service)clazz.getAnnotation(Service.class);
                    beans.put(service.value(),instance);
                }else{
                    continue;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private void packageScan(String packagePath) {
        //替换 . 为 /
        String pPath = packagePath.replaceAll("\\.","/");
        //获取资源路径
        URL url = this.getClass().getClassLoader().getResource(pPath);
        String filePath = url.getFile();
        String[] filePathStr = new File(filePath).list();
        for (String fileStr:filePathStr){
            File file = new File(filePath+fileStr);
            if (file.isDirectory()){
                packageScan(packagePath+"."+fileStr);
            }else{
                classFullyQualifiedName.add(packagePath+"."+file.getName());
                System.out.println("------packageScan类："+packagePath+"."+file.getName());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求URL
        String uri = req.getRequestURI();
        //获取请求项目名
        String context = req.getContextPath();
        //替换工程名 因为方法里没有工程名
        String path = uri.replace(context, "");
        //执行方法
        Method method = (Method) handlerMap.get(path);
        //拿实例去
        String  instancePath = path.split("/")[1];
        Object instance = beans.get("/" + instancePath);

        //拿参数
        String name = req.getParameter("reqName");

        /*HandlerAdapter ha = (HandlerAdapter)beans.get(prop.getProperty(HANDLERADAPTER));
        Object[] args = ha.hand(request, response, method, beans);*/

        try {
            //method.invoke(instance, args);
            method.invoke(instance, req, resp, name);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
