package org.thlws.payment.wechat.client;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.thlws.payment.wechat.api.WechatApi;
import org.thlws.payment.wechat.core.WechatCore;
import org.thlws.payment.wechat.entity.request.*;
import org.thlws.payment.wechat.entity.response.*;
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
public class WechatPayClient implements WechatApi {

	private static final Log log = LogFactory.get();


	/**
	 * 申请小微收款识别码
	 *
	 * @param request           the request
	 * @param apiKey      the api key
	 * @param p12FilePath the p 12 file path
	 * @return the micro mch response
	 */
	public static MicroMchResponse postMicroMch(MicroMchRequest request, String apiKey, String p12FilePath) throws  Exception{
		MicroMchResponse response = null;
		try {

			String xml = WechatUtil.buildXmlRequest(request, MicroMchRequest.class,apiKey);
			log.debug("申请小微收款识别码 [submchmanage] xml request:\n {}",xml);

			//p12FilePath = "/zone/1.p12";
			String xmlResponse = ConnUtil.encryptPost(xml, micro_mch_add, request.getMchId(), p12FilePath);
			log.debug("申请小微收款识别码 [submchmanage] xml response:\n {}", ThlwsBeanUtil.formatXml(xmlResponse));
			response = ThlwsBeanUtil.xmlToBean(xmlResponse,MicroMchResponse.class);

		} catch (Exception e) {
			log.error(e);
			throw e;
		}

		return response;

	}


	/***
	 * build request data for wechat ~ query submch
	 * api:https://api.mch.weixin.qq.com/secapi/mch/submchmanage?action=query
	 * @param request the request
	 * @param apiKey the api key
	 * @param p12FilePath the p 12 file path
	 * @return string
	 * @author HanleyTang
	 */
	public static MicroMchResponse queryMicroMch(MicroMchRequest request, String apiKey, String p12FilePath) throws  Exception{

		MicroMchResponse response;
		try {

			String xml = WechatUtil.buildXmlRequest(request, MicroMchRequest.class,apiKey);
			log.debug("查询小微收款人资料[submchmanage?action=query] xml request:\n {}",xml);

			String xmlResponse =ConnUtil.encryptPost(xml, micro_mch_qry, request.getMchId(), p12FilePath);
			log.debug("查询小微收款人资料[submchmanage?action=query] xml response:\n {}",xmlResponse);
			response = ThlwsBeanUtil.xmlToBean(xmlResponse,MicroMchResponse.class);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		return response;

	}


	/***
	 * 统一下单接口,若为扫码支付，调用此方法后需要另开 Thread 调用查询接口，检测用户是否完成支付
	 * @param request the request data
	 * @param apiKey the api key
	 * @return unified order response
	 * @author HanleyTang
	 */
	public static UnifiedOrderResponse unifiedorder(UnifiedOrderRequest request, String apiKey) throws  Exception{

		return WechatCore.unifiedorder(request,apiKey);
	}

	/**
	 * 微信退款
	 * {@link WechatCore#refund}
	 * @param request        the data
	 * @param apiKey      the api key
	 * @param p12FilePath the p 12 file path
	 * @return the wechat refund response
	 */
	public static WechatRefundResponse refund(WechatRefundRequest request, String apiKey, String p12FilePath)throws  Exception{

		return WechatCore.refund(request,apiKey,p12FilePath);

	}

	/**
	 * 支付撤销.
	 * {@link WechatCore#reverse}
	 * @param request        the data
	 * @param apiKey      the api key
	 * @param p12FilePath the p 12 file path
	 * @return the wechat reverse response
	 */
	public static WechatReverseResponse reverse(WechatReverseRequest request, String apiKey, String p12FilePath)throws  Exception{
		return WechatCore.reverse(request,apiKey,p12FilePath);
	}


	/**
	 * 刷卡支付.
	 * {@link WechatCore#micropay}
	 * @param request  the request
	 * @param apiKey the api key
	 * @return the wechat pay response
	 */
	public static WechatPayResponse micropay(WechatPayRequest request, String apiKey) throws  Exception{
		return WechatCore.micropay(request,apiKey);
	}

	/**
	 * 支付查询.
	 * {@link WechatCore#orderQuery}
	 * @param request  the request
	 * @param apiKey the api key
	 * @return the order query response
	 */
	public static OrderQueryResponse orderQuery(OrderQueryRequest request, String apiKey)throws  Exception{
		return	WechatCore.orderQuery(request,apiKey);
	}

	/**
	 * 订单关闭.
	 * {@link WechatCore#closeOrder}
	 * @param request  the request
	 * @param apiKey the api key
	 * @return the close order response
	 */
	public static CloseOrderResponse closeOrder(CloseOrderRequest request, String apiKey)throws  Exception{
		return WechatCore.closeOrder(request,apiKey);
	}


	/**
	 * 查询OpenId.
	 * {@link WechatCore#openidQuery}
	 * @param request   the data
	 * @param apiKey the api key
	 * @return the openid query response
	 */
	public static OpenidQueryResponse openidQuery(OpenidQueryRequest request, String apiKey)throws  Exception{
		return WechatCore.openidQuery(request,apiKey);
	}
}