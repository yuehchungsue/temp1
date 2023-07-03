package com.cybersoft.bean;
//20221021/dependency-check:弱點修補/Jeffery.Cheng/202210210533-00

import org.apache.logging.log4j.*;

public class LogUtils {

//    private static boolean a = false;//	20221021
	private Logger logger;

//	20221021:會自動尋找log4j2.xml配置檔
	public LogUtils(Class class1) {

		logger = null;
/**        20221021	1.x 升級成 2.x
//        logger.debug("this.getClass().getClassLoader().getResource(\"Log4j.properties\")=("+this.getClass().getClassLoader().getResource("Log4j.properties")+")");
//        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("Log4j.properties"));*/

/**		20221021	1.x 升級成 2.x
//		logger = Logger.getRootLogger();*/
		logger = LogManager.getRootLogger();
	}

/**	20221021:會自動尋找log4j2.xml配置檔*/
	public LogUtils(String s) {
		logger = LogManager.getRootLogger();
/**        PropertyConfigurator.configure("/home/payment/MerchantConsole/src/log4j.properties");
//        Properties pp=new Properties();
//        try{*/
		logger.debug("-------------------------- Merchant LogUtils Start -------------------");
/**        pp.load(this.getClass().getClassLoader().getResourceAsStream("log4j.properties"));
//            pp.load(new FileInputStream("C:/Log4j.properties"));
//        PropertyConfigurator.configure(pp);
//        logger.debug(pp);
//        logger.debug("-------------------------- Merchant LogUtils End -------------------");
//        }catch(Exception e){
//            e.printStackTrace();*/

/**		20221021	1.x 升級成 2.x              
//      logger = Logger.getLogger(s);*/
		logger = LogManager.getLogger(s);

	}

/**    20221021	1.x 升級成 2.x
//    public static void pushLog(String s)
//    {
//        NDC.push(s);
//    }
//
//    public static void pop()
//    {
//        NDC.pop();
//    }*/

	public void debug(Object obj) {
		/** if (obj instanceof String)
			logger.debug("", (Throwable) obj);
		else */
			logger.debug(obj);
	}

	public void debug(String strFunction, String strUserId) {
		logger.debug("strFunction={}, userid={}", strFunction, strUserId);
	}

	public void debug(Object obj, Throwable throwable) {
		logger.debug(obj, throwable);
	}

	public void error(Object obj) {
		if (obj instanceof String)
			logger.error("", (Throwable) obj);
		else
			logger.error(obj);
	}

	public void error(String strFunction, String strUserId, String strErrMsg) {
		logger.error("strFunction={}, strUserId={}, strErrMsg={}", strFunction, strUserId, strErrMsg);
	}

	public void error(Object obj, Throwable throwable) {
		logger.error(obj, throwable);
	}

	public void info(Object obj) {
		if (obj instanceof String)
			logger.info("", (Throwable) obj);
		else
			logger.info(obj);
	}

	public void info(String strFunction, String strUserId, String strAction) {
		logger.info("strFunction={}, strUserId={}, strAction={}", strFunction, strUserId, strAction);
	}

	public void info(Object obj, Throwable throwable) {
		logger.info(obj, throwable);
	}

	public void warn(Object obj) {
		if (obj instanceof String)
			logger.warn("", (Throwable) obj);
		else
			logger.warn(obj);
	}

	public void warn(Object obj, Throwable throwable) {
		logger.warn(obj, throwable);
	}

/**		20221021	1.x 升級成 2.x
//    private static synchronized void a() 
//    {
//        if (a) 
//        {
//            return;
//        } 
//        else 
//        {
//            PropertyConfigurator.configureAndWatch("log4j.properties", 60000L);
//            a = true;
//            return;
//        }
//    }*/

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

}
