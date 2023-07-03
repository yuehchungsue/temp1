/** 異動說明
 * 202208090854-01 20220801 HKP PCI-DSS更換密碼雜錯MD5為SHA256，MD5雜錯長度為32，SHA256雜錯長度為64，以此長度判斷是否為MD5，變更後的密碼一律使用SHA256與LOG遮蔽
 *                 20221124 Frog Jump Co., YC White Scan
 */
package com.cybersoft.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.owasp.encoder.EncodedWriter;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;

import com.crystaldecisions.report.web.viewer.ReportExportControl;
import com.crystaldecisions.reports.sdk.DatabaseController;
import com.crystaldecisions.sdk.occa.report.data.Fields;
import com.crystaldecisions.sdk.occa.report.data.IDatabase;
import com.crystaldecisions.sdk.occa.report.data.ITable;
import com.crystaldecisions.sdk.occa.report.data.ParameterField;
import com.crystaldecisions.sdk.occa.report.data.ParameterFieldDiscreteValue;
import com.crystaldecisions.sdk.occa.report.data.Tables;
import com.crystaldecisions.sdk.occa.report.data.Values;
import com.crystaldecisions.sdk.occa.report.exportoptions.ExportOptions;
import com.crystaldecisions.sdk.occa.report.exportoptions.ReportExportFormat;
import com.crystaldecisions.sdk.occa.report.reportsource.IReportSource;
import com.fubon.security.filter.SecurityTool;
import com.fubon.tp.util.XSSUtils;

public class createReport
{

	private HttpServletRequest request;
    private HttpServletResponse response;    

	/** The XSSUtils */
	private XSSUtils xssUtils = null;
	
	/** private String sqlPrefix = "select"; */
	
	public static final LogUtils log_createReport = new LogUtils("systeminfo");
	
    public createReport()
    {
		/** document why this constructor is empty */    	
    }

    /**
     * getSession</br>
     * 
     * @param HttpServletRequest request
     * @return HttpSession
     */
    private synchronized HttpSession getSession(HttpServletRequest request) {
    	return request.getSession(true);
    }
    
