package com.cybersoft.session;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.cybersoft.bean.SessionControlBean;
import javax.servlet.ServletContext;

public class SessionControlListener extends HttpServlet implements HttpSessionListener, HttpSessionAttributeListener, java.io.Serializable
{

    private HttpSession session;
    private String merchantid;
    private String sessionid;
    private String userid;
    private Hashtable comm=new Hashtable();

    //Notification that a session was created
    public void sessionCreated(HttpSessionEvent se)
    {
//        System.out.println("@@@@@@@@@@@@@@merchantid<"+merchantid+">userid<"+userid+">sessionid<"+sessionid+">@@@@@@@@@@@@@@@@@@@@@@@");
        session=se.getSession();
//        Enumeration key= session.getAttributeNames();
//        while(key.hasMoreElements()){
//            String key_name=String.valueOf(key.nextElement());
//            System.out.println(key_name+"=<"+String.valueOf(session.getAttribute(key_name))+">");
//        }
        System.out.println("sessionCreated!!!!!sessionid="+session.getId()+" time="+new Date());
//        throw new java.lang.UnsupportedOperationException(
//                "Method sessionCreated() not yet implemented.");
    }

    //Notification that a session was invalidated
    public void sessionDestroyed(HttpSessionEvent se)
    {
        System.out.println("Start sessionDestroyed!!!!!"+new Date());
//        System.out.println("se.getId()"+se.getSession().getId());
        if(session!=null)
        {
            ServletContext sc = session.getServletContext();
        //        String keyvalue=String.valueOf(comm.get(se.getSession().getId()));
            String keyvalue = String.valueOf(sc.getAttribute(se.getSession().getId()));
            if (!keyvalue.equalsIgnoreCase("null") && keyvalue.split("@").length == 3) 
            {
        //            System.out.println("@@@@@@@@@@@@@@merchantid<" + keyvalue.split("@")[0] +
        //                               ">userid<" + keyvalue.split("@")[1] + ">sessionid<" + keyvalue.split("@")[2] +
        //                               ">@@@@@@@@@@@@@@@@@@@@@@@");
                SessionControlBean scb = new SessionControlBean();
                if (scb.close_Session(keyvalue.split("@")[0], keyvalue.split("@")[1], keyvalue.split("@")[2], true)) {
                    System.out.println("logout active!!!");
                    
                    sc.removeAttribute(keyvalue.split("@")[0] + "@" + keyvalue.split("@")[1]);
                    sc.removeAttribute(keyvalue.split("@")[2]);
                }
            }
            
            System.out.println("End sessionDestroyed!!!!!" + new Date());
        }
//        throw new java.lang.UnsupportedOperationException(
//                "Method sessionDestroyed() not yet implemented.");
    }

    //Notification that a new attribute has been added to a session
    public void attributeAdded(HttpSessionBindingEvent se)
    {
        System.out.println("Start attributeAdded!!!!!"+new Date());
        session=se.getSession();
        try
        {
            if ( (Hashtable) ( (Hashtable) session.getAttribute("SYSCONFDATA")).get("MERCHANT_USER") != null) 
            {
                Hashtable userinfo = (Hashtable) ( (Hashtable) session.getAttribute("SYSCONFDATA")).get("MERCHANT_USER");
                merchantid = String.valueOf(userinfo.get("MERCHANT_ID"));
                userid = String.valueOf(userinfo.get("USER_ID"));
                sessionid = session.getId();
//            System.out.println("merchantid<"+merchantid+">userid<"+userid+">sessionid<"+sessionid+">");
                comm.put(session.getId(), merchantid + "@" + userid + "@" + sessionid);
                ServletContext sc = session.getServletContext();
                sc.setAttribute(session.getId(), merchantid + "@" + userid + "@" + sessionid);
                sc.setAttribute(merchantid + "@" + userid, session.getId());
            }
        }
        catch(Exception e)
        {
            e.toString();
        }
//        Enumeration key= session.getAttributeNames();
//        while(key.hasMoreElements()){
//            String key_name=String.valueOf(key.nextElement());
//            System.out.println(key_name+"=<"+String.valueOf(session.getAttribute(key_name))+">");
//        }
        System.out.println("End attributeAdded!!!!!"+new Date());
//        throw new java.lang.UnsupportedOperationException(
//                "Method attributeAdded() not yet implemented.");
    }

    //Notification that an attribute has been removed from a session
    public void attributeRemoved(HttpSessionBindingEvent se)
    {
//        System.out.println("Start@@@@@@@@@@@@@@merchantid<"+merchantid+">userid<"+userid+">sessionid<"+sessionid+">@@@@@@@@@@@@@@@@@@@@@@@");
//        System.out.println("attributeRemoved!!!!!"+new Date());
//        session=se.getSession();
//        Enumeration key= session.getAttributeNames();
//        while(key.hasMoreElements()){
//            String key_name=String.valueOf(key.nextElement());
//            System.out.println(key_name+"=<"+String.valueOf(session.getAttribute(key_name))+">");
//        }
//        System.out.println("End@@@@@@@@@@@@@@merchantid<"+merchantid+">userid<"+userid+">sessionid<"+sessionid+">@@@@@@@@@@@@@@@@@@@@@@@");
//        throw new java.lang.UnsupportedOperationException(
//                "Method attributeRemoved() not yet implemented.");
    }

    //Notification that an attribute of a session has been replaced
    public void attributeReplaced(HttpSessionBindingEvent se)
    {
//        System.out.println("Start@@@@@@@@@@@@@@merchantid<"+merchantid+">userid<"+userid+">sessionid<"+sessionid+">@@@@@@@@@@@@@@@@@@@@@@@");
//        System.out.println("attributeReplaced!!!!!"+new Date());
//        session=se.getSession();
//        Enumeration key= session.getAttributeNames();
//        while(key.hasMoreElements()){
//            String key_name=String.valueOf(key.nextElement());
//            System.out.println(key_name+"=<"+String.valueOf(session.getAttribute(key_name))+">");
//        }
//        System.out.println("End@@@@@@@@@@@@@@merchantid<"+merchantid+">userid<"+userid+">sessionid<"+sessionid+">@@@@@@@@@@@@@@@@@@@@@@@");
//        throw new java.lang.UnsupportedOperationException(
//                "Method attributeReplaced() not yet implemented.");
    }
}
