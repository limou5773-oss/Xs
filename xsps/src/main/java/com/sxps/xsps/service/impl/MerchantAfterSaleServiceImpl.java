package com.sxps.xsps.service.impl;

import com.github.pagehelper.PageHelper;
import com.sxps.xsps.aop.AfterSaleLogRecord;
import com.sxps.xsps.mapper.MerchantAfterSaleMapper;
import com.sxps.xsps.mapper.MerchantOrderMapper;
import com.sxps.xsps.pojo.AfterSale;
import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;
import com.sxps.xsps.service.MerchantAfterSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantAfterSaleServiceImpl implements MerchantAfterSaleService {

    private final MerchantAfterSaleMapper afterSaleMapper;
    private final MerchantOrderMapper orderMapper;

    /**
     * 用户提交售后申请
     */
    @Override
    @AfterSaleLogRecord(value = "用户申请售后", operator = "user")
    @Transactional
    public boolean apply(AfterSale afterSale) {
        // 售后单初始状态：0 = 待处理
        afterSale.setStatus(0);

        int rows = afterSaleMapper.insert(afterSale);
        if (rows <= 0) return false;

        // 把订单状态改为 5 = 售后中
        if (afterSale.getOrderId() != null) {
            orderMapper.updateOrderStatus(afterSale.getOrderId(), 5);
        }
        return true;
    }

    /**
     * 商家处理售后：1=同意 2=拒绝
     */
    @Override
    public boolean handle(Long id, Integer status) {
        MerchantAfterSaleService self =
                (MerchantAfterSaleService) AopContext.currentProxy();
        if (status == 1) return self.approve(id);
        if (status == 2) return self.reject(id);
        return false;
    }

    /**
     * 商家同意售后
     */
    @Override
    @AfterSaleLogRecord(value = "商家同意售后申请", operator = "merchant")
    @Transactional
    public boolean approve(Long id) {
        AfterSale as = afterSaleMapper.getById(id);
        if (as == null) return false;

        // 1：商家已同意
        afterSaleMapper.updateStatus(id, 1);

        // 这里订单状态保持 5=售后中（你想做“售后完成”可以另加 6 再改）
        return true;
    }

    /**
     * 商家拒绝售后
     */
    @Override
    @AfterSaleLogRecord(value = "商家拒绝售后申请", operator = "merchant")
    @Transactional
    public boolean reject(Long id) {
        AfterSale as = afterSaleMapper.getById(id);
        if (as == null) return false;

        // 2：商家已拒绝
        afterSaleMapper.updateStatus(id, 2);
        return true;
    }

    /**
     * 用户取消售后申请：根据 orderId + userId 找到最新一条售后记录取消
     */
    @Override
    @AfterSaleLogRecord(value = "用户取消售后申请", operator = "user")
    @Transactional
    public boolean cancel(Long orderId, Long userId) {
        AfterSale as = afterSaleMapper.getByOrderAndUser(orderId, userId);
        if (as == null) return false;

        // 只能取消自己的、且“待处理”的售后单
        if (!as.getUserId().equals(userId) || as.getStatus() != 0) {
            return false;
        }

        // 3：用户已取消
        afterSaleMapper.updateStatus(as.getId(), 3);

        // 订单状态从 5(售后中) 恢复为 3(已完成)
        orderMapper.updateOrderStatus(orderId, 3);
        return true;
    }

    @Override
    public List<AfterSale> list(Long merchantId, Integer status) {
        List<AfterSale> list = afterSaleMapper.listByMerchant(merchantId, status);
        for (AfterSale as : list) {
            as.setItems(afterSaleMapper.listItems(as.getOrderId()));
        }
        return list;
    }

    @Override
    public PageResult<AfterSale> page(PageParam param) {
        PageHelper.startPage(param.getPage(), param.getSize());
        List<AfterSale> list = afterSaleMapper.pageList(param);
        for (AfterSale as : list) {
            as.setItems(afterSaleMapper.listItems(as.getOrderId()));
        }
        return new PageResult<>(list);
    }

    @Override
    public AfterSale getByOrderId(Long orderId) {
        return afterSaleMapper.getLatestByOrderId(orderId);
    }
}
