package com.mcar.model;

import com.mcar.info.DriverLoc;
import com.mcar.info.UserInfo;

public class Model {
	

	public static String HTTPURL = "http://172.16.24.52:8080/mcar/";
	public static String GANHUO = "upcoming.php?";
	public static String NENCAO = "past.php?";
	public static String WENZI = "wenzi.php?";
	public static String AUDIT = "audit.php?";
	public static String RI = "ri.php?";
	public static String ZHOU = "zhou.php?";
	public static String YUE = "yue.php?";
	public static String YINGCAI = "yingcai.php?";
	public static String SHILING = "shiling.php?";
	public static String CHUANYUE = "chuanyue.php?";
	public static String COMMENTS = "comments.php?";
	public static String NEAR = "near.php?";
	public static String GETUSER = "getuser.php?";
	public static String ACTIVITY = "activity.php?";
	public static String CHILDREN = "children.php?";
	public static String ADDVALUE = "addvalue.php";
	public static String REGISTET = "adduser.php";
	public static String ADDCOMMENT = "addcomment.php";
	public static String LOGIN = "login.php";
	public static String USERINFOSETTING = "userinfosetting.php";
	public static String ADDCHILD = "addchild.php";
	public static String FRIEND = "friend.php?";
	public static String INFOMANAGER = "infomanager.php?";
	public static String ACTIVITYUSERS = "activityusers.php?";
	public static String USERHEADURL = "http://172.16.24.52:80/KidBuddies/Userimg/";
	public static String QIMGURL = "http://172.16.24.52:80/KidBuddies/Valueimg/";
	public static boolean IMGFLAG = false;
	public static UserInfo MYUSERINFO = null;
	public static String RETRIEVE_NEARBY_LIST = "retrievenearblist.php";
	// APP客服KEY
	public static String APPKEY = "fc6f181194f4f6e24e3a7842d5902498";
	/** 当前 DEMO 应用�? APP_KEY，第三方应用应该使用自己�? APP_KEY 替换�? APP_KEY */
	public static final String APP_KEY = "3987368837";
	
	public static String SENDREQUESTCAR = "handleServlet";

	/**
	 * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页�?
	 * 
	 * <p>
	 * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，�?以定义为何种形式都将不影响， 但是没有定义将无法使�? SDK 认证登录�?
	 * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
	 * </p>
	 */
	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

	/**
	 * Scope �? OAuth2.0 授权机制�? authorize 接口的一个参数�?��?�过 Scope，平台将�?放更多的微博
	 * 核心功能给开发�?�，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权�? 选择赋予应用的功能�??
	 * 
	 * 我们通过新浪微博�?放平�?-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的 使用权限，高级权限需要进行申请�??
	 * 
	 * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔�?
	 * 
	 * 有关哪些 OpenAPI �?要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
	 * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
	 */
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";
}
