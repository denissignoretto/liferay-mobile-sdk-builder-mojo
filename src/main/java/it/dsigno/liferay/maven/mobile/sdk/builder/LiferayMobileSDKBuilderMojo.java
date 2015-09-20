package it.dsigno.liferay.maven.mobile.sdk.builder;

/**
 * Copyright (c) 2015-present Denis Signoretto. All rights reserved.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.liferay.mobile.sdk.util.Validator;

/**
 * Goal which invoke Mobile SDK Builder, generate android java file.
 * 
 * @goal mobile-sdk-gen-sources
 * @phase process-sources
 */
@Mojo(name = "mobile-sdk-gen-sources")
public class LiferayMobileSDKBuilderMojo extends AbstractMojo {

    // https://maven.apache.org/plugin-tools/maven-plugin-tools-annotations/

    public void execute()
        throws MojoExecutionException {

        try {

            String[] args = new String[7];

            args[0] = "context=" + context;
            args[1] = "platforms=" + platforms;
            args[2] = "url=" + url;
            args[3] = "portalVersion=" + version;
            args[4] = "packageName=" + packageName;
            args[5] = "destination=" + destination;
            args[6] = "filter=" + filter;

            // executeTool(
            // "com.liferay.mobile.sdk.SDKBuilder", args);

            if (Validator.isNotNull(delay) && Long.valueOf(delay) >= 0) {
                getLog().info("Wainting " + Long.valueOf(delay) + " before Mobile SDK invocation.");
                Thread.sleep(Long.valueOf(delay));
            }

            com.liferay.mobile.sdk.SDKBuilder.main(args);

        }
        catch (Exception e) {
            throw new MojoExecutionException("Error invoking SDKBuilder ", e);
        }
        finally {

        }
    }

    protected void executeTool(
        String toolClassName, String[] args)
        throws Exception {

        Thread currentThread = Thread.currentThread();

        ClassLoader contextClassLoader = currentThread.getContextClassLoader();

        // currentThread.setContextClassLoader(classLoader);

        try {
            System.setProperty(
                "external-properties",
                "com/liferay/portal/tools/dependencies" +
                    "/portal-tools.properties");
            System.setProperty(
                "org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.Log4JLogger");

            Class<?> clazz = contextClassLoader.loadClass(toolClassName);

            Method method = clazz.getMethod("main", String[].class);

            method.invoke(null, (Object) args);
        }
        catch (InvocationTargetException ite) {

            throw ite;
        }
        finally {

            // currentThread.setContextClassLoader(contextClassLoader);

            System.clearProperty("org.apache.commons.logging.Log");

        }
    }

    /**
     * Your portlet's web context. Say for example you are generating an SDK for Liferay's Calendar portlet, which is generally deployed to the calendar-portlet
     * context, then you should set your context value to context=calendar-portlet. Under the hood, the SDK Builder will try to access
     * http://localhost:8080/calendar-portlet/api/jsonws?discover
     * 
     * @parameter
     */
    private String context;

    /**
     * By default, you can generate code for Android and iOS (android,ios).
     * 
     * @parameter default-value="android,ios"
     */
    private String platforms;

    /**
     * The URL to your Liferay instance, the SDK Builder will connect to this instance
     * and find out metadata about your services.
     * 
     * @parameter default-value="http://localhost:8080"
     */
    private String url;

    /**
     * Module Version (version) - The version number is appended to the JAR and ZIP file names
     * 
     * @parameter
     */
    private String version;

    /**
     * Package Name (packageName) - On Android, this is the package to which your SDK's classes are written. The iOS platform does not use packages. Note, that
     * the Liferay Portal version is appended to the end of the package name.
     * 
     * @parameter
     */
    private String packageName;

    /**
     * Generated sources destination.
     * 
     * @parameter default-value="${project.build.directory}/generated-sources"
     */
    private String destination;

    /**
     * Filter (filter) - Specifies your portlet's entities whose services to access; a blank value indicates the services of all of the portlet's entities. For
     * example, the Calendar portlet has entities such as CalendarBooking and CalendarResource. To generate an SDK for only the CalendarBooking entity, set the
     * filter's value to calendarbooking, all in lowercase. The SDK Builder will then make requests to the
     * http://localhost:8080/calendar-portlet/api/jsonws?discover=/calendarbooking/*. If you set filter=, specifying no filter value, the remote services of all
     * of the portlet's entities will be made available to your mobile SDK.
     * 
     * @parameter
     */
    private String filter;

    /**
     * Optional parameter to delay SDK Builder invocation.
     * 
     * @parameter
     */
    private String delay;

}