	/**
	 * createPDF</br>
	 * 
	 * @param DataBaseBean sysBean
	 * @param String sql
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @param String rptname
	 * @param Hashtable fields
	 * @param Hashtable
	 */
	public void createPDF(DataBaseBean2 sysBean, String sql, HttpServletRequest request, HttpServletResponse response,
			String rptname, Hashtable fields, String type) {

		log_createReport.debug("--Report Start time{" + new Date() + "}" + getSession(request).getId() + "--");

		try {
			setRequest(request);
			setResponse(response);
			
			ArrayList<Hashtable<String,String>> resultAl = null;
			
			ResultSet resultSet = null;
			// 2009/12/22 Update by Nancy 依據2009/12/02 resultset被關閉問題

			try {
				/** XSSUtils */
				this.xssUtils = new XSSUtils(request);
				sql = this.xssUtils.dealwithXSSContent(SecurityTool.output(sql));
								
				/** 2023/05/03 改用 DataBaseBean2.java 的 QuerySQLByParam3() (By : YC) *//** Test Case : IT-TESTCASE-023 */
				resultSet = sysBean.QuerySQLByParam3(sql);
			} catch (Exception ex) {
				log_createReport.debug(ex.getMessage());
			}

			if (type.equalsIgnoreCase("PDF")) {
				/** ReportClientDocument */
				com.crystaldecisions.reports.sdk.ReportClientDocument reportClientDocument = new com.crystaldecisions.reports.sdk.ReportClientDocument();
				reportClientDocument.open(rptname, 0);

				/**
				 * String tableName =
				 * reportClientDocument.getDatabaseController().getDatabase().getTables().getTable(0).getName();
				 */

				DatabaseController dc = reportClientDocument.getDatabaseController();
				IDatabase idb = dc.getDatabase();

				Tables tab = idb.getTables();
				ITable tab0 = tab.getTable(0);
				String tableName = tab0.getName();
				reportClientDocument.getDatabaseController().setDataSource(resultSet, tableName, "");

				// Get the IReportSource object from sesion and pass it to the viewer
				IReportSource reportSource = reportClientDocument.getReportSource();
				// ---------- Create the viewer and render the report -------------
				ReportExportControl exportControl = new ReportExportControl();
				ExportOptions exportOptions = new ExportOptions();
				exportOptions.setExportFormatType(ReportExportFormat.PDF);
				exportControl.setReportSource(reportSource);
				exportControl.setExportOptions(exportOptions);
				exportControl.setExportAsAttachment(true);
				/**
				 * log_createReport.debug("Locale{"+request.getLocale()+"}request.getCharacterEncoding{"+request.getCharacterEncoding()+"}");
				 */
				exportControl.setProductLocale(request.getLocale());
				/**
				 * log_createReport.debug("exportControl.getProductLocale().getLanguage("+exportControl.getProductLocale().getLanguage()+")");
				 */
				Fields oFields = new Fields();

				Enumeration<?> key = fields.keys();
				while (key.hasMoreElements()) {
					String paramName = String.valueOf(key.nextElement());
					ParameterField oParameterField = new ParameterField();
					// You must set the report name.
					// Set the report name to an empty string if your report does not contain a
					// subreport; otherwise, the report name will be the name of the subreport
					oParameterField.setReportName("");

					// Create a Values object and a ParameterFieldDiscreteValue object for each
					// object for each parameter field you wish to set.
					// If a ranged value is being set, a ParameterFieldRangeValue object should
					// be used instead of the discrete value object.
					Values oValues = new Values();
					ParameterFieldDiscreteValue oParameterFieldDiscreteValue = new ParameterFieldDiscreteValue();
					/** show paramname infomation */
					/** log_createReport.debug(paramName+"="+fields.get(paramName).toString()); */
					// Set the name of the parameter. This must match the name of the parameter as
					// defined in the
					// report.
					oParameterField.setName(paramName);
					oParameterFieldDiscreteValue.setValue(fields.get(paramName));

					// Add the parameter field values to the Values collection object.
					oValues.add(oParameterFieldDiscreteValue);

					// Set the current Values collection for each parameter field.
					oParameterField.setCurrentValues(oValues);

					// Add parameter field to the Fields collection. This object is then passed to
					// the
					// viewer as the collection of parameter fields values set.
					oFields.add(oParameterField);
					exportControl.setParameterFields(oFields);
				}

				exportControl.processHttpRequest(request, response, getSession(request).getServletContext(), null);
				exportControl.dispose();
			} else {
				response.setContentType("application/text");

				log_createReport.debug(request.getContextPath() + rptname.substring(0, rptname.indexOf(".")) + ".txt");
				ArrayList fieldarray = read(request.getContextPath(),
						rptname.substring(0, rptname.indexOf(".")) + ".txt");
				/** DataBaseBean dbb=new DataBaseBean(); */
				/** ArrayList resultAl=sysBean.transferFormat(resultSet); */
				
				/** 2023/05/03 改用 DataBaseBean2.java 的 private ArrayList<Hashtable<String,String>> transferFormat(ResultSet resultset) (By : YC) *//** Test Case : IT-TESTCASE-024 */
				resultAl = sysBean.transferFormat(resultSet);
				/** log_createReport.debug("createReport.createPDF().resultAl="+resultAl); */
				if (type.equalsIgnoreCase("TXT")) {
					response.setHeader("content-disposition", "attachment; filename=Report.txt;");
					save(xssUtils, doFileContent(resultAl, fieldarray, "txt"));
				} else if (type.equalsIgnoreCase("CSV")) {
					response.setHeader("content-disposition", "attachment; filename=Report.csv;");
					save(xssUtils, doFileContent(resultAl, fieldarray, "csv"));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		log_createReport.debug("--Report End  time{" + new Date() + "}" + getSession(request).getId() + "--");
	}

    public ArrayList read( String parent,String filename)
    {
        //read transaction file
    	/** log_createReport.debug("read Start time{"+new Date()+"}");*/
        String data = "";
        ArrayList<Hashtable<String, String>> result=new ArrayList<>();
        FileInputStream bis;
        String urlsSub1 = "/";
        File file1 = new File(parent,parent+urlsSub1+filename);
        /** log_createReport.debug(file1.getAbsolutePath()); */
        try 
        {
        	int index = 0;
            /** char c; */
            if (file1.exists())
            {
                bis = new FileInputStream(file1);
                byte[] inbt = new byte[(int) file1.length()];
                /** old : bis.read(inbt); */
                while((index+=bis.read())!=-1)
                {
                   /** c=(char)index;
                   log_createReport.debug(c); */
                }
                
                bis.close();
                data = new String(inbt, "big5");
                /** log_createReport.debug("File read out ====>" + data +"<========end"); */
            }
            else
            {
				/** log_createReport.debug("File can't read ====>" + parent+"/"+ parent+"/"+filename +"<========end"); */
				file1 = new File(getSession(this.request).getServletContext().getRealPath("") + filename);
				if (file1.exists()) {
					log_createReport.debug("filename={}", filename);
					bis = new FileInputStream(file1);
					byte[] inbt = new byte[(int) file1.length()];
					/** old : bis.read(inbt); */
					while ((index += bis.read()) != -1) {
						/**
						 * c=(char)index; log_createReport.debug(c);
						 */
					}
					bis.close();
					data = new String(inbt, "big5");
					log_createReport.debug("File read out ====>" + data + "<========end");
				} else {
					log_createReport.debug("File can't read ====>" + file1.getAbsolutePath() + "<========end");
				}

            }
            /** return data; */
        }
        catch (RuntimeException e)
        {
            data = null;
            e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}

        String configStr=data;
        configStr = configStr.replace("\r", ""); //將設定內的換行符號替代掉
        configStr = configStr.replace("\n", "");
        String[] configTag = configStr.split(";");
        boolean checkNew=true;//是否已存在
        String name = "Name";
        String type = "Type";
        String length = "Length";
        String desc = "Desc";
        
        for (int i = 0; i < configTag.length; i++)
        {
            String[] configKeyvalue = configTag[i].split("=", 2);
            Hashtable<String, String> content = new Hashtable<>();
            int a = 0;
            if (configKeyvalue[0].indexOf("#") != 0)
            {
            	//20130730 Jason Remove config print to log
            	/** log_createReport.debug(configKeyvalue[0]); */
                if (configKeyvalue.length == 2)
                {
                    if (configKeyvalue[0].equalsIgnoreCase("mark"))
                    {
                        content.put(configKeyvalue[0].replace(" ", ""),configKeyvalue[1]); //將變數放入System裡面
                        a = 0;
                        checkNew=true;
                    }
                    else if (configKeyvalue[0].indexOf("cul") == 0)
                    {
                        if (configKeyvalue[1].split(",").length == 3)
                        {
                            content.put(name,configKeyvalue[1].split(",")[0]);
                            content.put(type,configKeyvalue[1].split(",")[1]);
                            content.put(length,configKeyvalue[1].split(",")[2]);
                        }else if (configKeyvalue[1].split(",").length == 4)
                        {
                            content.put(name,configKeyvalue[1].split(",")[0]);
                            content.put(type,configKeyvalue[1].split(",")[1]);
                            content.put(length,configKeyvalue[1].split(",")[2]);
                            content.put(desc,configKeyvalue[1].split(",")[3]);
                        }
                        else
                        {
                            content.put(name,configKeyvalue[1].split(",")[0]);
                            content.put(type,configKeyvalue[1].split(",")[1]);
                        }

                        a = Integer.parseInt(configKeyvalue[0].replace(" ","").replace("cul", ""));
                        checkNew=true;
                    }
                    else if(configKeyvalue[0].indexOf("top") == 0||configKeyvalue[0].indexOf("end") == 0)
                    {
                        if(result.get(0)==null)
                        {
                            content.put(configKeyvalue[0].replace(" ",""), configKeyvalue[1]);
                            checkNew=true;
                        }
                        else
                        {
                            content=(Hashtable) result.get(0);
                            content.put(configKeyvalue[0].replace(" ",""), configKeyvalue[1]);
                            checkNew=false;
                        }
                        a=0;
                    }
                }

                if(checkNew)
                {
                   result.add(a, content);
                }
                else
                {
                    result.set(a, content);
                }
                checkNew=true;
            }
        }

        /** log_createReport.debug("Config <"+result+">");*/

        /** log_createReport.debug("read End time{"+new Date()+"}");*/
        return result;
    }

	/** 異動說明
	 *                 20221124 Frog Jump Co., YC White Scan/A03 Injection
	 *                 20221128 Frog Jump Co., YC White Scan/A03 Injection/Cross-Site Scripting: Persistent
	 */
    public boolean save(XSSUtils xssUtils, String content) throws IOException
    {
        //write transaction file
    	
    	/** encoder : org.owasp.esapi.Encoder */    	
    	final Encoder encoder = ESAPI.encoder();
    	
    	/** encoder : org.owasp.esapi.Encoder */
    	/** final org.owasp.esapi.Encoder encoder = ESAPIEncoder.getInstance(); */
		
    	EncodedWriter encodedWriter = null;
    	
        try(OutputStream out = response.getOutputStream(); 
        	OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);)
        {	
        	/** Validate byteData -- (1). Need to test*/
        	encodedWriter = new EncodedWriter(outputStreamWriter, (org.owasp.encoder.Encoder) encoder);
        	this.encodedWriterDoWrite(xssUtils, encodedWriter, content, "Big5");
        }        
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
                
        return true;
    }
     
    /**
     * encoded Writer Do Write
     * @param XSSUtils xssUtils
     * @param EncodedWriter util
     * @param String content
     * @param String charsetSet
     * @throws IOException
     */
    private void encodedWriterDoWrite(XSSUtils xssUtils, EncodedWriter util, String content, String charsetSet) throws IOException {
    	try {
    		String checkedContent = xssUtils.dealwithXSSContent(new String(content.getBytes(charsetSet)));
    		boolean checkIfhasXSSContent = xssUtils.hasXSSContent(content);
    		if(!checkIfhasXSSContent) {
    			util.write(checkedContent);
    		}/** else { */
    			/** A03 Injection/Cross-Site Scripting: Persistent
    			log_createReport.error("--checkIfhasXSSContent:true!--");
    			 */
    		/*}*/
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			util.flush();
			util.close();
        }
    }

    /**
     * checkLength
     *
     * @param indata String
     * @param len String
     * @param type String
     * @return String
     */
    private String checkLength(String indata,String  len,String type,String outputtype)
    {
        String retstr=indata;
        String type1= "String";
        String type2= "Number";
        String type3= "Amount";
        String type4= "CardId";
        
        try
        {
            int leng =len.indexOf(".")>=0? Integer.parseInt(len.substring(0,len.indexOf("."))):Integer.parseInt(len) ;
            if (type.equalsIgnoreCase(type1) || type.equalsIgnoreCase(type2)||
                type.equalsIgnoreCase(type3)||type.equalsIgnoreCase(type4))
            {
                if (outputtype.equalsIgnoreCase("txt"))
                {
                    if (indata.length() != leng)
                    {
                        if (indata.length() > leng)
                        {
                            retstr = indata.substring(0, leng);
                        }
                        else
                        {
                            if (type.equals(type1))
                            {
                                for (int i = 0; i < (leng - indata.length()); i++)
                                {
                                    retstr = retstr + " ";
                                }
                            }
                            else if (type.equals(type2))
                            {
                                for (int i = 0; i < (leng - indata.length()); i++)
                                {
                                    retstr = "0" + retstr;
                                }
                            }
                            else if(type.equalsIgnoreCase(type4))
                            {
                                UserBean UserBean = new UserBean();
                                //20140724 Jason 修改卡號遮蔽邏輯
                                retstr= UserBean.get_CardStar(retstr.trim(), 7,4);
                                for (int i = 0; i < (leng - indata.length()); i++)
                                {
                                    retstr = retstr + " ";
                                }
                            }
                            else if (type.equals(type3))
                            {
                                int maxLength = Integer.parseInt(len.substring(0,len.indexOf(".")));
                                int decimalLength = Integer.parseInt(len.substring(len.indexOf(".")+1));
                                String importData = retstr;
                                if(importData.trim().length()>0)
                                {
                                    if (Double.parseDouble(importData) < 0)
                                    {
                                        maxLength = maxLength - 1;
                                    }

                                    NumberFormat nf = NumberFormat.getInstance();
                                    nf.setMaximumFractionDigits(decimalLength);
                                    nf.setMinimumFractionDigits(decimalLength);

                                    DecimalFormat df = new DecimalFormat("0");
                                    df.setMaximumIntegerDigits(maxLength -decimalLength);
                                    df.setMinimumIntegerDigits(maxLength - decimalLength);

                                    importData = nf.format(Double.parseDouble(importData));
                                    /** log_createReport.debug("importData=" + importData); */
                                    importData = importData.replace(",", "");
                                    int decimalPoint = importData.indexOf(".");
                                    String data = importData.substring(0,decimalPoint);
                                    /** log_createReport.debug("data=" + data); */
                                    String decimalData = importData.substring( decimalPoint + 1, importData.length());
                                    /** log_createReport.debug("decimalData=" +decimalData); */
                                    importData = df.format(Double.parseDouble(data));
                                    String exportData = importData + "." +decimalData;
                                    exportData = exportData.substring(1);
                                    /** log_createReport.debug("exportData=" + exportData); */
                                    retstr = exportData;
                                }
                                else
                                {
                                    retstr=checkLength(importData,String.valueOf(maxLength) ,"String","txt");
                                }
                            }
                        }
                    }
                }
                else if(outputtype.equalsIgnoreCase("csv"))
                {
                    if (type.equals("Amount"))
                    {
                        if(indata.trim().length()>0)
                        {
                            //判斷是否為空白 若為空白時,值為'0'
                            if(indata.indexOf(",")<0)
                            {
                                NumberFormat nf = NumberFormat.getInstance();
                                nf.setMaximumFractionDigits(0);
                                nf.setMinimumFractionDigits(2);
                                retstr = nf.format(Double.parseDouble(indata));
                                retstr = retstr.replace(",","");
                            }
                            else
                            {
                                retstr=indata;
                            }
                        }
                        else
                        {
                            retstr=indata;
                        }
                    }
                    else if(type.equalsIgnoreCase("CardId"))
                    {
                        UserBean UserBean = new UserBean();
                      //20140724 Jason 修改卡號遮蔽邏輯
                        retstr= UserBean.get_CardStar(retstr.trim(), 7,4);
                    }
                    else
                    {
                        retstr=  indata;
                    }
                }
            }

        }
        catch (Exception e)
        {
            e.toString();
        }

        return retstr;
    }

    private String doFileContent(ArrayList resultset,ArrayList field,String type)
    {
    	/** log_createReport.debug("doFileContent Start time{"+new Date()+"}"); */
        String output="";
        StringBuilder output1=new StringBuilder();
        String length = "Length";
        try
        {
            String top="";
            String end="";

            if(( (Hashtable<?, ?>) field.get(0)).get("top")!=null)
            {
                top=( (Hashtable<?, ?>) field.get(0)).get("top").toString();
            }

            if(( (Hashtable<?, ?>) field.get(0)).get("end")!=null)
            {
                end=( (Hashtable<?, ?>) field.get(0)).get("end").toString();
            }

            if(top.length()>0 && type.equalsIgnoreCase("txt"))
            {
                output1.append(top);
                output1.append("\r\n");
            }

            for (int a = 0; resultset != null && a < resultset.size(); a++)
            {
            	/** log_createReport.debug("resultset Start time{"+new Date()+"}"); */
            	HashMap<?, ?> content = (HashMap<?, ?>) resultset.get(a);
                /** log_createReport.debug("Read Result<" + content + ">"); */
                if (type.equalsIgnoreCase("txt"))
                {
                    for (int i = 1; field != null && i < field.size(); i++)
                    {
                        String value = String.valueOf(content.get( ( (Hashtable<?, ?>) field.get(i)).get("Name")));
                        String value1 = checkLength(value.trim(),( (Hashtable<?, ?>) field.get(i)).get(length).toString(),( (Hashtable<?, ?>) field.get(i)).get("Type").toString(),"txt");
                        /** log_createReport.debug(i + "=" + (HashMap) field.get(i) +" ||read<" + value + ">out<" + value +">length" + value.length()); */
                        /** output += value; */
                        output1.append(value1);
                    }
                    /** output += "\r\n"; */
                    output1.append("\r\n");
                }
                else if (type.equalsIgnoreCase("csv"))
                {
                    String mark = ( (Hashtable<?, ?>) field.get(0)).get("mark").toString();
                    if(a==0)
                    {
                        //欄位名稱
                        for (int i = 1; field != null && i < field.size(); i++)
                        {
                            String value = String.valueOf( ( (Hashtable<?, ?>)field.get(i)).get("Desc"));
                            if(value!=null&& !value.equalsIgnoreCase("null"))
                            {
                            	/** value = checkLength(value,( (Hashtable) field.get(i)).get(length).toString(),( (Hashtable) field.get(i)).get("Type").toString(),"csv"); */
                                if (i == field.size() - 1)
                                {
                                	/** output +=  "'"+value; */
                                    output1.append("'" + value);
                                }
                                else
                                {
                                	/** output +=  "'"+value + mark; */
                                    output1.append("'" + value + mark);
                                }
                            }
                            /** log_createReport.debug(i + "=" + (Hashtable) field.get(i) +" ||read<" + value + ">out<" + value + ">length" + value.length());*/
                        }
                        output1.append("\r\n");
                    }

                    for (int i = 1; field != null && i < field.size(); i++)
                    {
                        String value = String.valueOf(content.get( ( (Hashtable<?, ?>)field.get(i)).get("Name")));

                        value = checkLength(value,( (Hashtable) field.get(i)).get(length).toString(),( (Hashtable) field.get(i)).get("Type").toString(),"csv");
                        if(i == field.size()-1)
                        {
                        	 /** output +=  "'"+value; */
                            output1.append("'"+value);
                        }
                        else
                        {
                        	 /** output +=  "'"+value + mark; */
                            output1.append( "'"+value + mark);
                        }
                        /** log_createReport.debug(i + "=" + (Hashtable) field.get(i) +" ||read<" + value + ">out<" + value + ">length" + value.length()); */
                    }
                    /** output += "\r\n";*/
                    output1.append("\r\n");
                }
                /** log_createReport.debug("resultset End time{"+new Date()+"}"); */
            }

            if(end.length()>0 && type.equalsIgnoreCase("txt"))
            {
                output1.append(end);
            }

            output=output1.toString();
            output=new String(output.getBytes(),"Big5");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        /** log_createReport.debug("doFileContent End time{"+new Date()+"}");*/

        return output;
    }

    public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
    
}
