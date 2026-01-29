package com.sxps.xsps.service.impl;

import com.sxps.xsps.mapper.MerchantAfterSaleLogMapper;
import com.sxps.xsps.pojo.AfterSaleLog;
import com.sxps.xsps.service.MerchantAfterSaleLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantAfterSaleLogServiceImpl implements MerchantAfterSaleLogService {

    private final MerchantAfterSaleLogMapper logMapper;

    @Override
    public List<AfterSaleLog> listByAfterSaleId(Long afterSaleId) {
        return logMapper.listByAfterSaleId(afterSaleId);
    }
}
