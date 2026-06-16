package com.zhaow.restful.common.resolver;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.zhaow.restful.annotations.PathMappingAnnotation;
import com.zhaow.restful.annotations.SpringControllerAnnotation;
import com.zhaow.restful.common.spring.RequestMappingAnnotationHelper;
import com.zhaow.restful.method.RequestPath;
import com.zhaow.restful.method.action.PropertiesHandler;
import com.zhaow.restful.navigation.action.RestServiceItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpringResolver  extends BaseServiceResolver  {
/*    Module myModule;
    Project myProject;*/
    PropertiesHandler propertiesHandler ;
    public SpringResolver(Module module) {
        myModule = module;
        propertiesHandler = new PropertiesHandler(module);
    }

    public SpringResolver(Project project) {
        myProject = project;

    }


    //Note: 当Controller 类上没有标记@RequestMapping 注解时，方法上的@RequestMapping 都是绝对路径。
    /*@Override
    public List<RestServiceItem> getServiceItemList(PsiMethod psiMethod) {
        List<RestServiceItem> itemList = new ArrayList<>();

        RequestPath[] methodRequestPaths = RequestMappingAnnotationHelper.getRequestPaths(psiMethod);

        //TODO:  controller 没有设置requestMapping，默认“/”
        // TODO : controller 设置了(rest)controller 所有方法未指定 requestMapping，所有方法均匹配； 存在方法指定 requestMapping，则只解析这些方法；
//        String[] controllerPaths = RequestMappingAnnotationHelper.getRequestPaths(psiMethod.getContainingClass());
        List<RequestPath> classRequestPaths = RequestMappingAnnotationHelper.getRequestPaths(psiMethod.getContainingClass());
        for (RequestPath classRequestPath : classRequestPaths) {
            for (RequestPath methodRequestPath : methodRequestPaths) {
                String path =  classRequestPath.getPath();
//                String path = tryReplacePlaceholderValueInPath( classRequestPath.getPath() );

                RestServiceItem item = createRestServiceItem(psiMethod, path, methodRequestPath);
                itemList.add(item);
            }
        }

        return itemList;
    }*/


    /*
    private String  tryReplacePlaceholderValueInPath(String path) {
        if (!path.contains("${")) return path;

        StringBuilder cleanerPath = new StringBuilder();

        for (String s : path.split("\\$\\{")) {
            if (s.contains("}")) {
                String placeholder = s.substring(0, s.indexOf("}"));
                String theOther = s.substring(s.indexOf("}")+1);

                String propertyValue = propertiesHandler.getProperty(placeholder);

                cleanerPath.append(StringUtils.isEmpty(propertyValue) ? s : propertyValue.trim()).append(theOther);
            } else {
                cleanerPath.append(s);
            }
        }
        return cleanerPath.toString();
    }*/


/*    @NotNull
//    @Override
    public List<PsiMethod> getServicePsiMethodList(Project project, GlobalSearchScope globalSearchScope) {
        List<PsiMethod> psiMethodList = new ArrayList<>();
        //TODO : 这块需要改写，支持更多注解方式。
        // TODO: 这种实现的局限了其他方式实现的url映射（xml（类似struts），webflux routers）
        SpringControllerAnnotation[] supportedAnnotations = SpringControllerAnnotation.values();
        //        for (SpringControllerAnnotation controllerAnnotations : SpringControllerAnnotation.values()) {
        for (PathMappingAnnotation controllerAnnotation : supportedAnnotations) {

            // 标注了 (Rest)Controller 注解的类，即 Controller 类
            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(controllerAnnotation.getShortName(), project, globalSearchScope);
            for (PsiAnnotation psiAnnotation : psiAnnotations) {
                PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
                PsiElement psiElement = psiModifierList.getParent();

                *//*if (!(psiElement instanceof PsiClass)) continue; // RestController annotation 只出现在class*//*
                PsiClass psiClass = (PsiClass) psiElement;
                PsiMethod[] psiMethods = psiClass.getMethods();
                // FIXME: 这里应该只包含 设置了，requstmapping的方法，除非所有方法都没标注 requstmapping
                if (psiMethods == null) {
                    continue;
                }

                for (PsiMethod psiMethod : psiMethods) {
                    // todo: 没有处理同时标注了 GET 和 POST 两种方法的方法，定义一个 RequestMapping 类{method，path}
                    psiMethodList.add(psiMethod);
                }
            }

        }
        return psiMethodList;
    }*/

    @Override
    public List<RestServiceItem> getRestServiceItemList(Project project, GlobalSearchScope globalSearchScope) {
        List<RestServiceItem> itemList = new ArrayList<>();

        // TODO: 这种实现的局限了其他方式实现的url映射（xml（类似struts），webflux routers）
        SpringControllerAnnotation[] supportedAnnotations = SpringControllerAnnotation.values();
        for (PathMappingAnnotation controllerAnnotation : supportedAnnotations) {

            // java: 标注了 (Rest)Controller 注解的类，即 Controller 类
            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(controllerAnnotation.getShortName(), project, globalSearchScope);
            for (PsiAnnotation psiAnnotation : psiAnnotations) {
                PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
                PsiElement psiElement = psiModifierList.getParent();

                /*if (!(psiElement instanceof PsiClass)) continue; // RestController annotation 只出现在 class*/
                PsiClass psiClass = (PsiClass) psiElement;
                List<RestServiceItem> serviceItemList = getServiceItemList(psiClass);
                itemList.addAll(serviceItemList);
            }

        }

        return itemList;
    }

    protected List<RestServiceItem> getServiceItemList(PsiClass psiClass) {

        PsiMethod[] psiMethods = psiClass.getMethods();
        if (psiMethods == null) {
            return new ArrayList<>();
        }

        List<RestServiceItem> itemList = new ArrayList<>();
        List<RequestPath> classRequestPaths = RequestMappingAnnotationHelper.getRequestPaths(psiClass);

        for (PsiMethod psiMethod : psiMethods) {
            RequestPath[] methodRequestPaths = RequestMappingAnnotationHelper.getRequestPaths(psiMethod);

            for (RequestPath classRequestPath : classRequestPaths) {
                for (RequestPath methodRequestPath : methodRequestPaths) {
                    String path =  classRequestPath.getPath();
//                String path = tryReplacePlaceholderValueInPath( classRequestPath.getPath() );

                    RestServiceItem item = createRestServiceItem(psiMethod, path, methodRequestPath);
                    itemList.add(item);
                }
            }

        }
        return itemList;
    }


}
