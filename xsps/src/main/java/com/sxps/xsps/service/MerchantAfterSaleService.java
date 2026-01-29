package com.sxps.xsps.service;

import com.sxps.xsps.pojo.AfterSale;
import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;

import java.util.List;

public interface MerchantAfterSaleService {

    /** 用户提交售后申请 */
    boolean apply(AfterSale afterSale);

    /** 商家处理售后：status=1 同意  status=2 拒绝 */
    boolean handle(Long id, Integer status);

    /** 商家同意售后 */
    boolean approve(Long id);

    /** 商家拒绝售后 */
    boolean reject(Long id);

    /** 用户取消售后申请：参数是订单ID + 用户ID */
    boolean cancel(Long orderId, Long userId);

    /** 根据订单ID获取最新的售后记录（用户端详情用） */
    AfterSale getByOrderId(Long orderId);

    /** 售后列表 （商家端）*/
    List<AfterSale> list(Long merchantId, Integer status);

    /** 售后分页列表 （商家端）*/
    PageResult<AfterSale> page(PageParam param);
}
