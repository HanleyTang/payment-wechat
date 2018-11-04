package org.thlws.payment.wechat.portal.client;

import com.thoughtworks.xstream.XStream;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.thlws.payment.wechat.api.WechatApi;
import org.thlws.payment.wechat.core.WechatCore;
import org.thlws.payment.wechat.entity.input.*;
import org.thlws.payment.wechat.entity.output.*;
import org.thlws.payment.wechat.extra.xml.XStreamCreator;
import org.thlws.payment.wechat.utils.ConnUtil;
import org.thlws.payment.wechat.utils.ThlwsBeanUtil;
import org.thlws.payment.wechat.utils.WechatUtil;


import java.util.Map;


/**
 * 微信支付相关[非公众号支付]
 *
 * @author Hanley Tang/ hanley@thlws.com
 * @version 1.0
 */
public class WechatClient implements WechatApi {

	private static final Log log = LogFactory.get();


	/**
	 * 申请小微收款识别码
	 *
	 * @param o           the o
	 * @param apiKey      the api key
	 * @param p12FilePath the p 12 file path
	 * @return the micro mch output
	 */
	public static MicroMchOutput postMicroMch(MicroMchInput o, String apiKey, String p12FilePath){
		MicroMchOutput resp = null;
		try {
			Map<String, Object> mapData = ThlwsBeanUtil.ObjectToMap(o);
			mapData = ThlwsBeanUtil.dataFilter(mapData);
			String sign = WechatUtil.sign(mapData,apiKey);
			mapData.put("sign", sign);
			MicroMchInput xwr = (MicroMchInput) ThlwsBeanUtil.mapToObject(mapData,MicroMchInput.class);

			String nonceStr = ThlwsBeanUtil.getRandomString(32);//随机生成32为的字符串
			xwr.setNonce_str(nonceStr);

			XStream xStream = XStreamCreator.create(MicroMchInput.class);
			String xml = xStream.toXML(xwr);
			log.info("申请小微收款识别码 [submchmanage] xml request:\n {}",xml);

			//p12FilePath = "/zone/1.p12";
			String xmlResp = ConnUtil.encryptPost(xml, micro_mch_add, o.getMch_id(), p12FilePath);
			log.info("申请小微收款识别码 [submchmanage] xml response:\n {}", ThlwsBeanUtil.formatXml(xmlResp));
			XStream xStreamOut = XStreamCreator.create( MicroMchOutput.class);
			resp = (MicroMchOutput) xStreamOut.fromXML(xmlResp);
		} catch (Exception e) {
			log.error("申请小微收款识别码失败:{}",e.getMessage());
		}

		return resp;

	}


	/***
	 * build request data for wechat ~ query submch
	 * api:https://api.mch.weixin.qq.com/secapi/mch/submchmanage?action=query
	 * @param o the o
	 * @param apiKey the api key
	 * @param p12FilePath the p 12 file path
	 * @return string
	 * @author HanleyTang
	 */
	public static String queryMicroMch(MicroMchInput o, String apiKey, String p12FilePath){

		String result = "";
		try {
			Map<String, Object> mapData = ThlwsBeanUtil.ObjectToMap(o);
			mapData = ThlwsBeanUtil.dataFilter(mapData);
			String sign = WechatUtil.sign(mapData,apiKey);
			mapData.put("sign", sign);
			MicroMchInput xwr = (MicroMchInput) ThlwsBeanUtil.mapToObject(mapData,MicroMchInput.class);
			xwr.setNonce_str(ThlwsBeanUtil.getRandomString(32));

			XStream xStream = XStreamCreator.create(MicroMchInput.class);
			String xml = xStream.toXML(xwr);
			log.info("查询小微收款人资料[submchmanage?action=query] xml request:\n {}",xml);

			result =ConnUtil.encryptPost(xml, micro_mch_qry, o.getMch_id(), p12FilePath);
			log.info("查询小微收款人资料[submchmanage?action=query] xml response:\n {}",result);
		} catch (Exception e) {
			log.error("queryMicroMch error:{}",e.getMessage());
		}
		return result;

	}


	/***
	 * 统一下单接口,若为扫码支付，调用此方法后需要另开 Thread 调用查询接口，检测用户是否完成支付
	 * @param data the data
	 * @param apiKey the api key
	 * @return unified order output
	 * @author HanleyTang
	 */
	public static UnifiedOrderOutput unifiedorder(UnifiedOrderInput data, String apiKey){

		return WechatCore.unifiedorder(data,apiKey);
	}

	/**
	 * 微信退款
	 * {@link WechatCore#refund}
	 * @param data        the data
	 * @param apiKey      the api key
	 * @param p12FilePath the p 12 file path
	 * @return the wechat refund output
	 */
	public static WechatRefundOutput refund(WechatRefundInput data, String apiKey, String p12FilePath){

		return WechatCore.refund(data,apiKey,p12FilePath);

	}

	/**
	 * 支付撤销.
	 * {@link WechatCore#reverse}
	 * @param data        the data
	 * @param apiKey      the api key
	 * @param p12FilePath the p 12 file path
	 * @return the wechat reverse output
	 */
	public static WechatReverseOutput reverse(WechatReverseInput data, String apiKey, String p12FilePath){
		return WechatCore.reverse(data,apiKey,p12FilePath);
	}


	/**
	 * 刷卡支付.
	 * {@link WechatCore#micropay}
	 * @param input  the input
	 * @param apiKey the api key
	 * @return the wechat pay output
	 */
	public static WechatPayOutput micropay(WechatPayInput input, String apiKey){
		return WechatCore.micropay(input,apiKey);
	}

	/**
	 * 支付查询.
	 * {@link WechatCore#orderQuery}
	 * @param input  the input
	 * @param apiKey the api key
	 * @return the order query output
	 */
	public static OrderQueryOutput orderQuery(OrderQueryInput input, String apiKey){
		return	WechatCore.orderQuery(input,apiKey);
	}

	/**
	 * 订单关闭.
	 * {@link WechatCore#closeOrder}
	 * @param input  the input
	 * @param apiKey the api key
	 * @return the close order output
	 */
	public static CloseOrderOutput closeOrder(CloseOrderInput input,String apiKey){
		return WechatCore.closeOrder(input,apiKey);
	}


	/**
	 * 查询OpenId.
	 * {@link WechatCore#openidQuery}
	 * @param data   the data
	 * @param apiKey the api key
	 * @return the openid query output
	 */
	public static OpenidQueryOutput openidQuery(OpenidQueryInput data,String apiKey){
		return WechatCore.openidQuery(data,apiKey);
	}
}
