package com.ssiot.remote.yun.webapi;

import android.util.Log;
import com.ssiot.remote.data.model.UserGroupModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class UserGroup extends WebBasedb2{
    private static final String tag = "UserGroup";
    private String MethodFile = "UserGroup.asmx";

    public void Delete() {

    }

    public List<UserGroupModel> GetUserGroups(int customerUserRootID) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " Parentid=" + customerUserRootID);
        String txt = exeRetString(MethodFile, "GetUserGroup", params);
        return parse(txt);
    }
    
    private List<UserGroupModel> parse(String str) {
        List<UserGroupModel> models = new ArrayList<UserGroupModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                UserGroupModel cType = new UserGroupModel();
                Node item = items.item(i);
                NodeList properties = item.getChildNodes();
                for (int j = 0; j < properties.getLength(); j++) {
                    Node property = properties.item(j);
                    String nodeName = property.getNodeName();
                    if (null == property.getFirstChild()){
                        continue;
                    }
                    String valueStr = property.getFirstChild().getNodeValue();
                    if (nodeName.equals("UserGroupID")) {
                        cType._id = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("UserGroupName")){
                        cType._groupname = valueStr;
                    }
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (UserGroupModel m : models){
            Log.v(tag, "------id:" +m._id + " UserGroudetail:" + m._groupname);
        }
        return models;
    }
}